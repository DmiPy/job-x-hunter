package dev.dmitriy.job_x_hunter.ingestion;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class AdzunaJobSource implements JobSource {
    private final AdzunaApiClient client;
    private final boolean enabled;
    private final int pageSize, maxPages;
    private final String[] keywords, locations, categories;

    public AdzunaJobSource(AdzunaApiClient client,
            @Value("${ingestion.adzuna.enabled:false}") boolean enabled,
            @Value("${ingestion.adzuna.page-size:20}") int pageSize,
            @Value("${ingestion.adzuna.max-pages:3}") int maxPages,
            @Value("${ingestion.adzuna.keywords:}") String keywords,
            @Value("${ingestion.adzuna.locations:}") String locations,
            @Value("${ingestion.adzuna.categories:}") String categories) {
        if (pageSize < 1 || pageSize > 50 || maxPages < 1 || maxPages > 100)
            throw new IllegalArgumentException("Invalid Adzuna pagination configuration");
        this.client = client; this.enabled = enabled; this.pageSize = pageSize; this.maxPages = maxPages;
        this.keywords = keywords.split(";", -1); this.locations = locations.split(";", -1); this.categories = categories.split(";", -1);
    }
    public String name() { return "ADZUNA"; }
    public boolean enabled() { return enabled; }
    public SourceFetchResult fetch() {
        List<JobVacancyData> jobs = new ArrayList<>();
        client.beginRun();
        boolean truncated = false;
        try {
            for (String keyword : keywords) for (String location : locations) for (String category : categories) {
                for (int page = 1; page <= maxPages; page++) {
                    AdzunaSearchResponse response = client.fetchPage(page, pageSize, keyword.trim(), location.trim(), category.trim());
                    List<JsonNode> results = response.results();
                    for (JsonNode item : results) jobs.add(normalize(item));
                    if (results.size() < pageSize) break;
                    if (page == maxPages && (response.count() == null
                            || response.count() > (long) page * pageSize)) truncated = true;
                }
            }
            return new SourceFetchResult(jobs, !truncated, truncated ? "Page limit reached" : null);
        } catch (RuntimeException e) {
            return new SourceFetchResult(jobs, false, "Adzuna fetch failed or budget exhausted");
        }
    }
    private JobVacancyData normalize(JsonNode j) {
        Instant created = null;
        try { if (text(j, "created") != null) created = Instant.parse(text(j, "created")); }
        catch (java.time.format.DateTimeParseException ignored) { }
        Boolean predicted = null;
        JsonNode p = j.path("salary_is_predicted");
        if (p.isBoolean()) predicted = p.booleanValue();
        else if (p.isIntegralNumber() && (p.intValue() == 0 || p.intValue() == 1)) predicted = p.intValue() == 1;
        return new JobVacancyData(name(), text(j, "id"), text(j, "title"), text(j, "redirect_url"),
                text(j.path("company"), "display_name"), text(j.path("location"), "display_name"), text(j, "description"),
                created, decimal(j, "salary_min"), decimal(j, "salary_max"), null, null, predicted);
    }
    private String text(JsonNode j, String key) { JsonNode v = j.path(key); return v.isTextual() || v.isNumber() ? v.asText() : null; }
    private BigDecimal decimal(JsonNode j, String key) { JsonNode v = j.path(key); return v.isNumber() ? v.decimalValue() : null; }
}
