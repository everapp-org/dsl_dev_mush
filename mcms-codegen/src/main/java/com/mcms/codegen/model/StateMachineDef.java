package com.mcms.codegen.model;

import java.util.List;

/**
 * Semantic model representing a state machine extracted from a model's
 * states and transitions blocks in the Kernel DSL.
 */
public record StateMachineDef(
    String modelName,
    List<String> states,
    List<TransitionDef> transitions,
    List<String> invariants
) {
    /** Returns all unique event names emitted across all transitions. */
    public List<String> allEventNames() {
        return transitions.stream()
            .flatMap(t -> t.emits().stream())
            .distinct()
            .toList();
    }

    /** Returns all unique action names across all transitions. */
    public List<String> allActionNames() {
        return transitions.stream()
            .flatMap(t -> t.actions().stream())
            .distinct()
            .toList();
    }

    /** Returns all unique guard names that are simple identifiers (no operators). */
    public List<String> simpleGuardNames() {
        return transitions.stream()
            .filter(t -> t.guard().isPresent())
            .map(t -> t.guard().get())
            .filter(g -> g.matches("[a-zA-Z_][a-zA-Z0-9_]*"))
            .distinct()
            .toList();
    }
}
