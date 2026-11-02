package dev.dmitriy.job_x_hunter.controller;

import dev.dmitriy.job_x_hunter.dto.CreateUserProfileRequest;
import dev.dmitriy.job_x_hunter.dto.UserProfileResponseDTO;
import dev.dmitriy.job_x_hunter.service.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

@RestController
@RequestMapping("/profiles")
public class UserProfileController {
    private UserProfileService service;

    public UserProfileController(UserProfileService service) {
        this.service = service;
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public UserProfileResponseDTO createUserProfile(@Valid @RequestBody CreateUserProfileRequest request, @AuthenticationPrincipal Jwt jwt){
        return this.service.createMyProfile(request, jwt);
    }

    @GetMapping("/me")
    public UserProfileResponseDTO getMyProfile(@AuthenticationPrincipal Jwt jwt) { return service.getMyProfile(jwt); }

    @GetMapping("/user/{userId}")
    public UserProfileResponseDTO getUserProfileByUserId(@PathVariable UUID userId, @AuthenticationPrincipal Jwt jwt){
        return this.service.getOwnedProfileByUserId(userId, jwt);
    }

    @GetMapping("/{profileId}")
    public UserProfileResponseDTO getUserProfileById(@PathVariable Long profileId, @AuthenticationPrincipal Jwt jwt){
        return this.service.getOwnedProfile(profileId, jwt);
    }
}
