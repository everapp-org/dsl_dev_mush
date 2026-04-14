# MCMS Marketing Materials
### Mushroom Cultivation Management System

---

## Hero Section

### Headline
**Run Your Mushroom Farm Like a Professional Operation**

### Subheadline
MCMS is the complete management platform purpose-built for commercial mushroom cultivation. Track every batch from inoculation to delivery, manage your team, monitor your environment, and know your numbers — all in one system.

### Call to Action
**Get Started Free** | **See a Demo**

---

## Value Proposition — One-liner

*"From spawn to sale, every step tracked, every decision informed."*

---

## The Problem We Solve

Running a commercial mushroom farm means keeping track of dozens of moving parts simultaneously: which batches are in which phase, whether your fruiting chambers are at the right humidity, whether you have enough spawn in stock, which customers are overdue to pay, and whether last month was actually profitable.

Most farms manage this with spreadsheets, whiteboards, and institutional memory. When something goes wrong — a contamination event, a missed harvest window, a stock-out — it's hard to know why or what to fix.

MCMS gives your farm a single source of truth.

---

## Feature Highlights

### Production Management
**Track every batch through its full lifecycle**

Every production batch follows a scientifically structured 10-phase lifecycle: Inoculation → Colonization → Fruiting Trigger → Harvest → and back for another flush if you're running multi-flush production. The system enforces the correct sequence so no step gets skipped, and every transition is dated and recorded.

- Log 10 distinct production phases per batch
- Support for unlimited harvest flushes per batch
- Record daily harvests with weight, quality grade, and picker
- Track planned vs actual duration for every phase
- Contamination events with severity, root cause, and yield impact

---

### Environmental Intelligence
**Know what's happening in your chambers right now**

Connect your IoT sensors and MCMS keeps a continuous record of temperature, humidity, CO₂, light levels, and airflow in every room. Set target ranges for each phase and each room — MCMS alerts you automatically when conditions drift outside acceptable limits.

- Supports sensors for temperature, humidity, CO₂, light, and airflow
- Set environmental targets per room and per production phase
- Automatic alerts when sensor readings breach thresholds
- Full time-series sensor reading history
- Acknowledge and resolve alerts with audit trail

---

### Supply Chain Traceability
**Know exactly what went into every batch**

MCMS tracks the complete journey from supplier to customer. Every inventory lot received from a supplier is linked to the purchase order, given a lot number, and tracked as it is consumed in production. When a batch ships to a customer, you can trace it back to the exact supplier lots used.

This is not just good practice — it's what food safety auditors and retailers increasingly require.

- Supplier management with quality ratings
- Purchase order tracking with goods receipt
- Inventory lots with quantity, expiry date, and storage location
- Every stock movement recorded (receipt, consumption, adjustment, waste)
- Batch material usage creates a complete farm-to-fork audit trail

---

### Sales and Revenue
**From harvest to invoice in one system**

Build your product catalog, manage your customer list, and create sales orders without leaving the application. Track delivery status and payment — know at a glance which customers are overdue.

- Product catalog with grades and standard prices
- Customer management with contact details and payment terms
- Sales orders with line-item detail
- Payment status tracking (Unpaid → Paid → Overdue)
- Quality grade tracking on every sold kg (Premium, Standard, Industrial, Waste)

---

### Financial Clarity
**Stop guessing whether you're profitable**

Log costs against specific batches — raw materials, labor, energy, packaging, transport. MCMS calculates your biological efficiency, cost per kg, and monthly profit margins automatically.

- Cost records by category per batch
- Monthly reports with yield, cost, revenue, and profit margin
- Contamination impact tracked in financial terms
- Biological efficiency % calculated per batch

---

### Team Coordination
**Everyone knows what they need to do today**

Assign tasks to operators with due dates and priority levels. Staff see their own tasks; managers see everything. No more missed handoffs.

- Task assignment with priority and due date
- Status tracking (open → completed)
- Visibility for managers across all staff

---

### Role-Based Access
**The right people see the right information**

Four roles — Admin, Manager, Operator, and Read-only User — give each team member access appropriate to their responsibilities. Financial data is restricted to management. Production floor staff see what they need and nothing more.

---

## Who It's For

