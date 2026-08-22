package dev.dmitriy.job_x_hunter.controller;

import dev.dmitriy.job_x_hunter.dto.*;
import dev.dmitriy.job_x_hunter.service.JobSkillService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/job-skills")
public class JobSkillController {
    private final JobSkillService service;

    public JobSkillController(JobSkillService service){
        this.service = service;
    }

    @PostMapping
    public JobSkillResponseDTO createJobSkill(@Valid @RequestBody CreateJobSkillRequest request){
        return service.createJobSkill(request);
    }

    @GetMapping("/job-vacancy/{jobVacancyId}")
    public List<SkillResponseDTO> getSkillsByJobVacancyId(@PathVariable Long jobVacancyId){
        return this.service.getSkillsByJobVacancyId(jobVacancyId);
    }
}
