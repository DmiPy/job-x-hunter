package dev.dmitriy.job_x_hunter.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateUserRequest {
    @NotBlank(message = "Email is required.")
    @Email(message = "Wrong format.")
    private String email;

    // idea is that we need only email to create a new user, bc everything else will be generated or auto completed
}
