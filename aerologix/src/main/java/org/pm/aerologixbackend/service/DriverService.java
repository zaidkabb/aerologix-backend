package org.pm.aerologixbackend.service;

import lombok.RequiredArgsConstructor;
import org.pm.aerologixbackend.dto.DriverDTO;
import org.pm.aerologixbackend.entity.Driver;
import org.pm.aerologixbackend.entity.DriverStatus;
import org.pm.aerologixbackend.entity.Truck;
import org.pm.aerologixbackend.entity.TruckStatus;
import org.pm.aerologixbackend.exception.BadRequestException;
import org.pm.aerologixbackend.exception.ResourceNotFoundException;
import org.pm.aerologixbackend.repository.DriverRepository;
import org.pm.aerologixbackend.repository.TruckRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverRepository driverRepository;
    private final TruckRepository truckRepository;

    public List<DriverDTO.Response> getAllDrivers() {
        return driverRepository.findAll()
                .stream()
                .map(DriverDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    public DriverDTO.Response getDriverById(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));
        return DriverDTO.Response.fromEntity(driver);
    }

    public DriverDTO.Response getDriverByEmail(String email) {
        Driver driver = driverRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with email: " + email));
        return DriverDTO.Response.fromEntity(driver);
    }

    @Transactional
    public DriverDTO.Response createDriver(DriverDTO.CreateRequest request) {
        if (driverRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Driver with this email already exists");
        }

        if (driverRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new BadRequestException("Driver with this license number already exists");
        }

        Driver driver = Driver.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .licenseNumber(request.getLicenseNumber())
                .status(DriverStatus.AVAILABLE)
                .totalDeliveries(0)
                .build();

        driver = driverRepository.save(driver);
        return DriverDTO.Response.fromEntity(driver);
    }

    @Transactional
    public DriverDTO.Response updateDriver(Long id, DriverDTO.UpdateRequest request) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));

        if (request.getName() != null) driver.setName(request.getName());
        if (request.getPhone() != null) driver.setPhone(request.getPhone());
        if (request.getStatus() != null) driver.setStatus(request.getStatus());

        if (request.getEmail() != null && !request.getEmail().equals(driver.getEmail())) {
            if (driverRepository.existsByEmail(request.getEmail())) {
                throw new BadRequestException("Email already in use");
            }
            driver.setEmail(request.getEmail());
        }

        if (request.getLicenseNumber() != null && !request.getLicenseNumber().equals(driver.getLicenseNumber())) {
            if (driverRepository.existsByLicenseNumber(request.getLicenseNumber())) {
                throw new BadRequestException("License number already in use");
            }
            driver.setLicenseNumber(request.getLicenseNumber());
        }

        driver = driverRepository.save(driver);
        return DriverDTO.Response.fromEntity(driver);
    }

    @Transactional
    public DriverDTO.Response assignTruck(Long driverId, Long truckId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));

        Truck truck = truckRepository.findById(truckId)
                .orElseThrow(() -> new ResourceNotFoundException("Truck not found"));

        if (truck.getStatus() != TruckStatus.AVAILABLE) {
            throw new BadRequestException("Truck is not available");
        }

        if (truck.getAssignedDriver() != null) {
            throw new BadRequestException("Truck is already assigned to another driver");
        }

        driver.setAssignedTruck(truck);
        truck.setStatus(TruckStatus.IN_USE);

        truckRepository.save(truck);
        driver = driverRepository.save(driver);
        
        return DriverDTO.Response.fromEntity(driver);
    }

    @Transactional
    public DriverDTO.Response unassignTruck(Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));

        if (driver.getAssignedTruck() == null) {
            throw new BadRequestException("Driver doesn't have an assigned truck");
        }

        if (driver.getStatus() == DriverStatus.ON_DELIVERY) {
            throw new BadRequestException("Cannot unassign truck while driver is on delivery");
        }

        Truck truck = driver.getAssignedTruck();
        truck.setStatus(TruckStatus.AVAILABLE);
        driver.setAssignedTruck(null);

        truckRepository.save(truck);
        driver = driverRepository.save(driver);
        
        return DriverDTO.Response.fromEntity(driver);
    }

    @Transactional
    public void deleteDriver(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));

        if (driver.getStatus() == DriverStatus.ON_DELIVERY) {
            throw new BadRequestException("Cannot delete driver who is currently on delivery");
        }

        // Unassign truck if any
        if (driver.getAssignedTruck() != null) {
            Truck truck = driver.getAssignedTruck();
            truck.setStatus(TruckStatus.AVAILABLE);
            truckRepository.save(truck);
        }

        driverRepository.delete(driver);
    }

    public List<DriverDTO.Response> getAvailableDrivers() {
        return driverRepository.findAvailableDrivers()
                .stream()
                .map(DriverDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    public List<DriverDTO.Response> getDriversByStatus(DriverStatus status) {
        return driverRepository.findByStatus(status)
                .stream()
                .map(DriverDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    public long countByStatus(DriverStatus status) {
        return driverRepository.countByStatus(status);
    }
}
