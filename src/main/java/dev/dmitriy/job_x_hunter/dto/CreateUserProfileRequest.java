package dev.dmitriy.job_x_hunter.dto;

import dev.dmitriy.job_x_hunter.enums.EmploymentType;
import dev.dmitriy.job_x_hunter.enums.WorkplaceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.UUID;

public class CreateUserProfileRequest {
    private UUID userId;

    @NotBlank
    private String firstname;

    @NotBlank
    private String surname;

    @PositiveOrZero
    private Double expectedSalary;

    private EmploymentType employmentType;

    private WorkplaceType workplaceType;

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

    public WorkplaceType getWorkplaceType() {
        return workplaceType;
    }

    public EmploymentType getEmploymentType() {
        return employmentType;
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

    public void setEmploymentType(EmploymentType employmentType) {
        this.employmentType = employmentType;
    }

    public void setWorkplaceType(WorkplaceType workplaceType) {
        this.workplaceType = workplaceType;
    }

}
