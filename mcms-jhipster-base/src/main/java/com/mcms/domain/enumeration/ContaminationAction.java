package com.mcms.domain.enumeration;

/**
 * Contamination response actions.
 */
public enum ContaminationAction {
    /**
     * Isolate affected units
     */
    ISOLATE,
    /**
     * Remove contaminated substrate
     */
    REMOVE,
    /**
     * Salt treatment
     */
    SALT_TREATMENT,
    /**
     * Hydrogen peroxide treatment
     */
    PEROXIDE_TREATMENT,
    /**
     * Increased ventilation
     */
    INCREASED_VENTILATION,
    /**
     * Discard entire batch
     */
    DISCARD_BATCH,
    /**
     * Continue monitoring
     */
    MONITOR,
}
