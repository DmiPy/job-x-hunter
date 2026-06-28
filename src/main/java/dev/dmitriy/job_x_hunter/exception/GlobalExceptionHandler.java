package dev.dmitriy.job_x_hunter.exception;


import dev.dmitriy.job_x_hunter.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex){
        ErrorResponse error = new ErrorResponse();
        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.setErrorMessage(ex.getMessage());
        error.setTimestamp(LocalDateTime.now());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException ex){
        ErrorResponse error = new ErrorResponse();
        error.setStatus(HttpStatus.CONFLICT.value());
        error.setErrorMessage(ex.getMessage());
        error.setTimestamp(LocalDateTime.now());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){
        ErrorResponse error = new ErrorResponse();
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setErrorMessage(ex.getBindingResult().getFieldError().getDefaultMessage());
        error.setTimestamp(LocalDateTime.now());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    @ExceptionHandler(UserProfileAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserProfileAlreadyExistsException(
            UserProfileAlreadyExistsException ex){

        ErrorResponse error = new ErrorResponse();

        error.setStatus(HttpStatus.CONFLICT.value());
        error.setErrorMessage(ex.getMessage());
        error.setTimestamp(LocalDateTime.now());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }

    @ExceptionHandler(UserProfileNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserProfileNotFoundException(
            UserProfileNotFoundException ex){

        ErrorResponse error = new ErrorResponse();

        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.setErrorMessage(ex.getMessage());
        error.setTimestamp(LocalDateTime.now());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    @ExceptionHandler(SkillNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSkillNotFoundException(
            SkillNotFoundException ex){

        ErrorResponse error = new ErrorResponse();

        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.setErrorMessage(ex.getMessage());
        error.setTimestamp(LocalDateTime.now());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    @ExceptionHandler(UserProfileSkillAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserProfileSkillAlreadyExistsException(
            UserProfileSkillAlreadyExistsException ex){

        ErrorResponse error = new ErrorResponse();

        error.setStatus(HttpStatus.CONFLICT.value());
        error.setErrorMessage(ex.getMessage());
        error.setTimestamp(LocalDateTime.now());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }

    @ExceptionHandler(UserProfileSkillNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserProfileSkillNotFoundException(
            UserProfileSkillNotFoundException ex){

        ErrorResponse error = new ErrorResponse();

        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.setErrorMessage(ex.getMessage());
        error.setTimestamp(LocalDateTime.now());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }


}

