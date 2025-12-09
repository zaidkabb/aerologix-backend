package org.pm.aerologixbackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "trucks")
@Data
public class Truck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String licensePlate;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private Double capacity; // in kg

    @Enumerated(EnumType.STRING)
    private TruckStatus status;

    private Double currentLatitude;

    private Double currentLongitude;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = TruckStatus.AVAILABLE;
        }
    }
}