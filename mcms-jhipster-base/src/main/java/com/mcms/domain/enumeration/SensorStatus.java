package com.mcms.domain.enumeration;

/**
 * Sensor operational status.
 */
public enum SensorStatus {
    /**
     * Online and reporting
     */
    ACTIVE,
    /**
     * Offline / disabled
     */
    INACTIVE,
    /**
     * Error / malfunction
     */
    ERROR,
    /**
     * Under calibration
     */
    CALIBRATING,
}
