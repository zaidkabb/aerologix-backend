package org.pm.aerologixbackend.exception;

public class DriverNotFoundException extends RuntimeException {
    public DriverNotFoundException(Long id) {
        super("Driver not found with id: " + id);
    }

    public DriverNotFoundException(String identifier) {
        super("Driver not found with identifier: " + identifier);
    }
}