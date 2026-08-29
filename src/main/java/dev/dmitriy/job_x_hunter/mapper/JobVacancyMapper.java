package dev.dmitriy.job_x_hunter.mapper;

import dev.dmitriy.job_x_hunter.dto.CreateJobVacancyRequest;
import dev.dmitriy.job_x_hunter.dto.JobVacancyResponseDTO;
import dev.dmitriy.job_x_hunter.entity.JobVacancy;
import org.springframework.stereotype.Component;

@Component
public class JobVacancyMapper {

    public JobVacancy toEntity(CreateJobVacancyRequest dto) {

        JobVacancy entity = new JobVacancy();

        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setUrl(dto.getUrl());
        entity.setCompany(dto.getCompany());
        entity.setCompanyLocation(dto.getCompanyLocation());
        entity.setSource(dto.getSource());
        entity.setSourceJobId(dto.getSourceJobId());
        entity.setMinSalary(dto.getMinSalary());
        entity.setMaxSalary(dto.getMaxSalary());
        entity.setContractType(dto.getContractType());
        entity.setContractTime(dto.getContractTime());

        if (dto.getPostDate() != null) {
            entity.setPostDate(dto.getPostDate());
        }

        return entity;
    }

    public JobVacancyResponseDTO toDto(JobVacancy entity) {

        JobVacancyResponseDTO dto = new JobVacancyResponseDTO();

        dto.setJobVacancyId(entity.getJobVacancyId());
        dto.setTitle(entity.getTitle());
        dto.setUrl(entity.getUrl());
        dto.setDescription(entity.getDescription());
        dto.setPostDate(entity.getPostDate());
        dto.setCompany(entity.getCompany());
        dto.setCompanyLocation(entity.getCompanyLocation());
        dto.setSource(entity.getSource());
        dto.setSourceJobId(entity.getSourceJobId());
        dto.setMinSalary(entity.getMinSalary());
        dto.setMaxSalary(entity.getMaxSalary());
        dto.setContractType(entity.getContractType());
        dto.setContractTime(entity.getContractTime());

        return dto;
    }
}