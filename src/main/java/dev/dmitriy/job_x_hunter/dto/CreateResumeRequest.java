package dev.dmitriy.job_x_hunter.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CreateResumeRequest {
    @NotBlank
    private String title;

    private String description;
}

