package dev.dmitriy.job_x_hunter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
public class ResumeResponseDTO {
    private Long resumeId;

    @NotBlank
    private String title;

    @NotNull
    private Long userProfileId;

    @NotBlank
    private String url;

    @NotNull
    private LocalDate uploadDate;

    private String description;

    @NotNull
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

