package dev.dmitriy.job_x_hunter.repository;

import dev.dmitriy.job_x_hunter.entity.UserProfile;

import java.util.Optional;
import java.util.UUID;

public interface UserProfileRepository {
    Optional<UserProfile> findByUser_UserId(UUID userId);
}
