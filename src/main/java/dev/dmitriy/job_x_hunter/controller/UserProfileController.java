package dev.dmitriy.job_x_hunter.controller;

import dev.dmitriy.job_x_hunter.dto.CreateUserProfileRequest;
import dev.dmitriy.job_x_hunter.dto.UserProfileResponseDTO;
import dev.dmitriy.job_x_hunter.service.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/profiles")
public class UserProfileController {
    private UserProfileService service;

    public UserProfileController(UserProfileService service) {
        this.service = service;
    }

    @PostMapping()
    public UserProfileResponseDTO createUserProfile(@Valid @RequestBody CreateUserProfileRequest request){
        return this.service.createUserProfile(request);
    }

    @GetMapping("/user/{userId}")
    public UserProfileResponseDTO getUserProfileByUserId(@PathVariable UUID userId){
        return this.service.getUserProfileByUserId(userId);
    }

    @GetMapping("/{profileId}")
    public UserProfileResponseDTO getUserProfileById(@PathVariable Long profileId){
        return this.service.getUserProfileById(profileId);
    }
}
