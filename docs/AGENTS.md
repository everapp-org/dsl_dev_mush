# AGENTS.md - AI Agent Collaboration Rules for MCMS

**Project**: Mushroom Cultivation Management System (MCMS)
**Methodology**: DSL-First Development with JHipster Integration
**Version**: 0.1.0-SNAPSHOT
**Last Updated**: 2026-02-12

---

## Project Identity

**MCMS is:**
- **DSL-First**: Domain knowledge captured in DSLs before implementation
- **Domain-Driven**: DDD patterns (aggregates, value objects, events)
- **Code Generation Focused**: 85% generated, 15% hand-written
- **Process Experimentation**: Refining DSL-First methodology

**MCMS is NOT:**
- Traditional CRUD application with manual coding
- Prototype or throwaway code
- Pure JHipster app without custom DSL enhancements

---

## Project Structure (Screaming Architecture)

```
dsl_dev_mush/
├── mcms-dsl/                    # DSL definitions + ANTLR grammars
│   ├── src/main/antlr4/        # Grammar files (KernelDSL.g4, BusinessRules.g4)
│   └── src/main/resources/     # DSL files (mcms-states.dsl, mcms-rules.dsl)
├── mcms-codegen/                # JavaPoet generators
│   └── src/main/java/.../generator/
│       ├── StateMachineGenerator.java
│       ├── EventClassGenerator.java
│       ├── BusinessRuleGenerator.java
│       ├── TestGenerator.java
│       └── DocGenerator.java
├── mcms-jhipster-base/          # JHipster CRUD scaffold (GENERATED)
├── mcms-domain-extensions/      # DSL-generated enhancements (GENERATED)
│   └── target/generated-sources/mcms/  # State machines, events
├── mcms-application/            # Hand-written orchestration (MANUAL)
├── docs/                        # Documentation
│   ├── AGENTS.md               # This file
│   ├── PREMORTEM.md            # Anticipated failures
│   ├── PROCESS_LEARNINGS.md    # Methodology insights (PRIMARY deliverable)
│   └── mushroom-farm-en.jdl    # JHipster entity definitions
└── dev-docs/                    # Development plans
    └── 01-dsl-first-implementation-plan.md
```

**Folder Naming Philosophy**: Domain concepts (batch/, sensor/, contamination/), NOT technical layers (services/, utils/, helpers/)

---

## Development Workflow

### 1. UNDERSTAND
- Read the plan (`dev-docs/01-dsl-first-implementation-plan.md`)
- Identify which phase you're in (0-9)
- Review relevant DSL files and JDL specifications
- Check existing generated code

### 2. CHANGE
- **If structural change**: Modify JDL (`docs/mushroom-farm-en.jdl`)
- **If behavioral change**: Modify DSL (`mcms-dsl/src/main/resources/*.dsl`)
- **If generator change**: Modify JavaPoet generator (`mcms-codegen`)
- **If orchestration logic**: Modify hand-written code (`mcms-application`)
- **NEVER modify generated code directly** (will be overwritten)

### 3. GENERATE
```bash
cd mcms-dsl && mvn clean generate-sources  # ANTLR parsers
cd mcms-codegen && mvn clean install       # Run generators
cd mcms-domain-extensions && mvn clean compile  # Compile generated code
```

### 4. TEST
```bash
mvn clean test                             # Run all tests
mvn test -pl mcms-domain-extensions        # Test specific module
```

### 5. DOCUMENT
```bash
# Regenerate living documentation
cd mcms-codegen
mvn exec:java -Dexec.mainClass="com.mcms.codegen.documentation.DocGenerator"
```

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

## Agent Roles & Responsibilities

### Architect Agent
**Focus**: Design DSLs, semantic models, architecture patterns

**Responsibilities**:
- Design DSL syntax (balance expressiveness vs simplicity)
- Review semantic models for correctness
- Ensure DDD patterns (aggregates, value objects, events)
- Make architectural decisions (composition vs inheritance, etc.)

**Tools**:
- ANTLR grammar design
- Domain modeling
- Pattern selection

