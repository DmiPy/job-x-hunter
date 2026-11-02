package dev.dmitriy.job_x_hunter.service;

import dev.dmitriy.job_x_hunter.dto.CreateUserProfileRequest;
import dev.dmitriy.job_x_hunter.dto.UserProfileResponseDTO;
import dev.dmitriy.job_x_hunter.entity.User;
import dev.dmitriy.job_x_hunter.entity.UserProfile;
import dev.dmitriy.job_x_hunter.exception.UserProfileAlreadyExistsException;
import dev.dmitriy.job_x_hunter.exception.UserProfileNotFoundException;
import dev.dmitriy.job_x_hunter.mapper.UserProfileMapper;
import dev.dmitriy.job_x_hunter.repository.UserProfileRepository;
import dev.dmitriy.job_x_hunter.repository.UserRepository;
import dev.dmitriy.job_x_hunter.security.CurrentUserService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserProfileService {
    private final UserProfileRepository upRepo;
    private final UserRepository uRepo;
    private final UserProfileMapper mapper;
    private final CurrentUserService currentUser;

    public UserProfileService(UserProfileRepository upRepo, UserRepository uRepo, UserProfileMapper mapper, CurrentUserService currentUser) {
        this.upRepo = upRepo;
        this.uRepo = uRepo;
        this.mapper = mapper;
        this.currentUser = currentUser;
    }

    public UserProfileResponseDTO getMyProfile(Jwt jwt) {
        User user = currentUser.getOrProvision(jwt);
        return getOwnedProfileByUserId(user.getUserId(), jwt);
    }

    public UserProfileResponseDTO getOwnedProfileByUserId(UUID id, Jwt jwt) {
        if (!currentUser.getOrProvision(jwt).getUserId().equals(id)) {
            throw new UserProfileNotFoundException("Profile not found.");
        }
        return mapper.toDto(upRepo.findByUser_UserId(id)
                .orElseThrow(() -> new UserProfileNotFoundException("Profile not found.")));
    }

    @Transactional
    public UserProfileResponseDTO createMyProfile(CreateUserProfileRequest request, Jwt jwt) {
        User user = currentUser.getOrProvision(jwt);
        if (upRepo.findByUser_UserId(user.getUserId()).isPresent()) {
            throw new UserProfileAlreadyExistsException("Profile already exists.");
        }
        UserProfile profile = mapper.toEntity(request);
        profile.setUser(user);
        return mapper.toDto(upRepo.save(profile));
    }

    public UserProfileResponseDTO getOwnedProfile(Long profileId, Jwt jwt) {
        UserProfile profile = upRepo.findById(profileId).filter(p -> p.getUser().getUserId().equals(currentUser.getOrProvision(jwt).getUserId()))
                .orElseThrow(() -> new UserProfileNotFoundException("Profile not found."));
        return mapper.toDto(profile);
    }

}
