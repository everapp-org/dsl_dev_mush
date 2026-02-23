package com.mcms.codegen.model;

import java.util.List;
import java.util.Optional;

/**
 * Semantic model representing a service block from the Kernel DSL.
 * Services group externally visible operations (application services, APIs).
 */
public record ServiceDef(
    String name,
    Optional<String> description,
    List<OperationDef> operations
) {}
