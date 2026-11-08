package dev.dmitriy.job_x_hunter.exception;

public class SearchFilterNotFoundException extends RuntimeException {
    public SearchFilterNotFoundException() { super("Search filter not found."); }
}
