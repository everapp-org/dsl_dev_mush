package com.mcms.domain.enumeration;

/**
 * Contamination severity classification.
 */
public enum ContaminationSeverity {
    /**
     * Isolated spot, manageable
     */
    LOW,
    /**
     * Spreading, requires intervention
     */
    MEDIUM,
    /**
     * Significant loss expected
     */
    HIGH,
    /**
     * Batch may be lost
     */
    CRITICAL,
}
