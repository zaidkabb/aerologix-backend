# AeroLogix - Fleet Management System

A full-stack fleet management application with a Spring Boot backend and React frontend (FleetHub).

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         Frontend (FleetHub)                      │
│                    React + TypeScript + Vite                     │
│                    Tailwind CSS + shadcn/ui                      │
│                         Port: 5173                               │
└─────────────────────────────────────────────────────────────────┘
                              │
                              │ REST API (JSON)
                              │ JWT Authentication
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Backend (Spring Boot 3.5)                     │
│                                                                  │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐ │
│  │ Controllers │  │  Services   │  │      Security           │ │
│  │  (REST API) │  │  (Business) │  │  (JWT + Spring Sec)     │ │
│  └─────────────┘  └─────────────┘  └─────────────────────────┘ │
│                                                                  │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐ │
│  │ Repositories│  │   Entities  │  │        DTOs             │ │
│  │   (JPA)     │  │   (Models)  │  │  (Request/Response)     │ │
│  └─────────────┘  └─────────────┘  └─────────────────────────┘ │
│                         Port: 8080                               │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Database (PostgreSQL/H2)                      │
│        Users, Shipments, Trucks, Drivers, Warehouses            │
└─────────────────────────────────────────────────────────────────┘
```

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Node.js 18+
- PostgreSQL 14+ (or use H2 for development)

### Backend Setup

1. **Navigate to backend directory:**
   ```bash
   cd aerologix-backend
   ```

2. **Run with H2 (Development - No DB setup required):**
   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
   ```

3. **Or with PostgreSQL (Production):**
   ```bash
   # Create database first
   createdb aerologix
   
   # Set environment variables
   export DB_USERNAME=postgres
   export DB_PASSWORD=yourpassword
   
   ./mvnw spring-boot:run
   ```

4. **Backend runs on:** `http://localhost:8080`

5. **H2 Console (dev mode):** `http://localhost:8080/h2-console`

### Frontend Setup

1. **Navigate to frontend directory:**
   ```bash
   cd fleethub-frontend
   ```

2. **Install dependencies:**
   ```bash
   npm install
   ```

3. **Start development server:**
   ```bash
   npm run dev
   ```

4. **Frontend runs on:** `http://localhost:5173`

## 🔐 Authentication

### Demo Credentials (Auto-seeded in dev mode)
| Role | Email | Password |
|------|-------|----------|
| Admin | admin@fleethub.com | password123 |
| Driver | driver@fleethub.com | password123 |
| Customer | customer@fleethub.com | password123 |

### JWT Token Flow
1. User logs in via `/api/auth/login`
2. Server returns JWT token
3. Frontend stores token in localStorage
4. All subsequent requests include `Authorization: Bearer <token>`

## 📡 API Endpoints

### Authentication (Public)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login and get JWT token |

### Shipments
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/shipments` | Get all shipments | ✅ |
| GET | `/api/shipments/{id}` | Get shipment by ID | ✅ |
| POST | `/api/shipments` | Create shipment | Admin |
| PUT | `/api/shipments/{id}` | Update shipment | Admin |
| POST | `/api/shipments/{id}/assign` | Assign driver | Admin |
| POST | `/api/shipments/{id}/deliver` | Mark delivered | Admin/Driver |
| DELETE | `/api/shipments/{id}` | Delete shipment | Admin |

### Tracking (Public)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/track/{trackingNumber}` | Track shipment with timeline |

### Trucks
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/trucks` | Get all trucks | ✅ |
| GET | `/api/trucks/available` | Get available trucks | ✅ |
| POST | `/api/trucks` | Add truck | Admin |
| PUT | `/api/trucks/{id}` | Update truck | Admin |
| PUT | `/api/trucks/{id}/location` | Update GPS location | Admin/Driver |
| DELETE | `/api/trucks/{id}` | Delete truck | Admin |

### Drivers
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/drivers` | Get all drivers | ✅ |
| GET | `/api/drivers/available` | Get available drivers | ✅ |
| POST | `/api/drivers` | Add driver | Admin |
| PUT | `/api/drivers/{id}` | Update driver | Admin |
| POST | `/api/drivers/{id}/assign-truck` | Assign truck | Admin |
| POST | `/api/drivers/{id}/unassign-truck` | Unassign truck | Admin |
| DELETE | `/api/drivers/{id}` | Delete driver | Admin |

### Warehouses
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/warehouses` | Get all warehouses | ✅ |
| POST | `/api/warehouses` | Add warehouse | Admin |
| PUT | `/api/warehouses/{id}` | Update warehouse | Admin |
| PUT | `/api/warehouses/{id}/inventory` | Update inventory | Admin |
| DELETE | `/api/warehouses/{id}` | Delete warehouse | Admin |

### Dashboard
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/dashboard` | Get dashboard statistics | Admin |

## 📁 Backend Project Structure

