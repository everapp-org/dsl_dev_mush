# Mushroom Farm Application - DSL-First Implementation Plan

**Project**: Mushroom Cultivation Management System (MCMS)
**Goal**: Refine DSL-First methodology while building a functional application
**Strategy**: Blend JHipster code generation with custom DSL-First approach
**Target**: 85% generated code, 15% hand-written

## Executive Summary

Use **JHipster JDL** (already defined: 25 entities in `mushroom-farm-en.jdl`) as the structural DSL for CRUD operations, then create **complementary behavioral DSLs** for state machines, business rules, and AI integration hooks. This hybrid approach maximizes code generation while allowing deep methodology experimentation.

## Architecture Strategy: Parallel Enhancement

```
JHipster JDL → JHipster CLI → Entities, Repositories, REST APIs, Frontend (80%)
                                          ↓
Custom Behavioral DSLs → ANTLR + JavaPoet → State Machines, Events, Validators (15%)
                                          ↓
Manual Orchestration → Hand-written Integration (5%)
```

### Module Structure

```
dsl_dev_mush/
├── mcms-dsl/                    # DSL definitions + ANTLR grammars
│   ├── src/main/antlr4/        # KernelDSL.g4, BusinessRules.g4
│   └── src/main/resources/     # mcms-states.dsl, mcms-rules.dsl
├── mcms-codegen/                # JavaPoet generators (from jCrew_02 patterns)
│   └── src/main/java/.../generator/
│       ├── StateMachineGenerator.java
│       ├── EventClassGenerator.java
│       ├── BusinessRuleGenerator.java
│       ├── TestGenerator.java
│       └── DocGenerator.java
├── mcms-jhipster-base/          # JHipster generated code (CRUD scaffold)
├── mcms-domain-extensions/      # DSL-generated enhancements (state machines, events)
├── mcms-application/            # Hand-written orchestration (5% only)
└── docs/
    ├── AGENTS.md                # AI agent rules and workflows
    ├── PREMORTEM.md             # Anticipated failure modes
    ├── PROCESS_LEARNINGS.md     # PRIMARY deliverable for methodology
    └── POSTMORTEM.md            # Actual vs predicted outcomes
```

**Separation Principle**: JHipster owns structure (entities, repos, REST), Custom DSLs own behavior (state machines, validation), Manual code owns orchestration (complex logic).

## DSL Strategy: Three-Tier Architecture

### Tier 1: Structural DSL (JHipster JDL) - EXISTS
- **File**: `docs/mushroom-farm-en.jdl`
- **Entities**: 25 (Strain, Batch, FlushCycle, Room, Sensor, Customer, Supplier, etc.)
- **Generates**: JPA entities, Spring Data repos, REST controllers, Angular/React UI
- **Tool**: `jhipster import-jdl`

### Tier 2: Behavioral DSL - TO CREATE
- **File**: `mcms-dsl/src/main/resources/mcms-states.dsl`
- **Grammar**: `mcms-dsl/src/main/antlr4/.../KernelDSL.g4` (adapted from jCrew_02)
- **Purpose**: State machines with explicit transitions, guards, events, actions

**Example** (Batch lifecycle with 10 phases):
```dsl
domain MushroomFarm {
  level production {
    model Batch {
      description "Production batch lifecycle with 10-phase enforcement"

      states {
        INOCULATION, EARLY_COLONIZATION, FULL_COLONIZATION,
        CONSOLIDATION, FRUITING_TRIGGER, PRIMORDIA,
        FRUITING_BODY_GROWTH, HARVEST, REHYDRATION_PAUSE, COMPLETED
      }

      transitions {
        INOCULATION -> EARLY_COLONIZATION on startColonization
          if substrateFullyInoculated == true
          emits ColonizationStarted
          do logPhaseStart, updatePhaseExecution

        HARVEST -> REHYDRATION_PAUSE on rehydrate
          if flushNumber < maxFlushes
          emits RehydrationStarted
          do addWater, pauseProduction

        HARVEST -> COMPLETED on endBatch
          if flushNumber >= maxFlushes || yieldDropsBelowThreshold
          emits BatchCompleted
          do finalizeYieldCalculations, generateReport
      }
    }
  }
}
```

