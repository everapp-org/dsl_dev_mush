package com.mcms.codegen.extractor;

import com.mcms.codegen.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for the StateMachineExtractor - validates semantic model extraction
 * from DSL content.
 */
@DisplayName("StateMachineExtractor")
class StateMachineExtractorTest {

    private final StateMachineExtractor extractor = new StateMachineExtractor();

    @Nested
    @DisplayName("Basic extraction")
    class BasicExtraction {

        @Test
        @DisplayName("should extract domain name and description")
        void shouldExtractDomain() {
            List<DomainDef> domains = extractor.extractFromString("""
                domain TestApp {
                  description "A test application"
                }
                """);

            assertThat(domains).hasSize(1);
            assertThat(domains.get(0).name()).isEqualTo("TestApp");
            assertThat(domains.get(0).description()).hasValue("A test application");
        }

        @Test
        @DisplayName("should extract level and model names")
        void shouldExtractLevelAndModel() {
            List<DomainDef> domains = extractor.extractFromString("""
                domain Test {
                  level domain {
                    model Note {
                      fields {
                        id: Long
                      }
                    }
                  }
                }
                """);

            assertThat(domains.get(0).levels()).hasSize(1);
            assertThat(domains.get(0).levels().get(0).name()).isEqualTo("domain");
            assertThat(domains.get(0).levels().get(0).models()).hasSize(1);
            assertThat(domains.get(0).levels().get(0).models().get(0).name()).isEqualTo("Note");
        }

        @Test
        @DisplayName("should extract fields with types")
        void shouldExtractFields() {
            List<DomainDef> domains = extractor.extractFromString("""
                domain Test {
                  level domain {
                    model Note {
                      fields {
                        id: Long
                        title: String
                        tags: List<String>
                      }
                    }
                  }
                }
                """);

            List<FieldDef> fields = domains.get(0).levels().get(0).models().get(0).fields();
            assertThat(fields).hasSize(3);
            assertThat(fields.get(0).name()).isEqualTo("id");
            assertThat(fields.get(0).type()).isEqualTo("Long");
            assertThat(fields.get(1).name()).isEqualTo("title");
            assertThat(fields.get(1).type()).isEqualTo("String");
            assertThat(fields.get(2).name()).isEqualTo("tags");
            assertThat(fields.get(2).type()).contains("List");
        }
    }

    @Nested
    @DisplayName("State machine extraction")
    class StateMachineExtraction {

        @Test
        @DisplayName("should extract states")
        void shouldExtractStates() {
            List<DomainDef> domains = extractor.extractFromString("""
                domain Test {
                  level domain {
                    model Note {
                      states { Draft Published Archived }
                    }
                  }
                }
                """);

            ModelDef model = domains.get(0).levels().get(0).models().get(0);
            assertThat(model.stateMachine()).isPresent();
            assertThat(model.stateMachine().get().states())
                .containsExactly("Draft", "Published", "Archived");
        }

