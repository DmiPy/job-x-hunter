package dev.dmitriy.job_x_hunter.repository;

import dev.dmitriy.job_x_hunter.entity.User;
import dev.dmitriy.job_x_hunter.entity.UserSkill;
import dev.dmitriy.job_x_hunter.entity.UserSkillId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserSkillRepository extends JpaRepository<UserSkill, UserSkillId> {
    <Optional> User findByUser_UserId(UUID userId);
    findByUser_UserIdAndSkill_SkillId(UUID userId, Long skillId);
    existsByUser_UserIdAndSkill_SkillId(UUID userId, Long skillId);
}
