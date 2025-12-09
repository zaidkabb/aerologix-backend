package org.pm.aerologixbackend.service;


import lombok.RequiredArgsConstructor;
import org.pm.aerologixbackend.dto.TruckRequestDTO;
import org.pm.aerologixbackend.dto.TruckResponseDTO;
import org.pm.aerologixbackend.entity.Truck;
import org.pm.aerologixbackend.entity.TruckStatus;
import org.pm.aerologixbackend.exception.DuplicateLicensePlateException;
import org.pm.aerologixbackend.exception.InvalidTruckException;
import org.pm.aerologixbackend.exception.TruckNotFoundException;
import org.pm.aerologixbackend.repository.TruckRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TruckService {

    private final TruckRepository truckRepository;

    @Transactional(readOnly = true)
    public List<TruckResponseDTO> getAllTrucks() {
        return truckRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TruckResponseDTO> getTrucksByStatus(TruckStatus status) {
        return truckRepository.findByStatus(status)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public TruckResponseDTO createTruck(TruckRequestDTO request) {
        // Business rule: license plate must be unique
        if (truckRepository.existsByLicensePlate(request.getLicensePlate())) {
            throw new DuplicateLicensePlateException(request.getLicensePlate());
        }

        Truck truck = new Truck();
        truck.setLicensePlate(request.getLicensePlate());
        truck.setModel(request.getModel());
        truck.setCapacity(request.getCapacity());
        truck.setStatus(TruckStatus.AVAILABLE);

        Truck saved = truckRepository.save(truck);
        return toResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public TruckResponseDTO getTruckById(Long id) {
        Truck truck = truckRepository.findById(id)
                .orElseThrow(() -> new TruckNotFoundException(id));
        return toResponseDTO(truck);
    }

    @Transactional
    public TruckResponseDTO updateTruck(Long id, TruckRequestDTO request) {
        Truck truck = truckRepository.findById(id)
                .orElseThrow(() -> new TruckNotFoundException(id));

        // Business rule: cannot update truck that is in use
        if (truck.getStatus() == TruckStatus.IN_USE) {
            throw new InvalidTruckException("Cannot update truck that is currently in use");
        }

        // Business rule: check if new license plate conflicts with another truck
        if (!truck.getLicensePlate().equals(request.getLicensePlate())) {
            if (truckRepository.existsByLicensePlate(request.getLicensePlate())) {
                throw new DuplicateLicensePlateException(request.getLicensePlate());
            }
        }

        truck.setLicensePlate(request.getLicensePlate());
        truck.setModel(request.getModel());
        truck.setCapacity(request.getCapacity());

        Truck updated = truckRepository.save(truck);
        return toResponseDTO(updated);
    }

    @Transactional
    public TruckResponseDTO updateTruckStatus(Long id, TruckStatus newStatus) {
        Truck truck = truckRepository.findById(id)
                .orElseThrow(() -> new TruckNotFoundException(id));

        // Business rule: validate status transitions
        validateStatusTransition(truck.getStatus(), newStatus);

        truck.setStatus(newStatus);
        Truck updated = truckRepository.save(truck);
        return toResponseDTO(updated);
    }

    @Transactional
    public TruckResponseDTO updateTruckLocation(Long id, Double latitude, Double longitude) {
        Truck truck = truckRepository.findById(id)
                .orElseThrow(() -> new TruckNotFoundException(id));

        // Business rule: validate coordinates
        if (latitude < -90 || latitude > 90) {
            throw new InvalidTruckException("Latitude must be between -90 and 90");
        }
        if (longitude < -180 || longitude > 180) {
            throw new InvalidTruckException("Longitude must be between -180 and 180");
        }

        truck.setCurrentLatitude(latitude);
        truck.setCurrentLongitude(longitude);

        Truck updated = truckRepository.save(truck);
        return toResponseDTO(updated);
    }

    @Transactional
    public void deleteTruck(Long id) {
        Truck truck = truckRepository.findById(id)
                .orElseThrow(() -> new TruckNotFoundException(id));

        // Business rule: cannot delete truck that is in use
        if (truck.getStatus() == TruckStatus.IN_USE) {
            throw new InvalidTruckException(
                    "Cannot delete truck that is currently in use. " +
                            "Change status to AVAILABLE or MAINTENANCE first.");
        }

        truckRepository.delete(truck);
    }

    // ========== HELPER METHODS ==========

    private void validateStatusTransition(TruckStatus current, TruckStatus next) {
        // Business rules for status transitions
        if (current == TruckStatus.MAINTENANCE && next == TruckStatus.IN_USE) {
            throw new InvalidTruckException(
                    "Truck in maintenance cannot be directly set to in-use. " +
                            "Set to AVAILABLE first.");
        }
    }

    private TruckResponseDTO toResponseDTO(Truck truck) {
        return new TruckResponseDTO(
                truck.getId(),
                truck.getLicensePlate(),
                truck.getModel(),
                truck.getCapacity(),
                truck.getStatus(),
                truck.getCurrentLatitude(),
                truck.getCurrentLongitude(),
                truck.getCreatedAt()
        );
    }
}