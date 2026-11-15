package com.mcms.web.rest.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for discarding a contaminated batch with a required reason.
 */
public class DiscardBatchRequest {

    @NotBlank(message = "Reason for discarding batch is required")
    private String reason;

    public DiscardBatchRequest() {}

    public DiscardBatchRequest(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
