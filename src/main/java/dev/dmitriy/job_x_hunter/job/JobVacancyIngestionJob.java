package dev.dmitriy.job_x_hunter.job;

import dev.dmitriy.job_x_hunter.dto.AdzunaApiClientQueryDTO;
import dev.dmitriy.job_x_hunter.dto.AdzunaApiClientResponseDTO;
import dev.dmitriy.job_x_hunter.jobparsing.AdzunaApiClient;
import dev.dmitriy.job_x_hunter.service.JobIngestionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class JobVacancyIngestionJob {
    private final JobIngestionService jobIngestionService;

    public JobVacancyIngestionJob(JobIngestionService jobIngestionService){
        this.jobIngestionService = jobIngestionService;
    }

    @Scheduled(fixedRate = 10_000)
    public void ingest(){
        this.jobIngestionService.ingest();
    }

}
