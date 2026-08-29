package dev.dmitriy.job_x_hunter.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class JobVacancyData {

    private String title;

    private String description;

    private String company;

    private String companyLocation;

    private String url;

    private String source;

    private String sourceJobId;

    private LocalDate postDate;

    private BigDecimal minSalary;

    private BigDecimal maxSalary;

    private String contractType;

    private String contractTime;
}