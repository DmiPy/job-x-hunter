package dev.dmitriy.job_x_hunter.ingestion;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JobIngestionServiceTest {
    @Test void preventsOverlapAndReleasesLock() throws Exception {
        CountDownLatch entered = new CountDownLatch(1), release = new CountDownLatch(1);
        JobSource source = new JobSource() {
            public String name() { return "TEST"; }
            public boolean enabled() { return true; }
            public SourceFetchResult fetch() {
                entered.countDown();
                try { if (!release.await(5, TimeUnit.SECONDS)) throw new IllegalStateException(); }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); throw new IllegalStateException(); }
                return new SourceFetchResult(List.of(), true, null);
            }
        };
        var service = new JobIngestionService(List.of(source), mock(VacancyUpsertRepository.class));
        try (var executor = Executors.newSingleThreadExecutor()) {
            var running = executor.submit(service::ingest);
            assertTrue(entered.await(5, TimeUnit.SECONDS));
            assertThrows(IngestionAlreadyRunningException.class, service::ingest);
            release.countDown();
            assertEquals(0, running.get(5, TimeUnit.SECONDS).failed());
        }
        assertEquals(0, service.ingest().failed());
    }
    @Test void disabledSchedulerDoesNotInvokeService() {
        var service = mock(JobIngestionService.class);
        new JobVacancyIngestionJob(service, false).run();
        verifyNoInteractions(service);
    }
    @Test void reportsPartialFailuresAndContinues() {
        var repo = mock(VacancyUpsertRepository.class);
        var job = new JobVacancyData("TEST", "id", "title", "https://example.test", null, null, null, null, null, null, null, null, null);
        when(repo.upsertWithResult(job)).thenReturn(new VacancyUpsertRepository.UpsertResult(1, true));
        JobSource source = new JobSource() {
            public String name() { return "TEST"; }
            public boolean enabled() { return true; }
            public SourceFetchResult fetch() { return new SourceFetchResult(List.of(job), false, "partial"); }
        };
        var summary = new JobIngestionService(List.of(source), repo).ingest();
        assertEquals(1, summary.fetched()); assertEquals(1, summary.inserted()); assertEquals(1, summary.failed());
    }
    @Test void reportsInvalidAndUpdatedRecords() {
        var repo = mock(VacancyUpsertRepository.class);
        var valid = new JobVacancyData("TEST", "id", "title", "https://example.test", null, null, null, null, null, null, null, null, null);
        var invalid = new JobVacancyData("TEST", "missing-title", null, "https://example.test", null, null, null, null, null, null, null, null, null);
        when(repo.upsertWithResult(valid)).thenReturn(new VacancyUpsertRepository.UpsertResult(1, false));
        JobSource source = new JobSource() {
            public String name() { return "TEST"; }
            public boolean enabled() { return true; }
            public SourceFetchResult fetch() { return new SourceFetchResult(List.of(valid, invalid), true, null); }
        };
        var result = new JobIngestionService(List.of(source), repo).ingest();
        assertEquals(2, result.fetched()); assertEquals(1, result.invalid()); assertEquals(1, result.updated());
        verify(repo, never()).upsertWithResult(invalid);
    }
}
