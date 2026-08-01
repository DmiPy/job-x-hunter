package dev.dmitriy.job_x_hunter.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class UpdateResumeRequest {
    private Long resumeId;
    private String title;
    private String url;
    private String description;

}
