package dev.dmitriy.job_x_hunter.job;

import dev.dmitriy.job_x_hunter.service.JobVacancyService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class JobVacancyCleanupJob {

    private final JobVacancyService jobVacancyService;

    public JobVacancyCleanupJob(JobVacancyService jobVacancyService) {
        this.jobVacancyService = jobVacancyService;
    }

    @Scheduled(
            cron = "0 0 3 * * *"
    )
    public void cleanup() {
        jobVacancyService.deleteExpiredJobVacancies();
    }
}