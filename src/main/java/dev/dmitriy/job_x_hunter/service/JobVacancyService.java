package dev.dmitriy.job_x_hunter.service;

import dev.dmitriy.job_x_hunter.dto.CreateJobVacancyRequest;
import dev.dmitriy.job_x_hunter.dto.JobVacancyResponseDTO;
import dev.dmitriy.job_x_hunter.entity.JobVacancy;
import dev.dmitriy.job_x_hunter.exception.JobVacancyAlreadyExistsException;
import dev.dmitriy.job_x_hunter.exception.JobVacancyNotFoundException;
import dev.dmitriy.job_x_hunter.mapper.JobVacancyMapper;
import dev.dmitriy.job_x_hunter.repository.JobVacancyRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class JobVacancyService {
    private final JobVacancyRepository repo;
    private final JobVacancyMapper mapper;

    public JobVacancyService(JobVacancyRepository repo, JobVacancyMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    public Page<JobVacancyResponseDTO> getJobVacancies(Pageable pageable){
        return repo.findAll(pageable).map(mapper::toDto);
    }

    private JobVacancy findJobVacancy(Long id){
        return repo.findById(id).orElseThrow( () -> new JobVacancyNotFoundException(
                "Job Vacancy with an id: " + id + " could not be found."
        ));
    }

    public JobVacancyResponseDTO createJobVacancy(CreateJobVacancyRequest request){
        JobVacancy entity = mapper.toEntity(request);
        if(repo.findBySourceAndSourceJobId(entity.getSource(), entity.getSourceJobId()).isPresent()){
            throw new JobVacancyAlreadyExistsException("Job Vacancy with the title: " + entity.getTitle() + " already exists.");
        }
        entity = repo.save(entity);
        return mapper.toDto(entity);
    }

    public JobVacancyResponseDTO getJobVacancyById(Long jobVacancyId){
        JobVacancy entity = findJobVacancy(jobVacancyId);
        return mapper.toDto(entity);
    }

    @Transactional
    public int deleteExpiredJobVacancies(){
        return repo.deleteByPostDateBefore(LocalDate.now().minusDays(30));
    }



}
