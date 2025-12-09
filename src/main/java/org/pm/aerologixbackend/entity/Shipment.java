package org.pm.aerologixbackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "shipments")
@Data
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String trackingNumber;

    @Column(nullable = false)
    private String origin;

    @Column(nullable = false)
    private String destination;

    private Double weight;

    private Double estimatedCost;

    // Assignment fields
    @ManyToOne
    @JoinColumn(name = "assigned_driver_id")
    private Driver assignedDriver;

    @ManyToOne
    @JoinColumn(name = "assigned_truck_id")
    private Truck assignedTruck;

    private LocalDateTime assignedAt;

    private LocalDateTime deliveredAt;
    

    @Enumerated(EnumType.STRING)
    private ShipmentStatus status;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = ShipmentStatus.CREATED;
        }
    }
}