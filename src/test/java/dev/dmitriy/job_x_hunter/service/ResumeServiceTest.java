package dev.dmitriy.job_x_hunter.service;

import dev.dmitriy.job_x_hunter.dto.ResumeResponseDTO;
import dev.dmitriy.job_x_hunter.dto.UpdateResumeRequest;
import dev.dmitriy.job_x_hunter.entity.Resume;
import dev.dmitriy.job_x_hunter.entity.UserProfile;
import dev.dmitriy.job_x_hunter.entity.User;
import dev.dmitriy.job_x_hunter.enums.Role;
import dev.dmitriy.job_x_hunter.mapper.ResumeMapper;
import dev.dmitriy.job_x_hunter.repository.ResumeRepository;
import dev.dmitriy.job_x_hunter.repository.UserProfileRepository;
import dev.dmitriy.job_x_hunter.security.CurrentUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import org.springframework.security.oauth2.jwt.Jwt;

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

    @Mock
    private CurrentUserService currentUser;

    private ResumeService resumeService;

    @BeforeEach
    void setUp() {
        resumeService = new ResumeService(resumeRepository, userProfileRepository, new ResumeMapper(), currentUser);
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
        User user = new User();
        user.setUserId(UUID.randomUUID());
        profile.setUser(user);
        resume.setUserProfile(profile);
        when(currentUser.getOrProvision(any(Jwt.class))).thenReturn(user);

        UpdateResumeRequest request = new UpdateResumeRequest();
        request.setResumeId(7L);
        request.setDescription(null);

        when(resumeRepository.findById(7L)).thenReturn(Optional.of(resume));
        when(resumeRepository.save(any(Resume.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResumeResponseDTO response = resumeService.updateResume(request, Jwt.withTokenValue("test")
                .header("alg", "none").issuer("https://issuer.test/realm")
                .subject("test").claim("email", "test@example.test").build());

        assertEquals("Original title", response.getTitle());
        assertEquals("/original.pdf", response.getUrl());
        assertNull(response.getDescription());
    }
}
