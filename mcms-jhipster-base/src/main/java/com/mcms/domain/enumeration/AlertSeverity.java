package com.mcms.domain.enumeration;

/**
 * Alert severity for environmental deviations.
 */
public enum AlertSeverity {
    /**
     * Informational
     */
    INFO,
    /**
     * Warning - approaching threshold
     */
    WARNING,
    /**
     * Critical - immediate action needed
     */
    CRITICAL,
}
