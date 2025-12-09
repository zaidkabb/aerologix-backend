package org.pm.aerologixbackend.entity;

public enum Role {
    ADMIN,      // Full access - manage everything
    DRIVER,     // View assigned shipments, update delivery status, send GPS
    CUSTOMER    // Track own shipments only
}