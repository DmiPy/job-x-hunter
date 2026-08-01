package dev.dmitriy.job_x_hunter.service;

import dev.dmitriy.job_x_hunter.dto.CreateUserProfileSkillRequest;
import dev.dmitriy.job_x_hunter.dto.UserProfileSkillResponseDTO;
import dev.dmitriy.job_x_hunter.entity.Skill;
import dev.dmitriy.job_x_hunter.entity.UserProfile;
import dev.dmitriy.job_x_hunter.entity.UserProfileSkill;
import dev.dmitriy.job_x_hunter.entity.UserProfileSkillId;
import dev.dmitriy.job_x_hunter.exception.UserProfileNotFoundException;
import dev.dmitriy.job_x_hunter.exception.UserProfileSkillAlreadyExistsException;
import dev.dmitriy.job_x_hunter.exception.UserProfileSkillNotFoundException;
import dev.dmitriy.job_x_hunter.mapper.UserProfileSkillMapper;
import dev.dmitriy.job_x_hunter.repository.SkillRepository;
import dev.dmitriy.job_x_hunter.repository.UserProfileRepository;
import dev.dmitriy.job_x_hunter.repository.UserProfileSkillRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class UserProfileSkillService {
    private final UserProfileRepository uprepo;
    private final UserProfileSkillRepository upsrepo;
    private final SkillRepository srepo;
    private final UserProfileSkillMapper mapper;

    public UserProfileSkillService(
            UserProfileRepository uprepo,
            UserProfileSkillRepository upsrepo,
            SkillRepository srepo,
            UserProfileSkillMapper mapper
    ) {
        this.uprepo = uprepo;
        this.upsrepo = upsrepo;
        this.srepo = srepo;
        this.mapper = mapper;
    }

    private UserProfile findUserProfile(Long id){
        return uprepo.findById(id).orElseThrow(() -> {
            throw new UserProfileNotFoundException("User with the id: " + id + " was not found.");
        });
    }

    private UserProfileSkill findUserProfileSkill(Long profileId, Long skillId){
        return upsrepo.findByUserProfile_UserProfileIdAndSkill_SkillId(profileId, skillId)
                .orElseThrow(() ->
                        new UserProfileSkillNotFoundException( "User profile with id " + profileId + "does not have a skill with id:" + skillId)
        );
    }

    private Skill findOrCreateSkill(String skillName){
        return srepo.findBySkillName(skillName)
                .orElseGet(() -> {
                    Skill newSkill = new Skill();
                    newSkill.setSkillName(skillName);
                    return srepo.save(newSkill);
                });
    }

    private void validateDuplicateSkill(Long profileId, Long skillId, String skillName){
        if(upsrepo.existsByUserProfile_UserProfileIdAndSkill_SkillId(profileId, skillId)) {
            throw new UserProfileSkillAlreadyExistsException("UserProfile with id:"+ profileId +" already has " + skillName);
        }
    }

    public UserProfileSkillResponseDTO createUserProfileSkill(CreateUserProfileSkillRequest dto){
        UserProfile userProfile = findUserProfile(dto.getUserProfileId());
        Skill skill = findOrCreateSkill(dto.getSkillName());
        validateDuplicateSkill(userProfile.getUserProfileId(), skill.getSkillId(), dto.getSkillName());

        UserProfileSkill upSkill = new UserProfileSkill();

        upSkill.setUserProfile(userProfile);
        upSkill.setSkill(skill);
        upSkill.setProficiency(dto.getProficiency());

        upSkill = upsrepo.save(upSkill);
        return mapper.toDto(upSkill);
    }

    public List<UserProfileSkillResponseDTO> getAllSkillsByUserProfileId(Long profileId){
        findUserProfile(profileId);
        List<UserProfileSkill> skillsEntityList = upsrepo.findByUserProfile_UserProfileId(profileId);
        List<UserProfileSkillResponseDTO> skillsDtoList = new ArrayList<>();
        for( UserProfileSkill skill : skillsEntityList){
            skillsDtoList.add(mapper.toDto(skill));
        }
        return skillsDtoList;
    }

    public UserProfileSkillResponseDTO updateProficiency(
            Long profileId,
            Long skillId,
            Integer newProficiency
    ){
        findUserProfile(profileId);
        UserProfileSkill upSkill = findUserProfileSkill(profileId, skillId);
        upSkill.setProficiency(newProficiency);
        upSkill = upsrepo.save(upSkill);
        return mapper.toDto(upSkill);
    }

    public UserProfileSkillResponseDTO deleteUserProfileSkill(Long profileId, Long skillId){
        findUserProfile(profileId);
        UserProfileSkill upSkill = findUserProfileSkill(profileId, skillId);
        UserProfileSkillResponseDTO dto = mapper.toDto(upSkill);
        upsrepo.delete(upSkill);
        return dto;
    }

    public UserProfileSkillResponseDTO getUserProfileSkill(
            Long profileId,
            Long skillId
    ) {
        findUserProfile(profileId);
        return mapper.toDto(findUserProfileSkill(profileId, skillId));
    }
}
