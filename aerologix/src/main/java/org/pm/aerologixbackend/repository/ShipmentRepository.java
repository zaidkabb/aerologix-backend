package org.pm.aerologixbackend.repository;

import org.pm.aerologixbackend.entity.Driver;
import org.pm.aerologixbackend.entity.Shipment;
import org.pm.aerologixbackend.entity.ShipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    
    Optional<Shipment> findByTrackingNumber(String trackingNumber);
    
    List<Shipment> findByStatus(ShipmentStatus status);
    
    List<Shipment> findByDriver(Driver driver);
    
    List<Shipment> findByDriverId(Long driverId);
    
    List<Shipment> findByDriverIdAndStatusNot(Long driverId, ShipmentStatus status);
    
    @Query("SELECT s FROM Shipment s WHERE s.status IN :statuses")
    List<Shipment> findByStatusIn(@Param("statuses") List<ShipmentStatus> statuses);
    
    @Query("SELECT s FROM Shipment s WHERE s.estimatedDelivery = :date")
    List<Shipment> findByEstimatedDeliveryDate(@Param("date") LocalDate date);
    
    @Query("SELECT s FROM Shipment s WHERE s.createdAt >= :startDate ORDER BY s.createdAt DESC")
    List<Shipment> findRecentShipments(@Param("startDate") java.time.LocalDateTime startDate);
    
    @Query("SELECT COUNT(s) FROM Shipment s WHERE s.status = :status")
    long countByStatus(@Param("status") ShipmentStatus status);
    
    @Query("SELECT s FROM Shipment s ORDER BY s.createdAt DESC")
    List<Shipment> findAllOrderByCreatedAtDesc();
    
    // Dashboard statistics
    @Query("SELECT COUNT(s) FROM Shipment s WHERE s.status = 'DELIVERED' AND s.actualDelivery >= :startDate")
    long countDeliveredSince(@Param("startDate") LocalDate startDate);
    
    @Query("SELECT COUNT(s) FROM Shipment s WHERE s.status IN ('PENDING', 'IN_TRANSIT', 'PICKED_UP', 'OUT_FOR_DELIVERY')")
    long countActiveShipments();
}
