package dev.dmitriy.job_x_hunter.ingestion;

import java.util.List;

public record SourceFetchResult(List<JobVacancyData> vacancies, boolean complete, String failureReason) {
    public SourceFetchResult { vacancies = List.copyOf(vacancies); }
}
