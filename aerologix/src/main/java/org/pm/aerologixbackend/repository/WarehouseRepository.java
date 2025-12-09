package org.pm.aerologixbackend.repository;

import org.pm.aerologixbackend.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    
    Optional<Warehouse> findByName(String name);
    
    List<Warehouse> findByLocation(String location);
    
    @Query("SELECT w FROM Warehouse w WHERE w.currentInventory < w.capacity * 0.5")
    List<Warehouse> findWarehousesWithLowOccupancy();
    
    @Query("SELECT w FROM Warehouse w WHERE w.currentInventory >= w.capacity * 0.8")
    List<Warehouse> findWarehousesWithHighOccupancy();
    
    @Query("SELECT SUM(w.capacity) FROM Warehouse w")
    Long getTotalCapacity();
    
    @Query("SELECT SUM(w.currentInventory) FROM Warehouse w")
    Long getTotalInventory();
    
    @Query("SELECT w FROM Warehouse w ORDER BY (w.currentInventory * 100.0 / w.capacity) DESC")
    List<Warehouse> findAllOrderByOccupancyDesc();
}
