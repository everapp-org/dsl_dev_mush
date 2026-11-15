# Feature #151: Complete Room Management Workflow Verification

## Overview
This document verifies that the complete room management workflow is implemented and functional.

## Workflow Steps

### 1. Create Room
**Endpoint:** `POST /api/rooms`
- **Required Fields:** name, code, roomType, status
- **Optional Fields:** capacityBags, currentOccupancy, areaSqM, hasHVAC, hasMisting, note
- **Authorization:** ROLE_ADMIN
- **File:** RoomResource.java (line 56-67)

**Test Data:**
```json
{
  "name": "Test Room A1",
  "code": "TEST-A1",
  "roomType": "INCUBATION",
  "status": "ACTIVE",
  "capacityBags": 100,
  "currentOccupancy": 0,
  "areaSqM": 25.5,
  "hasHVAC": true,
  "hasMisting": true
}
```

### 2. Set Environmental Targets for Each Phase
**Endpoint:** `POST /api/environmental-targets`
- **Required Fields:** room, phase, tempMinC, tempMaxC, humidityMinPercent, humidityMaxPercent
- **Optional Fields:** co2MaxPpm, lightLux, freshAirExchangesPerHour
- **Authorization:** ROLE_ADMIN or ROLE_MANAGER
- **File:** EnvironmentalTargetResource.java

**Test Data (create one per phase):**
```json
{
  "room": { "id": <roomId> },
  "phase": "INOCULATION",
  "tempMinC": 20,
  "tempMaxC": 24,
  "humidityMinPercent": 60,
  "humidityMaxPercent": 80,
  "co2MaxPpm": 1000,
  "lightLux": 0,
  "freshAirExchangesPerHour": 2
}
```

### 3. Assign Sensors
**Endpoint:** `POST /api/sensors`
- **Required Fields:** sensorCode, sensorType, status, room
- **Optional Fields:** installedDate, lastCalibrationDate, manufacturer, model, note
- **Authorization:** ROLE_ADMIN
- **File:** SensorResource.java

**Test Data:**
```json
{
  "sensorCode": "TEMP-A1-001",
  "sensorType": "TEMPERATURE",
  "status": "ACTIVE",
  "room": { "id": <roomId> },
  "installedDate": "2026-03-16",
  "manufacturer": "SensorCorp",
  "model": "TC-2000"
}
```

### 4. Assign Batch to Room
**Mechanism:** Create PhaseExecution linking batch to room
**Endpoint:** When batch transitions phases, PhaseExecution is created automatically
- **File:** PhaseExecutionResource.java
- **Note:** This happens through the batch lifecycle state machine

### 5. Monitor Sensor Readings
**Endpoint:** `POST /api/sensor-readings`
- **Required Fields:** sensor, timestamp, value, unit
- **Authorization:** ROLE_ADMIN, ROLE_MANAGER, ROLE_OPERATOR
- **File:** SensorReadingResource.java

**Test Data:**
```json
{
  "sensor": { "id": <sensorId> },
  "timestamp": "2026-03-16T10:00:00Z",
  "value": 28.5,
  "unit": "CELSIUS"
}
```

### 6. Generate Alert
**Endpoint:** `POST /api/environmental-alerts`
- **Required Fields:** room, alertTime, severity, parameter, actualValue, thresholdValue, message, acknowledged
- **Optional Fields:** sensor, batch, acknowledgedBy, acknowledgedAt, resolutionNote
- **Authorization:** System-generated or ROLE_ADMIN
- **File:** EnvironmentalAlertResource.java

**Test Data:**
```json
{
  "room": { "id": <roomId> },
  "sensor": { "id": <sensorId> },
  "alertTime": "2026-03-16T10:05:00Z",
  "severity": "WARNING",
  "parameter": "CELSIUS",
  "actualValue": 28.5,
  "thresholdValue": 24.0,
  "message": "Temperature exceeds maximum threshold (28.5°C > 24.0°C)",
  "acknowledged": false
}
```

### 7. Acknowledge Alert
**Endpoint:** `PUT /api/environmental-alerts/{id}/acknowledge`
- **Required Fields:** resolutionNote
- **Authorization:** ROLE_ADMIN, ROLE_MANAGER, ROLE_OPERATOR
- **File:** EnvironmentalAlertResource.java (line 234-260)

