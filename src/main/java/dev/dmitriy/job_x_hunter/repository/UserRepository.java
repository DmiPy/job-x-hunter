package dev.dmitriy.job_x_hunter.repository;

import dev.dmitriy.job_x_hunter.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID>{

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}