package dev.dmitriy.job_x_hunter.exception;

import dev.dmitriy.job_x_hunter.dto.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import dev.dmitriy.job_x_hunter.security.InvalidIdentityClaimsException;
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

    @ExceptionHandler(InvalidIdentityClaimsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidIdentity(InvalidIdentityClaimsException ex) {
        return response(HttpStatus.UNAUTHORIZED, "The access token lacks required verified identity claims.");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(AccessDeniedException ex) {
        return response(HttpStatus.FORBIDDEN, "You are not allowed to perform this operation.");
    }

    @ExceptionHandler({
            UserNotFoundException.class,
            UserProfileNotFoundException.class,
            UserProfileSkillNotFoundException.class,
            SkillNotFoundException.class,
            ResumeNotFoundException.class,
            JobVacancyNotFoundException.class,
            SearchFilterNotFoundException.class
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
            DataIntegrityViolationException.class,
            SearchFilterAlreadyExistsException.class
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

    @ExceptionHandler(InvalidSearchFilterException.class)
    public ResponseEntity<ErrorResponse> handleInvalidSearchFilter(InvalidSearchFilterException ex) {
        return response(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    private ResponseEntity<ErrorResponse> response(HttpStatus status, String message) {
        ErrorResponse error = new ErrorResponse();
        error.setStatus(status.value());
        error.setErrorMessage(message);
        error.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(status).body(error);
    }
}
