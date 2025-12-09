package org.pm.aerologixbackend.service;


import lombok.RequiredArgsConstructor;
import org.pm.aerologixbackend.dto.DriverRequestDTO;
import org.pm.aerologixbackend.dto.DriverResponseDTO;
import org.pm.aerologixbackend.entity.Driver;
import org.pm.aerologixbackend.entity.DriverStatus;
import org.pm.aerologixbackend.entity.Truck;
import org.pm.aerologixbackend.entity.TruckStatus;
import org.pm.aerologixbackend.exception.*;
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

    @Transactional(readOnly = true)
    public List<DriverResponseDTO> getAllDrivers() {
        return driverRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DriverResponseDTO> getDriversByStatus(DriverStatus status) {
        return driverRepository.findByStatus(status)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public DriverResponseDTO createDriver(DriverRequestDTO request) {
        // Business rule: email must be unique
        if (driverRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateDriverException("email", request.getEmail());
        }

        // Business rule: license number must be unique
        if (driverRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new DuplicateDriverException("license number", request.getLicenseNumber());
        }

        Driver driver = new Driver();
        driver.setFirstName(request.getFirstName());
        driver.setLastName(request.getLastName());
        driver.setEmail(request.getEmail());
        driver.setLicenseNumber(request.getLicenseNumber());
        driver.setPhoneNumber(request.getPhoneNumber());
        driver.setStatus(DriverStatus.AVAILABLE);

        Driver saved = driverRepository.save(driver);
        return toResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public DriverResponseDTO getDriverById(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new DriverNotFoundException(id));
        return toResponseDTO(driver);
    }

    @Transactional
    public DriverResponseDTO updateDriver(Long id, DriverRequestDTO request) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new DriverNotFoundException(id));

        // Business rule: cannot update driver that is on duty
        if (driver.getStatus() == DriverStatus.ON_DUTY) {
            throw new InvalidDriverException("Cannot update driver that is currently on duty");
        }

        // Business rule: check email uniqueness (if changed)
        if (!driver.getEmail().equals(request.getEmail())) {
            if (driverRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateDriverException("email", request.getEmail());
            }
        }

        // Business rule: check license number uniqueness (if changed)
        if (!driver.getLicenseNumber().equals(request.getLicenseNumber())) {
            if (driverRepository.existsByLicenseNumber(request.getLicenseNumber())) {
                throw new DuplicateDriverException("license number", request.getLicenseNumber());
            }
        }

        driver.setFirstName(request.getFirstName());
        driver.setLastName(request.getLastName());
        driver.setEmail(request.getEmail());
        driver.setLicenseNumber(request.getLicenseNumber());
        driver.setPhoneNumber(request.getPhoneNumber());

        Driver updated = driverRepository.save(driver);
        return toResponseDTO(updated);
    }

    @Transactional
    public DriverResponseDTO updateDriverStatus(Long id, DriverStatus newStatus) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new DriverNotFoundException(id));

        // Business rule: validate status transitions
        validateStatusTransition(driver, newStatus);

        driver.setStatus(newStatus);
        Driver updated = driverRepository.save(driver);
        return toResponseDTO(updated);
    }

    @Transactional
    public DriverResponseDTO assignTruckToDriver(Long driverId, Long truckId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException(driverId));

        Truck truck = truckRepository.findById(truckId)
                .orElseThrow(() -> new TruckNotFoundException(truckId));

        // Business rule: driver must be available or off-duty
        if (driver.getStatus() == DriverStatus.ON_DUTY) {
            throw new InvalidDriverException("Driver is already on duty with another truck");
        }

        if (driver.getStatus() == DriverStatus.ON_LEAVE) {
            throw new InvalidDriverException("Cannot assign truck to driver on leave");
        }

        // Business rule: truck must be available
        if (truck.getStatus() != TruckStatus.AVAILABLE) {
            throw new InvalidTruckException("Truck is not available for assignment");
        }

        // Business rule: check if truck is already assigned to another driver
        List<Driver> driversWithTruck = driverRepository.findByAssignedTruckId(truckId);
        if (!driversWithTruck.isEmpty()) {
            throw new InvalidDriverException("Truck is already assigned to another driver");
        }

        // Assign truck to driver
        driver.setAssignedTruck(truck);
        driver.setStatus(DriverStatus.ON_DUTY);

        // Update truck status
        truck.setStatus(TruckStatus.IN_USE);
        truckRepository.save(truck);

        Driver updated = driverRepository.save(driver);
        return toResponseDTO(updated);
    }

    @Transactional
    public DriverResponseDTO unassignTruckFromDriver(Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException(driverId));

        if (driver.getAssignedTruck() == null) {
            throw new InvalidDriverException("Driver has no truck assigned");
        }

        // Free up the truck
        Truck truck = driver.getAssignedTruck();
        truck.setStatus(TruckStatus.AVAILABLE);
        truckRepository.save(truck);

        // Unassign truck from driver
        driver.setAssignedTruck(null);
        driver.setStatus(DriverStatus.AVAILABLE);

        Driver updated = driverRepository.save(driver);
        return toResponseDTO(updated);
    }

    @Transactional
    public void deleteDriver(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new DriverNotFoundException(id));

        // Business rule: cannot delete driver that is on duty
        if (driver.getStatus() == DriverStatus.ON_DUTY) {
            throw new InvalidDriverException(
                    "Cannot delete driver that is currently on duty. " +
                            "Unassign truck first.");
        }

        driverRepository.delete(driver);
    }

    // ========== HELPER METHODS ==========

    private void validateStatusTransition(Driver driver, DriverStatus newStatus) {
        DriverStatus currentStatus = driver.getStatus();

        // Business rule: cannot go to ON_DUTY without a truck
        if (newStatus == DriverStatus.ON_DUTY && driver.getAssignedTruck() == null) {
            throw new InvalidDriverException(
                    "Cannot set driver to ON_DUTY without an assigned truck");
        }

        // Business rule: if going from ON_DUTY to anything else, must unassign truck first
        if (currentStatus == DriverStatus.ON_DUTY && newStatus != DriverStatus.ON_DUTY) {
            throw new InvalidDriverException(
                    "Must unassign truck before changing status from ON_DUTY");
        }
    }

    private DriverResponseDTO toResponseDTO(Driver driver) {
        Long truckId = driver.getAssignedTruck() != null ? driver.getAssignedTruck().getId() : null;
        String truckPlate = driver.getAssignedTruck() != null ? driver.getAssignedTruck().getLicensePlate() : null;

        return new DriverResponseDTO(
                driver.getId(),
                driver.getFirstName(),
                driver.getLastName(),
                driver.getEmail(),
                driver.getLicenseNumber(),
                driver.getPhoneNumber(),
                driver.getStatus(),
                truckId,
                truckPlate,
                driver.getCreatedAt()
        );
    }
}