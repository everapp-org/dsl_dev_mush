# CLAUDE.md - Instructions for Claude AI Assistant

**Purpose**: Claude-specific instructions for working on the MCMS project
**Last Updated**: 2026-02-12

---

## Before You Start

**CRITICAL**: Read these files in order before making any changes:

1. **dev-docs/AGENT_MEMORIES.md** - Complete conversation context and decisions (MUST READ FIRST)
2. **docs/AGENTS.md** - Collaboration rules and workflows
3. **dev-docs/01-dsl-first-implementation-plan.md** - Implementation roadmap
4. **README.md** - Project overview

---

## Project Context

This is **NOT** a typical software project. This is an **experiment in methodology**:
- **Primary Goal**: Refine DSL-First development process
- **Primary Deliverable**: PROCESS_LEARNINGS.md (not a perfect app)
- **Philosophy**: "Process improvement is the goal, not perfection"
- **Acceptable**: Hitting walls, pivoting, incomplete features
- **Encouraged**: Document learnings, try alternatives

---

## Critical Rules

### NEVER
1. ❌ **Never modify generated code manually** - It will be overwritten on regeneration
2. ❌ **Never skip state assertions** in transition methods - Leads to invalid states
3. ❌ **Never commit without regenerating documentation** - Docs must stay current
4. ❌ **Never use `System.out.println()`** - Use SLF4J logger
5. ❌ **Never edit files in `mcms-jhipster-base/` after JHipster generation** - Use composition
6. ❌ **Never hardcode configuration values** - Use properties or environment variables

### ALWAYS
1. ✅ **Always read AGENT_MEMORIES.md first** when resuming work
2. ✅ **Always assert current state before transitions** (`assertState(EXPECTED_STATE)`)
3. ✅ **Always emit events for state changes** (`emit(new EventName(...))`)
4. ✅ **Always log transitions at DEBUG level** (`logger.debug("TRANSITION: FROM → TO")`)
5. ✅ **Always commit DSL + generated code together** - Keep synchronized
6. ✅ **Always update AGENT_MEMORIES.md with learnings** - After each phase

---

## Documentation Workflow

### Living Documentation is MANDATORY

After **ANY** code change that affects behavior:

```bash
# 1. Run tests
mvn clean test

# 2. Regenerate documentation
cd mcms-codegen
mvn exec:java -Dexec.mainClass="com.mcms.codegen.documentation.DocGenerator"

# 3. Verify timestamp
cat LIVING_DOCUMENTATION.md | head -n 5

# 4. Commit code + docs together
git add .
git commit -m "[Phase-N] Description..."
```

**Documentation is NOT optional** - It's the specification.

### Agent Memories Must Be Current

After each phase completion:
1. Open `dev-docs/AGENT_MEMORIES.md`
2. Add new learnings to appropriate sections
3. Update "Current State" section
4. Update "Last Updated" timestamp
5. Commit with documentation updates

---

## Code Generation Workflow

### When to Regenerate

**Trigger Regeneration When**:
- DSL file modified (`*.dsl`)
- Grammar file modified (`*.g4`)
- Generator code modified (`*Generator.java`)
- Business rules changed

**Regeneration Commands**:
```bash
# ANTLR parsers
cd mcms-dsl && mvn generate-sources

# Custom generators
cd mcms-codegen && mvn install

# Verify compilation
cd mcms-domain-extensions && mvn compile

# Run tests
mvn clean test
```

### Composition Pattern (CRITICAL)

**CORRECT** - Use composition:
```java
@Component
public class BatchService {
    private final BatchRepository repository;      // JHipster
    private final BatchStateMachine stateMachine; // DSL-generated

    public void startColonization(Long batchId) {
        Batch batch = repository.findById(batchId);
        stateMachine.setBatch(batch);
        stateMachine.startColonization(); // DSL method
        repository.save(batch);
    }
}
```

**WRONG** - Don't use inheritance:
```java
// DON'T DO THIS - JHipster will overwrite
public class EnhancedBatch extends Batch {
    public void startColonization() { /* ... */ }
}
```

---

## State Machine Pattern (CRITICAL)

Every state transition method MUST follow this pattern:

```java
public void transitionName() {
    // 1. Log transition
    logger.debug("Entity[id={}] TRANSITION: FROM → TO | trigger=transitionName", this.id);

    // 2. Assert current state (MANDATORY)
    assertState(EXPECTED_FROM_STATE);

    // 3. Check guard conditions
    if (!guardCondition()) {
        throw new GuardFailedException("Guard condition not met");
    }

    // 4. Change state
    this.state = NEW_STATE;

    // 5. Emit event
    emit(new TransitionEvent(this.id, Instant.now()));

    // 6. Execute actions
    actionMethod1();
    actionMethod2();
}
```

**If generated code doesn't follow this pattern, the generator is broken - fix the generator, not the generated code.**

---

## Commit Message Format

**MUST** follow this format:

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
- Generated 10 domain event records
- Generated BatchStateMachineTest.java with 100% coverage

