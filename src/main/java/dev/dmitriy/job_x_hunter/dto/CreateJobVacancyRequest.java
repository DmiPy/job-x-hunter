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
    private String company;
    private String companyLocation;
    private String url;
    private String source;
    private LocalDate postDate;
}

