package dev.dmitriy.job_x_hunter.mapper;

import dev.dmitriy.job_x_hunter.dto.CreateResumeRequest;
import dev.dmitriy.job_x_hunter.dto.ResumeResponseDTO;
import dev.dmitriy.job_x_hunter.entity.Resume;
import org.springframework.stereotype.Component;

@Component
public class ResumeMapper {
    public Resume toEntity(CreateResumeRequest dto){
        Resume entity = new Resume();
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        return entity;
    }

    public ResumeResponseDTO toDto(Resume entity){
        ResumeResponseDTO dto = new ResumeResponseDTO();
        dto.setResumeId(entity.getResumeId());
        dto.setTitle(entity.getTitle());
        dto.setUrl(entity.getUrl());
        dto.setDescription(entity.getDescription());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUserProfileId(entity.getUserProfile().getUserProfileId());
        dto.setUploadDate(entity.getUploadDate());
        return dto;
    }
}

