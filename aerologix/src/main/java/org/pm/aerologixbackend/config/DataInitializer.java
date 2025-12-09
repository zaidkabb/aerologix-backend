package org.pm.aerologixbackend.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pm.aerologixbackend.entity.*;
import org.pm.aerologixbackend.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final TruckRepository truckRepository;
    private final DriverRepository driverRepository;
    private final WarehouseRepository warehouseRepository;
    private final ShipmentRepository shipmentRepository;
    private final ShipmentTimelineRepository timelineRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already initialized, skipping data seeding");
            return;
        }

        log.info("Initializing database with sample data...");

        // Create Users
        User admin = createUser("Admin User", "admin@fleethub.com", "password123", UserRole.ADMIN);
        User driver1User = createUser("John Smith", "driver@fleethub.com", "password123", UserRole.DRIVER);
        User driver2User = createUser("Jane Doe", "jane.driver@fleethub.com", "password123", UserRole.DRIVER);
        User customer = createUser("Customer User", "customer@fleethub.com", "password123", UserRole.CUSTOMER);

        // Create Trucks
        Truck truck1 = createTruck("ABC-1234", "Volvo FH16", 25000.0, TruckStatus.IN_USE, 125000L);
        Truck truck2 = createTruck("DEF-5678", "Scania R500", 22000.0, TruckStatus.AVAILABLE, 89000L);
        Truck truck3 = createTruck("GHI-9012", "Mercedes Actros", 24000.0, TruckStatus.MAINTENANCE, 156000L);
        Truck truck4 = createTruck("JKL-3456", "MAN TGX", 23000.0, TruckStatus.IN_USE, 78000L);
        Truck truck5 = createTruck("MNO-7890", "DAF XF", 21000.0, TruckStatus.AVAILABLE, 45000L);

        // Create Drivers
        Driver driver1 = createDriver("John Smith", "john.smith@fleethub.com", "+1 555-0101", "CDL-12345", truck1, driver1User);
        Driver driver2 = createDriver("Jane Doe", "jane.doe@fleethub.com", "+1 555-0102", "CDL-23456", null, driver2User);
        Driver driver3 = createDriver("Mike Johnson", "mike.johnson@fleethub.com", "+1 555-0103", "CDL-34567", truck4, null);
        Driver driver4 = createDriver("Sarah Williams", "sarah.williams@fleethub.com", "+1 555-0104", "CDL-45678", null, null);
        driver4.setStatus(DriverStatus.OFF_DUTY);
        driverRepository.save(driver4);

        // Create Warehouses
        Warehouse wh1 = createWarehouse("East Coast Hub", "New York, NY", "123 Logistics Way, Brooklyn, NY 11201", 50000L, 32500L, "Robert Chen", "+1 555-0201");
        Warehouse wh2 = createWarehouse("West Coast Center", "Los Angeles, CA", "456 Distribution Blvd, Long Beach, CA 90802", 75000L, 58000L, "Maria Garcia", "+1 555-0202");
        Warehouse wh3 = createWarehouse("Central Distribution", "Chicago, IL", "789 Freight St, Chicago, IL 60601", 40000L, 28000L, "James Wilson", "+1 555-0203");
        Warehouse wh4 = createWarehouse("Southern Depot", "Atlanta, GA", "321 Shipping Lane, Atlanta, GA 30301", 35000L, 31000L, "Lisa Thompson", "+1 555-0204");

        // Create Shipments
        Shipment s1 = createShipment("FH-2024-001", "New York, NY", "Los Angeles, CA", 250.0, ShipmentStatus.IN_TRANSIT, driver1, truck1, "Michael Brown", "+1 555-1234");
        Shipment s2 = createShipment("FH-2024-002", "Chicago, IL", "Miami, FL", 180.0, ShipmentStatus.PENDING, null, null, "Emily Davis", "+1 555-5678");
        Shipment s3 = createShipment("FH-2024-003", "Seattle, WA", "Denver, CO", 320.0, ShipmentStatus.DELIVERED, driver2, null, "Robert Johnson", "+1 555-9012");
        s3.setActualDelivery(LocalDate.now().minusDays(1));
        shipmentRepository.save(s3);
        
        Shipment s4 = createShipment("FH-2024-004", "Boston, MA", "Atlanta, GA", 420.0, ShipmentStatus.IN_TRANSIT, driver3, truck4, "Sarah Wilson", "+1 555-3456");
        Shipment s5 = createShipment("FH-2024-005", "San Francisco, CA", "Phoenix, AZ", 150.0, ShipmentStatus.CANCELLED, null, null, "David Lee", "+1 555-7890");

        // Create Timeline entries
        createTimeline(s1, ShipmentStatus.PENDING, "New York, NY", LocalDateTime.now().minusDays(5));
        createTimeline(s1, ShipmentStatus.PICKED_UP, "East Coast Hub, Brooklyn", LocalDateTime.now().minusDays(4));
        createTimeline(s1, ShipmentStatus.IN_TRANSIT, "Chicago, IL (Transfer)", LocalDateTime.now().minusDays(2));

        createTimeline(s3, ShipmentStatus.PENDING, "Seattle, WA", LocalDateTime.now().minusDays(7));
        createTimeline(s3, ShipmentStatus.PICKED_UP, "Seattle Distribution Center", LocalDateTime.now().minusDays(6));
        createTimeline(s3, ShipmentStatus.IN_TRANSIT, "Salt Lake City, UT", LocalDateTime.now().minusDays(4));
        createTimeline(s3, ShipmentStatus.OUT_FOR_DELIVERY, "Denver, CO Hub", LocalDateTime.now().minusDays(2));
        createTimeline(s3, ShipmentStatus.DELIVERED, "Denver, CO", LocalDateTime.now().minusDays(1));

        log.info("Database initialization completed successfully!");
        log.info("Sample login credentials:");
        log.info("  Admin: admin@fleethub.com / password123");
        log.info("  Driver: driver@fleethub.com / password123");
        log.info("  Customer: customer@fleethub.com / password123");
    }

    private User createUser(String name, String email, String password, UserRole role) {
        User user = User.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(role)
                .isActive(true)
                .build();
        return userRepository.save(user);
    }

    private Truck createTruck(String plate, String model, Double capacity, TruckStatus status, Long mileage) {
        Truck truck = Truck.builder()
                .licensePlate(plate)
                .model(model)
                .capacity(capacity)
                .status(status)
                .mileage(mileage)
                .lastService(LocalDate.now().minusMonths(1))
                .nextService(LocalDate.now().plusMonths(5))
                .build();
        return truckRepository.save(truck);
    }

    private Driver createDriver(String name, String email, String phone, String license, Truck truck, User user) {
        Driver driver = Driver.builder()
                .name(name)
                .email(email)
                .phone(phone)
                .licenseNumber(license)
                .status(truck != null ? DriverStatus.ON_DELIVERY : DriverStatus.AVAILABLE)
                .assignedTruck(truck)
                .user(user)
                .totalDeliveries((int) (Math.random() * 300) + 50)
                .build();
        return driverRepository.save(driver);
    }

    private Warehouse createWarehouse(String name, String location, String address, Long capacity, Long inventory, String manager, String phone) {
        Warehouse warehouse = Warehouse.builder()
                .name(name)
                .location(location)
                .address(address)
                .capacity(capacity)
                .currentInventory(inventory)
                .manager(manager)
                .phone(phone)
                .build();
        return warehouseRepository.save(warehouse);
    }

    private Shipment createShipment(String tracking, String origin, String dest, Double weight, ShipmentStatus status, Driver driver, Truck truck, String customerName, String phone) {
        Shipment shipment = Shipment.builder()
                .trackingNumber(tracking)
                .origin(origin)
                .destination(dest)
                .weight(weight)
                .status(status)
                .driver(driver)
                .truck(truck)
                .customerName(customerName)
                .customerPhone(phone)
                .estimatedDelivery(LocalDate.now().plusDays((long) (Math.random() * 7) + 2))
                .build();
        return shipmentRepository.save(shipment);
    }

    private void createTimeline(Shipment shipment, ShipmentStatus status, String location, LocalDateTime timestamp) {
        ShipmentTimeline timeline = ShipmentTimeline.builder()
                .shipment(shipment)
                .status(status)
                .location(location)
                .timestamp(timestamp)
                .build();
        timelineRepository.save(timeline);
    }
}
