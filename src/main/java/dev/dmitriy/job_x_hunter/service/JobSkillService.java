package dev.dmitriy.job_x_hunter.service;

import dev.dmitriy.job_x_hunter.dto.CreateJobSkillRequest;
import dev.dmitriy.job_x_hunter.dto.JobSkillResponseDTO;
import dev.dmitriy.job_x_hunter.dto.SkillResponseDTO;
import dev.dmitriy.job_x_hunter.entity.JobSkill;
import dev.dmitriy.job_x_hunter.entity.JobSkillId;
import dev.dmitriy.job_x_hunter.entity.JobVacancy;
import dev.dmitriy.job_x_hunter.entity.Skill;
import dev.dmitriy.job_x_hunter.exception.JobSkillAlreadyExistsException;
import dev.dmitriy.job_x_hunter.exception.JobVacancyNotFoundException;
import dev.dmitriy.job_x_hunter.exception.SkillNotFoundException;
import dev.dmitriy.job_x_hunter.mapper.SkillMapper;
import dev.dmitriy.job_x_hunter.repository.JobSkillRepository;
import dev.dmitriy.job_x_hunter.repository.JobVacancyRepository;
import dev.dmitriy.job_x_hunter.repository.SkillRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobSkillService {
    private final SkillMapper skillMapper;
    private JobSkillRepository jsrepo;
    private JobVacancyRepository jvrepo;
    private SkillRepository srepo;

    public JobSkillService(
            JobSkillRepository jsrepo,
            JobVacancyRepository jvrepo,
            SkillRepository srepo,
            SkillMapper skillMapper){
        this.jsrepo = jsrepo;
        this.jvrepo = jvrepo;
        this.srepo = srepo;
        this.skillMapper = skillMapper;
    }

    private JobVacancy findOrCreateJobVacancy(Long jobVacancyId){
        return jvrepo.findById(jobVacancyId).orElseThrow(() -> {
            throw new JobVacancyNotFoundException("Vacancy with an id:"+ jobVacancyId + " does not exist.");
        });
    }

    private Skill findOrCreateSkill(Long skillId){
        return srepo.findById(skillId).orElseThrow(() -> {
            throw new SkillNotFoundException("Skill with an id:"+ skillId + " does not exist.");
        });
    }

    public JobSkillResponseDTO createJobSkill(CreateJobSkillRequest request){
        Skill skill = findOrCreateSkill(request.getSkillId());
        JobVacancy jobVacancy = findOrCreateJobVacancy(request.getJobVacancyId());

        JobSkillId jobSkillId = new JobSkillId(jobVacancy.getJobVacancyId(), skill.getSkillId());

        if(jsrepo.existsById(jobSkillId)){
            throw new JobSkillAlreadyExistsException(
                    "Job Skill with an id of job vacancy:" + jobSkillId.getJobVacancyId()
                            + "and id of skill:" + jobSkillId.getSkillId() + " was not found."
            );
        }

        JobSkill jobSkill = new JobSkill();

        jobSkill.setId(jobSkillId);
        jobSkill.setJobVacancy(jobVacancy);
        jobSkill.setSkill(skill);

        jsrepo.save(jobSkill);

        JobSkillResponseDTO response = new JobSkillResponseDTO();
        response.setJobVacancyId(jobVacancy.getJobVacancyId());
        response.setSkillId(skill.getSkillId());

        return response;
    }

    public List<SkillResponseDTO> getSkillsByJobVacancyId(Long jobVacancyId) {

        List<JobSkill> jobSkills =
                jsrepo.findByJobVacancy_JobVacancyId(jobVacancyId);

        return jobSkills.stream()
                .map(JobSkill::getSkill)
                .map(skillMapper::toDto)
                .toList();
    }
}
