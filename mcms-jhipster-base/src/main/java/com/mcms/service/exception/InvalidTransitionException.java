package com.mcms.service.exception;

import com.mcms.domain.enumeration.PhaseName;
import java.util.List;

/**
 * Exception thrown when an invalid state machine transition is attempted.
 */
public class InvalidTransitionException extends RuntimeException {

    private final PhaseName currentPhase;
    private final PhaseName targetPhase;
    private final List<PhaseName> allowedTransitions;

    public InvalidTransitionException(PhaseName currentPhase, PhaseName targetPhase, List<PhaseName> allowedTransitions) {
        super(String.format(
            "Invalid transition from %s to %s. Allowed transitions: %s",
            currentPhase,
            targetPhase,
            allowedTransitions.isEmpty() ? "none (phase is terminal)" : allowedTransitions
        ));
        this.currentPhase = currentPhase;
        this.targetPhase = targetPhase;
        this.allowedTransitions = allowedTransitions;
    }

    public PhaseName getCurrentPhase() {
        return currentPhase;
    }

    public PhaseName getTargetPhase() {
        return targetPhase;
    }

    public List<PhaseName> getAllowedTransitions() {
        return allowedTransitions;
    }
}
