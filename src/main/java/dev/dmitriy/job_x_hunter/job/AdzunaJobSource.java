package dev.dmitriy.job_x_hunter.job;

import dev.dmitriy.job_x_hunter.entity.SearchFilter;
import dev.dmitriy.job_x_hunter.model.JobVacancyData;

import java.util.List;

public class AdzunaJobSource implements JobSource{
    @Override
    public List<JobVacancyData> search(SearchFilter filter) {
        return List.of();
    }
}
