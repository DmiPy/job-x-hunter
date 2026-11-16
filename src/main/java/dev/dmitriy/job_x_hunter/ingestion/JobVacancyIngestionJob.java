package dev.dmitriy.job_x_hunter.ingestion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class JobVacancyIngestionJob {
    private static final Logger log = LoggerFactory.getLogger(JobVacancyIngestionJob.class);
    private final JobIngestionService service;
    private final boolean enabled;
    public JobVacancyIngestionJob(JobIngestionService service, @Value("${ingestion.schedule.enabled:false}") boolean enabled) {
        this.service = service; this.enabled = enabled;
    }
    @Scheduled(fixedDelayString = "${ingestion.schedule.interval-ms:3600000}")
    public void run() {
        if (!enabled) return;
        try { service.ingest(); }
        catch (IngestionAlreadyRunningException ex) { log.info("Scheduled ingestion skipped: already running"); }
    }
}
