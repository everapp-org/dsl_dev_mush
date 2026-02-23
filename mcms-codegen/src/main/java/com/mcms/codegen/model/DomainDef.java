package com.mcms.codegen.model;

import java.util.List;
import java.util.Optional;

/**
 * Semantic model representing a domain block from the Kernel DSL.
 * A domain defines a bounded context containing levels, models, and constraints.
 */
public record DomainDef(
    String name,
    Optional<String> description,
    List<LevelDef> levels,
    List<ConstraintDef> constraints
) {
    /** Find all models with state machines across all levels. */
    public List<ModelDef> modelsWithStateMachines() {
        return levels.stream()
            .flatMap(l -> l.models().stream())
            .filter(m -> m.stateMachine().isPresent())
            .toList();
    }
}
