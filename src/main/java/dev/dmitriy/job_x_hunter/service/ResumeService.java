package dev.dmitriy.job_x_hunter.service;

import dev.dmitriy.job_x_hunter.dto.CreateResumeRequest;
import dev.dmitriy.job_x_hunter.dto.ResumeResponseDTO;
import dev.dmitriy.job_x_hunter.dto.UpdateResumeRequest;
import dev.dmitriy.job_x_hunter.entity.Resume;
import dev.dmitriy.job_x_hunter.entity.UserProfile;
import dev.dmitriy.job_x_hunter.exception.ResumeNotFoundException;
import dev.dmitriy.job_x_hunter.exception.UserProfileNotFoundException;
import dev.dmitriy.job_x_hunter.mapper.ResumeMapper;
import dev.dmitriy.job_x_hunter.repository.ResumeRepository;
import dev.dmitriy.job_x_hunter.repository.UserProfileRepository;
import dev.dmitriy.job_x_hunter.security.CurrentUserService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ResumeService {

    private final UserProfileRepository uprepo;
    private final ResumeRepository rrepo;

    private final ResumeMapper mapper;
    private final CurrentUserService currentUser;

    public ResumeService(ResumeRepository rrepo, UserProfileRepository uprepo, ResumeMapper mapper, CurrentUserService currentUser){
        this.rrepo = rrepo;
        this.uprepo = uprepo;
        this.mapper = mapper;
        this.currentUser = currentUser;
    }

    private UserProfile findUserProfile(Long id){
        return uprepo.findById(id)
                .orElseThrow(() -> new UserProfileNotFoundException("User with id " + id + " was not found."));
    }

    private Resume findResume(Long id){
        return rrepo.findById(id)
                .orElseThrow(() -> new ResumeNotFoundException("Resume with id " + id + " was not found."));
    }

    @Transactional
    public ResumeResponseDTO createResumeForCurrentUser(CreateResumeRequest request, Jwt jwt){
        Resume newResume = mapper.toEntity(request);
        UserProfile up = uprepo.findByUser_UserId(currentUser.getOrProvision(jwt).getUserId())
                .orElseThrow(() -> new UserProfileNotFoundException("Profile not found."));
        newResume.setUserProfile(up);
        newResume.setUrl("/");
        newResume = rrepo.save(newResume);
        return mapper.toDto(newResume);
    }

    @Transactional
    public ResumeResponseDTO updateResume(UpdateResumeRequest request, Jwt jwt){
        Resume resume = findResume(request.getResumeId());
        assertOwner(resume, jwt);
        if (request.isTitleProvided()) {
            resume.setTitle(request.getTitle());
        }
        if (request.isUrlProvided()) {
            resume.setUrl(request.getUrl());
        }
        if (request.isDescriptionProvided()) {
            resume.setDescription(request.getDescription());
        }
        resume = rrepo.save(resume);
        return mapper.toDto(resume);
    }

    @Transactional
    public ResumeResponseDTO deleteResume(Long resumeId, Jwt jwt){
        Resume resume = findResume(resumeId);
        assertOwner(resume, jwt);
        ResumeResponseDTO dto = mapper.toDto(resume);
        rrepo.delete(resume);
        return dto;
    }

    public ResumeResponseDTO getResumeById(Long resumeId, Jwt jwt){
        Resume resume = findResume(resumeId);
        assertOwner(resume, jwt);
        return mapper.toDto(resume);
    }

    public List<ResumeResponseDTO> getMyResumes(Jwt jwt) {
        Long profileId = uprepo.findByUser_UserId(currentUser.getOrProvision(jwt).getUserId())
                .orElseThrow(() -> new UserProfileNotFoundException("Profile not found.")).getUserProfileId();
        return rrepo.findByUserProfile_UserProfileId(profileId).stream().map(mapper::toDto).toList();
    }

    public List<ResumeResponseDTO> getResumesByProfileId(Long profileId, Jwt jwt) {
        UserProfile profile = uprepo.findById(profileId)
                .filter(p -> p.getUser().getUserId().equals(currentUser.getOrProvision(jwt).getUserId()))
                .orElseThrow(() -> new UserProfileNotFoundException("Profile not found."));
        return rrepo.findByUserProfile_UserProfileId(profile.getUserProfileId()).stream().map(mapper::toDto).toList();
    }

    private void assertOwner(Resume resume, Jwt jwt) {
        if (jwt == null || !resume.getUserProfile().getUser().getUserId().equals(currentUser.getOrProvision(jwt).getUserId())) {
            throw new ResumeNotFoundException("Resume not found.");
        }
    }


}
