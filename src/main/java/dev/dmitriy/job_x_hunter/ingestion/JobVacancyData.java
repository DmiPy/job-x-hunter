package dev.dmitriy.job_x_hunter.ingestion;

import java.math.BigDecimal;
import java.time.Instant;

/** Source values remain null when the provider does not supply them. */
public record JobVacancyData(String source, String sourceJobId, String title, String url,
                             String company, String location, String description,
                             Instant publishedAt, BigDecimal salaryMin, BigDecimal salaryMax,
                             String salaryCurrency, String salaryPeriod, Boolean salaryIsPredicted) { }