```
src/main/java/org/pm/aerologixbackend/
├── AerologixBackendApplication.java    # Main application
├── config/
│   ├── SecurityConfig.java             # Spring Security + JWT config
│   ├── CorsConfig.java                 # CORS configuration
│   └── DataInitializer.java            # Database seeding
├── controller/
│   ├── AuthController.java             # Login/Register endpoints
│   ├── ShipmentController.java         # Shipment CRUD
│   ├── TrackingController.java         # Public tracking
│   ├── TruckController.java            # Truck management
│   ├── DriverController.java           # Driver management
│   ├── WarehouseController.java        # Warehouse management
│   └── DashboardController.java        # Statistics
├── dto/
│   ├── AuthDTO.java                    # Auth request/response
│   ├── ShipmentDTO.java                # Shipment DTOs
│   ├── TruckDTO.java                   # Truck DTOs
│   ├── DriverDTO.java                  # Driver DTOs
│   ├── WarehouseDTO.java               # Warehouse DTOs
│   ├── DashboardDTO.java               # Dashboard stats
│   └── ApiResponse.java                # Standard API response wrapper
├── entity/
│   ├── User.java                       # User entity (Spring Security)
│   ├── Shipment.java                   # Shipment entity
│   ├── ShipmentTimeline.java           # Tracking history
│   ├── Truck.java                      # Truck entity
│   ├── Driver.java                     # Driver entity
│   ├── Warehouse.java                  # Warehouse entity
│   └── *Status.java, *Role.java        # Enums
├── exception/
│   ├── GlobalExceptionHandler.java     # Exception handling
│   ├── ResourceNotFoundException.java
│   └── BadRequestException.java
├── repository/
│   ├── UserRepository.java
│   ├── ShipmentRepository.java
│   ├── ShipmentTimelineRepository.java
│   ├── TruckRepository.java
│   ├── DriverRepository.java
│   └── WarehouseRepository.java
├── security/
│   ├── JwtService.java                 # JWT generation/validation
│   └── JwtAuthenticationFilter.java    # JWT filter
└── service/
    ├── AuthService.java                # Authentication logic
    ├── CustomUserDetailsService.java   # Spring Security
    ├── ShipmentService.java            # Shipment business logic
    ├── TruckService.java               # Truck business logic
    ├── DriverService.java              # Driver business logic
    ├── WarehouseService.java           # Warehouse business logic
    └── DashboardService.java           # Dashboard statistics
```

## 🔧 Configuration

### application.yml (Production - PostgreSQL)
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/aerologix
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:password}
  jpa:
    hibernate:
      ddl-auto: update

jwt:
  secret: ${JWT_SECRET:your-256-bit-secret-key-here}
  expiration: 86400000  # 24 hours

cors:
  allowed-origins: http://localhost:5173,http://localhost:3000
```

### application-dev.yml (Development - H2)
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:aerologix
  h2:
    console:
      enabled: true
      path: /h2-console
```

## 🎨 Frontend Integration

### API Service (`src/services/api.ts`)
The frontend includes a complete API service with:
- Type-safe TypeScript interfaces
- Automatic JWT token handling
- All CRUD operations for each entity
- Error handling

### Using the API
```typescript
import { shipmentsApi, trucksApi, driversApi } from '@/services/api';

// Get all shipments
const { data: shipments } = await shipmentsApi.getAll();

// Create a new shipment
const { data: newShipment } = await shipmentsApi.create({
  origin: 'New York, NY',
  destination: 'Los Angeles, CA',
  weight: 500,
  customerName: 'John Doe'
});

// Track a shipment (public)
const { data: tracking } = await trackingApi.track('FH-2024-001');
```

## 🧪 Testing the API

### Using cURL

**Login:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@fleethub.com","password":"password123"}'
```

**Get Shipments (with token):**
```bash
curl http://localhost:8080/api/shipments \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Track Shipment (public):**
```bash
curl http://localhost:8080/api/track/FH-2024-001
```

**Create Shipment:**
```bash
curl -X POST http://localhost:8080/api/shipments \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "origin": "Boston, MA",
    "destination": "Miami, FL",
    "weight": 350,
    "customerName": "Jane Smith",
    "customerPhone": "+1 555-1234"
  }'
```

## 🔒 Security Features

- **JWT Authentication**: Stateless token-based auth
- **Role-Based Access Control**: Admin, Driver, Customer roles
- **Password Encryption**: BCrypt hashing
- **CORS Configuration**: Configurable allowed origins
- **Method-Level Security**: `@PreAuthorize` annotations

## 📊 Database Schema

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│    users     │     │   drivers    │     │    trucks    │
├──────────────┤     ├──────────────┤     ├──────────────┤
│ id           │◄────│ user_id (FK) │     │ id           │
│ name         │     │ id           │◄────│ assigned_    │
│ email        │     │ name         │     │ driver_id(FK)│
│ password     │     │ email        │     │ license_plate│
│ role         │     │ phone        │     │ model        │
│ is_active    │     │ license_num  │     │ capacity     │
│ created_at   │     │ status       │     │ status       │
│ updated_at   │     │ total_       │     │ mileage      │
└──────────────┘     │ deliveries   │     │ gps_coords   │
                     │ assigned_    │     └──────────────┘
                     │ truck_id(FK) │
                     └──────────────┘
                            │
                            ▼
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│  shipments   │     │  shipment_   │     │  warehouses  │
├──────────────┤     │  timeline    │     ├──────────────┤
│ id           │◄────├──────────────┤     │ id           │
│ tracking_num │     │ id           │     │ name         │
│ origin       │     │ shipment_id  │     │ location     │
│ destination  │     │ status       │     │ address      │
│ status       │     │ location     │     │ capacity     │
│ weight       │     │ timestamp    │     │ inventory    │
│ driver_id(FK)│     │ notes        │     │ manager      │
│ truck_id(FK) │     └──────────────┘     │ phone        │
│ warehouse_id │                          │ gps_coords   │
│ customer_*   │                          └──────────────┘
│ est_delivery │
│ timestamps   │
└──────────────┘
```

## 📝 API Response Format

All API responses follow this structure:
```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... },
  "timestamp": "2024-01-15T10:30:00Z",
  "error": null
}
```

Error responses:
```json
{
  "success": false,
  "message": null,
  "data": null,
  "timestamp": "2024-01-15T10:30:00Z",
  "error": "Resource not found"
}
```

## 📄 License

MIT License - see LICENSE file for details
