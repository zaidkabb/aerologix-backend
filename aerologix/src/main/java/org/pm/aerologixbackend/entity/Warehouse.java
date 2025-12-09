package org.pm.aerologixbackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "warehouses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Location is required")
    @Column(nullable = false)
    private String location;

    @NotBlank(message = "Address is required")
    @Column(nullable = false)
    private String address;

    @Positive(message = "Capacity must be positive")
    @Column(nullable = false)
    private Long capacity;

    @Column(name = "current_inventory")
    private Long currentInventory = 0L;

    @NotBlank(message = "Manager name is required")
    @Column(nullable = false)
    private String manager;

    @NotBlank(message = "Phone is required")
    @Column(nullable = false)
    private String phone;

    // GPS coordinates
    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @OneToMany(mappedBy = "warehouse", fetch = FetchType.LAZY)
    private List<Shipment> shipments = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (currentInventory == null) {
            currentInventory = 0L;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper method to calculate capacity usage percentage
    public int getCapacityUsagePercentage() {
        if (capacity == null || capacity == 0) return 0;
        return (int) Math.round((currentInventory * 100.0) / capacity);
    }
}
