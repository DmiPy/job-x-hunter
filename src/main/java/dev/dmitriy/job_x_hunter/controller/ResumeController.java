package dev.dmitriy.job_x_hunter.controller;

import dev.dmitriy.job_x_hunter.dto.CreateResumeRequest;
import dev.dmitriy.job_x_hunter.dto.ResumeResponseDTO;
import dev.dmitriy.job_x_hunter.dto.UpdateResumeRequest;
import dev.dmitriy.job_x_hunter.service.ResumeService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/resumes")
public class ResumeController {
    private final ResumeService service;

    public ResumeController(ResumeService service){
        this.service = service;
    }

    @PostMapping
    public ResumeResponseDTO createResume(@RequestBody @Valid CreateResumeRequest request){
        return this.service.createResume(request);
    }

    @PatchMapping
    public ResumeResponseDTO updateResume(@RequestBody @Valid UpdateResumeRequest request){
        return this.service.updateResume(request.getResumeId(), request.getTitle(), request.getUrl(), request.getDescription());
    }

    @DeleteMapping("/{resumeId}")
    public ResumeResponseDTO deleteResume(@PathVariable Long resumeId){
        return this.service.deleteResume(resumeId);
    }

    @GetMapping("/{resumeId}")
    public ResumeResponseDTO getResumeById(@PathVariable Long resumeId){
        return this.service.getResumeById(resumeId);
    }

    @GetMapping("/profile/{profileId}")
    public List<ResumeResponseDTO> getAllResumesByProfileId(@PathVariable Long profileId){
        return this.service.getAllResumesByProfileId(profileId);
    }
}
