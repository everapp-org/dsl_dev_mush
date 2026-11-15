# AGENT MEMORIES - MCMS Project

**Purpose**: Capture all conversation context, decisions, and learnings for project portability
**Last Updated**: 2026-02-12 (Phase 0 complete)

---

## Project Context

### Initial Request
User requested to develop an application based on:
- **JDL Specification**: `docs/mushroom-farm-en.jdl` (25 entities for mushroom farm management)
- **Development Guide**: `docs/DSL_FIRST_DEVELOPMENT_GUIDE.md` (52KB methodology guide)
- **Reference Project**: `jCrew_02/` (Java + Maven DSL-First implementation)

### User Goals (from questions asked)
1. **Tech Stack**: Blend Java + Maven (jCrew_02 style) with JHipster to maximize finished product with maximum percentage of generated code
2. **Primary Goal**: Focus on PROCESS improvement over application functionality - treat as informative experiment to refine "DSL_First" or "Vibe_Meta-programming" methodology
3. **Scope**: Implement all 25 entities, then organize work by vertical slices
4. **AI Integration**: Design architecture to support future AI agent integration (like jCrew) but don't implement initially
5. **Process Documentation**: Clarify process terminology and steps along the way
6. **Git Workflow**: Create branch, commit regularly, premortem/postmortem as needed

### Project Philosophy
- **Experimentation over perfection**: "We can hit walls, count it as informative experiment and try another way"
- **Process over product**: PRIMARY deliverable is PROCESS_LEARNINGS.md, not a perfect app
- **Terminology clarification**: Need to establish clear definitions for DSL-First vs Vibe Meta-programming

---

## Key Decisions Made

### Architecture Decision: Parallel Enhancement Approach
**Decision**: Blend JHipster JDL (structural DSL) with custom behavioral DSLs
**Rationale**:
- JHipster provides 80% of CRUD for free
- Custom DSLs add state machines, business rules (15%)
- Hand-written orchestration fills gaps (5%)
- Target: 85% generated code

**Alternative Considered**: Full custom DSL (rejected - reinventing wheel)

