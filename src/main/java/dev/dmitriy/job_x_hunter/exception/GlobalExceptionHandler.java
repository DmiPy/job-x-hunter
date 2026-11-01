package dev.dmitriy.job_x_hunter.exception;

import dev.dmitriy.job_x_hunter.dto.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            UserNotFoundException.class,
            UserProfileNotFoundException.class,
            UserProfileSkillNotFoundException.class,
            SkillNotFoundException.class,
            ResumeNotFoundException.class,
            JobVacancyNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException ex) {
        return response(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({
            UserAlreadyExistsException.class,
            UserProfileAlreadyExistsException.class,
            UserProfileSkillAlreadyExistsException.class,
            JobVacancyAlreadyExistsException.class,
            JobSkillAlreadyExistsException.class,
            DataIntegrityViolationException.class
    })
    public ResponseEntity<ErrorResponse> handleConflict(RuntimeException ex) {
        return response(HttpStatus.CONFLICT, "The requested resource conflicts with existing data.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getAllErrors().isEmpty()
                ? "Request validation failed."
                : ex.getAllErrors().getFirst().getDefaultMessage();
        return response(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler({
            ConstraintViolationException.class,
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception ex) {
        return response(HttpStatus.BAD_REQUEST, "The request is invalid.");
    }

    private ResponseEntity<ErrorResponse> response(HttpStatus status, String message) {
        ErrorResponse error = new ErrorResponse();
        error.setStatus(status.value());
        error.setErrorMessage(message);
        error.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(status).body(error);
    }
}
