package dev.dmitriy.job_x_hunter.mapper;

import dev.dmitriy.job_x_hunter.dto.CreateUserRequest;
import dev.dmitriy.job_x_hunter.dto.UserResponseDTO;
import dev.dmitriy.job_x_hunter.enums.Role;
import dev.dmitriy.job_x_hunter.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponseDTO toDto(User user){
        UserResponseDTO response = new UserResponseDTO();
        response.setUserId(user.getUserId());
        response.setUserRole(user.getUserRole());
        response.setEmail(user.getEmail());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }

    public User toEntity(CreateUserRequest dto){
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setUserRole(Role.USER);
        return user;
    }
}
