package dev.dmitriy.job_x_hunter.dto;

import dev.dmitriy.job_x_hunter.enums.EmploymentType;
import dev.dmitriy.job_x_hunter.enums.WorkplaceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class CreateUserProfileRequest {
    private UUID userId;

    @NotBlank
    private String firstname;

    @NotBlank
    private String surname;

    @PositiveOrZero
    private BigDecimal expectedSalary;

    private EmploymentType employmentType;

    private WorkplaceType workplaceType;
}
