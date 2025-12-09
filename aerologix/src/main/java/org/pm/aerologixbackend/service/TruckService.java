package org.pm.aerologixbackend.service;

import lombok.RequiredArgsConstructor;
import org.pm.aerologixbackend.dto.TruckDTO;
import org.pm.aerologixbackend.entity.Truck;
import org.pm.aerologixbackend.entity.TruckStatus;
import org.pm.aerologixbackend.exception.BadRequestException;
import org.pm.aerologixbackend.exception.ResourceNotFoundException;
import org.pm.aerologixbackend.repository.TruckRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TruckService {

    private final TruckRepository truckRepository;

    public List<TruckDTO.Response> getAllTrucks() {
        return truckRepository.findAll()
                .stream()
                .map(TruckDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    public TruckDTO.Response getTruckById(Long id) {
        Truck truck = truckRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Truck not found with id: " + id));
        return TruckDTO.Response.fromEntity(truck);
    }

    public TruckDTO.Response getTruckByLicensePlate(String licensePlate) {
        Truck truck = truckRepository.findByLicensePlate(licensePlate)
                .orElseThrow(() -> new ResourceNotFoundException("Truck not found with license plate: " + licensePlate));
        return TruckDTO.Response.fromEntity(truck);
    }

    @Transactional
    public TruckDTO.Response createTruck(TruckDTO.CreateRequest request) {
        if (truckRepository.existsByLicensePlate(request.getLicensePlate())) {
            throw new BadRequestException("Truck with this license plate already exists");
        }

        Truck truck = Truck.builder()
                .licensePlate(request.getLicensePlate())
                .model(request.getModel())
                .capacity(request.getCapacity())
                .status(request.getStatus() != null ? request.getStatus() : TruckStatus.AVAILABLE)
                .mileage(0L)
                .build();

        truck = truckRepository.save(truck);
        return TruckDTO.Response.fromEntity(truck);
    }

    @Transactional
    public TruckDTO.Response updateTruck(Long id, TruckDTO.UpdateRequest request) {
        Truck truck = truckRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Truck not found with id: " + id));

        if (request.getLicensePlate() != null) {
            truckRepository.findByLicensePlate(request.getLicensePlate())
                    .ifPresent(existing -> {
                        if (!existing.getId().equals(id)) {
                            throw new BadRequestException("License plate already in use");
                        }
                    });
            truck.setLicensePlate(request.getLicensePlate());
        }
        
        if (request.getModel() != null) truck.setModel(request.getModel());
        if (request.getCapacity() != null) truck.setCapacity(request.getCapacity());
        if (request.getStatus() != null) truck.setStatus(request.getStatus());
        if (request.getMileage() != null) truck.setMileage(request.getMileage());
        if (request.getLastService() != null) truck.setLastService(request.getLastService());

        truck = truckRepository.save(truck);
        return TruckDTO.Response.fromEntity(truck);
    }

    @Transactional
    public TruckDTO.Response updateLocation(Long id, TruckDTO.LocationUpdate request) {
        Truck truck = truckRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Truck not found with id: " + id));

        truck.setCurrentLatitude(request.getLatitude());
        truck.setCurrentLongitude(request.getLongitude());
        truck.setCurrentSpeed(request.getSpeed());
        truck.setHeading(request.getHeading());
        truck.setLastLocationUpdate(LocalDateTime.now());

        truck = truckRepository.save(truck);
        return TruckDTO.Response.fromEntity(truck);
    }

    @Transactional
    public void deleteTruck(Long id) {
        Truck truck = truckRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Truck not found with id: " + id));

        if (truck.getAssignedDriver() != null) {
            throw new BadRequestException("Cannot delete truck that is assigned to a driver");
        }

        if (truck.getStatus() == TruckStatus.IN_USE) {
            throw new BadRequestException("Cannot delete truck that is currently in use");
        }

        truckRepository.delete(truck);
    }

    public List<TruckDTO.Response> getAvailableTrucks() {
        return truckRepository.findAvailableTrucks()
                .stream()
                .map(TruckDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TruckDTO.Response> getTrucksByStatus(TruckStatus status) {
        return truckRepository.findByStatus(status)
                .stream()
                .map(TruckDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TruckDTO.Response> getActiveTrucks() {
        return truckRepository.findActiveTrucks()
                .stream()
                .map(TruckDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    public long countByStatus(TruckStatus status) {
        return truckRepository.countByStatus(status);
    }
}
