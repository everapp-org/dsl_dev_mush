package com.mcms.domain.enumeration;

/**
 * Payment status for orders.
 */
public enum PaymentStatus {
    /**
     * Not yet paid
     */
    UNPAID,
    /**
     * Partial payment received
     */
    PARTIALLY_PAID,
    /**
     * Fully paid
     */
    PAID,
    /**
     * Payment past due date
     */
    OVERDUE,
    /**
     * Payment refunded
     */
    REFUNDED,
}
