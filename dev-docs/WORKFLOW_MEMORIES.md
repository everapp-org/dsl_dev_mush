# WORKFLOW MEMORIES - MCMS Project

**Purpose**: Capture all process decisions, workflow patterns, and methodology choices
**Last Updated**: 2026-02-12 (Phase 0 complete)

---

## Workflow Patterns Established

### Git Branching Strategy

**Decision**: Feature branches per phase
**Pattern**:
- `main` - Production-ready code (stable)
- `develop` - Integration branch (future)
- `feature/phase-N-description` - One branch per phase

**Example**: `feature/phase-0-foundation`

**Rationale**:
- Clear phase separation
- Easy to rollback to previous phase
- Facilitates experimentation (can abandon failed branches)

**Merge Strategy**:
- Squash merge feature branches to main (keeps history clean)
- Tag each phase completion: `v0.1.0-phase-0`, `v0.1.0-phase-1`, etc.

---

### Commit Message Convention

**Format**:
```
[Phase-N] Brief description (imperative mood)

- Detailed change 1
- Detailed change 2

Generated: X files, Y LOC
Learning: Key insight from this commit
```

**Examples**:
```
[Phase-0] Foundation setup - Maven structure, ANTLR, documentation

- Created parent POM with 6 modules
- Set up ANTLR 4.13.1 with test grammar
- Created AGENTS.md and PREMORTEM.md

Generated: 36 files, 4,968 LOC
Learning: ANTLR successfully generates all parser components.
          Modular structure supports clean separation.
```

**Rationale**:
- Phase prefix enables easy filtering (`git log --grep="Phase-2"`)
- Detailed changes serve as mini-documentation
- Generated stats track code generation progress
- Learning capture feeds into PROCESS_LEARNINGS.md

---

### File Organization Decisions

**Configuration & Settings**:
- `.mvn/maven.config` - Maven command-line options
- `.gitignore` - Standard Maven + IDE exclusions
- `pom.xml` files - All dependency versions pinned in parent

**Documentation Hierarchy**:
```
docs/               # Domain & specifications
├── AGENTS.md       # Collaboration rules
├── PREMORTEM.md    # Risk analysis
├── POSTMORTEM.md   # Phase 9 retrospective
└── *.jdl           # JHipster domain definitions

dev-docs/           # Process & development
├── AGENT_MEMORIES.md       # Conversation context
├── SETUP_GUIDE.md          # Installation guide
├── WORKFLOW_MEMORIES.md    # This file
└── NN-*.md                 # Numbered plans/guides
```

**Rationale**:
- `docs/` = stakeholder-facing (domain experts, managers)
- `dev-docs/` = developer-facing (AI agents, programmers)
- Numbered files in `dev-docs/` maintain sequence

---

### Code Generation Workflow

**Pattern**:
```
1. Modify DSL file(s) → mcms-dsl/src/main/resources/*.dsl
2. Generate parsers → cd mcms-dsl && mvn generate-sources
3. Run generators → cd mcms-codegen && mvn install
4. Verify compilation → cd mcms-domain-extensions && mvn compile
5. Run tests → mvn clean test
6. Update docs → (auto or manual)
7. Commit → DSL + generated code + docs together
```

**Critical Rule**: **Always commit DSL and generated code in same commit**
- Prevents drift between DSL and generated artifacts
- Makes code review easier (see what DSL produced)
- Facilitates debugging (can trace generated code to DSL)

---

### Testing Strategy

**Layer 1: Generator Tests** (most important)
- Location: `mcms-codegen/src/test/java`
- Test JavaPoet generators produce correct code
- If generator is correct, generated code is correct

**Layer 2: Generated Code Tests** (auto-generated)
- Location: `mcms-domain-extensions/src/test/java` (GENERATED)
- 100% state transition coverage
- Naming: `@DisplayName("FROM → TO: trigger")`

**Layer 3: Integration Tests** (some generated, some manual)
- Location: `mcms-application/src/test/java` (MANUAL)
- JHipster generates REST API tests
- Manual tests for complex workflows

**Layer 4: End-to-End Tests** (manual)
- Location: `mcms-application/src/test/java` (MANUAL)
- UI workflows
- Complete business scenarios

---

### Documentation Update Workflow

**Living Documentation**:
```
1. Modify code → change behavior
2. Run tests → mvn clean test
3. Generate docs → mvn exec:java -Dexec.mainClass="...DocGenerator"
4. Review → check LIVING_DOCUMENTATION.md
5. Commit → code + tests + docs together
```

