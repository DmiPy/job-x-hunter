package dev.dmitriy.job_x_hunter.security;

public class InvalidIdentityClaimsException extends RuntimeException {
    public InvalidIdentityClaimsException() {
        super("The access token lacks required verified identity claims.");
    }
}
