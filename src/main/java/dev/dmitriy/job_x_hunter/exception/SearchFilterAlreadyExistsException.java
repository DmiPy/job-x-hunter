package dev.dmitriy.job_x_hunter.exception;

public class SearchFilterAlreadyExistsException extends RuntimeException {
    public SearchFilterAlreadyExistsException() { super("Search filter already exists."); }
}
