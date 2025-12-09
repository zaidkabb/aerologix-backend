package org.pm.aerologixbackend.exception;

public class InvalidTruckException extends RuntimeException {
    public InvalidTruckException(String message) {
        super(message);
    }
}