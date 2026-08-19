package dev.dmitriy.job_x_hunter.entity;

import dev.dmitriy.job_x_hunter.enums.EmploymentType;
import dev.dmitriy.job_x_hunter.enums.WorkplaceType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Entity
@Getter
@Setter
@Table(name="user_profile")
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userProfileId;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String firstname;

    @Column(nullable = false)
    private String surname;

    @Column(precision = 10, scale = 2)
    private BigDecimal expectedSalary;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type")
    private EmploymentType employmentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "workplace_type")
    private WorkplaceType workplaceType;
}
