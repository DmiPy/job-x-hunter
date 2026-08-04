package dev.dmitriy.job_x_hunter.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(name="resume")
@Getter
@Setter
public class Resume {
    @Id
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

    @CreationTimestamp
    @Column(name = "upload_date", updatable = false)
    private LocalDate uploadDate;

    private String description;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

