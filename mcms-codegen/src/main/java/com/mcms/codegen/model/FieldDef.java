package com.mcms.codegen.model;

/**
 * Semantic model representing a field definition from the Kernel DSL.
 * Fields have a name and a type (type is a free-form string, domain-agnostic).
 */
public record FieldDef(
    String name,
    String type
) {}
