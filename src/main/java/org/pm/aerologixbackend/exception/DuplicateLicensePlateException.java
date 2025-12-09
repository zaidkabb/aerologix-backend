package org.pm.aerologixbackend.exception;

public class DuplicateLicensePlateException extends RuntimeException {
    public DuplicateLicensePlateException(String licensePlate) {
        super("Truck with license plate " + licensePlate + " already exists");
    }
}