### Tier 3: Business Rules DSL - TO CREATE
- **File**: `mcms-dsl/src/main/resources/mcms-rules.dsl`
- **Purpose**: Declarative validation, constraints, alerts

**Example**:
```dsl
rules MushroomFarmRules {
  entity EnvironmentalReading {
    rule "Temperature critical range" {
      validate tempC >= 10 && tempC <= 35
      error "Temperature {tempC}°C outside safe range (10-35°C)"
      severity CRITICAL
      action triggerEnvironmentalAlert
    }
  }
}
```

### Tier 4: AI Integration Hooks - DESIGN NOW, IMPLEMENT LATER
- **File**: `mcms-dsl/src/main/resources/mcms-ai-hooks.dsl` (aspirational)
- **Strategy**: Generate interfaces now, implement AI agents in Phase 2 of project lifecycle

## Implementation Phases (9 Phases)

### Phase 0: Foundation & Setup
**Goal**: Project structure, tooling, premortem

**Critical Files**:
1. `pom.xml` - Parent POM with 6 modules
2. `mcms-dsl/pom.xml` - ANTLR plugin (generate-sources phase)
3. `mcms-codegen/pom.xml` - JavaPoet dependencies
4. `docs/AGENTS.md` - AI agent collaboration rules
5. `docs/PREMORTEM.md` - Anticipated failure modes

**Commands**:
```bash
git checkout -b feature/phase-0-foundation
mvn clean install
```

**Success Criteria**:
- Maven build succeeds across all modules
- JHipster CLI available (`jhipster --version`)
- ANTLR generates parsers from test grammar

**Learning Focus**: Does modular structure support parallel development?

---

### Phase 1: JHipster Scaffolding (80% of app)
**Goal**: Generate complete CRUD application

**Commands**:
```bash
cd mcms-jhipster-base
jhipster import-jdl ../docs/mushroom-farm-en.jdl
```

**Generated** (automatic):
- 25 JPA entities with relationships
- Spring Data JPA repositories (100% generated)
- REST controllers with Swagger documentation
- Angular/React frontend with forms, tables, routing
- JUnit integration tests
- Liquibase database migrations

**Vertical Slice 1: Strain Management**
- Why first: Simplest entity (no state machine), foundational
- Test: CRUD operations via REST API and UI

**Success Criteria**:
- All 25 entities accessible via Swagger UI (`/swagger-ui.html`)
- Frontend displays all entity lists and forms
- Database schema created by Liquibase

**Learning Focus**: What does JHipster give us "for free"? What percentage is truly generated?

**Commit**: `[Phase-1] JHipster scaffolding - 25 entities generated`

---

### Phase 2: Batch State Machine (THE CRITICAL PHASE)
**Goal**: Implement 10-phase Batch lifecycle with explicit state machine

**Why Critical**: This phase proves the DSL-First approach works alongside JHipster. If successful, patterns repeat for remaining entities.

**Critical Files** (in order):
1. `mcms-dsl/src/main/antlr4/com/mcms/dsl/KernelDSL.g4` - Grammar (copy from jCrew_02, adapt)
2. `mcms-dsl/src/main/resources/mcms-states.dsl` - Batch state machine definition
3. `mcms-codegen/src/main/java/com/mcms/codegen/model/StateMachineDef.java` - Semantic model
4. `mcms-codegen/src/main/java/com/mcms/codegen/extractor/StateMachineExtractor.java` - ANTLR visitor
5. `mcms-codegen/src/main/java/com/mcms/codegen/generator/StateMachineGenerator.java` - JavaPoet generator
6. `mcms-codegen/src/main/java/com/mcms/codegen/generator/EventClassGenerator.java` - Event records
7. `mcms-domain-extensions/src/main/java/com/mcms/statemachine/BatchStateMachine.java` - GENERATED OUTPUT
8. `mcms-domain-extensions/src/test/java/com/mcms/statemachine/BatchStateMachineTest.java` - GENERATED TESTS

