package org.pm.aerologixbackend.exception;

public class DuplicateDriverException extends RuntimeException {
    public DuplicateDriverException(String field, String value) {
        super("Driver with " + field + " '" + value + "' already exists");
    }
}