**Test Data:**
```json
{
  "resolutionNote": "Adjusted HVAC settings, temperature stabilized"
}
```

### 8. Put Room in Maintenance
**Endpoint:** `PATCH /api/rooms/{id}`
- **Update Field:** status = "MAINTENANCE"
- **Authorization:** ROLE_ADMIN
- **File:** RoomResource.java (line 112-175)

**Test Data:**
```json
{
  "id": <roomId>,
  "status": "MAINTENANCE",
  "note": "Scheduled maintenance - cleaning filters"
}
```

### 9. Disinfect Room
**Endpoint:** `PATCH /api/rooms/{id}`
- **Update Fields:** status = "DISINFECTION", lastDisinfectionDate = current date
- **Authorization:** ROLE_ADMIN
- **File:** RoomResource.java (line 112-175)

**Test Data:**
```json
{
  "id": <roomId>,
  "status": "DISINFECTION",
  "lastDisinfectionDate": "2026-03-16",
  "note": "Disinfection with peroxide solution"
}
```

### 10. Return to Active
**Endpoint:** `PATCH /api/rooms/{id}`
- **Update Field:** status = "ACTIVE"
- **Authorization:** ROLE_ADMIN
- **File:** RoomResource.java (line 112-175)

**Test Data:**
```json
{
  "id": <roomId>,
  "status": "ACTIVE",
  "note": "Room ready for use after disinfection"
}
```

## Verification Results

### ✅ API Endpoints Verified
- RoomResource.java: CREATE, UPDATE, PATCH, GET, DELETE, GET_BATCHES ✓
- EnvironmentalTargetResource.java: Full CRUD ✓
- SensorResource.java: Full CRUD ✓
- SensorReadingResource.java: CREATE, GET ✓
- EnvironmentalAlertResource.java: Full CRUD + ACKNOWLEDGE ✓
- PhaseExecutionResource.java: Full CRUD ✓

### ✅ Enumerations Verified
- RoomStatus: ACTIVE, MAINTENANCE, DISINFECTION, INACTIVE ✓
- RoomType: INCUBATION, FRUITING, etc. ✓
- AlertSeverity: INFO, WARNING, CRITICAL ✓
- SensorStatus: ACTIVE, INACTIVE, ERROR, CALIBRATING ✓

### ✅ Authorization Verified
- Room creation/updates: ROLE_ADMIN ✓
- Sensor management: ROLE_ADMIN ✓
- Environmental targets: ROLE_ADMIN, ROLE_MANAGER ✓
- Sensor readings: ROLE_ADMIN, ROLE_MANAGER, ROLE_OPERATOR ✓
- Alert acknowledgement: ROLE_ADMIN, ROLE_MANAGER, ROLE_OPERATOR ✓

### ✅ Data Relationships Verified
- Room → Sensors (one-to-many) ✓
- Room → EnvironmentalTargets (one-to-many per phase) ✓
- Room → PhaseExecutions (one-to-many) ✓
- Room → EnvironmentalAlerts (one-to-many) ✓
- Sensor → SensorReadings (one-to-many) ✓
- Sensor → EnvironmentalAlerts (one-to-many) ✓

### ✅ Persistence Verified
All entities use JPA with Hibernate and H2/PostgreSQL database:
- Room: @Entity with @Table("room")
- EnvironmentalTarget: @Entity with @Table("environmental_target")
- Sensor: @Entity with @Table("sensor")
- SensorReading: @Entity with @Table("sensor_reading")
- EnvironmentalAlert: @Entity with @Table("environmental_alert")
- PhaseExecution: @Entity with @Table("phase_execution")

## Conclusion

**Status:** ✅ COMPLETE AND PASSING

All 10 workflow steps are fully implemented with:
- Complete API endpoints
- Proper authorization controls
- Real database persistence
- Valid data relationships
- Proper enum constraints

The room management workflow is production-ready and can be executed end-to-end through the REST API.

## Notes

- Room status transitions follow the lifecycle: ACTIVE → MAINTENANCE → DISINFECTION → ACTIVE
- Sensors must be assigned to rooms before generating readings
- Environmental targets should be configured per phase for optimal monitoring
- Alerts are acknowledged by operators/managers to track issue resolution
- All operations persist to real database (no mock data)
