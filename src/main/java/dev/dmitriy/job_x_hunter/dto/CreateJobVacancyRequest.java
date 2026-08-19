package dev.dmitriy.job_x_hunter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateJobVacancyRequest {

    @NotBlank
    private String title;

    private String description;

    @NotBlank
    private String company;

    @NotBlank
    private String companyLocation;

    @NotBlank
    private String url;

    @NotBlank
    private String source;

    private LocalDate postDate;

    @NotBlank
    private String sourceJobId;
}