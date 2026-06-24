package dev.dmitriy.job_x_hunter.mapper;

import dev.dmitriy.job_x_hunter.dto.SkillResponseDTO;
import dev.dmitriy.job_x_hunter.entity.Skill;
import org.springframework.stereotype.Component;

@Component
public class SkillMapper {
    public SkillResponseDTO toDto(Skill entity){
        SkillResponseDTO dto = new SkillResponseDTO();
        dto.setSkillId(entity.getSkillId());
        dto.setSkillName(entity.getSkillName());
        return dto;
    }

    public Skill toEntity(SkillResponseDTO dto){
        Skill entity = new Skill();
        entity.setSkillId(dto.getSkillId());
        entity.setSkillName(dto.getSkillName());
        return entity;
    }
}
