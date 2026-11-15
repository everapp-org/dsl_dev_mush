package com.mcms.web.rest.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO for acknowledging an environmental alert.
 */
public class AcknowledgeAlertRequest {

    @NotNull
    private String resolutionNote;

    public AcknowledgeAlertRequest() {
        // Empty constructor needed for Jackson
    }

    public AcknowledgeAlertRequest(String resolutionNote) {
        this.resolutionNote = resolutionNote;
    }

    public String getResolutionNote() {
        return resolutionNote;
    }

    public void setResolutionNote(String resolutionNote) {
        this.resolutionNote = resolutionNote;
    }
}
