package dev.dmitriy.job_x_hunter.ingestion;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneOffset;

@Component
public class AdzunaApiClient {
    private final HttpClient client;
    private final ObjectMapper mapper;
    private final String baseUrl, appId, appKey;
    private final int budget, retries, runBudget;
    private final long pacingMs, timeoutMs, backoffMs;
    private LocalDate budgetDay;
    private int requests;
    private int runRequests;
    private long lastRequest;

    public AdzunaApiClient(ObjectMapper mapper,
            @Value("${ingestion.adzuna.base-url:https://api.adzuna.com}") String baseUrl,
            @Value("${ingestion.adzuna.app-id:}") String appId,
            @Value("${ingestion.adzuna.app-key:}") String appKey,
            @Value("${ingestion.adzuna.daily-budget:100}") int budget,
            @Value("${ingestion.adzuna.run-budget:30}") int runBudget,
            @Value("${ingestion.adzuna.retries:2}") int retries,
            @Value("${ingestion.adzuna.pacing-ms:1000}") long pacingMs,
            @Value("${ingestion.adzuna.connect-timeout-ms:5000}") long connectMs,
            @Value("${ingestion.adzuna.read-timeout-ms:10000}") long timeoutMs,
            @Value("${ingestion.adzuna.backoff-ms:1000}") long backoffMs) {
        if (budget < 1 || runBudget < 1 || retries < 0 || retries > 5 || pacingMs < 0 || connectMs < 1 || timeoutMs < 1 || backoffMs < 0)
            throw new IllegalArgumentException("Invalid Adzuna request configuration");
        this.mapper = mapper; this.baseUrl = baseUrl; this.appId = appId; this.appKey = appKey;
        this.budget = budget; this.runBudget = runBudget; this.retries = retries; this.pacingMs = pacingMs; this.timeoutMs = timeoutMs; this.backoffMs = backoffMs;
        this.client = HttpClient.newBuilder().connectTimeout(Duration.ofMillis(connectMs)).build();
    }

    public AdzunaSearchResponse fetchPage(int page, int size, String keyword, String location, String category) {
        if (appId.isBlank() || appKey.isBlank()) throw new SourceRequestException("Adzuna credentials are missing");
        String url = baseUrl + "/v1/api/jobs/de/search/" + page + "?app_id=" + enc(appId) + "&app_key=" + enc(appKey)
                + "&results_per_page=" + size + "&content-type=application%2Fjson"
                + param("what", keyword) + param("where", location) + param("category", category);
        for (int attempt = 0; attempt <= retries; attempt++) {
            reserve();
            try {
                HttpResponse<String> response = client.send(HttpRequest.newBuilder(URI.create(url))
                        .timeout(Duration.ofMillis(timeoutMs)).GET().build(), HttpResponse.BodyHandlers.ofString());
                int status = response.statusCode();
                if (status == 200) {
                    JsonNode body = mapper.readTree(response.body());
                    if (body == null || !body.path("results").isArray()) throw new SourceRequestException("Malformed Adzuna response");
                    return mapper.treeToValue(body, AdzunaSearchResponse.class);
                }
                if ((status == 429 || status >= 500 && status <= 599) && attempt < retries) {
                    long delay = Math.min(30000, backoffMs * (1L << attempt));
                    if (status == 429) {
                        String retryAfter = response.headers().firstValue("Retry-After").orElse("");
                        if (!retryAfter.isBlank()) {
                            long requested;
                            try { requested = Math.multiplyExact(Long.parseLong(retryAfter), 1000); }
                            catch (NumberFormatException ex) {
                                try { requested = Duration.between(java.time.ZonedDateTime.now(ZoneOffset.UTC),
                                        java.time.ZonedDateTime.parse(retryAfter, java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME)).toMillis(); }
                                catch (java.time.DateTimeException badDate) { throw new SourceRequestException("Invalid Retry-After"); }
                            } catch (ArithmeticException ex) { throw new SourceRequestException("Retry-After too long"); }
                            if (requested > 30000) throw new SourceRequestException("Retry-After exceeds retry window");
                            delay = Math.max(delay, requested);
                        }
                    }
                    pause(delay); continue;
                }
                throw new SourceRequestException("Adzuna HTTP " + status);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); throw new SourceRequestException("Adzuna request interrupted");
            } catch (java.io.IOException e) {
                if (attempt == retries) throw new SourceRequestException("Adzuna network or response error");
                pause(Math.min(30000, backoffMs * (1L << attempt)));
            }
        }
        throw new SourceRequestException("Adzuna retries exhausted");
    }

    private synchronized void reserve() {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        if (!today.equals(budgetDay)) { budgetDay = today; requests = 0; }
        if (requests >= budget) throw new SourceRequestException("Adzuna daily budget exhausted");
        if (runRequests >= runBudget) throw new SourceRequestException("Adzuna run budget exhausted");
        pause(Math.max(0, pacingMs - (System.currentTimeMillis() - lastRequest)));
        requests++; runRequests++; lastRequest = System.currentTimeMillis();
    }
    public synchronized void beginRun() { runRequests = 0; }
    private void pause(long ms) {
        try { if (ms > 0) Thread.sleep(ms); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); throw new SourceRequestException("Adzuna request interrupted"); }
    }
    private String param(String name, String value) { return value == null || value.isBlank() ? "" : "&" + name + "=" + enc(value); }
    private String enc(String s) { return URLEncoder.encode(s, StandardCharsets.UTF_8); }
    public static class SourceRequestException extends RuntimeException {
        public SourceRequestException(String message) { super(message); }
    }
}
