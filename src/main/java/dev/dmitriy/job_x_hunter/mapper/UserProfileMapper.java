package dev.dmitriy.job_x_hunter.mapper;

import dev.dmitriy.job_x_hunter.dto.CreateUserProfileRequest;
import dev.dmitriy.job_x_hunter.dto.UserProfileResponseDTO;
import dev.dmitriy.job_x_hunter.entity.UserProfile;
import org.springframework.stereotype.Component;

@Component
public class UserProfileMapper {
    public UserProfileResponseDTO toDto(UserProfile profile){
        UserProfileResponseDTO dto = new UserProfileResponseDTO();
        dto.setUserProfileId(profile.getUserProfileId());
        dto.setFirstname(profile.getFirstname());
        dto.setSurname(profile.getSurname());
        dto.setUserId(profile.getUser().getUserId());
        dto.setExpectedSalary(profile.getExpectedSalary());
        dto.setEmploymentType(profile.getEmploymentType());
        dto.setWorkplaceType(profile.getWorkplaceType());
        return dto;
    };

    public UserProfile toEntity(CreateUserProfileRequest request){
        UserProfile up = new UserProfile();
        up.setFirstname(request.getFirstname());
        up.setSurname(request.getSurname());
        up.setEmploymentType(request.getEmploymentType());
        up.setExpectedSalary(request.getExpectedSalary());
        up.setWorkplaceType(request.getWorkplaceType());
        return up;
    }
}
