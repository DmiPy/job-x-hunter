package dev.dmitriy.job_x_hunter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class CreateUserProfileSkillRequest {
    @NotNull
    private Long userProfileId;

    @NotBlank
    private String skillName;

    @NotNull
    @Min(0)
    @Max(100)
    private Integer proficiency;
}
