package dev.dmitriy.job_x_hunter.repository;

import dev.dmitriy.job_x_hunter.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByUser_UserId(UUID userId);
}
