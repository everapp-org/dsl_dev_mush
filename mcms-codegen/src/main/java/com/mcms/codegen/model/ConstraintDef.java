package com.mcms.codegen.model;

/**
 * Semantic model representing an architectural constraint from the Kernel DSL.
 * Constraints define simple architecture or dependency rules (forbid/allow imports).
 */
public record ConstraintDef(
    ConstraintType type,
    String pattern
) {
    public enum ConstraintType {
        FORBID, ALLOW
    }
}