**Integration Strategy** (composition, not inheritance):
```java
// JHipster entity (already exists from Phase 1)
@Entity
public class Batch {
    @Id private Long id;
    @Enumerated(EnumType.STRING)
    private PhaseName currentPhase; // JHipster enum
    // ... other JPA fields
}

// DSL-generated enhancement (separate class)
@Component
public class BatchStateMachine {
    @Autowired private BatchRepository batchRepository;
    @Autowired private ApplicationEventPublisher eventPublisher;

    private Batch batch; // Composition

    public BatchStateMachine(Batch batch) {
        this.batch = batch;
    }

    // GENERATED METHOD
    public void startColonization() {
        logger.debug("Batch[id={}] TRANSITION: INOCULATION -> EARLY_COLONIZATION", batch.getId());
        assertState(INOCULATION); // CRITICAL: Always assert
        if (!substrateFullyInoculated()) {
            throw new GuardFailedException("Substrate not fully inoculated");
        }
        batch.setCurrentPhase(EARLY_COLONIZATION);
        batchRepository.save(batch);
        eventPublisher.publishEvent(new ColonizationStarted(batch.getId(), Instant.now()));
        logPhaseStart(); // Action method
    }
}
```

**Generated Artifacts**:
- `BatchStateMachine.java` - 10 transition methods
- `ColonizationStarted.java`, `BatchCompleted.java`, etc. - 10 domain event records
- `BatchStateMachineTest.java` - 100% transition coverage

