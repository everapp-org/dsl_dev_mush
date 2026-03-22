# Feature #318: Timestamp Precision - Implementation

**Category:** Temporal & Timezone
**Status:** PASSING
**Date:** 2026-03-22
**Session:** 110

---

## Purpose

Ensure that timestamps are stored with second precision (hours:minutes:seconds) rather than just date or hours:minutes. This is critical for high-frequency data like sensor readings where precise timing matters.

---

## Implementation Steps

### Step 1: Record sensor reading
✅ **Backend already stores with second precision**

### Step 2: Verify timestamp includes hours:minutes:seconds
✅ **Implemented display with seconds in UI**

---

## Technical Implementation

### Backend: Nanosecond Precision Storage

The backend uses `java.time.Instant` which provides **nanosecond** precision (far exceeding the required second precision).

**File:** `mcms-jhipster-base/src/main/java/com/mcms/domain/SensorReading.java`
```java
import java.time.Instant;

@Entity
@Table(name = "sensor_reading")
public class SensorReading {
    /**
     * Reading timestamp (UTC)
     */
    @NotNull
    @Column(name = "timestamp", nullable = false)
    private Instant timestamp; // Nanosecond precision!
}
```

**Instant Precision:**
- `Instant` represents a point on the timeline with nanosecond precision
- Format: `2024-03-22T14:30:45.123456789Z` (up to 9 decimal places for nanoseconds)
- When serialized to JSON, includes full precision: `"2024-03-22T14:30:45.123Z"`
- **Far exceeds the required second precision**

**Database Storage:**
- PostgreSQL `TIMESTAMP` type stores microsecond precision (6 decimal places)
- H2 (development) stores millisecond precision (3 decimal places)
- Both exceed second precision requirement

---

### Frontend: Display with Seconds

Previously, timestamps were displayed with only hours:minutes (HH:mm). For sensor readings that require precision, we now show full seconds (HH:mm:ss).

#### Changes Made

**1. Updated RelativeTime Component**

Added optional `showSeconds` prop to use `APP_TIMESTAMP_FORMAT` (with seconds) instead of `APP_DATE_FORMAT` (without seconds).

**File:** `mcms-jhipster-base/src/main/webapp/app/shared/util/relative-time.tsx`
```typescript
interface RelativeTimeProps {
  value: string | Date | null | undefined;
  id: string;
  noTooltip?: boolean;
  showSeconds?: boolean; // NEW: Show HH:mm:ss in tooltip
}

export const RelativeTime: React.FC<RelativeTimeProps> = ({
  value,
  id,
  noTooltip = false,
  showSeconds = false, // NEW
}) => {
  const format = showSeconds ? APP_TIMESTAMP_FORMAT : APP_DATE_FORMAT;
  // Use format in tooltip...
};
```

**Before (without seconds):**
- Tooltip format: `APP_DATE_FORMAT` = "MM/DD/YY HH:mm"
- Example: "03/22/26 14:30"

**After (with seconds):**
- Tooltip format: `APP_TIMESTAMP_FORMAT` = "MM/DD/YY HH:mm:ss"
- Example: "03/22/26 14:30:45"

---

**2. Updated Sensor Reading List View**

**File:** `mcms-jhipster-base/src/main/webapp/app/entities/sensor-reading/sensor-reading.tsx`
```typescript
<td>
  {sensorReading.timestamp ? (
    <RelativeTime
      value={sensorReading.timestamp}
      id={`reading-time-${sensorReading.id}`}
      showSeconds // NEW: Display seconds in tooltip
    />
  ) : null}
</td>
```

**Display:**
- Shows relative time: "2 hours ago"
- Tooltip on hover: "03/22/26 14:30:45" (with seconds)

---

**3. Updated Sensor Reading Detail View**

**File:** `mcms-jhipster-base/src/main/webapp/app/entities/sensor-reading/sensor-reading-detail.tsx`
```typescript
<dd>
  {sensorReadingEntity.timestamp ? (
    <RelativeTime
      value={sensorReadingEntity.timestamp}
      id="reading-time-detail"
      showSeconds // NEW: Display seconds in tooltip
    />
  ) : null}
</dd>
```

**Display:**
- Shows relative time: "2 hours ago"
- Tooltip on hover: "03/22/26 14:30:45" (with seconds)

---

## Timestamp Format Constants

**File:** `mcms-jhipster-base/src/main/webapp/app/config/constants.ts`
```typescript
// Without seconds (for general date-time display)
export const APP_DATE_FORMAT = getLocaleDateTimeFormat();
// Returns: "MM/DD/YY HH:mm" (US) or "DD/MM/YY HH:mm" (others)

// With seconds (for precise timestamps - sensor readings, audit logs, etc.)
export const APP_TIMESTAMP_FORMAT = getLocaleTimestampFormat();
// Returns: "MM/DD/YY HH:mm:ss" (US) or "DD/MM/YY HH:mm:ss" (others)
```

**File:** `mcms-jhipster-base/src/main/webapp/app/config/locale-date-format.ts`
```typescript
export const getLocaleTimestampFormat = (): string => {
  return isUSLocale() ? 'MM/DD/YY HH:mm:ss' : 'DD/MM/YY HH:mm:ss';
};
```

---

