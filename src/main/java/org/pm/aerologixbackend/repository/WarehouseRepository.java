package org.pm.aerologixbackend.repository;


import org.pm.aerologixbackend.entity.Warehouse;
import org.pm.aerologixbackend.entity.WarehouseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    Optional<Warehouse> findByName(String name);

    boolean existsByName(String name);

    List<Warehouse> findByStatus(WarehouseStatus status);

    List<Warehouse> findByCity(String city);

    List<Warehouse> findByCountry(String country);
}