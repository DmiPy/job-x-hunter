package dev.dmitriy.job_x_hunter.repository;

import dev.dmitriy.job_x_hunter.entity.JobVacancy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;


public interface JobVacancyRepository extends JpaRepository<JobVacancy, Long> {
    void deleteByPostDateBefore(LocalDate date);
    Optional<JobVacancy> findBySourceAndSourceJobId(String source, String sourceJobId);
}