**Example Tasks**:
- Design state machine DSL for Batch lifecycle
- Decide how to integrate JHipster entities with generated state machines
- Review guard conditions and event emissions

---

### Generator Agent
**Focus**: Implement JavaPoet code generators

**Responsibilities**:
- Write JavaPoet generators following jCrew_02 patterns
- Implement generator unit tests (TDD approach)
- Integrate generators with Maven (generate-sources phase)
- Ensure generated code compiles and follows patterns

**Tools**:
- JavaPoet API
- ANTLR visitors
- Maven exec plugin

**Example Tasks**:
- Implement `StateMachineGenerator.java`
- Generate transition methods with assertions
- Create domain event records
- Generate test scaffolding

**Critical Pattern from jCrew_02**:
```java
// ALWAYS include in generated code:
logger.debug("Entity[id={}] TRANSITION: FROM → TO | trigger", this.id);
assertState(EXPECTED_FROM_STATE); // CRITICAL
this.state = NEW_STATE;
emit(new TransitionEvent(this.id, Instant.now()));
```

---

### Domain Expert Agent
**Focus**: Validate business logic and DSL readability

**Responsibilities**:
- Review DSL readability (can non-programmers understand?)
- Validate business logic (are state transitions correct?)
- Identify missing constraints and edge cases
- Provide domain knowledge about mushroom cultivation

**Tools**:
- DSL file review
- JDL specification review
- Business rule validation

**Example Tasks**:
- Verify 10-phase Batch lifecycle matches real mushroom farming
- Validate guard conditions (e.g., `substrateFullyInoculated`)
- Identify missing validation rules
- Review multi-flush rehydration logic

---

### Documentation Agent
**Focus**: Maintain living documentation

**Responsibilities**:
- Regenerate living docs after every test run
- Update PROCESS_LEARNINGS.md with methodology insights
- Maintain AGENTS.md (this file)
- Archive documentation with timestamps

**Tools**:
- DocGenerator
- Markdown
- Test results parser

**Example Tasks**:
- Generate `LIVING_DOCUMENTATION.md` from test results
- Update process learnings after each phase
- Create PlantUML state diagrams

---

## Testing Philosophy

### Tests ARE Documentation

Every test should be readable as a specification:

```java
@DisplayName("Batch State Machine")
class BatchStateMachineTest {

    @Nested
    @DisplayName("Phase Transitions")
    class PhaseTransitions {

        @Test
        @DisplayName("INOCULATION → EARLY_COLONIZATION: startColonization")
        void shouldTransitionToEarlyColonizationWhenSubstrateFullyInoculated() {
            // Given
            Batch batch = createBatch(INOCULATION);
            batch.setSubstrateFullyInoculated(true);

            // When
            batchStateMachine.startColonization();

            // Then
            assertThat(batch.getCurrentPhase()).isEqualTo(EARLY_COLONIZATION);
            assertEventEmitted(ColonizationStarted.class);
        }
    }
}
```

### Testing Pyramid

1. **Generator Tests** (most important)
   - Test JavaPoet generators produce correct code
   - If generator is correct, generated code is correct

2. **Generated Code Tests** (auto-generated)
   - 100% state transition coverage
   - Guard condition tests
   - Event emission tests

3. **Integration Tests** (some generated, some manual)
   - JHipster REST API tests
   - Multi-step workflows

4. **End-to-End Tests** (manual)
   - UI workflows
   - Complete business scenarios

---

## Code Quality Standards

### Object Calisthenics Markers

**ALWAYS mark violations with rationale**:

```java
// VIOLATION: Rule 8 (Max 2 instance variables) - 16 fields
//   Rationale: Domain model requires all fields from JDL definition
// COMPLIANCE: Rule 3 (Wrap primitives) - All primitives wrapped
// COMPLIANCE: Rule 6 (No abbreviations) - Full descriptive names used

public class Batch {
    private Long id;
    private String batchCode;
    private PhaseName currentPhase;
    // ... 13 more fields from JDL
}
```

### Logging Standards

**DEBUG level for all state transitions**:
```java
logger.debug("Batch[id={}] TRANSITION: {} → {} | trigger={}",
    this.id, oldState, newState, triggerName);
```

