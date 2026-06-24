package dev.dmitriy.job_x_hunter.service;

import dev.dmitriy.job_x_hunter.dto.CreateUserProfileRequest;
import dev.dmitriy.job_x_hunter.dto.UserProfileResponseDTO;
import dev.dmitriy.job_x_hunter.entity.User;
import dev.dmitriy.job_x_hunter.entity.UserProfile;
import dev.dmitriy.job_x_hunter.exception.UserNotFoundException;
import dev.dmitriy.job_x_hunter.exception.UserProfileAlreadyExistsException;
import dev.dmitriy.job_x_hunter.exception.UserProfileNotFoundException;
import dev.dmitriy.job_x_hunter.mapper.UserProfileMapper;
import dev.dmitriy.job_x_hunter.repository.UserProfileRepository;
import dev.dmitriy.job_x_hunter.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserProfileService {
    private final UserProfileRepository upRepo;
    private final UserRepository uRepo;
    private final UserProfileMapper mapper;

    public UserProfileService(UserProfileRepository upRepo, UserRepository uRepo, UserProfileMapper mapper) {
        this.upRepo = upRepo;
        this.uRepo = uRepo;
        this.mapper = mapper;
    }

    public UserProfileResponseDTO createUserProfile(CreateUserProfileRequest dto){
        if(upRepo.findByUser_UserId(dto.getUserId()).isPresent() ){
            throw new UserProfileAlreadyExistsException("User profile for user id " + dto.getUserId() + " already exists.");
        } else {
            User user = uRepo.findById(dto.getUserId()).orElseThrow( () -> {
                throw new UserNotFoundException("User with the id: "+ dto.getUserId() +" was not found.");
            });
            UserProfile userProfile = mapper.toEntity(dto);
            userProfile.setUser(user);
            UserProfile savedProfile = upRepo.save(userProfile);
            return mapper.toDto(savedProfile);
        }

    }

    public UserProfileResponseDTO getUserProfileByUserId(UUID userId){
        UserProfile userProfile = upRepo.findByUser_UserId(userId).orElseThrow(() -> {
            throw new UserProfileNotFoundException("User with the id: "+ userId +" was not found.");
        });
        return mapper.toDto(userProfile);
    }

    public UserProfileResponseDTO getUserProfileById(Long profileId){
        UserProfile userProfile = upRepo.findById(profileId).orElseThrow(() -> {
            throw new UserProfileNotFoundException("User with the id: "+ profileId +" was not found.");
        });
        return mapper.toDto(userProfile);
    }
}
