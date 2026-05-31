package dev.dmitriy.job_x_hunter.service;

import dev.dmitriy.job_x_hunter.dto.CreateUserRequest;
import dev.dmitriy.job_x_hunter.dto.UserResponseDTO;
import dev.dmitriy.job_x_hunter.entity.User;
import dev.dmitriy.job_x_hunter.exception.UserAlreadyExistsException;
import dev.dmitriy.job_x_hunter.exception.UserNotFoundException;
import dev.dmitriy.job_x_hunter.mapper.UserMapper;
import dev.dmitriy.job_x_hunter.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    private final UserRepository repo;
    private final UserMapper mapper;

    public UserService(UserRepository repo, UserMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    public List<UserResponseDTO> getAllUsers() {
        List<User> users = repo.findAll();
        List<UserResponseDTO> responses = new ArrayList<>();
        for (User user : users){
            UserResponseDTO response = mapper.toDto(user);
            responses.add(response);
        }
        return responses;
    }

    public UserResponseDTO getUserById(UUID id) {
        User user = repo.findById(id).orElseThrow( () -> {
            throw new UserNotFoundException("User with the id: "+ id +" was not found.");
        });
        return mapper.toDto(user);
    }

    public UserResponseDTO createUser(CreateUserRequest request) {
        if(repo.existsByEmail(request.getEmail())){
            throw new UserAlreadyExistsException("User with email " + request.getEmail() + "already exists");
        }
        User user = mapper.toEntity(request);
        user.setUserId(UUID.randomUUID());

        User newUser = repo.save(user);
        return mapper.toDto(newUser);
    }


}
