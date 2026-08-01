package dev.dmitriy.job_x_hunter.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(name="resume")
@Getter
@Setter
public class Resume {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resumeId;

    @NotBlank
    private String title;

    @ManyToOne
    @JoinColumn(name = "user_profile_id")
    @NotNull
    private UserProfile userProfile;

    @NotBlank
    private String url;

    @NotNull
    private LocalDate uploadDate;

    private String description;

    @NotNull
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