**Agent Memories**:
- Update `dev-docs/AGENT_MEMORIES.md` after each phase
- Capture learnings immediately (don't wait until Phase 9)
- Include decision rationale, not just facts

**Process Learnings**:
- Draft insights in AGENT_MEMORIES.md during development
- Consolidate in PROCESS_LEARNINGS.md during Phase 9
- Format: "What worked well? What was harder than expected? Recommendations?"

---

### Separation of Concerns

**JHipster-Generated Code** (in `mcms-jhipster-base/`):
- ✅ Can be regenerated without losing work
- ❌ Never edit manually
- ❌ Never add custom methods
- Example: `Batch.java` (JPA entity)

**DSL-Generated Code** (in `mcms-domain-extensions/`):
- ✅ Can be regenerated from DSL
- ❌ Never edit manually
- ✅ Can be reviewed in code review
- Example: `BatchStateMachine.java` (state machine)

**Hand-Written Code** (in `mcms-application/`):
- ✅ Edit freely
- ✅ Custom business logic
- ❌ Don't duplicate generated code
- Example: Complex workflows, external integrations

**Composition Pattern**:
```java
// CORRECT: Composition
@Component
public class BatchService {
    private final BatchRepository repository;      // JHipster
    private final BatchStateMachine stateMachine; // DSL-generated

    public void progressBatch(Long id) {
        Batch batch = repository.findById(id);
        stateMachine.setBatch(batch);
        stateMachine.startColonization(); // DSL method
        repository.save(batch);
    }
}

// WRONG: Inheritance
public class EnhancedBatch extends Batch { // DON'T DO THIS
    // JHipster will overwrite Batch.java
}
```

---

### Portability Decisions

**Everything in Repository**:
- All memories, rules, configurations stored in repo
- No external knowledge bases
- Clone repo = complete context

**External Installations Required** (documented in SETUP_GUIDE.md):
- Java 21 JDK
- Maven 3.9+
- Node.js 18+ & npm 9+
- JHipster CLI (npm package)
- Git

**Simple Dependencies** (just Maven):
- ANTLR 4.13.1
- JavaPoet 1.13.0
- Spring Boot 3.2.2
- JUnit 5.10.1
- All other libraries

**Migration Checklist** (to new PC):
```bash
1. Install prerequisites (Java, Maven, Node, JHipster, Git)
2. Clone repository: git clone <url>
3. Build: mvn clean install
4. Read: dev-docs/AGENT_MEMORIES.md
5. Continue from current phase
```

---

### Error Handling Patterns

**Build Failures**:
1. Check Java version: `java -version`
2. Check JAVA_HOME: `echo $JAVA_HOME`
3. Clean cache: `rm -rf ~/.m2/repository/com/mcms`
4. Retry: `mvn clean install -U`

**Generation Failures**:
1. Check DSL syntax (ANTLR will report line numbers)
2. Test grammar: `cd mcms-dsl && mvn generate-sources`
3. Check generator logs
4. Fix DSL, regenerate

**JHipster Conflicts**:
1. Verify separation (composition, not inheritance)
2. Check module structure
3. Commit before JHipster operations
4. Rollback if needed: `git reset --hard HEAD`

---

### Experimentation Workflow

**When Hitting a Wall**:
1. Document the issue in AGENT_MEMORIES.md
2. Try alternative approach
3. If blocked > 2 hours, pivot
4. Count as learning, not failure

**A/B Testing Approaches**:
1. Create experimental branch: `experiment/alternative-approach`
2. Implement alternative
3. Compare results
4. Merge winner back to feature branch
5. Document decision rationale

**Premortem/Postmortem Cycle**:
1. Write PREMORTEM.md at project start (Phase 0) ✅
2. Check against premortem after each phase
3. Write POSTMORTEM.md at project end (Phase 9)
4. Compare predicted vs actual failures

---

## Process Improvement Decisions

### Terminology Clarifications (In Progress)

**Terms to Define** (will be completed in Phase 9):
- DSL-First Development
- Vibe Meta-programming
- Meta-program vs Generator vs Builder
- Model (DSL) vs Entity (JHipster) vs Aggregate (DDD)
- Structural DSL vs Behavioral DSL

**Working Definitions** (as of Phase 0):
- **DSL-First**: Domain knowledge captured in DSLs before code
- **Vibe Meta-programming**: Informal term for intuitive DSL design
- **Generator**: Program that produces source code from DSL
- **Structural DSL**: Defines data model (entities, fields, relationships)
- **Behavioral DSL**: Defines behavior (state machines, rules, events)

---

### Phase Transition Checklist

**Before Starting New Phase**:
- [ ] Previous phase committed
- [ ] AGENT_MEMORIES.md updated with learnings
- [ ] All tests passing
- [ ] Documentation regenerated
- [ ] Git tag created: `git tag v0.1.0-phase-N`

**After Completing Phase**:
- [ ] Commit with `[Phase-N]` prefix
- [ ] Update AGENT_MEMORIES.md
- [ ] Review against premortem risks
- [ ] Merge to main (or continue in feature branch)

---

### Metrics Tracking

**Quantitative** (measure at Phase 9):
- Lines of generated code vs hand-written code
- Number of manual edits to generated code (target: 0)
- Build time (target: < 5 minutes)
- Test coverage (target: 100% for state machines)
- JHipster regeneration conflicts (target: 0)

**Qualitative** (capture continuously):
- Developer onboarding time
- DSL readability (domain expert feedback)
- Process improvement insights (target: ≥5)
- Reusable patterns identified

**Tracking Mechanism**:
- Update AGENT_MEMORIES.md after each phase
- Use `git diff --stat` for LOC counts
- Time builds with `time mvn clean install`
- Document in PROCESS_LEARNINGS.md (Phase 9)

---

## Tool-Specific Workflows

### ANTLR Workflow

**Develop Grammar**:
1. Edit `.g4` file in `mcms-dsl/src/main/antlr4/`
2. Generate: `mvn generate-sources`
3. Test with grun (optional): `grun TestDSL dsl -gui`
4. Check generated code in `target/generated-sources/antlr4/`

**Debug Parse Errors**:
- ANTLR reports line and column numbers
- Use `-tokens` or `-tree` flags with grun
- Check for ambiguous grammar warnings

### JavaPoet Workflow

**Generate Code**:
```java
TypeSpec stateMachine = TypeSpec.classBuilder("BatchStateMachine")
    .addModifiers(Modifier.PUBLIC)
    .addAnnotation(Component.class)
    .addField(Batch.class, "batch", Modifier.PRIVATE)
    .addMethod(MethodSpec.methodBuilder("startColonization")
        .addModifiers(Modifier.PUBLIC)
        .addStatement("logger.debug(\"Transition: INOCULATION -> EARLY_COLONIZATION\")")
        .addStatement("assertState(INOCULATION)")
        .addStatement("batch.setCurrentPhase(EARLY_COLONIZATION)")
        .addStatement("emit(new ColonizationStarted(batch.getId()))")
        .build())
    .build();

JavaFile javaFile = JavaFile.builder("com.mcms.statemachine", stateMachine)
    .build();

javaFile.writeTo(outputDir);
```

**Best Practices**:
- Use type-safe builders (avoid string concatenation)
- Add Javadoc via `.addJavadoc()`
- Generate imports automatically
- Test generators thoroughly

### JHipster Workflow

**Generate Entities** (Phase 1):
```bash
cd mcms-jhipster-base
jhipster import-jdl ../docs/mushroom-farm-en.jdl
```

**Regenerate** (when JDL changes):
1. Commit current work
2. Run: `jhipster import-jdl ../docs/mushroom-farm-en.jdl`
3. Review conflicts (if any)
4. Commit regenerated code

**Customize** (after generation):
- ❌ Don't edit generated entities
- ✅ Create services in `mcms-application/`
- ✅ Use composition to enhance functionality

---

## Current State (Phase 0)

**Workflows Established**:
- ✅ Git branching strategy
- ✅ Commit message format
- ✅ File organization
- ✅ Documentation hierarchy
- ✅ Portability approach

**Workflows Pending**:
- ⏳ Code generation workflow (Phase 2)
- ⏳ JHipster workflow (Phase 1)
- ⏳ Living documentation workflow (Phase 8)
- ⏳ Metrics tracking (Phase 9)

**Decisions Deferred**:
- Frontend framework choice (Phase 1)
- Database selection (Phase 1)
- AI agent integration pattern (Phase 5)

---

**Last Updated**: 2026-02-12 23:30
**Current Phase**: 0 (Foundation Complete)
**Current Branch**: feature/phase-0-foundation
**Next Phase**: 1 (JHipster Scaffolding)
