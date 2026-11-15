package com.mcms.dsl;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for the KernelDSL ANTLR grammar.
 * Validates that DSL constructs parse correctly without syntax errors.
 */
@DisplayName("KernelDSL Grammar Parser")
class KernelDSLParserTest {

    private KernelDSLParser parse(String input) {
        CharStream stream = CharStreams.fromString(input);
        KernelDSLLexer lexer = new KernelDSLLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        KernelDSLParser parser = new KernelDSLParser(tokens);

        // Collect errors instead of printing them
        List<String> errors = new ArrayList<>();
        parser.removeErrorListeners();
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                                    int line, int charPositionInLine, String msg,
                                    RecognitionException e) {
                errors.add("line " + line + ":" + charPositionInLine + " " + msg);
            }
        });

        return parser;
    }

    private void assertParses(String input) {
        KernelDSLParser parser = parse(input);
        List<String> errors = new ArrayList<>();
        parser.removeErrorListeners();
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                                    int line, int charPositionInLine, String msg,
                                    RecognitionException e) {
                errors.add("line " + line + ":" + charPositionInLine + " " + msg);
            }
        });
        parser.compilationUnit();
        assertThat(errors)
            .as("Expected no parse errors but found: %s", errors)
            .isEmpty();
    }

    private void assertParseError(String input) {
        KernelDSLParser parser = parse(input);
        List<String> errors = new ArrayList<>();
        parser.removeErrorListeners();
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                                    int line, int charPositionInLine, String msg,
                                    RecognitionException e) {
                errors.add("line " + line + ":" + charPositionInLine + " " + msg);
            }
        });
        parser.compilationUnit();
        assertThat(errors)
            .as("Expected parse errors but got none")
            .isNotEmpty();
    }

    @Nested
    @DisplayName("Domain parsing")
    class DomainParsing {

        @Test
        @DisplayName("should parse minimal empty domain")
        void shouldParseEmptyDomain() {
            assertParses("domain Test { }");
        }

        @Test
        @DisplayName("should parse domain with description")
        void shouldParseDomainWithDescription() {
            assertParses("""
                domain MyApp {
                  description "A test application"
                }
                """);
        }

        @Test
        @DisplayName("should parse multiple domains")
        void shouldParseMultipleDomains() {
            assertParses("""
                domain First { }
                domain Second { }
                """);
        }
    }

    @Nested
    @DisplayName("Level and model parsing")
    class LevelModelParsing {

        @Test
        @DisplayName("should parse level with model")
        void shouldParseLevelWithModel() {
            assertParses("""
                domain Test {
                  level domain {
                    model Note {
                      fields {
                        id: Long
                        title: String
                      }
                    }
                  }
                }
                """);
        }

        @Test
        @DisplayName("should parse complex field types")
        void shouldParseComplexFieldTypes() {
            assertParses("""
                domain Test {
                  level domain {
                    model Frame {
                      fields {
                        id: FrameId
                        slots: Map<String, Value>
                        tags: List<String>
                      }
                    }
                  }
                }
                """);
        }
    }

    @Nested
    @DisplayName("State machine parsing")
    class StateMachineParsing {

        @Test
        @DisplayName("should parse states block")
        void shouldParseStates() {
            assertParses("""
                domain Test {
                  level domain {
                    model Note {
                      states {
                        Draft
                        Published
                        Archived
                      }
                    }
                  }
                }
                """);
        }

        @Test
        @DisplayName("should parse states with commas")
        void shouldParseStatesWithCommas() {
            assertParses("""
                domain Test {
                  level domain {
                    model Note {
                      states {
                        Draft, Published, Archived
                      }
                    }
                  }
                }
                """);
        }

        @Test
        @DisplayName("should parse simple transition")
        void shouldParseSimpleTransition() {
            assertParses("""
                domain Test {
                  level domain {
                    model Note {
                      states { Draft Published }
                      transitions {
                        Draft -> Published on publish
                      }
                    }
                  }
                }
                """);
        }

        @Test
        @DisplayName("should parse transition with emits")
        void shouldParseTransitionWithEmits() {
            assertParses("""
                domain Test {
                  level domain {
                    model Note {
                      states { Draft Published }
                      transitions {
                        Draft -> Published on publish
                          emits NotePublished
                      }
                    }
                  }
                }
                """);
        }

        @Test
        @DisplayName("should parse transition with guard, emits and do")
        void shouldParseFullTransition() {
            assertParses("""
                domain Test {
                  level domain {
                    model Invoice {
                      states { Sent Paid }
                      transitions {
                        Sent -> Paid on receivePayment(amount)
                          if amount > 0
                          emits InvoicePaid, LedgerUpdated
                          do updateLedger, notifyCustomer
                      }
                    }
                  }
                }
                """);
        }

        @Test
        @DisplayName("should parse transition with simple boolean guard")
        void shouldParseSimpleBooleanGuard() {
            assertParses("""
                domain Test {
                  level domain {
                    model Batch {
                      states { INOCULATION EARLY_COLONIZATION }
                      transitions {
                        INOCULATION -> EARLY_COLONIZATION on startColonization
                          if substrateFullyInoculated
                          emits ColonizationStarted
                          do logPhaseStart, updatePhaseExecution
                      }
                    }
                  }
                }
                """);
        }

        @Test
        @DisplayName("should parse transition with comparison guard")
        void shouldParseComparisonGuard() {
            assertParses("""
                domain Test {
                  level domain {
                    model Batch {
                      states { HARVEST REHYDRATION_PAUSE }
                      transitions {
                        HARVEST -> REHYDRATION_PAUSE on rehydrate
                          if flushNumber < maxFlushes
                          emits RehydrationStarted
                      }
                    }
                  }
                }
                """);
        }

        @Test
        @DisplayName("should parse transition with else clause")
        void shouldParseElseClause() {
            assertParses("""
                domain Test {
                  level domain {
                    model Invoice {
                      states { Sent Paid Error }
                      transitions {
                        Sent -> Paid on receivePayment(amount)
                          if amount > 0
                          else Error
                          emits InvoicePaid
                      }
                    }
                  }
                }
                """);
        }

        @Test
        @DisplayName("should parse multiple transitions")
        void shouldParseMultipleTransitions() {
            assertParses("""
                domain Test {
                  level domain {
                    model Batch {
                      states { HARVEST REHYDRATION_PAUSE COMPLETED }
                      transitions {
                        HARVEST -> REHYDRATION_PAUSE on rehydrate
                          if flushNumber < maxFlushes
                          emits RehydrationStarted

                        HARVEST -> COMPLETED on endBatch
                          if flushNumber >= maxFlushes
                          emits BatchCompleted
                      }
                    }
                  }
                }
                """);
        }
    }

    @Nested
    @DisplayName("Service parsing")
    class ServiceParsing {

        @Test
        @DisplayName("should parse service with operations")
        void shouldParseService() {
            assertParses("""
                domain Test {
                  level application {
                    service NoteService {
                      description "Operations for notes"
                      operation createNote(title, body)
                      operation publishNote(noteId)
                    }
                  }
                }
                """);
        }
    }

    @Nested
    @DisplayName("Constraints parsing")
    class ConstraintsParsing {

        @Test
        @DisplayName("should parse constraints block")
        void shouldParseConstraints() {
            assertParses("""
                domain Test {
                  constraints {
                    forbid imports from package notes.ui.*
                    allow imports from package core.*
                  }
                }
                """);
        }
    }

    @Nested
    @DisplayName("Invariants parsing")
    class InvariantsParsing {

        @Test
        @DisplayName("should parse invariants block with comments")
        void shouldParseInvariants() {
            assertParses("""
                domain Test {
                  level domain {
                    model Note {
                      invariants {
                        // Published implies title is not empty
                      }
                    }
                  }
                }
                """);
        }
    }

    @Nested
    @DisplayName("Full DSL file parsing")
    class FullFileParsing {

        @Test
        @DisplayName("should parse Notes example from Kernel DSL v1 spec")
        void shouldParseNotesExample() {
            assertParses("""
                domain Notes {
                  description "Note-taking domain, independent of storage and UI."

                  level domain {
                    model Note {
                      description "Represents a user note and its lifecycle."

                      fields {
                        id: NoteId
                        title: String
                        body: String
                        state: NoteState
                      }

                      states {
                        Draft
                        Published
                        Archived
                      }

                      transitions {
                        Draft -> Published on publish
                          emits NotePublished

                        Published -> Archived on archive(reason)
                          emits NoteArchived
                      }

                      invariants {
                        // Published implies title is not empty
                      }
                    }
                  }

                  level application {
                    service NoteService {
                      description "Operations exposed to UI or API."

                      operation createNote(title, body)
                      operation publishNote(noteId)
                      operation archiveNote(noteId, reason)
                    }
                  }

                  constraints {
                    forbid imports from package notes.ui.*
                  }
                }
                """);
        }

        @Test
        @DisplayName("should parse Batch lifecycle DSL (mcms-states.dsl content)")
        void shouldParseBatchLifecycle() {
            assertParses("""
                domain MushroomFarm {
                  description "Mushroom Cultivation Management System"

                  level production {
                    model Batch {
                      description "Production batch lifecycle"

                      fields {
                        id: Long
                        batchCode: String
                        currentPhase: PhaseName
                      }

                      states {
                        INOCULATION
                        EARLY_COLONIZATION
                        FULL_COLONIZATION
                        CONSOLIDATION
                        FRUITING_TRIGGER
                        PRIMORDIA
                        FRUITING_BODY_GROWTH
                        HARVEST
                        REHYDRATION_PAUSE
                        COMPLETED
                      }

                      transitions {
                        INOCULATION -> EARLY_COLONIZATION on startColonization
                          if substrateFullyInoculated
                          emits ColonizationStarted
                          do logPhaseStart, updatePhaseExecution

                        EARLY_COLONIZATION -> FULL_COLONIZATION on confirmFullColonization
                          if myceliumCoverageComplete
                          emits FullColonizationReached
                          do logPhaseStart, updatePhaseExecution

                        FULL_COLONIZATION -> CONSOLIDATION on startConsolidation
                          emits ConsolidationStarted
                          do logPhaseStart, updatePhaseExecution

                        CONSOLIDATION -> FRUITING_TRIGGER on triggerFruiting
                          if consolidationPeriodComplete
                          emits FruitingTriggered
                          do logPhaseStart, updatePhaseExecution, applyEnvironmentalShock

                        FRUITING_TRIGGER -> PRIMORDIA on confirmPrimordia
                          if pinFormationDetected
                          emits PrimordiaFormed
                          do logPhaseStart, updatePhaseExecution

                        PRIMORDIA -> FRUITING_BODY_GROWTH on startFruitingGrowth
                          emits FruitingBodyGrowthStarted
                          do logPhaseStart, updatePhaseExecution

                        FRUITING_BODY_GROWTH -> HARVEST on startHarvest
                          if fruitBodiesReady
                          emits HarvestReady
                          do logPhaseStart, updatePhaseExecution

                        HARVEST -> REHYDRATION_PAUSE on rehydrate
                          if flushNumber < maxFlushes
                          emits RehydrationStarted
                          do addWater, pauseProduction, updatePhaseExecution

                        HARVEST -> COMPLETED on endBatch
                          if flushNumber >= maxFlushes
                          emits BatchCompleted
                          do finalizeYieldCalculations, generateReport, updatePhaseExecution

                        REHYDRATION_PAUSE -> FRUITING_TRIGGER on restartFruiting
                          if rehydrationComplete
                          emits FruitingRestarted
                          do incrementFlushNumber, logPhaseStart, updatePhaseExecution
                      }

                      invariants {
                        // Active batch must have a current phase that is not COMPLETED
                        // Completed batch must have an end date
                      }
                    }
                  }
                }
                """);
        }
    }
}
