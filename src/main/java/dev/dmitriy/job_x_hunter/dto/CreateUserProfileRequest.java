package dev.dmitriy.job_x_hunter.dto;

import dev.dmitriy.job_x_hunter.enums.JobType;

import java.util.UUID;

public class CreateUserProfileRequest {
    private UUID userId;
    private String firstname;
    private String surname;
    private Double expectedSalary;
    private JobType jobType;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
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

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setExpectedSalary(Double expectedSalary) {
        this.expectedSalary = expectedSalary;
    }

    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }
}
