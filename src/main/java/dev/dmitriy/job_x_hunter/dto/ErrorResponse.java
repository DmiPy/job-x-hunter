package dev.dmitriy.job_x_hunter.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErrorResponse {
    private int status;
    private String errorMessage;
    private LocalDateTime timestamp;
}
