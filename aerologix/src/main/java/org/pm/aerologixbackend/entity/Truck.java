package org.pm.aerologixbackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trucks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Truck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "License plate is required")
    @Column(name = "license_plate", nullable = false, unique = true)
    private String licensePlate;

    @NotBlank(message = "Model is required")
    @Column(nullable = false)
    private String model;

    @Positive(message = "Capacity must be positive")
    @Column(nullable = false)
    private Double capacity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TruckStatus status;

    @Column(nullable = false)
    private Long mileage = 0L;

    @Column(name = "last_service")
    private LocalDate lastService;

    @Column(name = "next_service")
    private LocalDate nextService;

    // GPS tracking fields
    @Column(name = "current_latitude")
    private Double currentLatitude;

    @Column(name = "current_longitude")
    private Double currentLongitude;

    @Column(name = "current_speed")
    private Double currentSpeed;

    @Column(name = "heading")
    private String heading;

    @Column(name = "last_location_update")
    private LocalDateTime lastLocationUpdate;

    @OneToOne(mappedBy = "assignedTruck", fetch = FetchType.LAZY)
    private Driver assignedDriver;

    @OneToMany(mappedBy = "truck", fetch = FetchType.LAZY)
    private List<Shipment> shipments = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = TruckStatus.AVAILABLE;
        }
        if (mileage == null) {
            mileage = 0L;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
