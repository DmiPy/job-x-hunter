package dev.dmitriy.job_x_hunter.controller;

import dev.dmitriy.job_x_hunter.dto.CreateUserRequest;
import dev.dmitriy.job_x_hunter.dto.UserResponseDTO;
import org.springframework.web.bind.annotation.*;
import dev.dmitriy.job_x_hunter.service.UserService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserResponseDTO createUser(@RequestBody CreateUserRequest request){
        return userService.createUser(request);
    }

    @GetMapping
    public List<UserResponseDTO> getAllUsers(){
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserResponseDTO getUserById(@PathVariable UUID id){
        return userService.getUserById(id);
    }


}
