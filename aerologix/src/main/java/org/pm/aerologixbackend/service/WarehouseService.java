package org.pm.aerologixbackend.service;

import lombok.RequiredArgsConstructor;
import org.pm.aerologixbackend.dto.WarehouseDTO;
import org.pm.aerologixbackend.entity.Warehouse;
import org.pm.aerologixbackend.exception.BadRequestException;
import org.pm.aerologixbackend.exception.ResourceNotFoundException;
import org.pm.aerologixbackend.repository.WarehouseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;

    public List<WarehouseDTO.Response> getAllWarehouses() {
        return warehouseRepository.findAll()
                .stream()
                .map(WarehouseDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    public WarehouseDTO.Response getWarehouseById(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + id));
        return WarehouseDTO.Response.fromEntity(warehouse);
    }

    @Transactional
    public WarehouseDTO.Response createWarehouse(WarehouseDTO.CreateRequest request) {
        Warehouse warehouse = Warehouse.builder()
                .name(request.getName())
                .location(request.getLocation())
                .address(request.getAddress())
                .capacity(request.getCapacity())
                .currentInventory(0L)
                .manager(request.getManager())
                .phone(request.getPhone())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .build();

        warehouse = warehouseRepository.save(warehouse);
        return WarehouseDTO.Response.fromEntity(warehouse);
    }

    @Transactional
    public WarehouseDTO.Response updateWarehouse(Long id, WarehouseDTO.UpdateRequest request) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + id));

        if (request.getName() != null) warehouse.setName(request.getName());
        if (request.getLocation() != null) warehouse.setLocation(request.getLocation());
        if (request.getAddress() != null) warehouse.setAddress(request.getAddress());
        if (request.getCapacity() != null) warehouse.setCapacity(request.getCapacity());
        if (request.getManager() != null) warehouse.setManager(request.getManager());
        if (request.getPhone() != null) warehouse.setPhone(request.getPhone());
        if (request.getLatitude() != null) warehouse.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) warehouse.setLongitude(request.getLongitude());

        warehouse = warehouseRepository.save(warehouse);
        return WarehouseDTO.Response.fromEntity(warehouse);
    }

    @Transactional
    public WarehouseDTO.Response updateInventory(Long id, WarehouseDTO.InventoryUpdateRequest request) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + id));

        long newInventory = warehouse.getCurrentInventory() + request.getChangeAmount();

        if (newInventory < 0) {
            throw new BadRequestException("Inventory cannot be negative");
        }

        if (newInventory > warehouse.getCapacity()) {
            throw new BadRequestException("Inventory cannot exceed capacity");
        }

        warehouse.setCurrentInventory(newInventory);
        warehouse = warehouseRepository.save(warehouse);
        
        return WarehouseDTO.Response.fromEntity(warehouse);
    }

    @Transactional
    public void deleteWarehouse(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + id));

        if (warehouse.getCurrentInventory() > 0) {
            throw new BadRequestException("Cannot delete warehouse with existing inventory");
        }

        warehouseRepository.delete(warehouse);
    }

    public List<WarehouseDTO.Response> getWarehousesWithLowOccupancy() {
        return warehouseRepository.findWarehousesWithLowOccupancy()
                .stream()
                .map(WarehouseDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    public List<WarehouseDTO.Response> getWarehousesWithHighOccupancy() {
        return warehouseRepository.findWarehousesWithHighOccupancy()
                .stream()
                .map(WarehouseDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    public Long getTotalCapacity() {
        Long total = warehouseRepository.getTotalCapacity();
        return total != null ? total : 0L;
    }

    public Long getTotalInventory() {
        Long total = warehouseRepository.getTotalInventory();
        return total != null ? total : 0L;
    }
}