**Vertical Slice 2: Batch Lifecycle Tracking**
- Create batch → progress through 10 phases → complete
- UI enhancement: Add "Available Actions" buttons (startColonization, triggerFruiting, etc.)
- Invalid transitions prevented (e.g., can't skip from INOCULATION to FRUITING)

**Success Criteria**:
- Batch cannot skip phases (enforced by state machine)
- All 10 transitions have passing tests
- Domain events logged to event store
- UI shows current phase and available actions

**Learning Focus**:
- How do we blend JHipster entities with generated state machines?
- Composition vs inheritance trade-offs?
- How to prevent JHipster from overwriting enhancements on regeneration?

**Commit**: `[Phase-2] Batch state machine - 10 phases, 10 events, 100% test coverage`

---

### Phase 3: Business Rules & Validation
**Goal**: Generate declarative validators for environmental monitoring

**Critical Files**:
1. `mcms-dsl/src/main/antlr4/com/mcms/dsl/BusinessRules.g4` - Rules grammar
2. `mcms-dsl/src/main/resources/mcms-rules.dsl` - Validation rules
3. `mcms-codegen/src/main/java/com/mcms/codegen/generator/BusinessRuleGenerator.java` - Validator generator

**Generated**:
- `EnvironmentalReadingValidator.java` - Spring `Validator` implementation
- `BatchValidator.java` - Batch-specific rules

**Vertical Slice 3: Environmental Monitoring Dashboard**
- Sensor readings validated against rules
- Critical alerts (temp > 35°C) trigger immediately
- Dashboard shows real-time sensor status with color coding

**Success Criteria**:
- Invalid sensor data rejected with clear error messages
- Rule changes require only DSL edit + regeneration (no code changes)
- At least 5 validation rules active

**Learning Focus**: How expressive can declarative rules be? When must we drop to code?

---

### Phase 4: Multi-Flush Harvest Cycle
**Goal**: FlushCycle hierarchy with rehydration pauses

**Vertical Slice 4**:
- Harvest → Rehydrate → Harvest again (up to 5 flushes per batch)
- UI shows flush history and yield per flush

---

### Phase 5: Contamination Management
**Goal**: Secondary state machine for ContaminationEvent + AI extension hooks

**AI Hook Example**:
```java
// GENERATED INTERFACE (from future mcms-ai-hooks.dsl)
public interface ContaminationDiagnosticAgent {
    ContaminationAction diagnoseTreatment(ContaminationEvent event);
    RiskScore predictRisk(Batch batch);
}

// MANUAL PLACEHOLDER (Phase 5)
@Component
public class RuleBasedDiagnostic implements ContaminationDiagnosticAgent {
    // Simple heuristics for now, AI implementation in Phase 2 of project
}
```

---

### Phase 6: Supply Chain Traceability
**Goal**: Supplier → SupplyOrder → InventoryLot → Batch → SalesOrder → Customer

**Vertical Slice 6**:
- Trace any sold product back to supplier via complete chain
- UI: "Traceability Report" page

---

### Phase 7: Financial Dashboard
**Goal**: CostRecord aggregation, profitability per batch

**Vertical Slice 7**:
- Dashboard showing top 10 most profitable batches
- Cost breakdown by category (substrate, labor, utilities, etc.)

---

### Phase 8: Living Documentation System
**Goal**: Auto-generate documentation from DSL + test results (pattern from jCrew_02)

**Critical Files**:
1. `mcms-codegen/src/main/java/com/mcms/codegen/documentation/DocGenerator.java`
2. `LIVING_DOCUMENTATION.md` - Generated output

**Generator Pattern**:
```java
public class McmsDocumentationGenerator {
    public static void main(String[] args) {
        // Parse Surefire XML test reports
        TestResults results = new TestResultsParser("target/surefire-reports").parse();

        // Parse DSL files
        StateMachineModel model = new StateMachineExtractor()
            .extract("mcms-dsl/src/main/resources/mcms-states.dsl");

        // Generate markdown
        MarkdownBuilder md = new MarkdownBuilder();
        md.header(1, "MCMS Living Documentation");
        md.paragraph("Generated: " + LocalDateTime.now());

        for (ModelDef m : model.getModels()) {
            md.header(2, m.getName() + " State Machine");
            md.paragraph("States: " + String.join(" | ", m.getStates()));

            for (TransitionDef t : m.getTransitions()) {
                String status = results.isPassed(t) ? "✅" : "❌";
                md.listItem(status + " " + t.getFromState() + " → " +
                            t.getToState() + ": " + t.getTrigger());
            }
        }

        md.writeTo("LIVING_DOCUMENTATION.md");
    }
}
```

**Maven Integration**:
```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>exec-maven-plugin</artifactId>
    <executions>
        <execution>
            <id>generate-living-docs</id>
            <phase>test</phase>
            <goals><goal>java</goal></goals>
            <configuration>
                <mainClass>com.mcms.codegen.documentation.DocGenerator</mainClass>
            </configuration>
        </execution>
    </executions>
</plugin>
```

**Success Criteria**:
- Documentation regenerates in < 30 seconds
- Test status synchronized (✅/❌ indicators)
- PlantUML state diagrams auto-generated
- Timestamped archives (`LIVING_DOCUMENTATION_2026-02-12.md`)

**Learning Focus**: What documentation formats provide most value?

---

### Phase 9: Retrospective & Process Refinement (PRIMARY DELIVERABLE)
**Goal**: Capture methodology learnings to improve DSL-First approach

**Documents to Create**:

1. **PROCESS_LEARNINGS.md** (PRIMARY DELIVERABLE)
   - What worked well? What was harder than expected?
   - Specific recommendations for updating `DSL_FIRST_DEVELOPMENT_GUIDE.md`
   - When to use JHipster vs custom DSL vs hand-written code

2. **DSL_DESIGN_PATTERNS.md**
   - Reusable patterns discovered during mushroom domain modeling
   - Applicable to other agricultural/IoT domains?
   - Anti-patterns to avoid

3. **POSTMORTEM.md**
   - Compare with premortem predictions (what actually went wrong?)
   - Actual vs estimated generated code percentage (target: 85%)
   - Which failure modes occurred vs anticipated?

4. **TERMINOLOGY_CLARIFICATIONS.md**
   - DSL-First Development vs Vibe Meta-programming (when to use each term)
   - Meta-program vs Generator vs Builder (precise definitions)
   - Model (DSL) vs Entity (JHipster) vs Aggregate (DDD) (context-specific usage)

**Process Experiments to Document**:

**Experiment 1: JHipster + Custom DSL Integration**
- Hypothesis: They can coexist without conflicts if properly separated
- Measure: Number of merge conflicts, regeneration issues, resolution strategies
- Result: [To be filled]

**Experiment 2: Generated Code Percentage**
- Hypothesis: 85%+ achievable with hybrid approach
- Measure: LOC analysis (generated vs hand-written)
- Result: [To be filled]

**Experiment 3: DSL Comprehension by Non-Programmers**
- Hypothesis: Domain experts can read and validate DSL easily
- Measure: Feedback from agricultural specialists
- Result: [To be filled]

**Experiment 4: Regeneration Friction**
- Hypothesis: Full regeneration takes < 5 minutes
- Measure: Time tracking for clean build + generation
- Result: [To be filled]

**Success Criteria**:
- At least 5 actionable improvements identified for DSL_FIRST_DEVELOPMENT_GUIDE.md
- Clear guidance on when to use each approach (JHipster/DSL/manual)
- Reusable patterns documented for future projects

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

## AGENTS.md Rules & Workflows

### Core Principles
1. **DSL is Source of Truth** - Never edit generated code manually
2. **Commit DSL + Generated Code Together** - Keep synchronized
3. **Living Documentation is Mandatory** - Regenerate after every code change
4. **Test Generators, Not Generated Code** - Focus on generator correctness

### Agent Roles

**Architect Agent**:
- Design DSL syntax (balance expressiveness vs simplicity)
- Review semantic models for correctness
- Ensure DDD patterns (aggregates, value objects, events)

**Generator Agent**:
- Implement JavaPoet generators
- Write generator unit tests
- Integrate with Maven (generate-sources phase)

**Domain Expert Agent**:
- Review DSL readability (can non-programmers understand?)
- Validate business logic (are state transitions correct?)
- Identify missing constraints

**Documentation Agent**:
- Regenerate living docs after every test run
- Update PROCESS_LEARNINGS.md with insights
- Maintain AGENTS.md

### Workflow: Adding New State Machine

1. **Architect**: Design in `mcms-states.dsl` (states, transitions, guards, events)
2. **Domain Expert**: Review for domain correctness
3. **Architect**: Run `mvn generate-sources` (ANTLR + JavaPoet generators)
4. **Generator**: Verify generated code compiles
5. **All**: Run `mvn clean test`
6. **Documentation**: Run `DocGenerator` to update `LIVING_DOCUMENTATION.md`
7. **All**: Review generated tests (should be 100% passing)
8. **All**: Commit DSL + generated code + documentation together

### State Machine Implementation Rules (from jCrew_02)

**CRITICAL PATTERN**:
```java
public void transition() {
    logger.debug("Entity[id={}] TRANSITION: FROM → TO | trigger=eventName", this.id);
    assertState(EXPECTED_FROM_STATE); // ALWAYS assert current state first
    this.state = NEW_STATE;
    emit(new TransitionEvent(this.id, Instant.now()));
    // Action methods called here
}
```

**NEVER**:
- Skip state assertions (leads to invalid states)
- Modify generated code manually (will be overwritten)
- Commit code without regenerating documentation

## Git Workflow

### Branching Strategy
- `main` - Production-ready code
- `develop` - Integration branch
- `feature/phase-N-description` - Feature branches per phase

### Commit Convention (Conventional Commits)
```
[Phase-N] Brief description (imperative mood)

- Detailed change 1
- Detailed change 2

Generated: X files, Y LOC
Learning: Key insight from this commit
```

**Example**:
```
[Phase-2] Implement Batch state machine with 10 phases

- Created mcms-states.dsl with Batch lifecycle definition
- Generated BatchStateMachine.java with transition methods
- Generated 10 domain event records (ColonizationStarted, etc.)
- Generated BatchStateMachineTest.java with 100% coverage

Generated: 12 files, 1,847 LOC
Learning: Composition pattern (BatchStateMachine wraps Batch entity)
          cleanly separates JHipster code from DSL-generated code
```

### Commit Frequency
- **After each phase completion** (mandatory)
- **After each vertical slice** (recommended)
- **When learning something significant** (optional, encouraged)

### What to Commit
- ✅ DSL files (`*.dsl`)
- ✅ Generated code (for review, debugging, version control)
- ✅ Generator code (JavaPoet generators)
- ✅ Tests (both manual and generated)
- ✅ Documentation (`*.md`)
- ✅ Build files (`pom.xml`)
- ❌ IDE files (`.idea/`, `*.iml`)
- ❌ Build artifacts (`target/`, `*.jar`)
- ❌ Secrets (`.env`, credentials)

## Verification & Testing Strategy

### Testing Pyramid

**Layer 1: Generator Tests** (most important)
- Unit tests for each JavaPoet generator
- Example: `StateMachineGeneratorTest` validates JavaPoet output
- If generator is correct, generated code is correct

**Layer 2: Generated Code Tests** (auto-generated)
- State machine transition tests (generated by TestGenerator)
- 100% coverage of all transitions
- Example: `BatchStateMachineTest` with all 10 transitions

**Layer 3: Integration Tests** (some generated, some manual)
- JHipster generates basic REST API tests
- Manual tests for complex workflows (e.g., multi-flush harvest)

**Layer 4: End-to-End Tests** (manual)
- UI workflows (create batch → complete lifecycle)
- Traceability chain validation

### CI/CD Pipeline (Future)
```
1. Checkout code
2. Run mvn clean (clear generated code)
3. Run mvn generate-sources (ANTLR + JavaPoet generators)
4. Run mvn compile (verify generated code compiles)
5. Run mvn test (run all tests)
6. Run DocGenerator (regenerate documentation)
7. Verify documentation is current (fail if stale)
8. Package application
```

## Extension Points for Future AI Agents

### Design Principle
Create **interfaces NOW**, implement **agents LATER** (not in these 9 phases).

### Example: Contamination Diagnostic Agent

```java
// GENERATED INTERFACE (from future mcms-ai-hooks.dsl)
package com.mcms.ai;

public interface ContaminationDiagnosticAgent {
    /**
     * Analyzes contamination event and recommends treatment.
     * Default: rule-based heuristics
     * Future: LLM-powered analysis of photos, environmental data
     */
    ContaminationAction diagnoseTreatment(ContaminationEvent event);

    /**
     * Predicts contamination risk for a batch.
     * Future: ML model trained on historical data
     */
    RiskScore predictRisk(Batch batch, List<SensorReading> recentReadings);
}

// MANUAL PLACEHOLDER IMPLEMENTATION (Phase 5)
@Component
public class RuleBasedDiagnostic implements ContaminationDiagnosticAgent {
    @Override
    public ContaminationAction diagnoseTreatment(ContaminationEvent event) {
        // Simple rules for now
        if (event.getType() == TRICHODERMA && event.getSeverity() == LOW) {
            return SALT_TREATMENT;
        }
        return ISOLATE;
    }

    @Override
    public RiskScore predictRisk(Batch batch, List<SensorReading> readings) {
        // Placeholder: always low risk
        return RiskScore.LOW;
    }
}
```

### Event Bus Pattern for AI Agents

```java
// Domain events (generated from mcms-states.dsl)
@DomainEvent
public record ColonizationStarted(Long batchId, Instant timestamp) {}

// AI agent subscribes to events (manual, future implementation)
@Component
public class ContaminationMonitoringAgent {
    @EventListener
    public void onColonizationStarted(ColonizationStarted event) {
        Batch batch = batchRepository.findById(event.batchId());
        RiskScore risk = diagnosticAgent.predictRisk(batch, recentReadings);

        if (risk.isHigh()) {
            alertService.notify("High contamination risk: " + batch.getBatchCode());
        }
    }
}
```

### AI Capabilities (Design Only)
- **Environmental Monitoring**: Anomaly detection in sensor data streams
- **Contamination Diagnosis**: Image analysis + recommendation engine
- **Yield Optimization**: Recommend substrate recipes, environmental parameters
- **Predictive Maintenance**: Predict equipment failures from sensor trends
- **Supply Chain Optimization**: Suggest ordering schedule based on demand forecasts

## Premortem: Anticipated Failure Modes

### Risk 1: JHipster Version Incompatibility
**Symptom**: JHipster generator fails or produces incompatible code
**Mitigation**: Pin JHipster version in `.yo-rc.json`, test upgrades in separate branch
**Recovery**: Rollback to last known good version

### Risk 2: Generated Code Doesn't Compile
**Symptom**: JavaPoet generates syntactically invalid code
**Mitigation**: Write generator unit tests first (TDD), test with minimal DSL examples
**Recovery**: Fix generator, regenerate

### Risk 3: State Machines Too Rigid
**Symptom**: Real-world scenarios require manual overrides (emergency transitions)
**Mitigation**: Add "escape hatch" methods (`forceTransition()` with logging)
**Recovery**: Document cases where manual intervention was needed, refine DSL

### Risk 4: DSL Becomes Too Complex
**Symptom**: DSL files hard to read, domain experts confused
**Mitigation**: Regular reviews, remove unused features, prioritize readability
**Recovery**: Simplify DSL, move complexity to generators

### Risk 5: Regeneration Destroys Manual Changes
**Symptom**: Developer edits generated file, changes lost on regeneration
**Mitigation**: Clear `// GENERATED - DO NOT EDIT` headers, separate directories
**Recovery**: Restore from git, move changes to hand-written code

### Risk 6: Scope Creep (25 Entities)
**Symptom**: Implementation takes longer than expected, perfectionism delays
**Mitigation**: Focus on vertical slices, "good enough" beats "perfect"
**Recovery**: Reduce scope, defer non-critical entities to Phase 2

## Success Criteria

### Quantitative Metrics
- ✅ 85%+ of code generated (measure via LOC analysis)
- ✅ Zero merge conflicts between JHipster and custom generators
- ✅ < 5 minutes to regenerate all code (`mvn clean generate-sources`)
- ✅ 100% state transition test coverage for all state machines
- ✅ Living documentation always current (< 5 min old timestamp)

### Qualitative Metrics (PRIMARY GOAL)
- ✅ At least 5 actionable improvements to DSL_FIRST_DEVELOPMENT_GUIDE.md
- ✅ Clear terminology clarifications documented (DSL-First vs Vibe Meta-programming)
- ✅ Reusable patterns identified and documented (applicable to other domains)
- ✅ AI extension points designed (interfaces exist, even if not implemented)
- ✅ Methodology experiments documented with results

## Next Steps

1. **Review this plan** - Confirm alignment with goals
2. **Create branch** - `git checkout -b feature/phase-0-foundation`
3. **Write PREMORTEM.md** - Document anticipated failure modes
4. **Set up Phase 0** - Maven structure, ANTLR, JavaPoet dependencies
5. **Begin Phase 1** - JHipster scaffolding (80% of app)

---

**Remember**: This is an **experiment**. Hitting walls is informative. Document learnings, try another way if needed. Process improvement is the goal, not perfection.
