# MCMS User Guide
### Mushroom Cultivation Management System — Practical Guide

---

## Table of Contents

1. [What is MCMS?](#what-is-mcms)
2. [Prerequisites](#prerequisites)
3. [Installation](#installation)
4. [Starting the Application](#starting-the-application)
5. [First Login](#first-login)
6. [User Roles](#user-roles)
7. [Navigation Overview](#navigation-overview)
8. [Core Workflows](#core-workflows)
   - [Setting Up a New Farm](#setting-up-a-new-farm)
   - [Running a Production Batch](#running-a-production-batch)
   - [Recording a Harvest](#recording-a-harvest)
   - [Multi-Flush Production](#multi-flush-production)
   - [Environmental Monitoring](#environmental-monitoring)
   - [Managing Inventory](#managing-inventory)
   - [Sales Orders](#sales-orders)
   - [Supply Orders](#supply-orders)
   - [Financial Tracking](#financial-tracking)
   - [Task Management](#task-management)
9. [Batch Lifecycle Reference](#batch-lifecycle-reference)
10. [Search and Filtering](#search-and-filtering)
11. [Data Export](#data-export)
12. [Verifying Your Installation](#verifying-your-installation)
13. [Things to Avoid](#things-to-avoid)
14. [Troubleshooting](#troubleshooting)

---

## What is MCMS?

MCMS is a complete management system for commercial mushroom farms. It covers everything from inoculation to customer delivery:

- **Production** — track every batch through a 10-phase lifecycle with enforced state transitions
- **Facility** — manage cultivation rooms, sensors, and environmental targets
- **Quality** — record contamination events with severity, root cause, and outcome
- **Inventory** — full material traceability from supplier lot to batch consumption
- **Sales** — customers, product catalog, sales orders, payment tracking
- **Procurement** — suppliers, purchase orders, goods receipt
- **Financials** — cost records per batch, monthly profit/margin reports
- **Tasks** — assign daily operational work to staff

MCMS is a web application. You access it through a browser on your local network or server. There is nothing to install on individual workstations.

---

## Prerequisites

Before installing MCMS, ensure the server or development machine has:

| Requirement | Minimum | Notes |
|---|---|---|
| Java (JDK) | 21 | OpenJDK or Oracle JDK |
| Maven | 3.9+ | Build tool |
| Node.js | 18+ | Frontend build |
| npm | 9+ | Comes with Node.js |
| RAM | 2 GB free | 4 GB recommended |
| Disk | 1 GB free | Plus database storage |

**For production only:**
- PostgreSQL 14+ (development uses a built-in H2 database)

**Check your versions:**
```bash
java -version        # should show 21.x
mvn -version         # should show 3.9.x
node --version       # should show v18.x or higher
npm --version        # should show 9.x or higher
```

Run the automated check:
```bash
./check-prerequisites.sh    # Linux/macOS
check-prerequisites.bat     # Windows
```

---

## Installation

```bash
# 1. Clone the repository
git clone <repository-url>
cd dsl_dev_mush

# 2. Build all modules
mvn clean install

# Expected result: BUILD SUCCESS
```

That's it. The build downloads all dependencies and compiles everything. It takes 2–5 minutes the first time.

---

## Starting the Application

MCMS has two separate processes that must run simultaneously:

**Terminal 1 — Backend (Spring Boot API, port 8080):**
```bash
cd mcms-jhipster-base
./mvnw spring-boot:run -DskipTests -Dfrontend-maven-plugin.skip=true
```

**Terminal 2 — Frontend (webpack dev server, port 9000):**
```bash
cd mcms-jhipster-base
npm start
```

Or use the convenience script that handles both:
```bash
./init.sh
```

Once both are running, open your browser at: **http://localhost:9000**

> The backend takes about 30–60 seconds to start. The frontend is ready when you see
> `Webpack: Compiled successfully`.

---

## First Login

Default credentials:

| Field | Value |
|---|---|
| Username | `admin` |
| Password | `admin` |

**Change the admin password immediately** after first login:
1. Click the account menu (top right) → **Account Settings**
2. Set a strong password (minimum 8 characters, mix of letters, numbers, symbols)
3. Save

The development database comes pre-loaded with sample data so you can explore the application immediately.

---

## User Roles

MCMS has four roles. Assign the minimum role each person needs.

### ADMIN
Full access to everything including user management, financial reports, and system configuration. Typically the farm owner or IT administrator.

**Can do:** Everything — create, edit, delete all records; manage users; view financial data; access system metrics.

### MANAGER
Operational management. Cannot manage user accounts or access system internals.

**Can do:** Create and manage batches, phases, harvests, contamination events, environmental targets, inventory, cost records, monthly reports, sales orders, purchase orders, task assignment.

**Cannot do:** Create or delete user accounts; access system metrics/logs/configuration.

### OPERATOR
Production floor work. Data entry and monitoring.

**Can do:** View and update batches; record harvests and contamination events; enter sensor readings; manage stock movements; complete tasks assigned to them.

**Cannot do:** Delete records; access cost/financial data; assign tasks to others; manage suppliers/customers.

### USER
Read-only. Good for stakeholders who need visibility without making changes.

**Can do:** View all production data, inventory, batches, harvests, sales.

**Cannot do:** Create, edit, or delete anything.

---

## Navigation Overview

The sidebar groups functionality by domain:

**Production**
- Strains — mushroom species/genetics library
- Substrate Recipes — growth medium formulations
- Batches — your production units (start here)
- Phase Executions — detailed phase log per batch
- Flush Cycles — individual harvest cycles
- Harvest Records — daily harvest entries

**Facility & Environment**
- Rooms — cultivation chambers
- Environmental Targets — ideal conditions per room/phase
- Sensors — IoT devices
- Sensor Readings — time-series data from sensors
- Environmental Alerts — threshold breach notifications

**Quality**
- Contamination Events — contamination log with root cause analysis

**Sales**
- Products — your product catalog
- Customers — customer master data
- Sales Orders — outbound orders
- Sales Order Lines — individual items on orders

**Procurement**
- Suppliers — vendor master data
- Supply Orders — purchase orders
- Supply Order Lines — individual items on POs

**Inventory**
- Materials — material catalog
- Inventory Lots — specific stock batches
- Stock Movements — every inventory transaction
- Batch Material Usage — traceability link: what went into each batch

**Financials & Compliance** *(Admin and Manager only)*
- Cost Records — expenses per batch
- Mandatory Field Checks — data completeness audits
- Monthly Reports — profit/loss summaries
- Tasks — operational work items

---

## Core Workflows

### Setting Up a New Farm

Do this once before you start production.

**1. Define your strains**
Go to **Strains → New**. Create an entry for each mushroom species/variety you cultivate. Fill in:
- Name (e.g., "Pleurotus ostreatus - Grey Dove")
- Optimal temperature range, humidity range, CO2 maximum
- Expected yield percentage, shelf life days
- Colonization duration estimate

**2. Create substrate recipes**
Go to **Substrate Recipes → New**. Create your standard growth medium formulations. Include:
- Base material (wheat straw, sawdust, hardwood, etc.)
- Full composition, supplementation, moisture target, pH target
- Sterilization or pasteurization method
- Version the recipe — change the name when you update it

**3. Set up your rooms**
Go to **Rooms → New** for each cultivation chamber. Set:
- Room code (e.g., "INC-A1", "FRUIT-B2")
- Type: STERILE\_ZONE, INCUBATION, FRUITING\_CHAMBER, COLD\_STORAGE, WORK\_ZONE, or LABORATORY
- Capacity (bags), floor area, HVAC/misting equipment flags

**4. Set environmental targets per room**
Go to **Environmental Targets → New**. Create a target for each room + phase combination you want to monitor. This is what triggers alerts when sensor readings go out of range.

**5. Register materials and suppliers**
Go to **Suppliers → New** for each vendor. Then go to **Materials → New** for every input material (spawn, substrate components, packaging, chemicals). Set minimum stock levels to get early warning when stocks run low.

---

### Running a Production Batch

**1. Create the batch**
Go to **Batches → New**.
- Enter a unique batch code (e.g., "PO-2026-001" for Pleurotus ostreatus, year 2026, batch 1)
- Select the strain and substrate recipe
- Enter number of bags, substrate weight, spawn weight, target yield
- Set the start date
- Assign to a room

The batch starts in **INOCULATION** phase automatically.

**2. Advance through phases**
As the batch progresses, open it and click the phase transition button. Each transition records the date and advances the phase. The system enforces the correct sequence — you cannot skip phases.

The 10 phases in order:
1. INOCULATION — spawn introduced
2. EARLY\_COLONIZATION — mycelium visible
3. FULL\_COLONIZATION — substrate fully colonized
4. CONSOLIDATION — mycelium matures and toughens
5. FRUITING\_TRIGGER — environmental shock applied (temperature drop, fresh air)
6. PRIMORDIA — pin formation visible
7. FRUITING\_BODY\_GROWTH — mushrooms developing to harvest size
8. HARVEST — picking in progress
9. REHYDRATION\_PAUSE — rest and rehydration between flushes (optional loop)
10. COMPLETED — batch lifecycle ends

**3. Log phase executions**
For detailed records, go to **Phase Executions → New** and link each phase execution to the batch. Record the responsible person, planned vs actual duration, and any notes.

---

### Recording a Harvest

Go to **Harvest Records → New**:
- Link to the batch
- Enter harvest date, weight (kg), quality grade (A\_PREMIUM, B\_STANDARD, C\_INDUSTRIAL, or WASTE)
- Enter picker name and any notes

For each flush, also create a **Flush Cycle** record:
- Flush number (1, 2, 3...)
- Harvest start and end dates
- Total yield kg, bags harvested
- Whether rehydration was performed and duration

---

### Multi-Flush Production

Oyster mushrooms and other species produce multiple flushes from the same substrate block. MCMS tracks this explicitly.

After the HARVEST phase:
- If doing another flush: advance to **REHYDRATION\_PAUSE**, then back to **FRUITING\_TRIGGER**
- If done: advance to **COMPLETED**

Create a new **Flush Cycle** record for each flush. The batch remains active until you mark it COMPLETED.

---

### Environmental Monitoring

**Set up sensors**
Go to **Sensors → New** for each physical sensor device in each room. Record the sensor type (temperature, humidity, CO2, light, airflow), installation date, and calibration date.

**Log sensor readings**
Go to **Sensor Readings → New** to log measurements. In production with IoT hardware, this would be automated via the API. The timestamp is UTC — the UI displays in your local timezone.

**Respond to alerts**
When a sensor reading breaches an environmental target threshold, an **Environmental Alert** is created automatically. Go to **Environmental Alerts**, review open alerts, acknowledge them once addressed, and record the resolution.

> **Important:** Alerts only trigger if you have created Environmental Targets for the room and phase. If no targets are set, no alerts are generated.

---

### Managing Inventory

**Receive materials**
When goods arrive from a supplier:
1. Go to **Inventory Lots → New** — create a lot for the received goods (quantity, expiry date, storage location, supplier lot number)
2. Go to **Stock Movements → New** — record a RECEIPT movement linked to the lot and purchase order

**Record consumption**
When materials are used in production:
1. Go to **Batch Material Usage → New** — link the batch, inventory lot, quantity used, and purpose (e.g., "Substrate preparation")
2. Go to **Stock Movements → New** — record a CONSUMPTION movement

This creates full traceability: you can trace any batch back to which supplier lot was used, and trace any material lot forward to which batches consumed it.

**Monitor stock levels**
The Materials list shows current stock. Items below minimum stock level are flagged. Check this weekly.

---

### Sales Orders

**1. Create the customer** (first time)
Go to **Customers → New**. Enter contact details, delivery preference, payment terms (e.g., "Net 30", "COD").

**2. Create a product** (first time)
Go to **Products → New**. Create entries for each product you sell (e.g., "Fresh Oyster Mushrooms - Premium 500g"). Set the default grade, weight, and price per kg.

**3. Create the sales order**
Go to **Sales Orders → New**:
- Enter order date, customer, requested delivery date
- Enter shipping address
- Order status starts as ORDERED

**4. Add order lines**
Go to **Sales Order Lines → New** for each product on the order. Link to the sales order, select product, enter weight and price.

**5. Track payment**
On the sales order, update payment status (UNPAID → PARTIALLY\_PAID → PAID) and enter the payment received date.

---

### Supply Orders

**1. Create the supplier** (first time)
Go to **Suppliers → New**. Enter company name, type (SPAWN, SUBSTRATE, CHEMICALS, EQUIPMENT, PACKAGING), contact, and quality rating.

**2. Create the purchase order**
Go to **Supply Orders → New**:
- Enter PO code, supplier, order date, expected delivery date

**3. Add order lines**
Go to **Supply Order Lines → New** for each item ordered. Enter description, quantity, unit, and unit price.

**4. Receive goods**
When goods arrive, update the supply order line: set "Is Received" to true, enter quantity received, supplier lot number, expiry date, and your quality assessment. Then create the corresponding Inventory Lot and Stock Movement (see Managing Inventory above).

---

### Financial Tracking

**Record costs**
Go to **Cost Records → New** to log any expense related to a batch:
- Category: RAW\_MATERIALS, ENERGY, LABOR, PACKAGING, TRANSPORT, LABORATORY, MAINTENANCE, or OTHER
- Amount and currency
- Description

Do this regularly — daily or after each significant activity — to keep cost data accurate.

**Generate monthly reports**
Go to **Monthly Reports → New** and select year and month. MCMS calculates total yield, total cost, total revenue, profit margin, and contamination event count for that month.

---

### Task Management

Go to **Tasks → New** to create operational tasks:
- Title and detailed description
- Due date
- Priority (1 = urgent, 5 = low priority)
- Assign to a staff member

Operators can mark their own tasks complete. Managers can reassign and review all tasks.

---

## Batch Lifecycle Reference

```
INOCULATION
  → EARLY_COLONIZATION      (mycelium visible)
    → FULL_COLONIZATION     (substrate fully colonized)
      → CONSOLIDATION       (mycelium toughens)
        → FRUITING_TRIGGER  (environmental shock applied)
          → PRIMORDIA       (pins forming)
            → FRUITING_BODY_GROWTH  (mushrooms developing)
              → HARVEST     (picking)
                ├── more flushes? → REHYDRATION_PAUSE → FRUITING_TRIGGER (loop)
                └── done?         → COMPLETED
```

**Color coding in the UI:**
- Green badges — active, healthy phases
- Orange/amber — phases requiring attention (FRUITING\_TRIGGER, PRIMORDIA)
- Red — CONTAMINATED flag
- Grey — COMPLETED

---

## Search and Filtering

Every list screen supports search and filtering:

- **Text search** — searches names, codes, and descriptions. Case-insensitive. Works on partial matches.
- **Date range filters** — filter by start date, order date, etc. Dates are in your local timezone.
- **Status/phase filters** — dropdown filters on enumerated fields
- **Number range filters** — min/max filters on numeric fields (e.g., number of bags)

Search is debounced — results update automatically as you type, with a short delay to avoid unnecessary requests.

**Tips:**
- Use the batch code (e.g., "PO-2026") to quickly find related records across screens
- Date filters include the full day — "from 2026-01-01" includes all records from midnight local time
- Clear all filters with the Reset button to see the full list

---

## Data Export

Most list screens have an **Export CSV** button. This exports the current filtered view to a CSV file you can open in Excel or any spreadsheet tool.

Exported columns match what's visible in the table. Apply filters before exporting to narrow down the data.

---

## Verifying Your Installation

After installing, run the automated test suite to confirm everything works:

**Backend unit tests (2–5 minutes):**
```bash
cd mcms-jhipster-base
./mvnw test -DskipFrontend
```
Expected: `BUILD SUCCESS`, all tests pass.

**Frontend tests (1–2 minutes):**
```bash
cd mcms-jhipster-base
npm test -- --watchAll=false
```
Expected: All test suites pass, no failures.

**End-to-end tests with Cypress (requires both servers running):**
```bash
cd mcms-jhipster-base
npm run e2e:headless
```
Expected: All E2E scenarios pass. These tests verify login, CRUD operations, and key workflows against a live application.

**Quick manual check:**
1. Open http://localhost:9000 — login page appears
2. Log in as `admin` / `admin` — dashboard loads
3. Go to Batches — list loads (may have sample data)
4. Create a new Batch — form saves successfully
5. Go to http://localhost:8080/swagger-ui/ — API documentation loads
6. Go to http://localhost:8080/management/health — shows `{"status":"UP"}`

---

## Things to Avoid

**Do not skip phases in the batch lifecycle.**
The system enforces phase order. Attempting to jump from INOCULATION directly to HARVEST will be rejected. Work through each phase in sequence.

**Do not delete batches that have related records.**
If a batch has harvest records, phase executions, flush cycles, or material usage records, deleting it will cascade-delete all those records. Archive by marking COMPLETED instead.

**Do not edit generated records directly in the H2 console.**
The H2 database console (http://localhost:8080/h2-console) is for inspection only. Direct SQL edits bypass validations and can leave data inconsistent.

**Do not use the default admin/admin password in production.**
Change it immediately. Also change the JWT secret in application-prod.yml before going live.

**Do not set environmental targets after alerts start triggering.**
Set all environmental targets for each room and phase *before* you connect sensors or begin logging readings. Retroactive targets do not generate retroactive alerts.

**Do not create Stock Movements without a corresponding Inventory Lot.**
Every consumption movement should reference a specific lot. This is what makes traceability work. A movement without a lot is an orphaned record.

**Do not run two instances of the backend on the same database simultaneously.**
Liquibase migrations will conflict and may corrupt the schema.

**Do not clear browser cache while a form is open.**
You will lose unsaved form data. Save frequently.

---

## Troubleshooting

**Application shows "An unexpected error has occurred"**
Usually a JavaScript error. Open browser developer tools (F12), check the Console tab for the error message. Report it with the exact error text.

**"Error occurred while trying to proxy" on API calls**
The backend is not running. Start it in a second terminal (see Starting the Application above).

**Login fails with correct credentials**
The backend is starting up. Wait 30–60 seconds and try again. Check the backend terminal for startup errors.

**Batch phase transition button is missing or disabled**
You may not have the OPERATOR, MANAGER, or ADMIN role. Check with your administrator.

**Environmental alerts are not being generated**
Confirm that Environmental Targets exist for the room and phase. Without targets, there is no threshold to breach.

**Sensor readings appear in the wrong timezone**
MCMS stores timestamps in UTC and converts to your browser's local timezone for display. Ensure your workstation's system timezone is set correctly.

**CSV export is empty**
The export includes only the current filtered view. If filters are too restrictive, clear them and export again.

**The application is slow when loading large lists**
Use search/filter to reduce the result set. The Sensor Readings list in particular can grow very large in production — always filter by date range or sensor.

**Build fails: "Cannot read properties of undefined (reading 'buildMeta')"**
This is an intermittent webpack issue. Clean the build output and retry:
```bash
rm -rf target/classes/static
npm start
```

**Port 9060 already in use**
A previous webpack process is still running. Kill it:
```bash
fuser -k 9060/tcp
npm start
```
