package dev.dmitriy.job_x_hunter.service;

import dev.dmitriy.job_x_hunter.repository.JobVacancyRepository;
import org.springframework.stereotype.Service;

@Service
public class JobVacancyService {
    private final JobVacancyRepository repo;

    public JobVacancyService(JobVacancyRepository repo) {
        this.repo = repo;
    }


}
