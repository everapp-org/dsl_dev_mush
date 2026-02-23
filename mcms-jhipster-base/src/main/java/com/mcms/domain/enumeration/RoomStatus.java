package com.mcms.domain.enumeration;

/**
 * Room operational status for scheduling and maintenance.
 */
public enum RoomStatus {
    /**
     * Active / in use
     */
    ACTIVE,
    /**
     * Under maintenance / cleaning
     */
    MAINTENANCE,
    /**
     * Undergoing disinfection between batches
     */
    DISINFECTION,
    /**
     * Inactive / offline
     */
    INACTIVE,
}
