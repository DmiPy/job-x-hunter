package dev.dmitriy.job_x_hunter.entity;

import dev.dmitriy.job_x_hunter.enums.JobType;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
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

    private Double expectedSalary;

    @Enumerated(EnumType.STRING)
    private JobType jobType;

    public Long getUserProfileId() {
        return userProfileId;
    }

    public User getUser() {
        return user;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getSurname() {
        return surname;
    }

    public Double getExpectedSalary() {
        return expectedSalary;
    }

    public JobType getJobType() {
        return jobType;
    }

    public void setUserProfileId(Long userProfileId) {
        this.userProfileId = userProfileId;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setExpectedSalary(Double expectedSalary) {
        this.expectedSalary = expectedSalary;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }
}
