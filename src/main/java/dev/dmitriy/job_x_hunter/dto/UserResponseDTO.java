package dev.dmitriy.job_x_hunter.dto;

import dev.dmitriy.job_x_hunter.enums.Role;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
public class UserResponseDTO {
    private UUID userId;
    private String email;
    private Role userRole;
    private LocalDateTime createdAt;

}
