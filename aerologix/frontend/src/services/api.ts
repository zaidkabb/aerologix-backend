// API Configuration and Services for connecting to Spring Boot Backend
const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

// Types
export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data: T;
  timestamp: string;
  error?: string;
}

// Get auth token from localStorage
const getAuthToken = (): string | null => {
  const user = localStorage.getItem('fleet_user');
  if (user) {
    try {
      const parsed = JSON.parse(user);
      return parsed.token;
    } catch {
      return null;
    }
  }
  return null;
};

// Base fetch function with auth
const apiFetch = async <T>(
  endpoint: string,
  options: RequestInit = {}
): Promise<ApiResponse<T>> => {
  const token = getAuthToken();
  
  const headers: HeadersInit = {
    'Content-Type': 'application/json',
    ...options.headers,
  };

  if (token) {
    (headers as Record<string, string>)['Authorization'] = `Bearer ${token}`;
  }

  const response = await fetch(`${API_BASE_URL}${endpoint}`, {
    ...options,
    headers,
  });

  if (!response.ok) {
    const error = await response.json().catch(() => ({ message: 'Network error' }));
    throw new Error(error.error || error.message || 'Request failed');
  }

  return response.json();
};

// Auth API
export const authApi = {
  login: (email: string, password: string) =>
    apiFetch<{
      id: number;
      email: string;
      name: string;
      role: 'ADMIN' | 'DRIVER' | 'CUSTOMER';
      token: string;
    }>('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, password }),
    }),

  register: (data: { name: string; email: string; password: string; role?: string }) =>
    apiFetch<{
      id: number;
      email: string;
      name: string;
      role: 'ADMIN' | 'DRIVER' | 'CUSTOMER';
      token: string;
    }>('/auth/register', {
      method: 'POST',
      body: JSON.stringify(data),
    }),
};

