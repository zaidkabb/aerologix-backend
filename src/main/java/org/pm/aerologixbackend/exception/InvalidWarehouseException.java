package org.pm.aerologixbackend.exception;

public class InvalidWarehouseException extends RuntimeException {
    public InvalidWarehouseException(String message) {
        super(message);
    }
}