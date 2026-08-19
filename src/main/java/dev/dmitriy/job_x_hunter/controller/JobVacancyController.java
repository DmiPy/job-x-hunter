package dev.dmitriy.job_x_hunter.controller;

import dev.dmitriy.job_x_hunter.dto.*;
import dev.dmitriy.job_x_hunter.service.JobVacancyService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/job-vacancies")
public class JobVacancyController {
    private final JobVacancyService service;

    public JobVacancyController(JobVacancyService service){
        this.service = service;
    }

    @PostMapping
    public JobVacancyResponseDTO createJobVacancy(@Valid @RequestBody CreateJobVacancyRequest request){
        return service.createJobVacancy(request);
    }

    @GetMapping("/{id}")
    public JobVacancyResponseDTO getJobVacancyById(@PathVariable Long id){
        return this.service.getJobVacancyById(id);
    }

    @GetMapping
    public Page<JobVacancyResponseDTO> getJobVacancies(@PageableDefault(size = 20) Pageable pageable) {
        return this.service.getJobVacancies(pageable);
    }
}
