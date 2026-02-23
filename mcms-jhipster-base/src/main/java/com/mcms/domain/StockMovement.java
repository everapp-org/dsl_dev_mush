package com.mcms.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mcms.domain.enumeration.StockMovementType;
import com.mcms.domain.enumeration.UnitOfMeasure;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Stock movement - every inbound/outbound inventory transaction.
 * Provides a complete audit trail of all stock changes.
 * Each movement adjusts the quantityOnHand of its InventoryLot.
 */
@Schema(
    description = "Stock movement - every inbound/outbound inventory transaction.\nProvides a complete audit trail of all stock changes.\nEach movement adjusts the quantityOnHand of its InventoryLot."
)
@Entity
@Table(name = "stock_movement")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StockMovement implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * When the movement occurred
     */
    @Schema(description = "When the movement occurred", required = true)
    @NotNull
    @Column(name = "movement_date", nullable = false)
    private Instant movementDate;

    /**
     * RECEIPT, CONSUMPTION, ADJUSTMENT, WASTE, RETURN
     */
    @Schema(description = "RECEIPT, CONSUMPTION, ADJUSTMENT, WASTE, RETURN", required = true)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false)
    private StockMovementType movementType;

    /**
     * Quantity moved (always positive; direction from type)
     */
    @Schema(description = "Quantity moved (always positive; direction from type)", required = true)
    @NotNull
    @Column(name = "quantity", precision = 21, scale = 2, nullable = false)
    private BigDecimal quantity;

    /**
     * Unit of measure
     */
    @Schema(description = "Unit of measure", required = true)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "unit", nullable = false)
    private UnitOfMeasure unit;

    /**
     * External reference (PO number, batch code, etc.)
     */
    @Schema(description = "External reference (PO number, batch code, etc.)")
    @Column(name = "reference")
    private String reference;

    /**
     * Reason for adjustment/waste
     */
    @Schema(description = "Reason for adjustment/waste")
    @Column(name = "reason")
    private String reason;

    /**
     * Who performed this transaction
     */
    @Schema(description = "Who performed this transaction")
    @Column(name = "performed_by")
    private String performedBy;

    @Lob
    @Column(name = "note")
    private String note;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "material", "supplyOrderLine" }, allowSetters = true)
    private InventoryLot inventoryLot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "strain", "recipe" }, allowSetters = true)
    private Batch batch;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public StockMovement id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getMovementDate() {
        return this.movementDate;
    }

    public StockMovement movementDate(Instant movementDate) {
        this.setMovementDate(movementDate);
        return this;
    }

    public void setMovementDate(Instant movementDate) {
        this.movementDate = movementDate;
    }

    public StockMovementType getMovementType() {
        return this.movementType;
    }

    public StockMovement movementType(StockMovementType movementType) {
        this.setMovementType(movementType);
        return this;
    }

    public void setMovementType(StockMovementType movementType) {
        this.movementType = movementType;
    }

    public BigDecimal getQuantity() {
        return this.quantity;
    }

    public StockMovement quantity(BigDecimal quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public UnitOfMeasure getUnit() {
        return this.unit;
    }

    public StockMovement unit(UnitOfMeasure unit) {
        this.setUnit(unit);
        return this;
    }

    public void setUnit(UnitOfMeasure unit) {
        this.unit = unit;
    }

    public String getReference() {
        return this.reference;
    }

    public StockMovement reference(String reference) {
        this.setReference(reference);
        return this;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getReason() {
        return this.reason;
    }

    public StockMovement reason(String reason) {
        this.setReason(reason);
        return this;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getPerformedBy() {
        return this.performedBy;
    }

    public StockMovement performedBy(String performedBy) {
        this.setPerformedBy(performedBy);
        return this;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }

    public String getNote() {
        return this.note;
    }

    public StockMovement note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public InventoryLot getInventoryLot() {
        return this.inventoryLot;
    }

    public void setInventoryLot(InventoryLot inventoryLot) {
        this.inventoryLot = inventoryLot;
    }

    public StockMovement inventoryLot(InventoryLot inventoryLot) {
        this.setInventoryLot(inventoryLot);
        return this;
    }

    public Batch getBatch() {
        return this.batch;
    }

    public void setBatch(Batch batch) {
        this.batch = batch;
    }

    public StockMovement batch(Batch batch) {
        this.setBatch(batch);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StockMovement)) {
            return false;
        }
        return getId() != null && getId().equals(((StockMovement) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StockMovement{" +
            "id=" + getId() +
            ", movementDate='" + getMovementDate() + "'" +
            ", movementType='" + getMovementType() + "'" +
            ", quantity=" + getQuantity() +
            ", unit='" + getUnit() + "'" +
            ", reference='" + getReference() + "'" +
            ", reason='" + getReason() + "'" +
            ", performedBy='" + getPerformedBy() + "'" +
            ", note='" + getNote() + "'" +
            "}";
    }
}
