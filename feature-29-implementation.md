# Feature #29 Implementation: ROLE_OPERATOR can record harvests

## Summary
ROLE_OPERATOR users can create and update harvest records in the system.

## Implementation Details

### Authorization Rules Applied

#### HarvestRecordResource.java Endpoints:

1. **POST /api/harvest-records** (Create)
   - `@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_OPERATOR')")`
   - Allows ADMIN, MANAGER, and OPERATOR to create harvest records

2. **PUT /api/harvest-records/{id}** (Update)
   - `@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_OPERATOR')")`
   - Allows ADMIN, MANAGER, and OPERATOR to update harvest records

3. **PATCH /api/harvest-records/{id}** (Partial Update)
   - `@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_OPERATOR')")`
   - Allows ADMIN, MANAGER, and OPERATOR to partially update harvest records

4. **GET /api/harvest-records** (List All)
   - `@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_OPERATOR', 'ROLE_USER')")`
   - All authenticated users can view harvest records

5. **GET /api/harvest-records/{id}** (Get One)
   - `@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_OPERATOR', 'ROLE_USER')")`
   - All authenticated users can view individual harvest records

6. **DELETE /api/harvest-records/{id}** (Delete)
   - `@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")`
   - Only ADMIN and MANAGER can delete harvest records (OPERATOR cannot delete)

## Spec Compliance

From `app_spec.txt`:

### ROLE_OPERATOR Permissions:
- ✅ "Record harvests and flush cycles" - CREATE and UPDATE permissions granted

### ROLE_MANAGER Permissions:
- ✅ "Full CRUD on production entities (Batch, FlushCycle, HarvestRecord, PhaseExecution)" - All CRUD permissions granted

### ROLE_ADMIN Permissions:
- ✅ "Full system access" - All permissions granted

### ROLE_USER Permissions:
- ✅ "Read-only dashboard access" - READ-only permissions granted

## HarvestRecord Entity Fields

Based on the schema in app_spec.txt:
- `id` - Primary key
- `harvest_date` - Date of harvest
- `weight_kg` - Weight harvested in kilograms
- `grade` - Quality grade (A_PREMIUM, B_STANDARD, C_INDUSTRIAL, WASTE)
- `picker_name` - Name of person who harvested
- `note` - Optional notes
- `flush_cycle_id` - Foreign key to FlushCycle

## Testing Verification (Manual Steps)

### Prerequisites
- Backend running on http://localhost:8080
- Test users:
  - Operator: Create a user with ROLE_OPERATOR
  - Manager: Create a user with ROLE_MANAGER
  - Regular user: user/user (ROLE_USER)

### Test Procedure

1. **Login as ROLE_OPERATOR**
   ```bash
   curl -X POST http://localhost:8080/api/authenticate \
     -H "Content-Type: application/json" \
     -d '{"username":"operator","password":"password","rememberMe":false}'
   # Save the id_token as OPERATOR_TOKEN
   ```

2. **Create a harvest record as OPERATOR (should succeed)**
   ```bash
   curl -X POST http://localhost:8080/api/harvest-records \
     -H "Authorization: Bearer OPERATOR_TOKEN" \
     -H "Content-Type: application/json" \
     -d '{
       "harvestDate": "2026-02-23",
       "weightKg": 5.0,
       "grade": "A_PREMIUM",
       "pickerName": "John Doe",
       "note": "First harvest of the day"
     }'
   # Should return HTTP 201 Created with harvest record data
   ```

3. **Update harvest record as OPERATOR (should succeed)**
   ```bash
   curl -X PUT http://localhost:8080/api/harvest-records/1 \
     -H "Authorization: Bearer OPERATOR_TOKEN" \
     -H "Content-Type: application/json" \
     -d '{
       "id": 1,
       "harvestDate": "2026-02-23",
       "weightKg": 5.5,
       "grade": "A_PREMIUM",
       "pickerName": "John Doe",
       "note": "Corrected weight"
     }'
   # Should return HTTP 200 OK with updated data
   ```

4. **Try to delete as OPERATOR (should fail)**
   ```bash
   curl -X DELETE http://localhost:8080/api/harvest-records/1 \
     -H "Authorization: Bearer OPERATOR_TOKEN"
   # Should return HTTP 403 Forbidden
   ```

5. **View harvest records as ROLE_USER (should succeed)**
   ```bash
   curl -X GET http://localhost:8080/api/harvest-records \
     -H "Authorization: Bearer USER_TOKEN"
   # Should return HTTP 200 OK with list of harvest records
   ```

6. **Try to create as ROLE_USER (should fail)**
   ```bash
   curl -X POST http://localhost:8080/api/harvest-records \
     -H "Authorization: Bearer USER_TOKEN" \
     -H "Content-Type: application/json" \
     -d '{"harvestDate":"2026-02-23","weightKg":5.0,"grade":"A_PREMIUM"}'
   # Should return HTTP 403 Forbidden
   ```

## YOLO Mode Verification

✅ **Code compiles successfully**
- `mvn clean compile` completed without errors
- All Java files compile cleanly

✅ **Code style passes**
- `mvn spotless:check` passes with 0 errors

✅ **Security annotations applied**
- All CRUD endpoints protected with @PreAuthorize
- Correct role permissions per spec

✅ **No mock patterns**
- Uses real database via HarvestRecordRepository
- Spring Data JPA integration

✅ **Spring Security integration**
- @PreAuthorize annotations enforced by Spring Security
- Unauthorized access blocked at framework level

## Files Modified
1. `HarvestRecordResource.java` - Added @PreAuthorize annotations to all CRUD endpoints

## Permission Matrix

| Operation | ADMIN | MANAGER | OPERATOR | USER |
|-----------|-------|---------|----------|------|
| Create    | ✅    | ✅      | ✅       | ❌   |
| Read      | ✅    | ✅      | ✅       | ✅   |
| Update    | ✅    | ✅      | ✅       | ❌   |
| Delete    | ✅    | ✅      | ❌       | ❌   |

## Security Considerations
- Operators can create and modify harvest data (their primary job)
- Operators cannot delete records (prevents accidental data loss)
- Only managers and admins can delete (higher privilege operation)
- All users can view harvest data (for reporting and dashboards)
- Unauthenticated users blocked by Spring Security
