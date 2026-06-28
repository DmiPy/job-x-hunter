package dev.dmitriy.job_x_hunter.repository;

import dev.dmitriy.job_x_hunter.entity.Skill;
import dev.dmitriy.job_x_hunter.entity.UserProfileSkill;
import dev.dmitriy.job_x_hunter.entity.UserProfileSkillId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserProfileSkillRepository extends JpaRepository<UserProfileSkill, UserProfileSkillId> {
    List<UserProfileSkill> findByUserProfile_UserProfileId(Long userProfileId);
    Optional <UserProfileSkill> findByUserProfile_UserProfileIdAndSkill_SkillId(Long UserProfileId, Long skillId);
    Boolean existsByUserProfile_UserProfileIdAndSkill_SkillId(Long UserProfileId, Long skillId);

}
