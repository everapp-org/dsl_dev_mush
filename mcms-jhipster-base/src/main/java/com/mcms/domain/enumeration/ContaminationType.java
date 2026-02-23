package com.mcms.domain.enumeration;

/**
 * Common contamination agent types in mushroom cultivation.
 */
public enum ContaminationType {
    /**
     * Green mold - most common competitor
     */
    TRICHODERMA,
    /**
     * Cobweb mold (Dactylium)
     */
    COBWEB,
    /**
     * Aspergillus niger
     */
    BLACK_MOLD,
    /**
     * Pseudomonas tolaasii
     */
    BACTERIAL_BLOTCH,
    /**
     * Mycogone perniciosa
     */
    WET_BUBBLE,
    /**
     * Lecanicillium fungicola
     */
    DRY_BUBBLE,
    /**
     * Insect infestation (sciarid flies, mites)
     */
    INSECT,
    /**
     * Unknown - requires lab analysis
     */
    UNKNOWN,
    /**
     * Other
     */
    OTHER,
}
