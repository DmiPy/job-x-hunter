package dev.dmitriy.job_x_hunter.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;

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