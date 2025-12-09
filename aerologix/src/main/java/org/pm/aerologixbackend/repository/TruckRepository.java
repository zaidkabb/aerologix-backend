package org.pm.aerologixbackend.repository;

import org.pm.aerologixbackend.entity.Truck;
import org.pm.aerologixbackend.entity.TruckStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TruckRepository extends JpaRepository<Truck, Long> {
    
    Optional<Truck> findByLicensePlate(String licensePlate);
    
    boolean existsByLicensePlate(String licensePlate);
    
    List<Truck> findByStatus(TruckStatus status);
    
    @Query("SELECT t FROM Truck t WHERE t.status = 'AVAILABLE' AND t.assignedDriver IS NULL")
    List<Truck> findAvailableTrucks();
    
    @Query("SELECT COUNT(t) FROM Truck t WHERE t.status = :status")
    long countByStatus(@Param("status") TruckStatus status);
    
    @Query("SELECT t FROM Truck t WHERE t.status = 'IN_USE'")
    List<Truck> findActiveTrucks();
    
    @Query("SELECT t FROM Truck t LEFT JOIN FETCH t.assignedDriver")
    List<Truck> findAllWithDrivers();
}
