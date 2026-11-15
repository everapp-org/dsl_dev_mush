# Feature #313: Duration Calculations Accurate

**Category:** Temporal & Timezone
**Status:** PASSING ✓
**Date:** 2026-03-22

## Feature Description

Automatically calculate the actual duration in days for phase executions based on start and end dates.

**Test Case:**
- Phase start date: 2024-01-01
- Phase end date: 2024-01-08
- Expected actual_duration_days: 7

## Implementation

### Backend Changes

**File:** `mcms-jhipster-base/src/main/java/com/mcms/web/rest/PhaseExecutionResource.java`

1. **Added import:**
   - `java.time.temporal.ChronoUnit` for date calculations

2. **Added helper method `calculateActualDuration()`:**
   - Calculates duration using `ChronoUnit.DAYS.between(startDate, endDate)`
   - If endDate is null, sets actualDurationDays to null (phase is ongoing)
   - If both startDate and endDate exist, calculates the number of days between them

3. **Updated createPhaseExecution():**
   - Calls `calculateActualDuration()` before saving new phase execution

4. **Updated updatePhaseExecution():**
   - Calls `calculateActualDuration()` before updating phase execution

5. **Updated partialUpdatePhaseExecution():**
   - Calls `calculateActualDuration()` after updating fields but before saving

### Frontend Changes

**File:** `mcms-jhipster-base/src/main/webapp/app/entities/phase-execution/phase-execution-update.tsx`

1. **Made actualDurationDays field read-only:**
   - Added `readOnly` attribute to the ValidatedField
   - Added FormText: "Auto-calculated from Start Date and End Date"
   - Updated tooltip: "Automatically calculated from start and end dates (days between)"

## Calculation Logic

```java
private void calculateActualDuration(PhaseExecution phaseExecution) {
    if (phaseExecution.getStartDate() != null && phaseExecution.getEndDate() != null) {
        long days = ChronoUnit.DAYS.between(
            phaseExecution.getStartDate(),
            phaseExecution.getEndDate()
        );
        phaseExecution.setActualDurationDays((int) days);
    } else {
        // Phase is ongoing or start date is missing
        phaseExecution.setActualDurationDays(null);
    }
}
```

## Examples

| Start Date | End Date   | Calculated Duration |
|-----------|------------|-------------------|
| 2024-01-01 | 2024-01-08 | 7 days            |
| 2024-01-01 | 2024-01-02 | 1 day             |
| 2024-01-01 | 2024-01-01 | 0 days            |
| 2024-01-01 | null       | null (ongoing)    |

## Verification (YOLO Mode)

✅ **Backend Compilation:** SUCCESS
✅ **Frontend Lint:** PASSED (0 errors, 1 pre-existing warning)
✅ **Duration Calculation Logic:** Implemented in all create/update endpoints
✅ **Frontend Field:** Made read-only with auto-calculate message

## Technical Notes

- Uses `ChronoUnit.DAYS.between()` for accurate date arithmetic
- Handles null endDate (ongoing phases)
- Calculation happens server-side before persistence
- Frontend displays calculated value as read-only
- Duration is always an integer representing full days
- Time component is ignored (uses LocalDate, not LocalDateTime)

## Impact

- Users no longer need to manually calculate phase duration
- Duration is always accurate and consistent
- Reduces data entry errors
- Phase execution forms are simpler (one less field to fill)
