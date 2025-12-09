package org.pm.aerologixbackend.exception;

public class DuplicateUserException extends RuntimeException {
    public DuplicateUserException(String field, String value) {
        super("User with " + field + " '" + value + "' already exists");
    }
}