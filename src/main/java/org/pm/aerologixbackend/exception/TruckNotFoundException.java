package org.pm.aerologixbackend.exception;

public class TruckNotFoundException extends RuntimeException {
    public TruckNotFoundException(Long id) {
        super("Truck not found with id: " + id);
    }

    public TruckNotFoundException(String licensePlate) {
        super("Truck not found with license plate: " + licensePlate);
    }
}