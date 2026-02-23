package com.mcms.codegen;

import com.mcms.codegen.extractor.StateMachineExtractor;
import com.mcms.codegen.generator.*;
import com.mcms.codegen.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Main entry point for MCMS code generation pipeline.
 *
 * Pipeline: DSL file -> ANTLR parser -> Semantic model -> JavaPoet/template generators -> Output files
 *
 * Usage:
 *   java com.mcms.codegen.McmsCodeGenerator [dslFile] [outputDir] [testOutputDir]
 *
 * Default paths (when run from project root):
 *   dslFile:       mcms-dsl/src/main/resources/mcms-states.dsl
 *   outputDir:     mcms-domain-extensions/target/generated-sources/mcms
 *   testOutputDir: mcms-domain-extensions/target/generated-test-sources/mcms
 */
public class McmsCodeGenerator {

    private static final Logger logger = LoggerFactory.getLogger(McmsCodeGenerator.class);

    public static void main(String[] args) throws Exception {
        Path dslFile = Path.of(args.length > 0 ? args[0]
            : "mcms-dsl/src/main/resources/mcms-states.dsl");
        Path outputDir = Path.of(args.length > 1 ? args[1]
            : "mcms-domain-extensions/target/generated-sources/mcms");
        Path testOutputDir = Path.of(args.length > 2 ? args[2]
            : "mcms-domain-extensions/target/generated-test-sources/mcms");

        new McmsCodeGenerator().run(dslFile, outputDir, testOutputDir);
    }

    public void run(Path dslFile, Path outputDir, Path testOutputDir) throws Exception {
        logger.info("============================================================");
        logger.info("  MCMS Code Generator");
        logger.info("============================================================");
        logger.info("DSL file:        {}", dslFile.toAbsolutePath());
        logger.info("Output dir:      {}", outputDir.toAbsolutePath());
        logger.info("Test output dir: {}", testOutputDir.toAbsolutePath());
        logger.info("");

        // Validate input
        if (!Files.exists(dslFile)) {
            throw new IllegalArgumentException("DSL file not found: " + dslFile);
        }

        // Create output directories
        Files.createDirectories(outputDir);
        Files.createDirectories(testOutputDir);

        // Step 1: Parse DSL
        logger.info("Step 1: Parsing DSL file...");
        StateMachineExtractor extractor = new StateMachineExtractor();
        List<DomainDef> domains = extractor.extract(dslFile);
        logger.info("  Found {} domain(s)", domains.size());

        // Collect all models with state machines
        List<ModelDef> models = domains.stream()
            .flatMap(d -> d.levels().stream())
            .flatMap(l -> l.models().stream())
            .filter(m -> m.stateMachine().isPresent())
            .toList();

        logger.info("  Found {} model(s) with state machines", models.size());

        if (models.isEmpty()) {
            logger.warn("No state machines found in DSL file. Nothing to generate.");
            return;
        }

        // Step 2: Generate exception classes (shared by all state machines)
        logger.info("Step 2: Generating exception classes...");
        ExceptionClassGenerator exceptionGen = new ExceptionClassGenerator(outputDir);
        exceptionGen.generate();

        // Step 3: Generate code for each state machine
        StateMachineGenerator smGen = new StateMachineGenerator(outputDir);
        EventClassGenerator eventGen = new EventClassGenerator(outputDir);
        TestGenerator testGen = new TestGenerator(testOutputDir);

        int totalEvents = 0;
        int totalTransitions = 0;

        for (ModelDef model : models) {
            StateMachineDef sm = model.stateMachine().get();

            logger.info("");
            logger.info("Step 3: Generating state machine for '{}'...", sm.modelName());
            logger.info("  States:      {}", sm.states().size());
            logger.info("  Transitions: {}", sm.transitions().size());
            logger.info("  Events:      {}", sm.allEventNames().size());
            logger.info("  Actions:     {}", sm.allActionNames().size());

            // Generate state machine class
            smGen.generate(sm);

            // Generate event records
            logger.info("Step 4: Generating {} event classes...", sm.allEventNames().size());
            eventGen.generate(sm);

            // Generate test class
            logger.info("Step 5: Generating test class...");
            testGen.generate(sm);

            totalEvents += sm.allEventNames().size();
            totalTransitions += sm.transitions().size();
        }

        // Summary
        logger.info("");
        logger.info("============================================================");
        logger.info("  Generation Complete");
        logger.info("============================================================");
        logger.info("  State machines: {}", models.size());
        logger.info("  Transitions:    {}", totalTransitions);
        logger.info("  Event classes:  {}", totalEvents);
        logger.info("  Exception classes: 2");
        logger.info("  Test classes:   {}", models.size());
        logger.info("");
        logger.info("  Output:      {}", outputDir.toAbsolutePath());
        logger.info("  Test output: {}", testOutputDir.toAbsolutePath());
        logger.info("============================================================");
    }
}
