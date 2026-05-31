package dev.dmitriy.job_x_hunter.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String errorMessage){
        super(errorMessage);
    }
}
