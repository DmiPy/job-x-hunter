package dev.dmitriy.job_x_hunter.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
public class ResumeResponseDTO {
    private Long resumeId;

    private String title;

    private Long userProfileId;

    private String url;

    private LocalDate uploadDate;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

