package com.mcms.web.rest.dto;

import com.mcms.domain.enumeration.PhaseName;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for admin force batch state transition requests.
 */
public class ForceTransitionRequest {

    @NotNull
    private PhaseName targetPhase;

    @NotNull
    private String adminPassword;

    private String reason;

    public PhaseName getTargetPhase() {
        return targetPhase;
    }

    public void setTargetPhase(PhaseName targetPhase) {
        this.targetPhase = targetPhase;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
