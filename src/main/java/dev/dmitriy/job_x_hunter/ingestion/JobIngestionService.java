package dev.dmitriy.job_x_hunter.ingestion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class JobIngestionService {
    private static final Logger log = LoggerFactory.getLogger(JobIngestionService.class);
    private final List<JobSource> sources;
    private final VacancyUpsertRepository vacancies;
    private final AtomicBoolean running = new AtomicBoolean();

    public JobIngestionService(List<JobSource> sources, VacancyUpsertRepository vacancies) {
        this.sources = sources;
        this.vacancies = vacancies;
    }

    public RunSummary ingest() {
        if (!running.compareAndSet(false, true)) throw new IngestionAlreadyRunningException();
        int fetched = 0, inserted = 0, updated = 0, invalid = 0, failed = 0;
        try {
            for (JobSource source : sources) {
                if (!source.enabled()) continue;
                try {
                    SourceFetchResult result = source.fetch();
                    if (!result.complete()) {
                        failed++;
                        log.warn("Ingestion source returned incomplete result source={}", source.name());
                    }
                    for (JobVacancyData job : result.vacancies()) {
                        fetched++;
                        if (job == null || blank(job.source()) || blank(job.sourceJobId()) || blank(job.title()) || blank(job.url())) {
                            invalid++;
                            continue;
                        }
                        try { if (vacancies.upsertWithResult(job).inserted()) inserted++; else updated++; }
                        catch (RuntimeException ex) { failed++; log.warn("Vacancy persistence failed source={}", source.name()); }
                    }
                } catch (RuntimeException ex) {
                    failed++;
                    log.warn("Ingestion source failed source={}", source.name());
                }
            }
            RunSummary summary = new RunSummary(fetched, inserted, updated, invalid, failed);
            log.info("Ingestion finished fetched={} inserted={} updated={} invalid={} failed={}", fetched, inserted, updated, invalid, failed);
            return summary;
        } finally { running.set(false); }
    }

    private boolean blank(String value) { return value == null || value.isBlank(); }
    public record RunSummary(int fetched, int inserted, int updated, int invalid, int failed) { }
}