**Independent mushroom farms** producing 100kg–5,000kg/month
- Tired of spreadsheets that don't scale
- Want to improve consistency and reduce contamination rates
- Need to pass food safety audits or enter retail supply chains

**Multi-site mushroom operations**
- Need a single system across multiple growing locations
- Want to compare performance across sites and strains

**Contract growers**
- Need documentation to show clients quality control is in place
- Want to demonstrate traceability for premium buyers

---

## By the Numbers

| Capability | Count |
|---|---|
| Production phases tracked | 10 |
| Entity types managed | 27 |
| Sensor types supported | 5 (temp, humidity, CO₂, light, airflow) |
| Contamination types | 8 |
| Quality grades | 4 (Premium, Standard, Industrial, Waste) |
| User roles | 4 |
| Harvest flushes per batch | Unlimited |

---

## Technology

MCMS is a web application — access it from any browser on any device on your local network. There is nothing to install on individual workstations.

- **Backend:** Java 21 / Spring Boot — stable, production-proven, runs on any server
- **Frontend:** React 18 — fast, responsive interface that works on desktop, tablet, and mobile
- **Database:** PostgreSQL (production) or built-in H2 (development/evaluation)
- **Authentication:** JWT — secure token-based login
- **API:** Full REST API with Swagger documentation — integrate with existing systems

**Self-hosted.** Your production data stays on your infrastructure. No subscription, no vendor lock-in, no data leaving your network.

**Open source.** Full source code included. Customise it, extend it, integrate it.

---

## Getting Started

```
1. Install Java 21, Maven, and Node.js on your server
2. Clone the repository
3. Run: mvn clean install
4. Start the backend and frontend
5. Open http://your-server:9000
6. Log in as admin/admin and create your first batch
```

Full installation instructions: see `INSTALL.md`
Full user guide: see `docs/USER_GUIDE.md`

---

## Testimonial Placeholder

> "Before MCMS we were running five spreadsheets and still losing track of which batches were at which stage. Now everything is in one place and the contamination alert alone has saved us from three crop losses in the first month."
>
> — *Farm Manager, Oyster Mushroom Operation*

---

## FAQ

**Is MCMS cloud-hosted or self-hosted?**
Self-hosted. You run it on your own server or local machine. Your data never leaves your infrastructure.

**Does it work on mobile?**
Yes. The interface is fully responsive and works on phones and tablets. Operators can log harvests and sensor readings from the floor.

**What database does it use?**
PostgreSQL in production. For evaluation and development, it includes a built-in H2 database — no PostgreSQL installation required to get started.

**Can I connect real IoT sensors?**
Yes, via the REST API. Any sensor with network connectivity and the ability to make HTTP requests can send readings directly to MCMS. The API is fully documented in Swagger.

**Can I export my data?**
Yes. Every list screen has a CSV export. The full REST API also allows programmatic data access for integration with accounting or ERP systems.

**How many users can it support?**
There is no hard user limit. The application is designed for team sizes typical of commercial mushroom farms — from 2–3 person operations to teams of 20+.

**Is there a free trial?**
The software is open source. Download it, install it, and run it. There is no trial period — it's fully functional from day one.

**What do I need to run it?**
A server or computer with Java 21 and 2 GB of free RAM. Any Linux, macOS, or Windows machine will work for development. For production, a small Linux server (even a VPS) running PostgreSQL is the recommended setup.

---

## Comparison

| Capability | Spreadsheets | Generic ERP | MCMS |
|---|---|---|---|
| Mushroom phase lifecycle | Manual | No | Yes — enforced |
| Multi-flush tracking | Manual | No | Yes |
| Environmental alerts | No | No | Yes |
| Farm-to-fork traceability | Difficult | Partial | Yes — built-in |
| Contamination tracking | Manual | No | Yes — structured |
| Biological efficiency calculation | Manual | No | Automatic |
| Monthly profit reports | Manual | Expensive | Automatic |
| Mobile access | No | Sometimes | Yes |
| Self-hosted | Yes | Sometimes | Yes |
| Open source | N/A | Rarely | Yes |
| Cost | Free | $$$–$$$$ | Free |

---

## Contact / Repository

Project repository: *(link)*
Issue tracker: *(link)*
Documentation: `docs/USER_GUIDE.md`, `docs/ADMIN_GUIDE.md`
License: *(add your license here)*
