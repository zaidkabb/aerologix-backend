package org.pm.aerologixbackend.repository;

import org.pm.aerologixbackend.entity.Driver;
import org.pm.aerologixbackend.entity.DriverStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    
    Optional<Driver> findByEmail(String email);
    
    Optional<Driver> findByLicenseNumber(String licenseNumber);
    
    Optional<Driver> findByUserId(Long userId);
    
    boolean existsByEmail(String email);
    
    boolean existsByLicenseNumber(String licenseNumber);
    
    List<Driver> findByStatus(DriverStatus status);
    
    @Query("SELECT d FROM Driver d WHERE d.status = 'AVAILABLE' AND d.assignedTruck IS NULL")
    List<Driver> findAvailableDrivers();
    
    @Query("SELECT COUNT(d) FROM Driver d WHERE d.status = :status")
    long countByStatus(@Param("status") DriverStatus status);
    
    @Query("SELECT d FROM Driver d LEFT JOIN FETCH d.assignedTruck")
    List<Driver> findAllWithTrucks();
    
    @Query("SELECT d FROM Driver d WHERE d.status = 'ON_DELIVERY'")
    List<Driver> findActiveDrivers();
}
