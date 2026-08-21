package dev.dmitriy.job_x_hunter.repository;

import dev.dmitriy.job_x_hunter.entity.JobSkill;
import dev.dmitriy.job_x_hunter.entity.JobSkillId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobSkillRepository extends JpaRepository<JobSkill, JobSkillId> {
    List<JobSkill> findByJobVacancy_JobVacancyId(Long jobVacancyId);
    boolean existsById(JobSkillId id);
}
