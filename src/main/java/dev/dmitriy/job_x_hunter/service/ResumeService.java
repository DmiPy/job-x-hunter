package dev.dmitriy.job_x_hunter.service;

import dev.dmitriy.job_x_hunter.dto.CreateResumeRequest;
import dev.dmitriy.job_x_hunter.dto.ResumeResponseDTO;
import dev.dmitriy.job_x_hunter.entity.Resume;
import dev.dmitriy.job_x_hunter.entity.UserProfile;
import dev.dmitriy.job_x_hunter.exception.ResumeNotFoundException;
import dev.dmitriy.job_x_hunter.exception.UserProfileNotFoundException;
import dev.dmitriy.job_x_hunter.mapper.ResumeMapper;
import dev.dmitriy.job_x_hunter.repository.ResumeRepository;
import dev.dmitriy.job_x_hunter.repository.UserProfileRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ResumeService {

    private final UserProfileRepository uprepo;
    private final ResumeRepository rrepo;

    private final ResumeMapper mapper;

    public ResumeService(ResumeRepository rrepo, UserProfileRepository uprepo, ResumeMapper mapper){
        this.rrepo = rrepo;
        this.uprepo = uprepo;
        this.mapper = mapper;
    }

    private UserProfile findUserProfile(Long id){
        return uprepo.findById(id)
                .orElseThrow(() -> new UserProfileNotFoundException("User with id " + id + " was not found."));
    }

    private Resume findResume(Long id){
        return rrepo.findById(id)
                .orElseThrow(() -> new ResumeNotFoundException("Resume with id " + id + " was not found."));
    }

    public ResumeResponseDTO createResume(CreateResumeRequest request){
        Resume newResume = mapper.toEntity(request);
        UserProfile up = findUserProfile(request.getUserProfileId());
        newResume.setUserProfile(up);
        newResume.setUrl("/");
        newResume = rrepo.save(newResume);
        return mapper.toDto(newResume);
    }

    public ResumeResponseDTO updateResume(Long resumeId, String title, String url, String description){
        Resume resume = findResume(resumeId);
        resume.setTitle(title);
        resume.setUrl(url);
        resume.setDescription(description);
        resume = rrepo.save(resume);
        return mapper.toDto(resume);
    }

    public ResumeResponseDTO deleteResume(Long resumeId){
        Resume resume = findResume(resumeId);
        ResumeResponseDTO dto = mapper.toDto(resume);
        rrepo.delete(resume);
        return dto;
    }

    public ResumeResponseDTO getResumeById(Long resumeId){
        return mapper.toDto(findResume(resumeId));
    }

    public List<ResumeResponseDTO> getAllResumesByProfileId(Long profileId){
        List<Resume> resumes = rrepo.findByUserProfile_UserProfileId(profileId);
        List<ResumeResponseDTO> resumesDtoList = new ArrayList<>();
        for(Resume r: resumes){
            resumesDtoList.add(mapper.toDto(r));
        }
        return resumesDtoList;
    }


}
