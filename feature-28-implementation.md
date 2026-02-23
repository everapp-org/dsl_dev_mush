# Feature #28 Implementation: Session invalidated on password change

## Summary
After a user changes their password, all previously issued JWT tokens become invalid, forcing re-authentication.

## Implementation Details

### 1. Database Schema Change
- **File**: `User.java`
- **Change**: Added `passwordChangedAt` field (Instant) to track when password was last changed
- **Migration**: `20260223140000_add_password_changed_at_to_user.xml`
  - Adds `password_changed_at` column to `jhi_user` table
  - Nullable timestamp field

### 2. User Entity Updates
- **File**: `com.mcms.domain.User`
- Added field: `private Instant passwordChangedAt = null;`
- Added getter/setter methods

### 3. Password Change Logic
- **File**: `com.mcms.service.UserService.changePassword()`
- **Change**: Sets `passwordChangedAt` to `Instant.now()` when password is changed
- This timestamp is used to invalidate old tokens

### 4. JWT Token Enhancement
- **File**: `com.mcms.security.SecurityUtils`
- Added constant: `PASSWORD_CHANGED_AT_CLAIM = "pwdChangedAt"`

- **File**: `com.mcms.security.DomainUserDetailsService.UserWithId`
- Extended to include `passwordChangedAt` field
- Constructor updated to accept and store passwordChangedAt from User entity

- **File**: `com.mcms.web.rest.AuthenticateController.createToken()`
- When creating JWT, adds `pwdChangedAt` claim with epoch milliseconds timestamp
- Claim is only added if user has a passwordChangedAt value (non-null)

### 5. Token Validation
- **File**: `com.mcms.config.SecurityJwtConfiguration.jwtDecoder()`
- **Custom Validator**: After standard JWT validation, checks passwordChangedAt
- Validation logic:
  1. Extract `userId` and `pwdChangedAt` claims from JWT
  2. Fetch user from database
  3. Compare token's `pwdChangedAt` with current user's `passwordChangedAt`
  4. If user's timestamp is AFTER token's timestamp → reject with JwtException
  5. Log warning: "JWT rejected: password changed after token was issued"

## How It Works

### Normal Flow (No Password Change)
1. User logs in → gets JWT with `pwdChangedAt: null` (or old timestamp)
2. User makes API calls → JWT validates successfully
3. Token remains valid until expiry

### Password Change Flow
1. User logs in → gets JWT_A with `pwdChangedAt: 1234567890` (timestamp from DB)
2. User changes password → `User.passwordChangedAt` set to `Instant.now()` (e.g., 1234567999)
3. User tries to use JWT_A → validation fails because:
   - JWT_A has `pwdChangedAt: 1234567890`
   - Database has `passwordChangedAt: 1234567999`
   - 1234567999 > 1234567890 → token invalidated
4. User must login again → gets JWT_B with new `pwdChangedAt: 1234567999`
5. JWT_B now validates successfully

## Testing Verification (Manual Steps)

### Prerequisites
- Backend running on http://localhost:8080
- Test user: admin/admin

### Test Procedure
1. **Login and get first token**
   ```bash
   curl -X POST http://localhost:8080/api/authenticate \
     -H "Content-Type: application/json" \
     -d '{"username":"admin","password":"admin","rememberMe":false}'
   # Save the id_token as TOKEN1
   ```

2. **Verify first token works**
   ```bash
   curl -X GET http://localhost:8080/api/account \
     -H "Authorization: Bearer TOKEN1"
   # Should return HTTP 200 with user data
   ```

3. **Change password**
   ```bash
   curl -X POST http://localhost:8080/api/account/change-password \
     -H "Authorization: Bearer TOKEN1" \
     -H "Content-Type: application/json" \
     -d '{"currentPassword":"admin","newPassword":"newpass123"}'
   # Should return HTTP 200
   ```

4. **Try to use old token (should fail)**
   ```bash
   curl -X GET http://localhost:8080/api/account \
     -H "Authorization: Bearer TOKEN1"
   # Should return HTTP 401 Unauthorized
   ```

5. **Login with new password**
   ```bash
   curl -X POST http://localhost:8080/api/authenticate \
     -H "Content-Type: application/json" \
     -d '{"username":"admin","password":"newpass123","rememberMe":false}'
   # Should return HTTP 200 with new id_token (TOKEN2)
   ```

6. **Restore password for next tests**
   ```bash
   curl -X POST http://localhost:8080/api/account/change-password \
     -H "Authorization: Bearer TOKEN2" \
     -H "Content-Type: application/json" \
     -d '{"currentPassword":"newpass123","newPassword":"admin"}'
   ```

## YOLO Mode Verification

✅ **Code compiles successfully**
- `mvn clean compile` completed without errors
- All Java files compile cleanly

✅ **Code style passes**
- `mvn spotless:check` passes with 0 errors

✅ **Liquibase migration registered**
- Migration file created and added to master.xml

✅ **No mock patterns**
- Implementation uses real database queries
- UserRepository injected and used properly

✅ **Security best practices followed**
- JWT validation happens at Spring Security layer
- No bypassing of standard validation
- Proper exception handling

## Files Modified
1. `User.java` - Added passwordChangedAt field
2. `UserService.java` - Updated changePassword() to set timestamp
3. `SecurityUtils.java` - Added PASSWORD_CHANGED_AT_CLAIM constant
4. `DomainUserDetailsService.java` - Extended UserWithId to include passwordChangedAt
5. `AuthenticateController.java` - Added pwdChangedAt claim to JWT
6. `SecurityJwtConfiguration.java` - Added custom validation logic
7. `20260223140000_add_password_changed_at_to_user.xml` - Database migration
8. `master.xml` - Registered new migration

## Security Considerations
- Old tokens cannot be reused after password change
- Prevents session hijacking after password reset
- Validates on every request (no caching issues)
- Database is source of truth for password change timestamp
- Works across multiple servers (no in-memory state)
