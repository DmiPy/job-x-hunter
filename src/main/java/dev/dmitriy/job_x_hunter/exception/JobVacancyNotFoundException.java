package dev.dmitriy.job_x_hunter.exception;

public class JobVacancyNotFoundException extends RuntimeException {
    public JobVacancyNotFoundException(String message) {
        super(message);
    }
}
