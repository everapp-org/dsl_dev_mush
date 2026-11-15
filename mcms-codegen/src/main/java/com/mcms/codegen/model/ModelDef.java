package com.mcms.codegen.model;

import java.util.List;
import java.util.Optional;

/**
 * Semantic model representing a model block from the Kernel DSL.
 * A model is a domain entity or value object that can have fields, states, and transitions.
 */
public record ModelDef(
    String name,
    Optional<String> description,
    List<FieldDef> fields,
    Optional<StateMachineDef> stateMachine
) {}