**INFO level for important business events**:
```java
logger.info("Batch[code={}] completed with yield {}kg, efficiency {}%",
    batchCode, totalYieldKg, biologicalEfficiencyPercent);
```

**ERROR level for violations**:
```java
logger.error("Invalid state transition attempted: {} → {} for Batch[id={}]",
    currentState, targetState, batchId);
```

**NEVER use `System.out.println()`**

---

## State Machine Rules (CRITICAL)

### ALWAYS Assert Current State

```java
public void transitionToNextPhase() {
    assertState(EXPECTED_CURRENT_STATE); // MANDATORY
    // ... rest of logic
}

private void assertState(PhaseName expected) {
    if (this.currentPhase != expected) {
        throw new IllegalStateException(
            "Expected phase " + expected + " but was " + this.currentPhase
        );
    }
}
```

### ALWAYS Emit Events

```java
public void complete() {
    assertState(HARVEST);
    this.currentPhase = COMPLETED;
    eventPublisher.publishEvent(new BatchCompleted(this.id, Instant.now()));
}
```

### ALWAYS Log Transitions

```java
logger.debug("Batch[id={}] TRANSITION: HARVEST → COMPLETED | trigger=complete",
    this.id);
```

---

## Git Workflow

### Commit Message Format

```
[Phase-N] Brief description (imperative mood)

- Detailed change 1
- Detailed change 2

Generated: X files, Y LOC
Learning: Key insight from this commit
```

### When to Commit

1. **After each phase completion** (mandatory)
2. **After each vertical slice** (recommended)
3. **When discovering a significant learning** (encouraged)
4. **Before risky changes** (good practice)

### What to Commit

✅ **COMMIT**:
- DSL files (`*.dsl`, `*.jdl`)
- Generated code (for review, debugging)
- Generator code (JavaPoet generators)
- Tests (manual and generated)
- Documentation (`*.md`)
- Build files (`pom.xml`)

❌ **DO NOT COMMIT**:
- IDE files (`.idea/`, `*.iml`, `.vscode/`)
- Build artifacts (`target/`, `*.jar`, `*.class`)
- Secrets (`.env`, credentials, API keys)
- OS files (`.DS_Store`, `Thumbs.db`)

---

## Code Review Checklist

Before committing, verify:

- [ ] DSL changes are syntactically valid
- [ ] Generators produce compilable code
- [ ] All tests pass (`mvn clean test`)
- [ ] Living documentation regenerated
- [ ] No manual edits to generated code
- [ ] State transitions follow CRITICAL PATTERN
- [ ] Logging at appropriate levels
- [ ] Object Calisthenics violations documented
- [ ] Commit message follows format
- [ ] Learning insight captured (if applicable)

---

## CRITICAL RULES

### NEVER

1. **Never modify generated code manually** (will be overwritten on regeneration)
2. **Never skip state assertions** (leads to invalid states)
3. **Never commit without regenerating documentation**
4. **Never use `System.out.println()` for logging**
5. **Never hardcode configuration values** (use properties, environment variables)
6. **Never commit secrets or credentials**

### ALWAYS

1. **Always assert current state before transitions**
2. **Always emit events for state changes**
3. **Always log transitions at DEBUG level**
4. **Always regenerate living docs after test changes**
5. **Always commit DSL + generated code together**
6. **Always mark Object Calisthenics violations with rationale**

---

## Workflow Example: Adding New State Machine

1. **Architect**: Design in `mcms-states.dsl`
   ```dsl
   model ContaminationEvent {
       states { DETECTED, ISOLATED, TREATED, RESOLVED, DISCARDED }
       transitions { /* ... */ }
   }
   ```

2. **Domain Expert**: Review for correctness
   - Are states realistic?
   - Are transitions valid?
   - Any missing edge cases?

3. **Architect**: Run generators
   ```bash
   cd mcms-dsl && mvn generate-sources
   cd mcms-codegen && mvn install
   ```

4. **Generator**: Verify generated code compiles
   ```bash
   cd mcms-domain-extensions && mvn compile
   ```