Generated: 12 files, 1,847 LOC
Learning: Composition pattern cleanly separates JHipster code from DSL-generated code
```

**Why this format?**
- Phase prefix enables filtering: `git log --grep="Phase-2"`
- Detailed changes serve as mini-documentation
- Generated stats track code generation progress
- Learning capture feeds into PROCESS_LEARNINGS.md

---

## File Organization

### Where Things Go

**DSL Definitions** → `mcms-dsl/src/main/resources/`
- `mcms-states.dsl` - State machines
- `mcms-rules.dsl` - Business rules
- `mcms-ai-hooks.dsl` - AI agent interfaces (future)

**Grammars** → `mcms-dsl/src/main/antlr4/com/mcms/dsl/`
- `KernelDSL.g4` - State machine grammar (Phase 2)
- `BusinessRules.g4` - Rules grammar (Phase 3)

**Generators** → `mcms-codegen/src/main/java/com/mcms/codegen/generator/`
- `StateMachineGenerator.java`
- `EventClassGenerator.java`
- `BusinessRuleGenerator.java`
- `TestGenerator.java`
- `DocGenerator.java`

**Generated Code** → `mcms-domain-extensions/target/generated-sources/mcms/`
- State machines, events, validators (NEVER edit manually)

**Hand-Written Code** → `mcms-application/src/main/java/`
- Orchestration, complex business logic (edit freely)

**JHipster Code** → `mcms-jhipster-base/src/main/java/`
- Entities, repositories, REST controllers (NEVER edit - regenerate)

---

## Testing Philosophy

### Tests ARE Documentation

Every test name should be readable as a specification:

```java
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
```

### Test the Generators, Not the Generated Code

**Priority 1**: Generator tests (most important)
- Test JavaPoet generators produce correct code
- If generator is correct, generated code is correct

**Priority 2**: Generated tests (auto-generated)
- 100% state transition coverage
- Guard condition tests
- Event emission tests

**Priority 3**: Integration tests (some generated, some manual)

**Priority 4**: End-to-end tests (manual)

---

## Error Handling

### Build Failures

```bash
# 1. Check Java version
java -version  # Must be 21.x.x

# 2. Check JAVA_HOME
echo $JAVA_HOME  # (Unix/Mac)
echo %JAVA_HOME% # (Windows)

# 3. Clean corrupted cache
rm -rf ~/.m2/repository/com/mcms

# 4. Force update
mvn clean install -U
```

### Generation Failures

```bash
# 1. Check DSL syntax
cd mcms-dsl && mvn generate-sources
# ANTLR will report line/column of syntax errors

# 2. Check generator logs
cd mcms-codegen && mvn clean install
# Look for JavaPoet exceptions

# 3. Verify generated code compiles
cd mcms-domain-extensions && mvn compile
```

### JHipster Conflicts

```bash
# 1. Always commit before JHipster operations
git commit -m "WIP: Before JHipster regeneration"

# 2. Run JHipster
cd mcms-jhipster-base
jhipster import-jdl ../docs/mushroom-farm-en.jdl

# 3. Review conflicts
git diff

# 4. Rollback if needed
git reset --hard HEAD
```

---

## Process Improvement Focus

### This is an Experiment

**Remember**:
- Primary goal is **process improvement**, not a perfect app
- Hitting walls provides **learning opportunities**
- Document insights in AGENT_MEMORIES.md **immediately**
- Count failures as **informative experiments**
- Pivot to alternative approaches if blocked > 2 hours

### Capture Learnings Continuously

**After each phase**:
1. What worked well?
2. What was harder than expected?
3. What would you do differently next time?
4. Update AGENT_MEMORIES.md with insights

**Don't wait until Phase 9** - capture learnings as you go.

---

## Useful Commands

```bash
# Build entire project
mvn clean install

# Build specific module
mvn clean install -pl mcms-dsl

# Generate ANTLR parsers
cd mcms-dsl && mvn generate-sources

# Run generators
cd mcms-codegen && mvn install

# Run all tests
mvn clean test

# Run specific module tests
mvn test -pl mcms-domain-extensions

# Check dependency tree
mvn dependency:tree

# Show recent commits
git log --oneline -n 10

# Filter by phase
git log --grep="Phase-2" --oneline

# Check current phase
git log --oneline -n 1
```

---

## Phase Checklist

### Before Starting New Phase

- [ ] Previous phase committed
- [ ] AGENT_MEMORIES.md updated
- [ ] All tests passing
- [ ] Documentation regenerated
- [ ] Git tag created: `git tag v0.1.0-phase-N`
- [ ] Read plan for next phase

### After Completing Phase

- [ ] Commit with `[Phase-N]` prefix
- [ ] Update AGENT_MEMORIES.md with learnings
- [ ] Review against premortem risks (docs/PREMORTEM.md)
- [ ] Update README.md phase badge

---

## Current Project Status

**Phase**: 0 (Foundation) - ✅ COMPLETE
**Branch**: `feature/phase-0-foundation`
**Commits**: 3 total
**Next Phase**: 1 (JHipster Scaffolding)

**Prerequisites for Phase 1**:
- [ ] JHipster CLI installed: `npm install -g generator-jhipster`
- [ ] Verify: `jhipster --version` (should show 8.x.x)

---

## Quick Start for New Session

```bash
# 1. Navigate to project
cd /path/to/dsl_dev_mush

# 2. Check current branch
git branch --show-current

# 3. Pull latest (if working with team)
git pull

# 4. Read agent memories
cat dev-docs/AGENT_MEMORIES.md

# 5. Check current status
git log --oneline -n 5

# 6. Build project
mvn clean install

# 7. Continue from current phase
```

---

## When in Doubt

1. **Read AGENT_MEMORIES.md** - Full context is there
2. **Read docs/AGENTS.md** - Collaboration rules
3. **Read PREMORTEM.md** - Common pitfalls and solutions
4. **Ask questions** - Better to clarify than assume

---

## Contact & Collaboration

**This file is for AI assistants** (Claude, GPT, etc.)

**For human developers**: See README.md and docs/AGENTS.md

---

**Last Updated**: 2026-02-12
**Maintained By**: Documentation Agent
**Project Phase**: 0 (Foundation Complete)
