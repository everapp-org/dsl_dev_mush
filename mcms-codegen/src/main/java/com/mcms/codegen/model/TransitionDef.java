package com.mcms.codegen.model;

import java.util.List;
import java.util.Optional;

/**
 * Semantic model representing a single state transition from the Kernel DSL.
 * Contains: source state, target state, trigger name, optional guard, optional else state,
 * emitted events, and actions to execute.
 */
public record TransitionDef(
    String fromState,
    String toState,
    String trigger,
    List<String> parameters,
    Optional<String> guard,
    Optional<String> elseState,
    List<String> emits,
    List<String> actions
) {
    /** Returns true if this transition has a guard condition. */
    public boolean hasGuard() {
        return guard.isPresent();
    }

    /** Returns true if the guard is a simple boolean identifier (no operators). */
    public boolean hasSimpleGuard() {
        return guard.isPresent() && guard.get().matches("[a-zA-Z_][a-zA-Z0-9_]*");
    }
}
