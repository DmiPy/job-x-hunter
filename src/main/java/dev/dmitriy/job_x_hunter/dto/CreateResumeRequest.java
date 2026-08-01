package dev.dmitriy.job_x_hunter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CreateResumeRequest {
    @NotBlank
    private String title;

    @NotNull
    private Long userProfileId;

    private String description;
}

