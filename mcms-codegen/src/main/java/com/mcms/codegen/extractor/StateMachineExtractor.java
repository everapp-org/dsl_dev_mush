package com.mcms.codegen.extractor;

import com.mcms.codegen.model.*;
import com.mcms.dsl.KernelDSLBaseVisitor;
import com.mcms.dsl.KernelDSLLexer;
import com.mcms.dsl.KernelDSLParser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Extracts semantic model from Kernel DSL files using ANTLR visitor pattern.
 * Walks the parse tree and builds DomainDef, ModelDef, StateMachineDef, etc.
 */
public class StateMachineExtractor {

    private static final Logger logger = LoggerFactory.getLogger(StateMachineExtractor.class);

    /**
     * Parse a .dsl file and extract all domain definitions.
     */
    public List<DomainDef> extract(Path dslFile) throws IOException {
        logger.info("Parsing DSL file: {}", dslFile);
        CharStream input = CharStreams.fromPath(dslFile);
        return extractFromCharStream(input);
    }

    /**
     * Parse DSL content from a string and extract all domain definitions.
     */
    public List<DomainDef> extractFromString(String dslContent) {
        CharStream input = CharStreams.fromString(dslContent);
        return extractFromCharStream(input);
    }

    private List<DomainDef> extractFromCharStream(CharStream input) {
        KernelDSLLexer lexer = new KernelDSLLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        KernelDSLParser parser = new KernelDSLParser(tokens);

        // Replace default error listener with one that throws on errors
        parser.removeErrorListeners();
        parser.addErrorListener(new ThrowingErrorListener());
        lexer.removeErrorListeners();
        lexer.addErrorListener(new ThrowingErrorListener());

        KernelDSLParser.CompilationUnitContext tree = parser.compilationUnit();
        DomainVisitor visitor = new DomainVisitor();
        return visitor.visitCompilationUnit(tree);
    }

    /**
     * Convenience: extract and return all state machine definitions found.
     */
    public List<StateMachineDef> extractStateMachines(Path dslFile) throws IOException {
        return extract(dslFile).stream()
            .flatMap(d -> d.levels().stream())
            .flatMap(l -> l.models().stream())
            .filter(m -> m.stateMachine().isPresent())
            .map(m -> m.stateMachine().get())
            .toList();
    }

    // ========================================================================
    // ANTLR Visitor
    // ========================================================================

    private static class DomainVisitor extends KernelDSLBaseVisitor<Object> {

        public List<DomainDef> visitCompilationUnit(KernelDSLParser.CompilationUnitContext ctx) {
            List<DomainDef> domains = new ArrayList<>();
            for (KernelDSLParser.DomainContext dc : ctx.domain()) {
                domains.add(visitDomain(dc));
            }
            return domains;
        }

        public DomainDef visitDomain(KernelDSLParser.DomainContext ctx) {
            String name = ctx.name.getText();
            Optional<String> description = Optional.empty();
            List<LevelDef> levels = new ArrayList<>();
            List<ConstraintDef> constraints = new ArrayList<>();

            for (KernelDSLParser.DomainBodyContext body : ctx.domainBody()) {
                if (body.description() != null) {
                    description = Optional.of(unquote(body.description().STRING().getText()));
                } else if (body.level() != null) {
                    levels.add(visitLevel(body.level()));
                } else if (body.constraints() != null) {
                    constraints.addAll(visitConstraints(body.constraints()));
                }
            }

            return new DomainDef(name, description, levels, constraints);
        }

        public LevelDef visitLevel(KernelDSLParser.LevelContext ctx) {
            String name = ctx.name.getText();
            List<ModelDef> models = new ArrayList<>();
            List<ServiceDef> services = new ArrayList<>();

            for (KernelDSLParser.LevelBodyContext body : ctx.levelBody()) {
                if (body.model() != null) {
                    models.add(visitModel(body.model()));
                } else if (body.service() != null) {
                    services.add(visitService(body.service()));
                }
            }

            return new LevelDef(name, models, services);
        }

        public ModelDef visitModel(KernelDSLParser.ModelContext ctx) {
            String name = ctx.name.getText();
            Optional<String> description = Optional.empty();
            List<FieldDef> fields = new ArrayList<>();
            List<String> states = new ArrayList<>();
            List<TransitionDef> transitions = new ArrayList<>();
            List<String> invariants = new ArrayList<>();

            for (KernelDSLParser.ModelBodyContext body : ctx.modelBody()) {
                if (body.description() != null) {
                    description = Optional.of(unquote(body.description().STRING().getText()));
                } else if (body.fields() != null) {
                    fields.addAll(visitFields(body.fields()));
                } else if (body.states() != null) {
                    states.addAll(visitStates(body.states()));
                } else if (body.transitions() != null) {
                    transitions.addAll(visitTransitions(body.transitions()));
                } else if (body.invariants() != null) {
                    invariants.addAll(visitInvariants(body.invariants()));
                }
            }

            Optional<StateMachineDef> stateMachine = Optional.empty();
            if (!states.isEmpty() || !transitions.isEmpty()) {
                stateMachine = Optional.of(new StateMachineDef(name, states, transitions, invariants));
            }

            return new ModelDef(name, description, fields, stateMachine);
        }

