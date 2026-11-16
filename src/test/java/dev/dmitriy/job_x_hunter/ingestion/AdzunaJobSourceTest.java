package dev.dmitriy.job_x_hunter.ingestion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;

class AdzunaJobSourceTest {
    private HttpServer server;
    private AtomicInteger calls;
    @BeforeEach void start() throws Exception {
        calls = new AtomicInteger();
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.start();
    }
    @AfterEach void stop() { server.stop(0); }
    private AdzunaApiClient client(int budget, int retries, long timeout) {
        return new AdzunaApiClient(new ObjectMapper(), "http://127.0.0.1:" + server.getAddress().getPort(),
                "test", "test", budget, budget, retries, 0, 1000, timeout, 1);
    }
    private void respond(com.sun.net.httpserver.HttpExchange e, int code, String body) throws java.io.IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        e.sendResponseHeaders(code, bytes.length); e.getResponseBody().write(bytes); e.close();
    }
    @Test void paginatesAndNormalizesMissingOptionalFields() {
        server.createContext("/", e -> {
            calls.incrementAndGet();
            if (e.getRequestURI().getPath().endsWith("/1")) respond(e, 200, "{\"results\":[{\"id\":\"42\",\"title\":\"Java\",\"redirect_url\":\"https://example.test\"}]}");
            else respond(e, 200, "{\"results\":[]}");
        });
        var result = new AdzunaJobSource(client(5, 0, 1000), true, 1, 3, "java", "Berlin", "").fetch();
        assertTrue(result.complete()); assertEquals(2, calls.get()); assertEquals(1, result.vacancies().size());
        assertEquals("42", result.vacancies().getFirst().sourceJobId());
        assertNull(result.vacancies().getFirst().company()); assertNull(result.vacancies().getFirst().salaryCurrency());
    }
    @Test void retries429AndCountsAttemptsAgainstBudget() {
        server.createContext("/", e -> { int n = calls.incrementAndGet(); respond(e, n == 1 ? 429 : 200, n == 1 ? "busy" : "{\"results\":[]}"); });
        var result = new AdzunaJobSource(client(2, 2, 1000), true, 1, 1, "", "", "").fetch();
        assertTrue(result.complete()); assertEquals(2, calls.get());
        calls.set(0);
        var limited = new AdzunaJobSource(client(1, 2, 1000), true, 1, 1, "", "", "").fetch();
        assertFalse(limited.complete()); assertEquals(1, calls.get());
    }
    @Test void preservesPartialResultsWhenSubsequentPageFails() {
        server.createContext("/", e -> { int n = calls.incrementAndGet(); respond(e, n == 1 ? 200 : 503,
                n == 1 ? "{\"results\":[{\"id\":\"1\"}]}" : "error"); });
        var result = new AdzunaJobSource(client(5, 1, 1000), true, 1, 3, "", "", "").fetch();
        assertFalse(result.complete()); assertEquals(1, result.vacancies().size()); assertEquals(3, calls.get());
    }
    @Test void malformedResponseIsPartialFailure() {
        server.createContext("/", e -> respond(e, 200, "{}"));
        assertFalse(new AdzunaJobSource(client(2, 0, 1000), true, 1, 1, "", "", "").fetch().complete());
    }
    @Test void timeoutIsRetriedButBounded() {
        server.createContext("/", e -> {
            calls.incrementAndGet();
            try { Thread.sleep(300); respond(e, 200, "{\"results\":[]}"); }
            catch (InterruptedException ex) { Thread.currentThread().interrupt(); }
            catch (java.io.IOException ignored) { }
        });
        var result = new AdzunaJobSource(client(3, 1, 50), true, 1, 1, "", "", "").fetch();
        assertFalse(result.complete());
    }
}
