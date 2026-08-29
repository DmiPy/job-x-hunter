package dev.dmitriy.job_x_hunter.job;

import dev.dmitriy.job_x_hunter.entity.SearchFilter;
import dev.dmitriy.job_x_hunter.model.JobVacancyData;

import java.util.List;

public interface JobSource {
    List<JobVacancyData> search(SearchFilter filter);
}
