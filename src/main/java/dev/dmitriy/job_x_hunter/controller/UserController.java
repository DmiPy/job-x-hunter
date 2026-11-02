package dev.dmitriy.job_x_hunter.controller;

import dev.dmitriy.job_x_hunter.dto.CreateUserRequest;
import dev.dmitriy.job_x_hunter.dto.UserResponseDTO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import dev.dmitriy.job_x_hunter.service.UserService;

import java.util.List;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import dev.dmitriy.job_x_hunter.security.CurrentUserService;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final CurrentUserService currentUser;

    public UserController(UserService userService, CurrentUserService currentUser) {
        this.userService = userService;
        this.currentUser = currentUser;
    }

    @GetMapping
    public List<UserResponseDTO> getAllUsers(){
        return userService.getAllUsers();
    }

    @GetMapping("/me")
    public UserResponseDTO getMe(@AuthenticationPrincipal Jwt jwt) {
        return new dev.dmitriy.job_x_hunter.mapper.UserMapper().toDto(currentUser.getOrProvision(jwt));
    }

    @GetMapping("/{id}")
    public UserResponseDTO getUserById(@PathVariable UUID id){
        return userService.getUserById(id);
    }


}
