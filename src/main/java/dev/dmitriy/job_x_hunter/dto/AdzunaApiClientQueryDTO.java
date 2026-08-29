package dev.dmitriy.job_x_hunter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdzunaApiClientQueryDTO {
    @NotNull
    private Integer jobsAmount;

    @NotEmpty
    private String[] keywords;

    @NotEmpty
    private String[] locations;

    @NotNull
    private Integer distance;

    @NotNull
    private Integer maxDaysOld;

    @NotNull
    private Integer salaryMin;

    private Integer salaryMax;

    @NotBlank
    private String fullTime;

    @NotBlank
    private String partTime;
}