// Shipments API
export const shipmentsApi = {
  getAll: () =>
    apiFetch<Shipment[]>('/shipments'),

  getById: (id: number) =>
    apiFetch<Shipment>(`/shipments/${id}`),

  getByTracking: (trackingNumber: string) =>
    apiFetch<Shipment>(`/shipments/tracking/${trackingNumber}`),

  create: (data: CreateShipmentRequest) =>
    apiFetch<Shipment>('/shipments', {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  update: (id: number, data: UpdateShipmentRequest) =>
    apiFetch<Shipment>(`/shipments/${id}`, {
      method: 'PUT',
      body: JSON.stringify(data),
    }),

  assignDriver: (shipmentId: number, driverId: number) =>
    apiFetch<Shipment>(`/shipments/${shipmentId}/assign`, {
      method: 'POST',
      body: JSON.stringify({ driverId }),
    }),

  markDelivered: (id: number) =>
    apiFetch<Shipment>(`/shipments/${id}/deliver`, {
      method: 'POST',
    }),

  delete: (id: number) =>
    apiFetch<void>(`/shipments/${id}`, {
      method: 'DELETE',
    }),

  getByDriver: (driverId: number) =>
    apiFetch<Shipment[]>(`/shipments/driver/${driverId}`),

  getActiveByDriver: (driverId: number) =>
    apiFetch<Shipment[]>(`/shipments/driver/${driverId}/active`),
};

// Tracking API (Public)
export const trackingApi = {
  track: (trackingNumber: string) =>
    apiFetch<TrackingResponse>(`/track/${trackingNumber}`),
};

// Trucks API
export const trucksApi = {
  getAll: () =>
    apiFetch<Truck[]>('/trucks'),

  getById: (id: number) =>
    apiFetch<Truck>(`/trucks/${id}`),

  getAvailable: () =>
    apiFetch<Truck[]>('/trucks/available'),

  getActive: () =>
    apiFetch<Truck[]>('/trucks/active'),

  create: (data: CreateTruckRequest) =>
    apiFetch<Truck>('/trucks', {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  update: (id: number, data: UpdateTruckRequest) =>
    apiFetch<Truck>(`/trucks/${id}`, {
      method: 'PUT',
      body: JSON.stringify(data),
    }),

  updateLocation: (id: number, data: LocationUpdate) =>
    apiFetch<Truck>(`/trucks/${id}/location`, {
      method: 'PUT',
      body: JSON.stringify(data),
    }),

  delete: (id: number) =>
    apiFetch<void>(`/trucks/${id}`, {
      method: 'DELETE',
    }),
};

// Drivers API
export const driversApi = {
  getAll: () =>
    apiFetch<Driver[]>('/drivers'),

  getById: (id: number) =>
    apiFetch<Driver>(`/drivers/${id}`),

  getAvailable: () =>
    apiFetch<Driver[]>('/drivers/available'),

  create: (data: CreateDriverRequest) =>
    apiFetch<Driver>('/drivers', {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  update: (id: number, data: UpdateDriverRequest) =>
    apiFetch<Driver>(`/drivers/${id}`, {
      method: 'PUT',
      body: JSON.stringify(data),
    }),

  assignTruck: (driverId: number, truckId: number) =>
    apiFetch<Driver>(`/drivers/${driverId}/assign-truck`, {
      method: 'POST',
      body: JSON.stringify({ truckId }),
    }),

  unassignTruck: (driverId: number) =>
    apiFetch<Driver>(`/drivers/${driverId}/unassign-truck`, {
      method: 'POST',
    }),

  delete: (id: number) =>
    apiFetch<void>(`/drivers/${id}`, {
      method: 'DELETE',
    }),
};

// Warehouses API
export const warehousesApi = {
  getAll: () =>
    apiFetch<Warehouse[]>('/warehouses'),

  getById: (id: number) =>
    apiFetch<Warehouse>(`/warehouses/${id}`),

  create: (data: CreateWarehouseRequest) =>
    apiFetch<Warehouse>('/warehouses', {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  update: (id: number, data: UpdateWarehouseRequest) =>
    apiFetch<Warehouse>(`/warehouses/${id}`, {
      method: 'PUT',
      body: JSON.stringify(data),
    }),

  updateInventory: (id: number, changeAmount: number) =>
    apiFetch<Warehouse>(`/warehouses/${id}/inventory`, {
      method: 'PUT',
      body: JSON.stringify({ changeAmount }),
    }),

  delete: (id: number) =>
    apiFetch<void>(`/warehouses/${id}`, {
      method: 'DELETE',
    }),
};

// Dashboard API
export const dashboardApi = {
  getStats: () =>
    apiFetch<DashboardStats>('/dashboard'),
};

// Type definitions
export interface Shipment {
  id: number;
  trackingNumber: string;
  origin: string;
  destination: string;
  status: 'PENDING' | 'PICKED_UP' | 'IN_TRANSIT' | 'OUT_FOR_DELIVERY' | 'DELIVERED' | 'CANCELLED';
  weight: number;
  driverId?: number;
  driverName?: string;
  truckId?: number;
  truckLicensePlate?: string;
  customerName?: string;
  customerPhone?: string;
  customerEmail?: string;
  notes?: string;
  estimatedDelivery: string;
  actualDelivery?: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateShipmentRequest {
  origin: string;
  destination: string;
  weight: number;
  customerName?: string;
  customerPhone?: string;
  customerEmail?: string;
  notes?: string;
  estimatedDelivery?: string;
  warehouseId?: number;
}

export interface UpdateShipmentRequest {
  origin?: string;
  destination?: string;
  weight?: number;
  status?: Shipment['status'];
  customerName?: string;
  customerPhone?: string;
  customerEmail?: string;
  notes?: string;
  estimatedDelivery?: string;
}

export interface TrackingResponse {
  trackingNumber: string;
  status: Shipment['status'];
  origin: string;
  destination: string;
  estimatedDelivery: string;
  weight: number;
  timeline: {
    status: Shipment['status'];
    location: string;
    timestamp: string;
    completed: boolean;
  }[];
}

export interface Truck {
  id: number;
  licensePlate: string;
  model: string;
  capacity: number;
  status: 'AVAILABLE' | 'IN_USE' | 'MAINTENANCE';
  mileage: number;
  lastService?: string;
  nextService?: string;
  currentLatitude?: number;
  currentLongitude?: number;
  currentSpeed?: number;
  heading?: string;
  lastLocationUpdate?: string;
  assignedDriverId?: number;
  assignedDriverName?: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateTruckRequest {
  licensePlate: string;
  model: string;
  capacity: number;
  status?: Truck['status'];
}

export interface UpdateTruckRequest {
  licensePlate?: string;
  model?: string;
  capacity?: number;
  status?: Truck['status'];
  mileage?: number;
  lastService?: string;
}

export interface LocationUpdate {
  latitude: number;
  longitude: number;
  speed: number;
  heading: string;
}

export interface Driver {
  id: number;
  name: string;
  email: string;
  phone: string;
  licenseNumber: string;
  status: 'AVAILABLE' | 'ON_DELIVERY' | 'OFF_DUTY';
  assignedTruckId?: number;
  assignedTruckPlate?: string;
  totalDeliveries: number;
  createdAt: string;
  updatedAt: string;
}

export interface CreateDriverRequest {
  name: string;
  email: string;
  phone: string;
  licenseNumber: string;
}

export interface UpdateDriverRequest {
  name?: string;
  email?: string;
  phone?: string;
  licenseNumber?: string;
  status?: Driver['status'];
}

export interface Warehouse {
  id: number;
  name: string;
  location: string;
  address: string;
  capacity: number;
  currentInventory: number;
  capacityUsagePercentage: number;
  manager: string;
  phone: string;
  latitude?: number;
  longitude?: number;
  createdAt: string;
  updatedAt: string;
}

export interface CreateWarehouseRequest {
  name: string;
  location: string;
  address: string;
  capacity: number;
  manager: string;
  phone: string;
  latitude?: number;
  longitude?: number;
}

export interface UpdateWarehouseRequest {
  name?: string;
  location?: string;
  address?: string;
  capacity?: number;
  manager?: string;
  phone?: string;
  latitude?: number;
  longitude?: number;
}

export interface DashboardStats {
  totalShipments: number;
  activeShipments: number;
  deliveredShipments: number;
  pendingShipments: number;
  totalTrucks: number;
  availableTrucks: number;
  trucksInUse: number;
  trucksInMaintenance: number;
  totalDrivers: number;
  availableDrivers: number;
  driversOnDelivery: number;
  driversOffDuty: number;
  totalWarehouses: number;
  totalWarehouseCapacity: number;
  totalInventory: number;
  deliverySuccessRate: number;
  averageDeliveryTime: number;
  weeklyDeliveries: {
    day: string;
    deliveries: number;
    pending: number;
  }[];
  recentShipments: Shipment[];
}
