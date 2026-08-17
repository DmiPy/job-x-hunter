package dev.dmitriy.job_x_hunter.service;

import dev.dmitriy.job_x_hunter.dto.CreateJobVacancyRequest;
import dev.dmitriy.job_x_hunter.dto.JobVacancyResponseDTO;
import dev.dmitriy.job_x_hunter.entity.JobVacancy;
import dev.dmitriy.job_x_hunter.mapper.JobVacancyMapper;
import dev.dmitriy.job_x_hunter.repository.JobVacancyRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JobVacancyService {
    private final JobVacancyRepository repo;

    private final JobVacancyMapper mapper;
    public JobVacancyService(JobVacancyRepository repo, JobVacancyMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    private JobVacancy findJobVacancy(Long id){
        return repo.findById(id).orElseThrow( () -> new JobVacancyNotFoundException(
                "Job Vacancy with an id: " + id + " could not be found."
        ));
    }

    public JobVacancyResponseDTO createJobVacancy(CreateJobVacancyRequest request){
        JobVacancy entity = mapper.toEntity(request);
        entity = repo.save(entity);
        return mapper.toDto(entity);
    }

    public JobVacancyResponseDTO getJobVacancyById(Long jobVacancyId){
        JobVacancy entity = findJobVacancy(jobVacancyId);
        return mapper.toDto(entity);
    }



}
