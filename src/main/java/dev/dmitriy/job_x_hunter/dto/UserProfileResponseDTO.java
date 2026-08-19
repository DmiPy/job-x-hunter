package dev.dmitriy.job_x_hunter.dto;

import dev.dmitriy.job_x_hunter.enums.EmploymentType;
import dev.dmitriy.job_x_hunter.enums.WorkplaceType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class UserProfileResponseDTO {
    private Long UserProfileId;

    private UUID userId;

    private String firstname;

    private String surname;

    private BigDecimal expectedSalary;

    private EmploymentType employmentType;

    private WorkplaceType workplaceType;
}