        @Test
        @DisplayName("should extract simple transition")
        void shouldExtractSimpleTransition() {
            List<DomainDef> domains = extractor.extractFromString("""
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

            StateMachineDef sm = domains.get(0).levels().get(0).models().get(0).stateMachine().get();
            assertThat(sm.transitions()).hasSize(1);

            TransitionDef t = sm.transitions().get(0);
            assertThat(t.fromState()).isEqualTo("Draft");
            assertThat(t.toState()).isEqualTo("Published");
            assertThat(t.trigger()).isEqualTo("publish");
            assertThat(t.guard()).isEmpty();
            assertThat(t.emits()).containsExactly("NotePublished");
        }

        @Test
        @DisplayName("should extract transition with guard")
        void shouldExtractTransitionWithGuard() {
            List<DomainDef> domains = extractor.extractFromString("""
                domain Test {
                  level domain {
                    model Batch {
                      states { A B }
                      transitions {
                        A -> B on move
                          if isReady
                          emits Moved
                          do doSomething
                      }
                    }
                  }
                }
                """);

            TransitionDef t = domains.get(0).levels().get(0).models().get(0)
                .stateMachine().get().transitions().get(0);
            assertThat(t.guard()).hasValue("isReady");
            assertThat(t.actions()).containsExactly("doSomething");
        }

        @Test
        @DisplayName("should extract transition with parameters")
        void shouldExtractTransitionWithParams() {
            List<DomainDef> domains = extractor.extractFromString("""
                domain Test {
                  level domain {
                    model Invoice {
                      states { Sent Paid }
                      transitions {
                        Sent -> Paid on receivePayment(amount)
                          emits InvoicePaid
                      }
                    }
                  }
                }
                """);

            TransitionDef t = domains.get(0).levels().get(0).models().get(0)
                .stateMachine().get().transitions().get(0);
            assertThat(t.trigger()).isEqualTo("receivePayment");
            assertThat(t.parameters()).containsExactly("amount");
        }

        @Test
        @DisplayName("should extract transition with multiple emits and actions")
        void shouldExtractMultipleEmitsAndActions() {
            List<DomainDef> domains = extractor.extractFromString("""
                domain Test {
                  level domain {
                    model X {
                      states { A B }
                      transitions {
                        A -> B on go
                          emits EventOne, EventTwo
                          do actionA, actionB, actionC
                      }
                    }
                  }
                }
                """);

            TransitionDef t = domains.get(0).levels().get(0).models().get(0)
                .stateMachine().get().transitions().get(0);
            assertThat(t.emits()).containsExactly("EventOne", "EventTwo");
            assertThat(t.actions()).containsExactly("actionA", "actionB", "actionC");
        }
    }

    @Nested
    @DisplayName("Batch lifecycle extraction (mcms-states.dsl)")
    class BatchLifecycleExtraction {

        private List<DomainDef> parseBatchDsl() {
            return extractor.extractFromString("""
                domain MushroomFarm {
                  description "Mushroom Cultivation Management System"

                  level production {
                    model Batch {
                      description "Production batch lifecycle"

                      fields {
                        id: Long
                        batchCode: String
                        currentPhase: PhaseName
                        flushNumber: Integer
                        maxFlushes: Integer
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
                      }
                    }
                  }
                }
                """);
        }

        @Test
        @DisplayName("should extract domain with correct name")
        void shouldExtractDomainName() {
            List<DomainDef> domains = parseBatchDsl();
            assertThat(domains).hasSize(1);
            assertThat(domains.get(0).name()).isEqualTo("MushroomFarm");
        }

        @Test
        @DisplayName("should extract 10 states")
        void shouldExtract10States() {
            StateMachineDef sm = parseBatchDsl().get(0).levels().get(0).models().get(0)
                .stateMachine().get();
            assertThat(sm.states()).hasSize(10);
            assertThat(sm.states()).containsExactly(
                "INOCULATION", "EARLY_COLONIZATION", "FULL_COLONIZATION",
                "CONSOLIDATION", "FRUITING_TRIGGER", "PRIMORDIA",
                "FRUITING_BODY_GROWTH", "HARVEST", "REHYDRATION_PAUSE", "COMPLETED"
            );
        }

        @Test
        @DisplayName("should extract 10 transitions")
        void shouldExtract10Transitions() {
            StateMachineDef sm = parseBatchDsl().get(0).levels().get(0).models().get(0)
                .stateMachine().get();
            assertThat(sm.transitions()).hasSize(10);
        }

        @Test
        @DisplayName("should extract model name as Batch")
        void shouldExtractModelName() {
            StateMachineDef sm = parseBatchDsl().get(0).levels().get(0).models().get(0)
                .stateMachine().get();
            assertThat(sm.modelName()).isEqualTo("Batch");
        }

        @Test
        @DisplayName("should extract 10 unique events")
        void shouldExtract10Events() {
            StateMachineDef sm = parseBatchDsl().get(0).levels().get(0).models().get(0)
                .stateMachine().get();
            assertThat(sm.allEventNames()).hasSize(10);
            assertThat(sm.allEventNames()).contains(
                "ColonizationStarted", "BatchCompleted", "RehydrationStarted"
            );
        }

        @Test
        @DisplayName("should extract startColonization transition details")
        void shouldExtractStartColonizationDetails() {
            StateMachineDef sm = parseBatchDsl().get(0).levels().get(0).models().get(0)
                .stateMachine().get();
            TransitionDef t = sm.transitions().get(0);

            assertThat(t.fromState()).isEqualTo("INOCULATION");
            assertThat(t.toState()).isEqualTo("EARLY_COLONIZATION");
            assertThat(t.trigger()).isEqualTo("startColonization");
            assertThat(t.guard()).hasValue("substrateFullyInoculated");
            assertThat(t.emits()).containsExactly("ColonizationStarted");
            assertThat(t.actions()).containsExactly("logPhaseStart", "updatePhaseExecution");
        }

        @Test
        @DisplayName("should extract HARVEST branching transitions")
        void shouldExtractHarvestBranching() {
            StateMachineDef sm = parseBatchDsl().get(0).levels().get(0).models().get(0)
                .stateMachine().get();

            // Find rehydrate transition
            TransitionDef rehydrate = sm.transitions().stream()
                .filter(t -> t.trigger().equals("rehydrate"))
                .findFirst().orElseThrow();
            assertThat(rehydrate.fromState()).isEqualTo("HARVEST");
            assertThat(rehydrate.toState()).isEqualTo("REHYDRATION_PAUSE");
            assertThat(rehydrate.guard()).isPresent();

            // Find endBatch transition
            TransitionDef endBatch = sm.transitions().stream()
                .filter(t -> t.trigger().equals("endBatch"))
                .findFirst().orElseThrow();
            assertThat(endBatch.fromState()).isEqualTo("HARVEST");
            assertThat(endBatch.toState()).isEqualTo("COMPLETED");
            assertThat(endBatch.guard()).isPresent();
        }

        @Test
        @DisplayName("should extract fields")
        void shouldExtractFields() {
            ModelDef model = parseBatchDsl().get(0).levels().get(0).models().get(0);
            assertThat(model.fields()).hasSizeGreaterThanOrEqualTo(5);
            assertThat(model.fields().get(0).name()).isEqualTo("id");
            assertThat(model.fields().get(0).type()).isEqualTo("Long");
        }
    }

    @Nested
    @DisplayName("Service extraction")
    class ServiceExtraction {

        @Test
        @DisplayName("should extract service with operations")
        void shouldExtractService() {
            List<DomainDef> domains = extractor.extractFromString("""
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

            LevelDef level = domains.get(0).levels().get(0);
            assertThat(level.services()).hasSize(1);

            ServiceDef service = level.services().get(0);
            assertThat(service.name()).isEqualTo("NoteService");
            assertThat(service.description()).hasValue("Operations for notes");
            assertThat(service.operations()).hasSize(2);
            assertThat(service.operations().get(0).name()).isEqualTo("createNote");
            assertThat(service.operations().get(0).parameters()).containsExactly("title", "body");
        }
    }

    @Nested
    @DisplayName("Error handling")
    class ErrorHandling {

        @Test
        @DisplayName("should throw DslParseException on invalid syntax")
        void shouldThrowOnInvalidSyntax() {
            assertThatThrownBy(() -> extractor.extractFromString("not a valid dsl"))
                .isInstanceOf(StateMachineExtractor.DslParseException.class);
        }
    }
}
