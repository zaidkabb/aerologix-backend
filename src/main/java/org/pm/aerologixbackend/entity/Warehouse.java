package org.pm.aerologixbackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "warehouses")
@Data
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String country;

    private Double latitude;

    private Double longitude;

    @Column(nullable = false)
    private Double capacity; // in cubic meters

    private Double currentOccupancy; // in cubic meters

    @Enumerated(EnumType.STRING)
    private WarehouseStatus status;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = WarehouseStatus.OPERATIONAL;
        }
        if (currentOccupancy == null) {
            currentOccupancy = 0.0;
        }
    }
}