package dev.dmitriy.job_x_hunter.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String errorMessage){super(errorMessage);}
}
