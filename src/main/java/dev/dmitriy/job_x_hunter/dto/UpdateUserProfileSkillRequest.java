package dev.dmitriy.job_x_hunter.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserProfileSkillRequest {

    @NotNull
    private Long profileId;

    @NotNull
    private Long skillId;

    @Min(1)
    @Max(5)
    private Integer proficiency;
}