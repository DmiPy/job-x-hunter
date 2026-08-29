package dev.dmitriy.job_x_hunter.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class JobVacancyResponseDTO {

    private Long jobVacancyId;

    private String title;

    private String url;

    private LocalDate postDate;

    private String description;

    private String source;

    private String sourceJobId;

    private String company;

    private String companyLocation;

    private BigDecimal minSalary;

    private BigDecimal maxSalary;

    private String contractType;

    private String contractTime;
}