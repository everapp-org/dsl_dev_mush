# MCMS - Mushroom Cultivation Management System

**A DSL-First Experiment in Code Generation and Methodology Refinement**

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)]()
[![Java Version](https://img.shields.io/badge/java-21-blue)]()
[![Maven](https://img.shields.io/badge/maven-3.9+-blue)]()
[![Phase](https://img.shields.io/badge/phase-0%20(Foundation)-yellow)]()

---

## What is MCMS?

MCMS is a **smart agriculture platform** for commercial mushroom farming that combines:
- Traditional agricultural tracking (batches, harvests, yields)
- Modern IoT integration (sensors, real-time monitoring)
- Enterprise supply chain management (procurement, sales, inventory)
- Financial analytics (cost, revenue, profitability)
- Quality assurance (contamination tracking, grading)

**But more importantly**, MCMS is an **experiment in software methodology** to:
- Refine the **DSL-First development** approach
- Maximize **code generation** (target: 85%+ generated code)
- Blend **JHipster** scaffolding with **custom DSL** enhancements
- Document **process learnings** for future projects
- Clarify terminology (DSL-First vs Vibe Meta-programming)

---

## Project Philosophy

> **"Process improvement is the goal, not perfection."**

This is NOT a traditional software project. It's an **informative experiment** where:
- Hitting walls provides learning opportunities
- Process documentation is the PRIMARY deliverable
- Application completeness is secondary to methodology insights
- We can pivot and try alternative approaches

---

## Architecture Overview

### Hybrid Code Generation Strategy

```
JHipster JDL → JHipster CLI → Entities, Repositories, REST APIs, Frontend (80%)
                                          ↓
Custom Behavioral DSLs → ANTLR + JavaPoet → State Machines, Events, Validators (15%)
                                          ↓
Manual Orchestration → Hand-written Integration (5%)
```

**Target**: 85% generated code, 15% hand-written

### Module Structure

```
dsl_dev_mush/
├── mcms-dsl/                    # ANTLR grammars + DSL definitions
├── mcms-codegen/                # JavaPoet code generators
├── mcms-jhipster-base/          # JHipster CRUD scaffold (GENERATED)
├── mcms-domain-extensions/      # DSL-generated enhancements (GENERATED)
├── mcms-application/            # Hand-written orchestration (MANUAL)
├── docs/                        # Domain specifications, agent rules
└── dev-docs/                    # Plans, memories, setup guides
```

**Separation Principle**: JHipster owns structure (entities, repos, REST), Custom DSLs own behavior (state machines, validation), Manual code owns orchestration.

---

## Quick Start

### Prerequisites

Install the following (see `dev-docs/SETUP_GUIDE.md` for details):
- **Java 21** - `java -version`
- **Maven 3.9+** - `mvn -version`
- **Node.js 18+ & npm 9+** - `node -v && npm -v`
- **JHipster CLI** - `jhipster --version`
- **Git** - `git --version`

### Build Project

```bash
# Clone repository
git clone <repo-url> dsl_dev_mush
cd dsl_dev_mush

# Checkout current branch
git checkout feature/phase-0-foundation

# Build all modules
mvn clean install

# Expected output:
# [INFO] BUILD SUCCESS
# [INFO] Total time: ~4 seconds
```

### Read Context

Before making changes, read these files:
1. **dev-docs/AGENT_MEMORIES.md** - Full conversation context and decisions
2. **docs/AGENTS.md** - Collaboration rules and workflows
3. **dev-docs/01-dsl-first-implementation-plan.md** - Implementation roadmap

---

## Domain Model Summary

MCMS manages a complex mushroom cultivation operation with **25 entities** organized into 6 domains:

### 1. Production Core (6 entities)
- **Batch** (central aggregate root) - 10-phase lifecycle
- Strain, SubstrateRecipe, PhaseExecution, FlushCycle, HarvestRecord

### 2. Facility & IoT (6 entities)
- Room, Sensor, SensorReading, EnvironmentalTarget, EnvironmentalAlert

### 3. Quality Management (1 entity)
- ContaminationEvent (8 contamination types)

### 4. Supply Chain - Sales (4 entities)
- Product, Customer, SalesOrder, SalesOrderLine

### 5. Supply Chain - Procurement (3 entities)
- Supplier, SupplyOrder, SupplyOrderLine

### 6. Inventory & Traceability (4 entities)
- Material, InventoryLot, StockMovement, BatchMaterialUsage

**Full farm-to-fork traceability**:
```
Supplier → SupplyOrder → InventoryLot → Batch → SalesOrderLine → Customer
```

---

## Development Workflow

### 1. UNDERSTAND
- Check current phase: `git log --oneline -n 1`
- Read agent memories: `dev-docs/AGENT_MEMORIES.md`
- Review plan for current phase

### 2. CHANGE
- **Structural**: Modify JDL (`docs/mushroom-farm-en.jdl`)
- **Behavioral**: Modify DSL (`mcms-dsl/src/main/resources/*.dsl`)
- **Generator**: Modify JavaPoet code (`mcms-codegen/`)
- **Orchestration**: Modify hand-written code (`mcms-application/`)
- **NEVER edit generated code directly** (will be overwritten)

### 3. GENERATE
```bash
cd mcms-dsl && mvn generate-sources      # ANTLR parsers
cd mcms-codegen && mvn install           # Run generators
```

### 4. TEST
```bash
mvn clean test                           # All tests
mvn test -pl mcms-domain-extensions      # Specific module
```

### 5. DOCUMENT
Update `dev-docs/AGENT_MEMORIES.md` with learnings

### 6. COMMIT
```bash
git add .
git commit -m "[Phase-N] Description

- Change 1
- Change 2

Generated: X files, Y LOC
Learning: Key insight"
```

---

## Technology Stack

| Category | Technology | Version | Purpose |
|----------|-----------|---------|---------|
| **Language** | Java | 21 | Application code |
| **Build** | Maven | 3.9+ | Multi-module build |
| **Framework** | Spring Boot | 3.2.2 | Runtime framework |
| **DSL Parser** | ANTLR 4 | 4.13.1 | Grammar → AST |
| **Code Gen** | JavaPoet | 1.13.0 | Type-safe code generation |
| **Scaffolding** | JHipster | 8.x | Entity/REST/UI generation |
| **Testing** | JUnit 5 | 5.10.1 | Unit & integration tests |
| **Database** | H2 / PostgreSQL | - | Development / Production |
| **Frontend** | Angular/React/Vue | - | UI (JHipster choice) |

---

## Implementation Phases

### ✅ Phase 0: Foundation (COMPLETE)
- Maven multi-module structure
- ANTLR integration verified
- JavaPoet dependencies configured
- Documentation framework established

### 🔄 Phase 1: JHipster Scaffolding (NEXT)
- Generate 25 entities from JDL
- REST APIs with Swagger
- Frontend CRUD screens
- Database migrations

### 📋 Phase 2: Batch State Machine (CRITICAL)
- Implement 10-phase Batch lifecycle
- Prove DSL-First approach works
- State machine with guards, events, actions
- 100% test coverage

### 📋 Phase 3-7: Vertical Slices
- Business rules & validation
- Multi-flush harvest cycle
- Contamination management + AI hooks
- Supply chain traceability
- Financial dashboard

### 📋 Phase 8: Living Documentation
- Auto-generate docs from tests
- PlantUML state diagrams
- Keep docs synchronized

### 📋 Phase 9: Retrospective (PRIMARY DELIVERABLE)
- Document process learnings
- 5+ actionable improvements
- Terminology clarifications
- Postmortem vs premortem analysis

---

## Key Documents

### Planning & Process
- **dev-docs/01-dsl-first-implementation-plan.md** - Complete 9-phase plan
- **dev-docs/AGENT_MEMORIES.md** - Conversation context & decisions
- **docs/PREMORTEM.md** - Anticipated failure modes
- **docs/POSTMORTEM.md** - Actual vs predicted outcomes (Phase 9)

### Rules & Workflows
- **docs/AGENTS.md** - AI agent collaboration rules
- **dev-docs/SETUP_GUIDE.md** - Installation prerequisites
- **dev-docs/WORKFLOW_MEMORIES.md** - Process decisions

### Domain Specifications
- **docs/mushroom-farm-en.jdl** - JHipster entity definitions (25 entities)
- **docs/DSL_FIRST_DEVELOPMENT_GUIDE.md** - Methodology guide (52KB)
- **docs/KERNEL_DSL_v1.txt** - KERNEL DSL v1 grammar reference

---

## Code Generation Breakdown

| Artifact | Source | Generator | % Generated |
|----------|--------|-----------|-------------|
| JPA Entities | JHipster JDL | JHipster CLI | 95% |
| Repositories | JHipster JDL | JHipster CLI | 100% |
| REST Controllers | JHipster JDL | JHipster CLI | 90% |
| Frontend UI | JHipster JDL | JHipster CLI | 85% |
| State Machines | mcms-states.dsl | StateMachineGenerator | 95% |
| Domain Events | mcms-states.dsl | EventClassGenerator | 100% |
| Validators | mcms-rules.dsl | BusinessRuleGenerator | 80% |
| Unit Tests | mcms-states.dsl | TestGenerator | 90% |
| Documentation | DSL + Tests | DocGenerator | 95% |
| **Overall** | | | **~85%** |

---

## Process Experiments

This project tests 4 key hypotheses:

1. **JHipster + Custom DSL Coexistence** - Can they work together without conflicts?
2. **85% Generated Code** - Is it achievable with hybrid approach?
3. **DSL Comprehension** - Can non-programmers understand the DSL?
4. **Regeneration Speed** - Can we regenerate all code in < 5 minutes?

Results documented in **docs/PROCESS_LEARNINGS.md** (Phase 9)

---

## Contributing

This is an **experimental project** focused on methodology refinement. Key principles:

### NEVER
1. Never modify generated code manually (will be overwritten)
2. Never skip state assertions in transitions
3. Never commit without regenerating documentation
4. Never use `System.out.println()` for logging

### ALWAYS
1. Always assert current state before transitions
2. Always emit events for state changes
3. Always commit DSL + generated code together
4. Always update AGENT_MEMORIES.md with learnings

See **docs/AGENTS.md** for complete collaboration rules.

---

## Project Status

**Current Phase**: 0 (Foundation) ✅
**Current Branch**: `feature/phase-0-foundation`
**Last Commit**: `5ff62c1` - Foundation setup
**Build Status**: ✅ SUCCESS (3.8s)
**Next Phase**: 1 (JHipster Scaffolding)

**Generated Code**: 36 files (Phase 0 infrastructure)
**Hand-Written Code**: Documentation, POMs, test grammar

---

## License

[To be determined - specify your license here]

---

## Contact & Support

**Documentation**: See `dev-docs/` directory
**Issues**: [To be determined - link to issue tracker]
**Questions**: Read `docs/AGENTS.md` for workflows

---

**Remember**: This is an experiment. Hitting walls is informative. Document learnings, try another way if needed. **Process improvement is the goal, not perfection.**

---

**Last Updated**: 2026-02-12
**Project Started**: 2026-02-12
**Maintained By**: AI Agents (Architect, Generator, Domain Expert, Documentation)