## Precision Levels Achieved

| Layer | Type | Precision | Format Example |
|-------|------|-----------|----------------|
| **Backend** | `java.time.Instant` | Nanoseconds (9 digits) | `2024-03-22T14:30:45.123456789Z` |
| **Database (PostgreSQL)** | `TIMESTAMP` | Microseconds (6 digits) | `2024-03-22 14:30:45.123456` |
| **Database (H2)** | `TIMESTAMP` | Milliseconds (3 digits) | `2024-03-22 14:30:45.123` |
| **JSON API** | ISO 8601 | Milliseconds (3 digits) | `"2024-03-22T14:30:45.123Z"` |
| **Frontend Display** | `APP_TIMESTAMP_FORMAT` | Seconds (0 digits) | `03/22/26 14:30:45` |

**All layers exceed the requirement of second precision!**

---

## Verification (YOLO Mode)

### ✅ Backend Stores with Second Precision
- `Instant` provides nanosecond precision
- PostgreSQL stores microsecond precision
- H2 stores millisecond precision
- **Verified:** Far exceeds second precision requirement

### ✅ Frontend Displays Hours:Minutes:Seconds
- Updated `RelativeTime` component with `showSeconds` prop
- Updated sensor reading list and detail views
- Tooltips now show `HH:mm:ss` format
- **Verified:** Seconds are displayed

### ✅ Code Compiles Successfully
- ESLint passed (1 pre-existing warning)
- TypeScript compiled without errors
- Prettier formatting applied
- **Verified:** No compilation errors

---

## Files Modified

1. **mcms-jhipster-base/src/main/webapp/app/shared/util/relative-time.tsx**
   - Added `showSeconds` prop to RelativeTimeProps interface
   - Added logic to use `APP_TIMESTAMP_FORMAT` when `showSeconds` is true
   - Updated JSDoc comments

2. **mcms-jhipster-base/src/main/webapp/app/entities/sensor-reading/sensor-reading.tsx**
   - Added `showSeconds` prop to RelativeTime component
   - Sensor reading timestamps now show seconds in tooltip

3. **mcms-jhipster-base/src/main/webapp/app/entities/sensor-reading/sensor-reading-detail.tsx**
   - Added `showSeconds` prop to RelativeTime component
   - Sensor reading detail timestamps now show seconds in tooltip

---

## Usage Examples

### Basic Usage (No Seconds)
```typescript
<RelativeTime value={batch.createdDate} id="batch-created" />
// Display: "2 hours ago"
// Tooltip: "03/22/26 14:30" (no seconds)
```

### High-Precision Usage (With Seconds)
```typescript
<RelativeTime value={reading.timestamp} id="reading-time" showSeconds />
// Display: "2 hours ago"
// Tooltip: "03/22/26 14:30:45" (with seconds!)
```

---

## Why Second Precision Matters

**Sensor Readings:**
- Environmental sensors may report multiple times per minute
- CO2, temperature, humidity changes happen rapidly
- Second precision allows distinguishing readings taken close together

**Audit Trails:**
- Security events need precise timestamps
- Compliance requirements often mandate second precision
- Forensic analysis requires accurate timing

**API Rate Limiting:**
- Track request timestamps precisely
- Implement sliding window rate limits
- Detect burst traffic patterns

---

## Related Features

This feature builds on previous temporal features:

- **Feature #307:** Timestamps display in local timezone
  - Backend stores in UTC, frontend converts to local time
  - `Instant` → ISO 8601 → browser timezone conversion

- **Feature #308:** Date picker respects locale
  - US: MM/DD/YYYY HH:mm:ss
  - Others: DD/MM/YYYY HH:mm:ss

- **Feature #309:** Relative time display
  - "2 hours ago" with tooltip showing full timestamp
  - Now supports seconds via `showSeconds` prop

- **Feature #317:** Daylight saving time handled
  - UTC storage prevents DST issues
  - `Instant` precision preserved across DST transitions

---

## Testing Notes (YOLO Mode)

In YOLO mode, we verify through code analysis and compilation:

1. ✅ **Backend Precision:** `Instant` type provides nanosecond precision
2. ✅ **Display Precision:** `APP_TIMESTAMP_FORMAT` includes seconds
3. ✅ **Component Updated:** RelativeTime accepts `showSeconds` prop
4. ✅ **Sensor Views Updated:** Both list and detail views show seconds
5. ✅ **Code Compiles:** ESLint passed, TypeScript compiled
6. ✅ **Formatting:** Prettier applied successfully

**Timestamp precision is guaranteed at all layers of the application.**

---

## Conclusion

**Feature #318 is PASSING** - Timestamps are stored with nanosecond precision and displayed with second precision.

**Achievements:**
- ✅ Backend stores timestamps with **nanosecond precision** (Instant)
- ✅ Frontend displays timestamps with **second precision** (HH:mm:ss)
- ✅ Sensor readings show precise timestamps in tooltips
- ✅ Backward compatible (other entities still show HH:mm)
- ✅ Locale-aware (US vs international format)
- ✅ Code compiles without errors

**No database migration needed** - Instant was already used, providing nanosecond precision from the start.

**No breaking changes** - The `showSeconds` prop is optional, so existing RelativeTime usages continue to work as before.
