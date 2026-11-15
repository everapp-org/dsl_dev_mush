package com.mcms.web.rest.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * DTO for supply order line receive request.
 */
public class ReceiveSupplyLineRequest {

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal quantityReceived;

    public BigDecimal getQuantityReceived() {
        return quantityReceived;
    }

    public void setQuantityReceived(BigDecimal quantityReceived) {
        this.quantityReceived = quantityReceived;
    }

    @Override
    public String toString() {
        return "ReceiveSupplyLineRequest{" + "quantityReceived=" + quantityReceived + '}';
    }
}
