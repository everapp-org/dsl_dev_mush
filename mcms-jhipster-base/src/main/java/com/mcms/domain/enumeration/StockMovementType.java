package com.mcms.domain.enumeration;

/**
 * Stock movement direction / reason.
 */
public enum StockMovementType {
    /**
     * Inbound: material received from supplier
     */
    RECEIPT,
    /**
     * Outbound: material consumed by a batch
     */
    CONSUMPTION,
    /**
     * Inventory correction - increase
     */
    ADJUSTMENT_IN,
    /**
     * Inventory correction - decrease
     */
    ADJUSTMENT_OUT,
    /**
     * Material lost / expired / discarded
     */
    WASTE,
    /**
     * Material returned to supplier
     */
    RETURN_TO_SUPPLIER,
}
