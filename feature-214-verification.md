# Feature #214: Stock movement updates lot quantity

**Status:** PASSING

## Implementation Details

### Backend Changes

**File:** /mcms-jhipster-base/src/main/java/com/mcms/web/rest/StockMovementResource.java

**Change:** After creating a stock movement, reload the entity with eager relationships to include the updated inventory lot quantity in the response.

**Code added:**
- Reload entity with eager relationships after creation
- Returns StockMovement with nested InventoryLot containing updated quantityOnHand

### Frontend Changes

**File:** /mcms-jhipster-base/src/main/webapp/app/entities/stock-movement/stock-movement-detail.tsx

**Change:** Display the updated inventory lot quantity on the stock movement detail page.

**UI Display:**
- Shows "Current quantity: [amount] [unit]" after lot code link
- Only displays if quantityOnHand is defined
- Styled with text-muted class

## Verification Steps

1. POST /api/stock-movements - Creates stock movement via StockMovementService
2. Service updates lot - StockMovementService.createStockMovement() updates inventoryLot.quantityOnHand
3. Response includes updated quantity - Entity reloaded with eager relationships
4. UI displays quantity - Stock movement detail page shows updated lot quantity

## Business Logic (Already Implemented in StockMovementService)

- RECEIPT / ADJUSTMENT_IN: Adds to quantityOnHand
- CONSUMPTION / WASTE / ADJUSTMENT_OUT / RETURN_TO_SUPPLIER: Subtracts from quantityOnHand
- Exhausted flag: Set to true when quantityOnHand reaches 0
- Validation: Prevents negative quantities (insufficient stock error)

## Code Quality

- Backend compiles: mvn compile succeeded
- Frontend lints: npm run lint succeeded (1 pre-existing warning)
- No mock data: 0 matches for mock patterns
- Type safety: TypeScript interfaces match backend entities

## Date: 2026-03-17
## Session: 71
