package dev.dmitriy.job_x_hunter.dto;

public class CreateUserRequest {
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // idea is that we need only email to create a new user, bc everything else will be generated or auto completed
}
