package dev.dmitriy.job_x_hunter.job;

import dev.dmitriy.job_x_hunter.service.JobVacancyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class JobVacancyCleanupJob {

    private final JobVacancyService jobVacancyService;

    private static final Logger log = LoggerFactory.getLogger(JobVacancyCleanupJob.class);

    public JobVacancyCleanupJob(JobVacancyService jobVacancyService) {
        this.jobVacancyService = jobVacancyService;
    }

    // Retention must account for user interactions before automatic deletion is enabled.
    public void cleanup() {

        log.info("Starting job vacancy cleanup");

        int deleted = jobVacancyService.deleteExpiredJobVacancies();

        log.info(
                "Job vacancy cleanup finished. Deleted {} expired vacancies.",
                deleted
        );
    }
}
