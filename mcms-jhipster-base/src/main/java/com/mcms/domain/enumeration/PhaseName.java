package com.mcms.domain.enumeration;

/**
 * Biological production phases - ordered lifecycle.
 * Phase ordering is enforced at the service layer via a state machine.
 * Sequence: 1->2->3->4->5->6->7->8->(9->10->8)* -> COMPLETED
 */
public enum PhaseName {
    /**
     * 1 - Spawn injected into substrate
     */
    INOCULATION,
    /**
     * 2 - Mycelium begins spreading
     */
    EARLY_COLONIZATION,
    /**
     * 3 - Substrate fully colonized
     */
    FULL_COLONIZATION,
    /**
     * 4 - Mycelium matures and strengthens
     */
    CONSOLIDATION,
    /**
     * 5 - Environmental shock applied to initiate pinning
     */
    FRUITING_TRIGGER,
    /**
     * 6 - Pin formation (baby mushrooms)
     */
    PRIMORDIA,
    /**
     * 7 - Mushrooms develop to harvest size
     */
    FRUITING_BODY_GROWTH,
    /**
     * 8 - Picking (per flush cycle)
     */
    HARVEST,
    /**
     * 9 - Substrate rest and rehydration between flushes
     */
    REHYDRATION_PAUSE,
    /**
     * 10 - Batch lifecycle ended
     */
    COMPLETED,
}
