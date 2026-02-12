# PREMORTEM - MCMS Project

**Date**: 2026-02-12
**Phase**: 0 (Foundation)
**Project**: Mushroom Cultivation Management System (MCMS)
**Methodology**: DSL-First + JHipster Hybrid

---

## What is a Premortem?

A premortem is a risk management technique where we imagine the project has **already failed** spectacularly, then work backwards to identify what could have caused that failure.

**Purpose**: Anticipate problems before they occur, so we can:
1. Mitigate risks proactively
2. Prepare recovery strategies
3. Learn from hypothetical failures
4. Validate or refute our hypotheses in the postmortem

---

## Scenario: "It's 2026-04-12 (2 months from now). The MCMS project has failed."

### What went wrong?

---

## Risk 1: JHipster Version Incompatibility

### Symptom
JHipster generator fails or produces code incompatible with Spring Boot 3.2.2, Java 21, or our custom DSL enhancements.

### Likely Causes
- JHipster version too old (doesn't support Java 21)
- JHipster version too new (breaking changes in entity generation)
- JHipster template customization conflicts with our requirements
- Spring Boot version mismatch

### Impact
- **Severity**: HIGH
- **Probability**: MEDIUM
- **Detection**: Early (Phase 1 during scaffolding)

### Mitigation Strategies
1. **Pin JHipster version** in `.yo-rc.json` configuration
2. **Test JHipster scaffolding** in separate branch before Phase 1
3. **Check JHipster compatibility matrix** for Java 21 + Spring Boot 3.2.2
4. **Document exact versions** that work together
5. **Create snapshot backup** before regeneration

### Recovery Plan
1. Rollback to last known good JHipster version
2. Fork JHipster templates if customization needed
3. Use older Spring Boot version if required
4. Consider manual entity creation as last resort

### Success Criteria (to validate in postmortem)
- [ ] JHipster successfully generates 25 entities
- [ ] No version-related compilation errors
- [ ] All relationships work correctly
- [ ] Database migrations generated successfully

---

## Risk 2: Generated Code Doesn't Compile

### Symptom
JavaPoet generators produce syntactically invalid Java code that fails to compile.

### Likely Causes
- Incorrect JavaPoet API usage
- Invalid identifiers (spaces, special characters in names)
- Missing imports
- Type resolution errors
- Circular dependencies between generated classes

### Impact
- **Severity**: HIGH
- **Probability**: HIGH (especially early)
- **Detection**: Immediate (compile phase)

### Mitigation Strategies
1. **Write generator unit tests first** (TDD approach)
2. **Start with minimal DSL examples** (1 state, 1 transition)
3. **Use JavaPoet's type-safe builders** (avoid string concatenation)
4. **Generate to separate directory first** (inspect before integration)
5. **Test each generator independently** before combining

### Recovery Plan
1. Fix generator code
2. Regenerate all affected files
3. Run `mvn clean compile` to verify
4. Commit working generator + test

### Success Criteria (to validate in postmortem)
- [ ] All generated code compiles without errors
- [ ] Generator tests have 100% coverage
- [ ] No manual fixes needed for generated code

---

## Risk 3: State Machines Too Rigid

### Symptom
Real-world mushroom farming scenarios require manual overrides or emergency transitions that our state machine doesn't support.

### Likely Causes
- Insufficient domain analysis (missed edge cases)
- No "escape hatch" for exceptional situations
- Guard conditions too strict
- No support for batch corrections or rollbacks

### Impact
- **Severity**: MEDIUM
- **Probability**: MEDIUM
- **Detection**: Late (Phase 2+ during testing)

### Mitigation Strategies
1. **Add `forceTransition()` method** with logging
2. **Include "MANUAL_OVERRIDE" event** in DSL
3. **Consult domain experts** early (Phase 2)
4. **Test with real contamination scenarios**
5. **Allow state corrections** (but log them as audit trail)

### Recovery Plan
1. Document specific cases requiring manual intervention
2. Add escape hatches to DSL
3. Regenerate state machines
4. Create "CompensatingAction" pattern for corrections

### Success Criteria (to validate in postmortem)
- [ ] All real-world scenarios handled
- [ ] Emergency transitions available (with audit log)
- [ ] Domain experts validated state machine
- [ ] Zero "impossible state" bugs in production

---

## Risk 4: DSL Becomes Too Complex

### Symptom
DSL files are hard to read, domain experts are confused, new developers take weeks to understand.

### Likely Causes
- Feature creep (adding every possible construct)
- Poor syntax design (too verbose or too cryptic)
- Insufficient examples and documentation
- Trying to solve problems better left to code

### Impact
- **Severity**: MEDIUM
- **Probability**: MEDIUM
- **Detection**: Mid-project (Phase 3-5)

### Mitigation Strategies
1. **Regular DSL reviews** (weekly)
2. **Remove unused features** aggressively
3. **Prioritize readability over power**
4. **Provide extensive examples**
5. **Test DSL with non-programmers** (domain experts)
6. **Use "Convention over Configuration"** where possible

### Recovery Plan
1. Simplify DSL syntax (breaking change if needed)
2. Move complexity to generators
3. Create migration tool for old DSL files
4. Write comprehensive DSL guide

### Success Criteria (to validate in postmortem)
- [ ] Domain expert can read DSL in < 30 minutes training
- [ ] DSL files are self-documenting
- [ ] No features exist that are never used
- [ ] DSL guide is < 20 pages

---

## Risk 5: Regeneration Destroys Manual Changes

### Symptom
Developer edits generated file, changes are lost when DSL is updated and code regenerated.

### Likely Causes
- Unclear separation between generated and manual code
- No "DO NOT EDIT" headers
- Generated code mixed in same directories as manual code
- Poor developer onboarding

### Impact
- **Severity**: HIGH
- **Probability**: HIGH (without mitigation)
- **Detection**: When regeneration happens

### Mitigation Strategies
1. **Clear `// GENERATED - DO NOT EDIT` headers** on all generated files
2. **Separate directories** (`mcms-domain-extensions/target/generated-sources` vs `mcms-application/src`)
3. **Git pre-commit hook** to warn if generated files manually edited
4. **Use composition, not inheritance** (never extend generated classes)
5. **Comprehensive AGENTS.md** documentation

### Recovery Plan
1. Restore from git
2. Move manual changes to hand-written code
3. Regenerate from DSL
4. Add pre-commit hook to prevent future occurrences

### Success Criteria (to validate in postmortem)
- [ ] Zero incidents of lost manual changes
- [ ] All developers understand separation principle
- [ ] Pre-commit hook catches 100% of violations

---

## Risk 6: Scope Creep (25 Entities)

### Symptom
Implementation takes far longer than expected, perfectionism delays, features never marked "done".

### Likely Causes
- Trying to implement all 25 entities at once
- Perfectionism (not accepting "good enough")
- Lack of vertical slices
- Gold plating (features not in requirements)

### Impact
- **Severity**: MEDIUM
- **Probability**: HIGH (common in experimentation)
- **Detection**: Throughout project (time tracking)

### Mitigation Strategies
1. **Focus on vertical slices** (complete one feature end-to-end)
2. **"Good enough" beats "perfect"** mantra
3. **Defer non-critical entities** to Phase 2 of project
4. **Time-box each phase** (strict limits)
5. **Mark tasks as DONE** even if improvements possible

### Recovery Plan
1. Reduce scope (defer entities)
2. Focus on process learning (primary goal)
3. Accept partial implementation
4. Document what was skipped and why

### Success Criteria (to validate in postmortem)
- [ ] At least 3 vertical slices completed
- [ ] Batch lifecycle fully working
- [ ] Process learnings documented
- [ ] No more than 2 weeks per phase

---

## Risk 7: Maven Build Fails

### Symptom
`mvn clean install` fails with cryptic errors, can't build any modules.

### Likely Causes
- Module dependency cycle
- Missing dependency versions
- ANTLR plugin misconfiguration
- Java version mismatch
- Corrupted local Maven cache

### Impact
- **Severity**: HIGH
- **Probability**: LOW (if careful)
- **Detection**: Immediate (Phase 0)

### Mitigation Strategies
1. **Test parent POM first** (Phase 0)
2. **Add modules incrementally**
3. **Use dependency:tree** to check cycles
4. **Pin all dependency versions** in parent POM
5. **Clean local repo** if issues arise

### Recovery Plan
1. `mvn clean` all modules
2. `rm -rf ~/.m2/repository/com/mcms` (clear local cache)
3. Check dependency cycles with `mvn dependency:tree`
4. Fix module order in parent POM
5. Build modules one at a time to isolate issue

### Success Criteria (to validate in postmortem)
- [ ] `mvn clean install` succeeds from project root
- [ ] No circular dependencies
- [ ] All modules compile
- [ ] All tests pass

---

## Risk 8: JHipster Overwrites DSL Enhancements

### Symptom
After running `jhipster import-jdl` to add/modify entities, our custom state machines and events are deleted.

### Likely Causes
- Generated code in same directory as JHipster entities
- Extending JHipster entities (inheritance)
- JHipster regeneration overwrites everything

### Impact
- **Severity**: CRITICAL
- **Probability**: HIGH (if architecture wrong)
- **Detection**: When JHipster regenerates

### Mitigation Strategies
1. **Use composition, not inheritance** (wrap entities, don't extend)
2. **Separate modules** (`mcms-jhipster-base` vs `mcms-domain-extensions`)
3. **Never put custom code in JHipster module**
4. **Test regeneration** in separate branch first
5. **Git commit** before any JHipster operation

### Recovery Plan
1. Rollback from git
2. Restructure to use composition
3. Move enhancements to separate module
4. Regenerate from JHipster

### Success Criteria (to validate in postmortem)
- [ ] JHipster regeneration doesn't affect DSL code
- [ ] Composition pattern works correctly
- [ ] No code loss incidents
- [ ] Can add entities without breaking enhancements

---

## Risk 9: ANTLR Grammar Bugs

### Symptom
ANTLR parser fails to parse valid DSL files, or parses invalid files without errors.

### Likely Causes
- Incorrect grammar rules
- Ambiguous grammar (multiple parse trees)
- Missing lexer rules
- Poor error messages

### Impact
- **Severity**: MEDIUM
- **Probability**: MEDIUM
- **Detection**: Phase 2 (when writing real DSL)

### Mitigation Strategies
1. **Start with simple grammar** (copy from jCrew_02)
2. **Test grammar with ANTLR's TestRig** (`grun` command)
3. **Add examples directory** with valid/invalid DSL files
4. **Write parser tests** for edge cases
5. **Use ANTLR visitor, not listener** (more control)

### Recovery Plan
1. Simplify grammar
2. Add more test cases
3. Use ANTLR debugger
4. Consult ANTLR documentation
5. Ask for help in ANTLR forums

### Success Criteria (to validate in postmortem)
- [ ] Grammar parses all valid DSL files
- [ ] Grammar rejects all invalid DSL files
- [ ] Clear error messages for syntax errors
- [ ] No ambiguous grammar warnings

---

## Risk 10: No Time for Retrospective

### Symptom
Rush to finish implementation, skip Phase 9 (retrospective), lose all process learnings.

### Likely Causes
- Underestimated implementation time
- Focused on app, not process
- Perfectionism in earlier phases
- External deadline pressure

### Impact
- **Severity**: CRITICAL (defeats primary goal)
- **Probability**: HIGH
- **Detection**: End of project

### Mitigation Strategies
1. **Schedule Phase 9 first** (block time in calendar)
2. **Capture learnings continuously** (not just at end)
3. **PROCESS_LEARNINGS.md is PRIMARY deliverable** (not the app)
4. **Accept incomplete app** if needed for learnings
5. **Time-box all earlier phases** strictly

### Recovery Plan
1. Even if app incomplete, DO the retrospective
2. Learnings from partial implementation are valuable
3. Focus on "what worked" and "what didn't"
4. Document for next DSL-First project

### Success Criteria (to validate in postmortem)
- [ ] PROCESS_LEARNINGS.md completed
- [ ] At least 5 actionable improvements identified
- [ ] POSTMORTEM.md compares with PREMORTEM.md
- [ ] Terminology clarifications documented

---

## Hypotheses to Test

### Hypothesis 1: JHipster + Custom DSL Can Coexist
**Prediction**: They can coexist without conflicts if properly separated.
**Test**: Track merge conflicts, regeneration issues.
**Success**: Zero conflicts, clean separation maintained.

### Hypothesis 2: 85% Generated Code is Achievable
**Prediction**: With JHipster + custom generators, we can achieve 85%+ generated code.
**Test**: LOC analysis at project end.
**Success**: ≥85% of code is generated.

### Hypothesis 3: DSL is Comprehensible to Non-Programmers
**Prediction**: Domain experts can read and validate DSL with minimal training.
**Test**: Show DSL to mushroom farming expert.
**Success**: Expert understands DSL in < 1 hour, validates correctness.

### Hypothesis 4: Full Regeneration Takes < 5 Minutes
**Prediction**: From clean state, full code generation completes in < 5 minutes.
**Test**: Time `mvn clean install` from root.
**Success**: < 5 minutes on standard developer machine.

### Hypothesis 5: Living Documentation Stays Current
**Prediction**: Automated doc generation keeps docs synchronized with code.
**Test**: Check documentation timestamps, review accuracy.
**Success**: Docs always < 5 minutes old, 100% accurate.

---

## Metrics to Track

### Quantitative
- [ ] Lines of generated code vs hand-written code (target: 85/15)
- [ ] Number of manual edits to generated code (target: 0)
- [ ] Build time (target: < 5 minutes)
- [ ] Test coverage (target: 100% for state machines)
- [ ] Number of JHipster regeneration conflicts (target: 0)

### Qualitative
- [ ] Developer onboarding time
- [ ] DSL readability (domain expert feedback)
- [ ] Process improvement insights (target: ≥5)
- [ ] Terminology clarifications made
- [ ] Reusable patterns identified

---

## Go/No-Go Decision Points

### Phase 0 (NOW)
**Criteria**: Maven build succeeds, ANTLR generates test parser
**If failed**: Fix build before proceeding

### Phase 1
**Criteria**: JHipster generates 25 entities without errors
**If failed**: Reconsider JHipster integration, may need manual entities

### Phase 2
**Criteria**: Batch state machine generates and compiles
**If failed**: Pivot to simpler DSL or manual implementation

### Phase 5
**Criteria**: At least 3 vertical slices working
**If failed**: Reduce scope, focus on process learnings

---

## Success Definition (What "Success" Looks Like)

This project **SUCCEEDS** if:

1. **Process Learnings Captured**: PROCESS_LEARNINGS.md documents ≥5 actionable improvements
2. **Methodology Refined**: Clear guidance on when to use JHipster/DSL/manual
3. **Patterns Identified**: Reusable patterns documented for future projects
4. **Hypotheses Tested**: All 5 hypotheses validated or refuted with data
5. **Experiment Complete**: Even if app incomplete, we learned about DSL-First

This project **DOES NOT REQUIRE**:
- Complete implementation of all 25 entities
- Production-ready application
- Perfect code quality
- Zero bugs

**Remember**: This is an **experiment** in methodology, not a product launch.

---

## Review Schedule

- **Week 2 (Phase 1)**: Check JHipster integration
- **Week 4 (Phase 2)**: Validate state machine approach
- **Week 6 (Phase 5)**: Assess scope and timeline
- **Week 8 (Phase 9)**: Begin retrospective (DO NOT SKIP)

---

**Date**: 2026-02-12
**Authors**: AI Agents (Architect, Generator, Domain Expert, Documentation)
**Status**: Active
**Next Review**: After Phase 1 completion