        public List<FieldDef> visitFields(KernelDSLParser.FieldsContext ctx) {
            List<FieldDef> fields = new ArrayList<>();
            for (KernelDSLParser.FieldContext fc : ctx.field()) {
                String name = fc.name.getText();
                String type = fc.type().typePart().stream()
                    .map(tp -> tp.getText())
                    .collect(Collectors.joining(""));
                fields.add(new FieldDef(name, type));
            }
            return fields;
        }

        public List<String> visitStates(KernelDSLParser.StatesContext ctx) {
            return ctx.stateName().stream()
                .map(sn -> sn.NAME().getText())
                .toList();
        }

        public List<TransitionDef> visitTransitions(KernelDSLParser.TransitionsContext ctx) {
            List<TransitionDef> transitions = new ArrayList<>();
            for (KernelDSLParser.TransitionContext tc : ctx.transition()) {
                transitions.add(visitTransition(tc));
            }
            return transitions;
        }

        public TransitionDef visitTransition(KernelDSLParser.TransitionContext ctx) {
            String fromState = ctx.fromState.getText();
            String toState = ctx.toState.getText();
            String trigger = ctx.trigger.getText();

            // Parameters
            List<String> parameters = new ArrayList<>();
            if (ctx.paramList() != null) {
                parameters = ctx.paramList().NAME().stream()
                    .map(TerminalNode::getText)
                    .toList();
            }

            // Guard
            Optional<String> guard = Optional.empty();
            if (ctx.guardClause() != null) {
                guard = Optional.of(
                    ctx.guardClause().conditionExpr().conditionToken().stream()
                        .map(ct -> ct.getText())
                        .collect(Collectors.joining(" "))
                        .trim()
                );
            }

            // Else state
            Optional<String> elseState = Optional.empty();
            if (ctx.elseClause() != null) {
                elseState = Optional.of(ctx.elseClause().NAME().getText());
            }

            // Emits
            List<String> emits = new ArrayList<>();
            if (ctx.emitsClause() != null) {
                emits = ctx.emitsClause().nameList().NAME().stream()
                    .map(TerminalNode::getText)
                    .toList();
            }

            // Actions
            List<String> actions = new ArrayList<>();
            if (ctx.doClause() != null) {
                actions = ctx.doClause().nameList().NAME().stream()
                    .map(TerminalNode::getText)
                    .toList();
            }

            return new TransitionDef(fromState, toState, trigger, parameters, guard, elseState, emits, actions);
        }

        public ServiceDef visitService(KernelDSLParser.ServiceContext ctx) {
            String name = ctx.name.getText();
            Optional<String> description = Optional.empty();
            List<OperationDef> operations = new ArrayList<>();

            for (KernelDSLParser.ServiceBodyContext body : ctx.serviceBody()) {
                if (body.description() != null) {
                    description = Optional.of(unquote(body.description().STRING().getText()));
                } else if (body.operation() != null) {
                    operations.add(visitOperation(body.operation()));
                }
            }

            return new ServiceDef(name, description, operations);
        }

        public OperationDef visitOperation(KernelDSLParser.OperationContext ctx) {
            String name = ctx.name.getText();
            List<String> parameters = new ArrayList<>();
            if (ctx.paramList() != null) {
                parameters = ctx.paramList().NAME().stream()
                    .map(TerminalNode::getText)
                    .toList();
            }
            return new OperationDef(name, parameters);
        }

        public List<ConstraintDef> visitConstraints(KernelDSLParser.ConstraintsContext ctx) {
            List<ConstraintDef> constraints = new ArrayList<>();
            for (KernelDSLParser.ConstraintContext cc : ctx.constraint()) {
                ConstraintDef.ConstraintType type = cc.constraintType.getType() == KernelDSLParser.FORBID
                    ? ConstraintDef.ConstraintType.FORBID
                    : ConstraintDef.ConstraintType.ALLOW;
                String pattern = cc.constraintPattern().getText();
                constraints.add(new ConstraintDef(type, pattern));
            }
            return constraints;
        }

        public List<String> visitInvariants(KernelDSLParser.InvariantsContext ctx) {
            // Invariants are captured as raw text for documentation purposes
            List<String> invariants = new ArrayList<>();
            for (KernelDSLParser.InvariantContentContext ic : ctx.invariantContent()) {
                String text = ic.getText().trim();
                if (!text.isEmpty()) {
                    invariants.add(text);
                }
            }
            return invariants;
        }

        /** Remove surrounding quotes from a string literal. */
        private String unquote(String quoted) {
            if (quoted.length() >= 2 && quoted.startsWith("\"") && quoted.endsWith("\"")) {
                return quoted.substring(1, quoted.length() - 1)
                    .replace("\\\"", "\"")
                    .replace("\\n", "\n")
                    .replace("\\t", "\t")
                    .replace("\\\\", "\\");
            }
            return quoted;
        }
    }

    // ========================================================================
    // Error Listener
    // ========================================================================

    private static class ThrowingErrorListener extends BaseErrorListener {
        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                                int line, int charPositionInLine, String msg,
                                RecognitionException e) {
            throw new DslParseException(
                String.format("Parse error at line %d:%d - %s", line, charPositionInLine, msg)
            );
        }
    }

    /**
     * Exception thrown when DSL parsing fails.
     */
    public static class DslParseException extends RuntimeException {
        public DslParseException(String message) {
            super(message);
        }
    }
}
