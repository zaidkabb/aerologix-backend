package org.pm.aerologixbackend.service;


import lombok.RequiredArgsConstructor;
import org.pm.aerologixbackend.dto.WarehouseRequestDTO;
import org.pm.aerologixbackend.dto.WarehouseResponseDTO;
import org.pm.aerologixbackend.entity.Warehouse;
import org.pm.aerologixbackend.entity.WarehouseStatus;
import org.pm.aerologixbackend.exception.DuplicateWarehouseException;
import org.pm.aerologixbackend.exception.InvalidWarehouseException;
import org.pm.aerologixbackend.exception.WarehouseCapacityException;
import org.pm.aerologixbackend.exception.WarehouseNotFoundException;
import org.pm.aerologixbackend.repository.WarehouseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;

    @Transactional(readOnly = true)
    public List<WarehouseResponseDTO> getAllWarehouses() {
        return warehouseRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<WarehouseResponseDTO> getWarehousesByStatus(WarehouseStatus status) {
        return warehouseRepository.findByStatus(status)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<WarehouseResponseDTO> getWarehousesByCity(String city) {
        return warehouseRepository.findByCity(city)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public WarehouseResponseDTO createWarehouse(WarehouseRequestDTO request) {
        // Business rule: warehouse name must be unique
        if (warehouseRepository.existsByName(request.getName())) {
            throw new DuplicateWarehouseException(request.getName());
        }

        Warehouse warehouse = new Warehouse();
        warehouse.setName(request.getName());
        warehouse.setAddress(request.getAddress());
        warehouse.setCity(request.getCity());
        warehouse.setCountry(request.getCountry());
        warehouse.setLatitude(request.getLatitude());
        warehouse.setLongitude(request.getLongitude());
        warehouse.setCapacity(request.getCapacity());
        warehouse.setCurrentOccupancy(0.0);
        warehouse.setStatus(WarehouseStatus.OPERATIONAL);

        Warehouse saved = warehouseRepository.save(warehouse);
        return toResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public WarehouseResponseDTO getWarehouseById(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new WarehouseNotFoundException(id));
        return toResponseDTO(warehouse);
    }

    @Transactional
    public WarehouseResponseDTO updateWarehouse(Long id, WarehouseRequestDTO request) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new WarehouseNotFoundException(id));

        // Business rule: check if new name conflicts with another warehouse
        if (!warehouse.getName().equals(request.getName())) {
            if (warehouseRepository.existsByName(request.getName())) {
                throw new DuplicateWarehouseException(request.getName());
            }
        }

        // Business rule: cannot reduce capacity below current occupancy
        if (request.getCapacity() < warehouse.getCurrentOccupancy()) {
            throw new InvalidWarehouseException(
                    "Cannot reduce capacity to " + request.getCapacity() +
                            " m³ when current occupancy is " + warehouse.getCurrentOccupancy() + " m³");
        }

        warehouse.setName(request.getName());
        warehouse.setAddress(request.getAddress());
        warehouse.setCity(request.getCity());
        warehouse.setCountry(request.getCountry());
        warehouse.setLatitude(request.getLatitude());
        warehouse.setLongitude(request.getLongitude());
        warehouse.setCapacity(request.getCapacity());

        Warehouse updated = warehouseRepository.save(warehouse);
        return toResponseDTO(updated);
    }

    @Transactional
    public WarehouseResponseDTO updateWarehouseStatus(Long id, WarehouseStatus newStatus) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new WarehouseNotFoundException(id));

        // Business rule: cannot close warehouse with occupancy
        if (newStatus == WarehouseStatus.CLOSED && warehouse.getCurrentOccupancy() > 0) {
            throw new InvalidWarehouseException(
                    "Cannot close warehouse with current occupancy of " +
                            warehouse.getCurrentOccupancy() + " m³. Empty warehouse first.");
        }

        warehouse.setStatus(newStatus);
        Warehouse updated = warehouseRepository.save(warehouse);
        return toResponseDTO(updated);
    }

    @Transactional
    public WarehouseResponseDTO addInventory(Long id, Double volume) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new WarehouseNotFoundException(id));

        // Business rule: warehouse must be operational
        if (warehouse.getStatus() != WarehouseStatus.OPERATIONAL) {
            throw new InvalidWarehouseException(
                    "Cannot add inventory to warehouse with status: " + warehouse.getStatus());
        }

        // Business rule: check capacity
        double newOccupancy = warehouse.getCurrentOccupancy() + volume;
        if (newOccupancy > warehouse.getCapacity()) {
            throw new WarehouseCapacityException(
                    "Adding " + volume + " m³ would exceed warehouse capacity. " +
                            "Available space: " + (warehouse.getCapacity() - warehouse.getCurrentOccupancy()) + " m³");
        }

        warehouse.setCurrentOccupancy(newOccupancy);
        Warehouse updated = warehouseRepository.save(warehouse);
        return toResponseDTO(updated);
    }

    @Transactional
    public WarehouseResponseDTO removeInventory(Long id, Double volume) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new WarehouseNotFoundException(id));

        // Business rule: cannot remove more than current occupancy
        if (volume > warehouse.getCurrentOccupancy()) {
            throw new InvalidWarehouseException(
                    "Cannot remove " + volume + " m³. Current occupancy: " +
                            warehouse.getCurrentOccupancy() + " m³");
        }

        warehouse.setCurrentOccupancy(warehouse.getCurrentOccupancy() - volume);
        Warehouse updated = warehouseRepository.save(warehouse);
        return toResponseDTO(updated);
    }

    @Transactional
    public void deleteWarehouse(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new WarehouseNotFoundException(id));

        // Business rule: cannot delete warehouse with inventory
        if (warehouse.getCurrentOccupancy() > 0) {
            throw new InvalidWarehouseException(
                    "Cannot delete warehouse with inventory. " +
                            "Current occupancy: " + warehouse.getCurrentOccupancy() + " m³");
        }

        warehouseRepository.delete(warehouse);
    }

    // ========== HELPER METHODS ==========

    private WarehouseResponseDTO toResponseDTO(Warehouse warehouse) {
        double availableSpace = warehouse.getCapacity() - warehouse.getCurrentOccupancy();
        int occupancyPercentage = (int) ((warehouse.getCurrentOccupancy() / warehouse.getCapacity()) * 100);

        return new WarehouseResponseDTO(
                warehouse.getId(),
                warehouse.getName(),
                warehouse.getAddress(),
                warehouse.getCity(),
                warehouse.getCountry(),
                warehouse.getLatitude(),
                warehouse.getLongitude(),
                warehouse.getCapacity(),
                warehouse.getCurrentOccupancy(),
                availableSpace,
                occupancyPercentage,
                warehouse.getStatus(),
                warehouse.getCreatedAt()
        );
    }
}