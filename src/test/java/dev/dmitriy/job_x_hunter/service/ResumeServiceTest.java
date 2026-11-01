package dev.dmitriy.job_x_hunter.service;

import dev.dmitriy.job_x_hunter.dto.ResumeResponseDTO;
import dev.dmitriy.job_x_hunter.dto.UpdateResumeRequest;
import dev.dmitriy.job_x_hunter.entity.Resume;
import dev.dmitriy.job_x_hunter.entity.UserProfile;
import dev.dmitriy.job_x_hunter.mapper.ResumeMapper;
import dev.dmitriy.job_x_hunter.repository.ResumeRepository;
import dev.dmitriy.job_x_hunter.repository.UserProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResumeServiceTest {

    @Mock
    private ResumeRepository resumeRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    private ResumeService resumeService;

    @BeforeEach
    void setUp() {
        resumeService = new ResumeService(resumeRepository, userProfileRepository, new ResumeMapper());
    }

    @Test
    void clearsOptionalDescriptionWithoutOverwritingOmittedMandatoryFields() {
        Resume resume = new Resume();
        resume.setResumeId(7L);
        resume.setTitle("Original title");
        resume.setUrl("/original.pdf");
        resume.setDescription("Original description");
        UserProfile profile = new UserProfile();
        profile.setUserProfileId(3L);
        resume.setUserProfile(profile);

        UpdateResumeRequest request = new UpdateResumeRequest();
        request.setResumeId(7L);
        request.setDescription(null);

        when(resumeRepository.findById(7L)).thenReturn(Optional.of(resume));
        when(resumeRepository.save(any(Resume.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResumeResponseDTO response = resumeService.updateResume(request);

        assertEquals("Original title", response.getTitle());
        assertEquals("/original.pdf", response.getUrl());
        assertNull(response.getDescription());
    }
}
