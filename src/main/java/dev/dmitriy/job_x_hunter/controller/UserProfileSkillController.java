package dev.dmitriy.job_x_hunter.controller;

import dev.dmitriy.job_x_hunter.dto.CreateUserProfileSkillRequest;
import dev.dmitriy.job_x_hunter.dto.UpdateUserProfileSkillRequest;
import dev.dmitriy.job_x_hunter.dto.UserProfileSkillIdDTO;
import dev.dmitriy.job_x_hunter.dto.UserProfileSkillResponseDTO;
import dev.dmitriy.job_x_hunter.service.UserProfileSkillService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-profile-skill")
public class UserProfileSkillController {
    private final UserProfileSkillService service;

    public UserProfileSkillController(UserProfileSkillService service) {
        this.service = service;
    }

    @PostMapping()
    public UserProfileSkillResponseDTO createUserProfileSkill(@RequestBody @Valid CreateUserProfileSkillRequest request){
        return this.service.createUserProfileSkill(request);
    }

    @GetMapping("/profile/{profileId}")
    public List<UserProfileSkillResponseDTO> getAllSkillsByUserProfileId(@PathVariable Long profileId){
        return this.service.getAllSkillsByUserProfileId(profileId);
    }

    @PatchMapping()
    public UserProfileSkillResponseDTO updateProficiency(@RequestBody @Valid UpdateUserProfileSkillRequest request){
        return this.service.updateProficiency(
                request.getProfileId(),
                request.getSkillId(),
                request.getProficiency()
        );
    }

    @DeleteMapping()
    public UserProfileSkillResponseDTO deleteUserProfileSkill(@RequestBody @Valid UserProfileSkillIdDTO dto){
        return this.service.deleteUserProfileSkill(
                dto.getUserProfileId(),
                dto.getSkillId()
        );
    }

    @GetMapping("/{profileId}/{skillId}")
    public UserProfileSkillResponseDTO getUserProfileSkill(
            @PathVariable Long profileId,
            @PathVariable Long skillId
    ){
        return this.service.getUserProfileSkill(profileId, skillId);
    }
}
