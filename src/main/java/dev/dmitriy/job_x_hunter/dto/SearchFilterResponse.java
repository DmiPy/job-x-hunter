package dev.dmitriy.job_x_hunter.dto;

import dev.dmitriy.job_x_hunter.enums.EmploymentType;
import dev.dmitriy.job_x_hunter.enums.WorkplaceType;
import java.util.Set;

public record SearchFilterResponse(
        Long id, String preferredLocation, Integer radiusKm, Integer minimumSalary,
        String salaryCurrency, String salaryPeriod, String keywords,
        Set<EmploymentType> employmentTypes, Set<WorkplaceType> workplaceTypes,
        Set<Long> preferredSkillIds, Set<Long> excludedSkillIds) {}
