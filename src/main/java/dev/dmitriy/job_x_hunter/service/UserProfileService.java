package dev.dmitriy.job_x_hunter.service;

import dev.dmitriy.job_x_hunter.repository.UserProfileRepository;

public class UserProfileService {
    private final UserProfileRepository repo;

    public UserProfileService(UserProfileRepository repo) {
        this.repo = repo;
    }
}