5. **All**: Run tests
   ```bash
   mvn clean test
   ```

6. **Documentation**: Regenerate living docs
   ```bash
   mvn exec:java -Dexec.mainClass="com.mcms.codegen.documentation.DocGenerator"
   ```

7. **All**: Review generated tests
   - 100% transition coverage?
   - All tests passing?

8. **All**: Commit together
   ```bash
   git add mcms-dsl/ mcms-codegen/ mcms-domain-extensions/ docs/
   git commit -m "[Phase-5] Add ContaminationEvent state machine

   - Created ContaminationEvent DSL with 5 states
   - Generated state machine with transition methods
   - Generated 5 domain events
   - Generated test suite with 100% coverage

   Generated: 8 files, 423 LOC
   Learning: Isolation state critical for contamination containment"
   ```

---

## Domain Model Relationships (Key Entities)

### Batch (Central Aggregate Root)
- **Lifecycle**: 10 phases (INOCULATION → COMPLETED)
- **Multi-Flush Support**: HARVEST → REHYDRATION_PAUSE → HARVEST (repeat)
- **Relationships**: Strain, SubstrateRecipe, PhaseExecution, FlushCycle, CostRecord

### Environmental Monitoring
- **Hierarchy**: Room → Sensor → SensorReading → EnvironmentalAlert
- **Real-time**: Critical alerts trigger immediately (temp > 35°C)

### Traceability Chain
```
Supplier → SupplyOrder → InventoryLot → BatchMaterialUsage → Batch → SalesOrderLine → Customer
```

### State Machines (Identified So Far)
1. **Batch**: 10-phase lifecycle (Phase 2)
2. **ContaminationEvent**: Detection → Resolution (Phase 5)
3. **PaymentStatus**: PENDING → PAID → OVERDUE (Future)
4. **TaskStatus**: ASSIGNED → IN_PROGRESS → COMPLETED (Future)

---

## Process Improvement Focus

### Experiments to Track

1. **JHipster + Custom DSL Integration**
   - Measure conflicts, regeneration issues
   - Document resolution strategies

2. **Generated Code Percentage**
   - LOC analysis (generated vs hand-written)
   - Target: 85%+

3. **DSL Comprehension**
   - Can non-programmers read DSL?
   - Feedback from domain experts

4. **Regeneration Friction**
   - Time to full regeneration
   - Target: < 5 minutes

### Learning Capture Points

- After each phase completion
- When hitting walls (expected!)
- When discovering better patterns
- When JHipster and DSL conflict
- When generators produce unexpected results

### PRIMARY Deliverable

**PROCESS_LEARNINGS.md** with:
- What worked well?
- What was harder than expected?
- 5+ actionable improvements to DSL_FIRST_DEVELOPMENT_GUIDE.md
- When to use JHipster vs DSL vs manual code

---

## Questions & Clarifications

### When to Use JHipster vs DSL vs Manual?

**JHipster (Structural)**:
- Entity definitions
- CRUD operations
- REST APIs
- Database migrations
- Basic UI scaffolding

**Custom DSL (Behavioral)**:
- State machines with guards
- Business rules and validation
- Domain events
- Complex workflows

**Manual Code (Orchestration)**:
- Complex business logic
- External integrations
- Framework-specific code
- Performance optimizations

### How to Handle Conflicts?

**JHipster overwrites enhancements**:
- Use composition, not inheritance
- Separate directories (`mcms-domain-extensions` vs `mcms-jhipster-base`)
- Never edit JHipster-generated files directly

**DSL changes break existing code**:
- Version DSL files
- Write generator tests first
- Regenerate all dependent code
- Run full test suite

---

## Contact & Support

This is an **experimental project** focused on **process improvement**.

**Expected behavior**:
- Hitting walls is INFORMATIVE
- Try alternative approaches
- Document learnings
- Iterate on methodology

**Remember**: Process improvement is the goal, not perfection.

---

**Last Updated**: 2026-02-12
**Version**: 0.1.0-SNAPSHOT
**Phase**: 0 (Foundation)
