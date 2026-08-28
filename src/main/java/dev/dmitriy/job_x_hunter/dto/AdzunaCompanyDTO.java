package dev.dmitriy.job_x_hunter.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AdzunaCompanyDTO {
    @JsonProperty("display_name")
    private String displayName;

    @JsonProperty("canonical_name")
    private String canonicalName;

    private Integer count;

    @JsonProperty("average_salary")
    private Integer averageSalary;
}
