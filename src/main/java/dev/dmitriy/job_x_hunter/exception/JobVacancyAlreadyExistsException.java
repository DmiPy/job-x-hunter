package dev.dmitriy.job_x_hunter.exception;

public class JobVacancyAlreadyExistsException extends RuntimeException {
    public JobVacancyAlreadyExistsException(String message) {
        super(message);
    }
}
