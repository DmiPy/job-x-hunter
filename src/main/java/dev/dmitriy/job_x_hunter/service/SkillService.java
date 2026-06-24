package dev.dmitriy.job_x_hunter.service;

import dev.dmitriy.job_x_hunter.dto.SkillResponseDTO;
import dev.dmitriy.job_x_hunter.entity.Skill;
import dev.dmitriy.job_x_hunter.exception.SkillNotFoundException;
import dev.dmitriy.job_x_hunter.exception.UserNotFoundException;
import dev.dmitriy.job_x_hunter.mapper.SkillMapper;
import dev.dmitriy.job_x_hunter.repository.SkillRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SkillService {
    private final SkillRepository repo;
    private final SkillMapper mapper;

    public SkillService(SkillRepository repo, SkillMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    public List<SkillResponseDTO> getAllSkills(){
        List<Skill> skills = repo.findAll();
        List<SkillResponseDTO>  skillsDto = new ArrayList<>();

        for(Skill element : skills){
            skillsDto.add(mapper.toDto(element));
        }

        return skillsDto;
    }

    public SkillResponseDTO getSkillById(Long skillId){
        Skill skill = repo.findById(skillId).orElseThrow( () -> {
            throw new SkillNotFoundException("Skill with the id: "+ skillId +" was not found.");
        });
        return mapper.toDto(skill);
    }

    // createSkillRequest extension
}
