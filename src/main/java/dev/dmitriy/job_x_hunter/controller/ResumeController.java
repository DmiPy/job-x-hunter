package dev.dmitriy.job_x_hunter.controller;

import dev.dmitriy.job_x_hunter.dto.CreateResumeRequest;
import dev.dmitriy.job_x_hunter.dto.ResumeResponseDTO;
import dev.dmitriy.job_x_hunter.dto.UpdateResumeRequest;
import dev.dmitriy.job_x_hunter.service.ResumeService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;


import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

@RestController
@RequestMapping("/resumes")
public class ResumeController {
    private final ResumeService service;

    public ResumeController(ResumeService service){
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResumeResponseDTO createResume(@RequestBody @Valid CreateResumeRequest request, @AuthenticationPrincipal Jwt jwt){
        return this.service.createResumeForCurrentUser(request, jwt);
    }

    @GetMapping("/me")
    public List<ResumeResponseDTO> getMyResumes(@AuthenticationPrincipal Jwt jwt) { return service.getMyResumes(jwt); }

    @PatchMapping
    public ResumeResponseDTO updateResume(@RequestBody @Valid UpdateResumeRequest request, @AuthenticationPrincipal Jwt jwt){
        return this.service.updateResume(request, jwt);
    }

    @DeleteMapping("/{resumeId}")
    public ResumeResponseDTO deleteResume(@PathVariable Long resumeId, @AuthenticationPrincipal Jwt jwt){
        return this.service.deleteResume(resumeId, jwt);
    }

    @GetMapping("/{resumeId}")
    public ResumeResponseDTO getResumeById(@PathVariable Long resumeId, @AuthenticationPrincipal Jwt jwt){
        return this.service.getResumeById(resumeId, jwt);
    }

    @GetMapping("/profile/{profileId}")
    public List<ResumeResponseDTO> getAllResumesByProfileId(@PathVariable Long profileId, @AuthenticationPrincipal Jwt jwt){
        return this.service.getResumesByProfileId(profileId, jwt);
    }
}
