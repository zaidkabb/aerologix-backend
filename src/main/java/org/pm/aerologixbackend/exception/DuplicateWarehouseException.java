package org.pm.aerologixbackend.exception;

public class DuplicateWarehouseException extends RuntimeException {
    public DuplicateWarehouseException(String name) {
        super("Warehouse with name '" + name + "' already exists");
    }
}