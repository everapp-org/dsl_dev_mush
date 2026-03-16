package com.mcms.service;

import com.mcms.domain.enumeration.PhaseName;
import com.mcms.service.exception.InvalidTransitionException;
import java.util.*;
import org.springframework.stereotype.Component;

/**
 * Validates batch state machine transitions.
 *
 * State machine rules:
 * - INOCULATION → EARLY_COLONIZATION or COMPLETED
 * - EARLY_COLONIZATION → FULL_COLONIZATION or COMPLETED
 * - FULL_COLONIZATION → CONSOLIDATION or COMPLETED
 * - CONSOLIDATION → FRUITING_TRIGGER or COMPLETED
 * - FRUITING_TRIGGER → PRIMORDIA or COMPLETED
 * - PRIMORDIA → FRUITING_BODY_GROWTH or COMPLETED
 * - FRUITING_BODY_GROWTH → HARVEST or COMPLETED
 * - HARVEST → REHYDRATION_PAUSE or COMPLETED
 * - REHYDRATION_PAUSE → HARVEST or COMPLETED
 * - COMPLETED → none (terminal state)
 */
@Component
public class BatchStateMachineValidator {

    private static final Map<PhaseName, List<PhaseName>> STATE_MACHINE = new HashMap<>();

    static {
        // Linear progression with ability to complete at any phase
        STATE_MACHINE.put(PhaseName.INOCULATION,
            Arrays.asList(PhaseName.EARLY_COLONIZATION, PhaseName.COMPLETED));

        STATE_MACHINE.put(PhaseName.EARLY_COLONIZATION,
            Arrays.asList(PhaseName.FULL_COLONIZATION, PhaseName.COMPLETED));

        STATE_MACHINE.put(PhaseName.FULL_COLONIZATION,
            Arrays.asList(PhaseName.CONSOLIDATION, PhaseName.COMPLETED));

        STATE_MACHINE.put(PhaseName.CONSOLIDATION,
            Arrays.asList(PhaseName.FRUITING_TRIGGER, PhaseName.COMPLETED));

        STATE_MACHINE.put(PhaseName.FRUITING_TRIGGER,
            Arrays.asList(PhaseName.PRIMORDIA, PhaseName.COMPLETED));

        STATE_MACHINE.put(PhaseName.PRIMORDIA,
            Arrays.asList(PhaseName.FRUITING_BODY_GROWTH, PhaseName.COMPLETED));

        STATE_MACHINE.put(PhaseName.FRUITING_BODY_GROWTH,
            Arrays.asList(PhaseName.HARVEST, PhaseName.COMPLETED));

        STATE_MACHINE.put(PhaseName.HARVEST,
            Arrays.asList(PhaseName.REHYDRATION_PAUSE, PhaseName.COMPLETED));

        // Rehydration allows loop back to harvest for additional flushes
        STATE_MACHINE.put(PhaseName.REHYDRATION_PAUSE,
            Arrays.asList(PhaseName.HARVEST, PhaseName.COMPLETED));

        // COMPLETED is terminal - no transitions allowed
        STATE_MACHINE.put(PhaseName.COMPLETED, Collections.emptyList());
    }

    /**
     * Validate if a transition is allowed by the state machine.
     *
     * @param currentPhase the current phase
     * @param targetPhase the target phase to transition to
     * @throws InvalidTransitionException if the transition is not allowed
     */
    public void validateTransition(PhaseName currentPhase, PhaseName targetPhase) {
        if (currentPhase == null) {
            throw new IllegalArgumentException("Current phase cannot be null");
        }
        if (targetPhase == null) {
            throw new IllegalArgumentException("Target phase cannot be null");
        }

        List<PhaseName> allowedTransitions = getAllowedTransitions(currentPhase);

        if (!allowedTransitions.contains(targetPhase)) {
            throw new InvalidTransitionException(currentPhase, targetPhase, allowedTransitions);
        }
    }

    /**
     * Get list of allowed transitions from a given phase.
     *
     * @param currentPhase the current phase
     * @return list of allowed target phases
     */
    public List<PhaseName> getAllowedTransitions(PhaseName currentPhase) {
        return STATE_MACHINE.getOrDefault(currentPhase, Collections.emptyList());
    }

    /**
     * Check if a transition is valid without throwing an exception.
     *
     * @param currentPhase the current phase
     * @param targetPhase the target phase
     * @return true if transition is allowed, false otherwise
     */
    public boolean isTransitionValid(PhaseName currentPhase, PhaseName targetPhase) {
        if (currentPhase == null || targetPhase == null) {
            return false;
        }
        return getAllowedTransitions(currentPhase).contains(targetPhase);
    }
}
