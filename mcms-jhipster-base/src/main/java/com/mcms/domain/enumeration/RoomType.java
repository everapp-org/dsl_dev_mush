package com.mcms.domain.enumeration;

/**
 * Physical room classification within the farm.
 * Rooms are now entities; this enum classifies their function.
 */
public enum RoomType {
    /**
     * Sterile/clean zone for inoculation
     */
    STERILE_ZONE,
    /**
     * Incubation chambers - dark, warm
     */
    INCUBATION,
    /**
     * Fruiting chambers - high humidity, fresh air
     */
    FRUITING_CHAMBER,
    /**
     * Cold storage for harvested product
     */
    COLD_STORAGE,
    /**
     * General work area - packing, prep
     */
    WORK_ZONE,
    /**
     * Lab - cultures, quality testing
     */
    LABORATORY,
}
