package com.mcms.codegen.model;

import java.util.List;

/**
 * Semantic model representing a service operation from the Kernel DSL.
 * Operations may correspond to transition triggers.
 */
public record OperationDef(
    String name,
    List<String> parameters
) {}
