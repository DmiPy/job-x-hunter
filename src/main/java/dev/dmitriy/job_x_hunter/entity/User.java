package dev.dmitriy.job_x_hunter.entity;

import dev.dmitriy.job_x_hunter.enums.Role;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;


import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table( name= "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private UUID userId;

    @OneToOne(mappedBy = "user")
    private UserProfile userProfile;

    @Column(name="email" ,nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name="user_role", nullable = false)
    private Role userRole;

    @CreationTimestamp
    @Column(name="created_at", updatable = false)
    private LocalDateTime createdAt;

    public User() {}

    public UUID getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public Role getUserRole() {
        return userRole;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public void setUserRole(Role userRole) {
        this.userRole = userRole;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }
}

