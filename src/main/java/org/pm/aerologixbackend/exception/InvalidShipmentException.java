package org.pm.aerologixbackend.exception;

public class InvalidShipmentException extends RuntimeException {
    public InvalidShipmentException(String message) {
        super(message);
    }
}