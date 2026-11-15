package com.mcms.codegen.model;

import java.util.List;

/**
 * Semantic model representing a level block from the Kernel DSL.
 * A level subdivides a domain by abstraction (e.g., domain, application, infrastructure).
 */
public record LevelDef(
    String name,
    List<ModelDef> models,
    List<ServiceDef> services
) {}