### Module Structure Decision
**Decision**: 5 Maven modules with clear separation
**Modules**:
1. `mcms-dsl` - ANTLR grammars + DSL files
2. `mcms-codegen` - JavaPoet generators
3. `mcms-jhipster-base` - JHipster CRUD (GENERATED, don't edit)
4. `mcms-domain-extensions` - DSL-generated enhancements (GENERATED)
5. `mcms-application` - Hand-written orchestration (MANUAL only)

**Rationale**: Clean separation prevents JHipster regeneration from destroying DSL enhancements

### Composition over Inheritance Decision
**Decision**: Wrap JHipster entities, don't extend them
**Example**:
```java
// JHipster entity (in mcms-jhipster-base)
@Entity
public class Batch { /* ... */ }

// DSL-generated enhancement (in mcms-domain-extensions)
@Component
public class BatchStateMachine {
    private Batch batch; // Composition
    public void startColonization() { /* ... */ }
}
```
**Rationale**: JHipster can regenerate entities without destroying state machines

### DSL Strategy Decision
**Decision**: Three-tier DSL architecture
1. **Tier 1**: JHipster JDL (structural) - already exists
2. **Tier 2**: Custom behavioral DSL (mcms-states.dsl) - for state machines
3. **Tier 3**: Business rules DSL (mcms-rules.dsl) - for validation
4. **Tier 4**: AI hooks DSL (mcms-ai-hooks.dsl) - aspirational, design interfaces now

**Rationale**:
- Don't extend JHipster JDL (would require forking generator)
- Complementary DSLs are cleaner, more maintainable
- Each DSL has single responsibility

---

## Domain Knowledge (Mushroom Farming)

### JDL Analysis Summary
**25 Entities organized into 6 domains**:

1. **Production Core** (6): Strain, SubstrateRecipe, Batch, PhaseExecution, FlushCycle, HarvestRecord
2. **Facility & IoT** (6): Room, EnvironmentalTarget, Sensor, SensorReading, EnvironmentalAlert
3. **Quality**: ContaminationEvent (with 8 contamination types)
4. **Supply Chain - Sales** (4): Product, Customer, SalesOrder, SalesOrderLine
5. **Supply Chain - Procurement** (3): Supplier, SupplyOrder, SupplyOrderLine
6. **Inventory & Traceability** (4): Material, InventoryLot, StockMovement, BatchMaterialUsage

### Critical Domain Insights

**Batch is Central Aggregate Root**:
- 10-phase lifecycle (INOCULATION → COMPLETED)
- Multi-flush support: HARVEST → REHYDRATION_PAUSE → HARVEST (repeat up to 5 times)
- Traceability hub: links materials, phases, harvests, sales, costs

**State Machines Identified**:
1. **Batch Lifecycle** (10 phases) - Phase 2 implementation
   - INOCULATION → EARLY_COLONIZATION → FULL_COLONIZATION → CONSOLIDATION → FRUITING_TRIGGER → PRIMORDIA → FRUITING_BODY_GROWTH → HARVEST → REHYDRATION_PAUSE → COMPLETED
2. **ContaminationEvent** (5 states) - Phase 5
3. **PaymentStatus** (3 states) - Future
4. **TaskStatus** (3 states) - Future

**Traceability Chain** (farm-to-fork):
```
Supplier → SupplyOrder → InventoryLot → BatchMaterialUsage → Batch → SalesOrderLine → Customer
```

**IoT Integration**:
- Real-time sensor readings (temp, humidity, CO2, light, airflow)
- Critical alerts (e.g., temp > 35°C triggers immediate action)
- Phase-specific environmental targets

---

## Technical Decisions

### Technology Stack
- **Java**: 21 (LTS)
- **Maven**: 3.9+
- **Spring Boot**: 3.2.2
- **ANTLR**: 4.13.1 (parser generator)
- **JavaPoet**: 1.13.0 (code generation)
- **JUnit**: 5.10.1 (testing)
- **JHipster**: TBD (to be installed before Phase 1)

### Code Generation Strategy
| Artifact | Generator | % Generated |
|----------|-----------|-------------|
| JPA Entities | JHipster | 95% |
| Repositories | JHipster | 100% |
| REST APIs | JHipster | 90% |
| Frontend UI | JHipster | 85% |
| State Machines | Custom (JavaPoet) | 95% |
| Domain Events | Custom (JavaPoet) | 100% |
| Validators | Custom (JavaPoet) | 80% |
| Tests | Custom (JavaPoet) | 90% |
| Docs | Custom (JavaPoet) | 95% |

### State Machine Pattern (from jCrew_02)
**CRITICAL PATTERN** (must always follow):
```java
public void transition() {
    logger.debug("Entity[id={}] TRANSITION: FROM → TO | trigger=eventName", this.id);
    assertState(EXPECTED_FROM_STATE); // MANDATORY
    this.state = NEW_STATE;
    emit(new TransitionEvent(this.id, Instant.now()));
    // Action methods called here
}
```

**Never skip state assertions** - leads to invalid states

---

## Learnings So Far

### Phase 0 Learnings

**Learning 1: ANTLR Integration is Straightforward**
- Test grammar (`TestDSL.g4`) generated all components successfully
- Build time: 3.8s for full project (well under 5min target)
- Generated: Lexer, Parser, Visitor, Listener, Tokens

**Learning 2: Modular Maven Structure Works**
- Clean separation between modules
- No circular dependencies
- Build helper plugin correctly adds generated sources
- Parent POM dependency management keeps versions consistent

**Learning 3: Generated Code Should Be Committed**
- Helps with debugging
- Provides version history
- Makes code reviewable
- Build artifacts (target/) excluded via .gitignore

**Learning 4: JHipster Not Installed Yet**
- Will need: Node.js + npm + JHipster CLI
- Installation before Phase 1: `npm install -g generator-jhipster`

---

## Patterns from jCrew_02 (Reusable)

### 1. DSL-First Workflow
```
1. Define domain in .dsl file
2. Run ANTLR parser to generate AST
3. Extract semantic model (Visitor pattern)
4. Generate code with JavaPoet
5. Write hand-coded runtime logic
6. Generate tests from DSL
7. Generate documentation from tests
```

### 2. Generator Architecture
```java
public class McmsCodeGenerator {
    public static void main(String[] args) {
        // Parse DSL
        StateMachineModel model = new StateMachineExtractor()
            .extract("mcms-states.dsl");

        // Generate code
        new StateMachineGenerator(outputDir).generate(model);
        new EventClassGenerator(outputDir).generate(model);
        new TestGenerator(outputDir).generate(model);
    }
}
```

### 3. Living Documentation Pattern
- Tests with `@DisplayName` annotations
- Hierarchical `@Nested` structure
- Auto-generated markdown from test results
- Always current (regenerated after changes)

### 4. Object Calisthenics Markers
```java
// VIOLATION: Rule 8 (Max 2 instance variables) - 16 fields
//   Rationale: Domain model requires all fields from JDL definition
// COMPLIANCE: Rule 3 (Wrap primitives) - All primitives wrapped
```

---

## Open Questions & Future Decisions

### To Decide in Phase 1
- [ ] Which JHipster version to use? (check Java 21 compatibility)
- [ ] Angular, React, or Vue for frontend?
- [ ] Database: H2 (dev) + PostgreSQL (prod)?
- [ ] Liquibase vs Flyway for migrations?

### To Decide in Phase 2
- [ ] How to extend KERNEL DSL v1 grammar for mushroom domain?
- [ ] Guard condition syntax - boolean expressions or method references?
- [ ] Event payload - what data to include?
- [ ] Action methods - generate stubs or require manual implementation?

### To Decide in Phase 5
- [ ] AI agent integration pattern - Spring @EventListener or custom?
- [ ] When to generate interfaces vs full implementations?
- [ ] Contamination diagnosis - rule-based or ML model?

### Terminology Questions (for TERMINOLOGY_CLARIFICATIONS.md)
- [ ] DSL-First Development vs Vibe Meta-programming - when to use each?
- [ ] Meta-program vs Generator vs Builder - precise definitions?
- [ ] Model (DSL) vs Entity (JHipster) vs Aggregate (DDD) - context usage?

---

## Risks Being Monitored (from PREMORTEM.md)

**High Priority Risks**:
1. JHipster version incompatibility (Phase 1)
2. Generated code doesn't compile (Phase 2)
3. JHipster overwrites DSL enhancements (ongoing)
4. Regeneration destroys manual changes (ongoing)

**Medium Priority Risks**:
5. State machines too rigid (Phase 2-3)
6. DSL becomes too complex (Phase 3-5)
7. Scope creep (ongoing)

**Monitored Continuously**:
- Build time (target: < 5 min)
- Generated code percentage (target: 85%+)
- Documentation currency (target: < 5 min old)

---

## Vertical Slices Planned

### Phase 1: Strain Management
- Simplest entity (no state machine)
- Test CRUD operations

### Phase 2: Batch Lifecycle Tracking
- Create batch → progress through 10 phases → complete
- UI shows current phase and available actions
- **CRITICAL**: Proves DSL-First approach works

### Phase 3: Environmental Monitoring Dashboard
- Sensor readings validated
- Critical alerts trigger immediately
- Real-time status display

### Phase 4: Multi-Flush Harvest Cycle
- Harvest → Rehydrate → Harvest (repeat)
- UI shows flush history

### Phase 5: Contamination Management
- Secondary state machine
- AI extension hooks designed

### Phase 6: Supply Chain Traceability
- Complete supplier → customer chain
- Traceability report UI

### Phase 7: Financial Dashboard
- Profitability per batch
- Cost breakdown by category

---

## Process Experiments Tracking

### Experiment 1: JHipster + Custom DSL Integration
**Hypothesis**: They can coexist without conflicts if properly separated
**Measure**: Number of merge conflicts, regeneration issues
**Status**: Not started (Phase 1)

### Experiment 2: Generated Code Percentage
**Hypothesis**: 85%+ achievable with hybrid approach
**Measure**: LOC analysis (generated vs hand-written)
**Status**: Not started (will measure at Phase 9)

### Experiment 3: DSL Comprehension
**Hypothesis**: Domain experts can read DSL easily
**Measure**: Feedback from non-programmers
**Status**: Not started (Phase 2+)

### Experiment 4: Regeneration Friction
**Hypothesis**: Full regeneration takes < 5 minutes
**Measure**: Time tracking
**Status**: Phase 0 baseline: 3.8s (✅ well under target)

---

## Agent Roles & Current Assignments

### Architect Agent
**Current Phase**: Phase 0 complete
**Next Task**: Review JHipster compatibility matrix for Phase 1

### Generator Agent
**Current Phase**: Phase 0 complete (JavaPoet dependencies set up)
**Next Task**: Study jCrew_02 generators before Phase 2

### Domain Expert Agent
**Current Phase**: Phase 0 complete (JDL analyzed)
**Next Task**: Validate Batch lifecycle with real mushroom farming knowledge

### Documentation Agent
**Current Phase**: Phase 0 complete
**Next Task**: Create README.md for project overview

---

## Commit History

1. **7b4a15d** - Added DSL-First implementation plan (dev-docs/01-dsl-first-implementation-plan.md)
2. **5ff62c1** - Phase 0 foundation setup (36 files, 4,968 LOC)

---

## Next Session Continuity

### When Resuming Project
1. Read this file (AGENT_MEMORIES.md)
2. Read AGENTS.md for rules and workflows
3. Check current branch: `git branch --show-current`
4. Check current phase: Look at last commit message
5. Review plan: `dev-docs/01-dsl-first-implementation-plan.md`
6. Check todos in last commit or create new ones

### Environment Setup Checklist
1. Java 21 installed: `java -version`
2. Maven installed: `mvn -version`
3. Node.js + npm installed: `node -v && npm -v`
4. JHipster CLI installed: `jhipster --version`
5. Git configured: `git config --global user.name && git config --global user.email`

### Quick Start Commands
```bash
# Build project
mvn clean install

# Generate ANTLR parsers
cd mcms-dsl && mvn generate-sources

# Run tests
mvn clean test

# Check git status
git status
```

---

## Configuration & Settings

### Maven Settings
- Local repository: Default (~/.m2/repository)
- No custom settings.xml required
- All versions pinned in parent POM

### IDE Recommendations
- IntelliJ IDEA (preferred for Maven + ANTLR)
- Eclipse with M2E plugin
- VS Code with Java Extension Pack

### Git Configuration
- Line endings: CRLF on Windows, LF on Unix (auto-detected)
- Default branch: `main`
- Feature branches: `feature/phase-N-description`

---

**Last Updated**: 2026-02-12 23:15 (Phase 0 complete)
**Current Phase**: 0 (Foundation)
**Current Branch**: feature/phase-0-foundation
**Next Phase**: 1 (JHipster Scaffolding)
