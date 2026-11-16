package dev.dmitriy.job_x_hunter.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.Instant;
import java.math.BigDecimal;

@Setter
@Getter
public class JobVacancyResponseDTO {
    private String title;
    private Long jobVacancyId;
    private String url;
    private LocalDate postDate;
    private String description;
    private String source;
    private String sourceJobId;
    private String company;
    private String companyLocation;
    private Instant sourcePublishedAt;
    private Instant firstSeenAt;
    private Instant lastSeenAt;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private String salaryCurrency;
    private String salaryPeriod;
    private Boolean salaryIsPredicted;
}

