package dev.dmitriy.job_x_hunter.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileSkillIdDTO {
    @NotNull
    private Long userProfileId;

    @NotNull
    private Long skillId;
}
