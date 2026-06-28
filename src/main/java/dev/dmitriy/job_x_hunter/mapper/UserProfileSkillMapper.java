package dev.dmitriy.job_x_hunter.mapper;

import dev.dmitriy.job_x_hunter.dto.CreateUserProfileRequest;
import dev.dmitriy.job_x_hunter.dto.UserProfileResponseDTO;
import dev.dmitriy.job_x_hunter.dto.UserProfileSkillResponseDTO;
import dev.dmitriy.job_x_hunter.entity.UserProfile;
import dev.dmitriy.job_x_hunter.entity.UserProfileSkill;
import dev.dmitriy.job_x_hunter.repository.UserProfileSkillRepository;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

@Component
public class UserProfileSkillMapper {
    public UserProfileSkillResponseDTO toDto(UserProfileSkill upSkill){
        UserProfileSkillResponseDTO dto = new UserProfileSkillResponseDTO();
        dto.setUserProfileId(upSkill.getUserProfile().getUserProfileId());
        dto.setSkillId(upSkill.getSkill().getSkillId());
        dto.setSkillName(upSkill.getSkill().getSkillName());
        dto.setProficiency(upSkill.getProficiency());
        return dto;
    };

}

//public UserProfileSkill(UserProfile userProfile, Skill skill, @NotNull Integer proficiency) {
//    this.userProfile = userProfile;
//    this.skill = skill;
//    this.proficiency = proficiency;
//
//public class UserProfileSkillResponseDTO {
//    private Long userProfileId;
//    private Long skillId;
//    private String skillName;
//    private Integer proficiency;
//}
