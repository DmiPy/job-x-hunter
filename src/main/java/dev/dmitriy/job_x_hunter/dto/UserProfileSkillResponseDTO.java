package dev.dmitriy.job_x_hunter.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileSkillResponseDTO {
    private Long userProfileId;
    private Long skillId;
    private String skillName;
    private Integer proficiency;
}
