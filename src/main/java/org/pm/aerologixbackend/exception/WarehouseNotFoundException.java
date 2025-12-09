package org.pm.aerologixbackend.exception;

public class WarehouseNotFoundException extends RuntimeException {
    public WarehouseNotFoundException(Long id) {
        super("Warehouse not found with id: " + id);
    }

    public WarehouseNotFoundException(String name) {
        super("Warehouse not found with name: " + name);
    }
}