package dev.dmitriy.job_x_hunter.repository;

import dev.dmitriy.job_x_hunter.entity.JobVacancy;
import org.springframework.data.jpa.repository.JpaRepository;


public interface JobVacancyRepository extends JpaRepository<JobVacancy, Long> {
}