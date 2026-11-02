package dev.dmitriy.job_x_hunter.controller;

import dev.dmitriy.job_x_hunter.dto.CreateUserProfileSkillRequest;
import dev.dmitriy.job_x_hunter.dto.UpdateUserProfileSkillRequest;
import dev.dmitriy.job_x_hunter.dto.UserProfileSkillIdDTO;
import dev.dmitriy.job_x_hunter.dto.UserProfileSkillResponseDTO;
import dev.dmitriy.job_x_hunter.service.UserProfileSkillService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

@RestController
@RequestMapping("/user-profile-skill")
public class UserProfileSkillController {
    private final UserProfileSkillService service;

    public UserProfileSkillController(UserProfileSkillService service) {
        this.service = service;
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public UserProfileSkillResponseDTO createUserProfileSkill(@RequestBody @Valid CreateUserProfileSkillRequest request, @AuthenticationPrincipal Jwt jwt){
        return this.service.createUserProfileSkill(request, jwt);
    }

    @GetMapping("/profile/{profileId}")
    public List<UserProfileSkillResponseDTO> getAllSkillsByUserProfileId(@PathVariable Long profileId, @AuthenticationPrincipal Jwt jwt){
        return this.service.getAllSkillsByUserProfileId(profileId, jwt);
    }

    @PatchMapping()
    public UserProfileSkillResponseDTO updateProficiency(@RequestBody @Valid UpdateUserProfileSkillRequest request, @AuthenticationPrincipal Jwt jwt){
        return this.service.updateProficiency(
                request.getProfileId(),
                request.getSkillId(),
                request.getProficiency(), jwt
        );
    }

    @DeleteMapping()
    public UserProfileSkillResponseDTO deleteUserProfileSkill(@RequestBody @Valid UserProfileSkillIdDTO dto, @AuthenticationPrincipal Jwt jwt){
        return this.service.deleteUserProfileSkill(
                dto.getUserProfileId(),
                dto.getSkillId(), jwt
        );
    }

    @GetMapping("/{profileId}/{skillId}")
    public UserProfileSkillResponseDTO getUserProfileSkill(
            @PathVariable Long profileId,
            @PathVariable Long skillId
            , @AuthenticationPrincipal Jwt jwt
    ){
        return this.service.getUserProfileSkill(profileId, skillId, jwt);
    }
}
