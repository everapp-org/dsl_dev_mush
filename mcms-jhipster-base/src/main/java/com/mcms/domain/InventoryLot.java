package com.mcms.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mcms.domain.enumeration.UnitOfMeasure;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Inventory lot - a specific batch of material received into stock.
 * Each lot is linked to the SupplyOrderLine it came from,
 * providing full traceability back to the supplier and PO.
 *
 * Lot quantity lifecycle:
 * quantityReceived (initial) -> quantityOnHand (current, decremented by consumption)
 */
@Schema(
    description = "Inventory lot - a specific batch of material received into stock.\nEach lot is linked to the SupplyOrderLine it came from,\nproviding full traceability back to the supplier and PO.\n\nLot quantity lifecycle:\nquantityReceived (initial) -> quantityOnHand (current, decremented by consumption)"
)
@Entity
@Table(name = "inventory_lot")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InventoryLot implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * Internal lot identifier, e.g. \"LOT-2025-0042\"
     */
    @Schema(description = "Internal lot identifier, e.g. \"LOT-2025-0042\"", required = true)
    @NotNull
    @Column(name = "lot_code", nullable = false, unique = true)
    private String lotCode;

    /**
     * Date material was received into inventory
     */
    @Schema(description = "Date material was received into inventory", required = true)
    @NotNull
    @Column(name = "received_date", nullable = false)
    private LocalDate receivedDate;

    /**
     * Original quantity received
     */
    @Schema(description = "Original quantity received", required = true)
    @NotNull
    @Column(name = "quantity_received", precision = 21, scale = 2, nullable = false)
    private BigDecimal quantityReceived;

    /**
     * Current remaining quantity
     */
    @Schema(description = "Current remaining quantity", required = true)
    @NotNull
    @Column(name = "quantity_on_hand", precision = 21, scale = 2, nullable = false)
    private BigDecimal quantityOnHand;

    /**
     * Unit of measure
     */
    @Schema(description = "Unit of measure", required = true)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "unit", nullable = false)
    private UnitOfMeasure unit;

    /**
     * Material expiry date
     */
    @Schema(description = "Material expiry date")
    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    /**
     * Where it's stored, e.g. \"Warehouse A, Shelf 3\"
     */
    @Schema(description = "Where it's stored, e.g. \"Warehouse A, Shelf 3\"")
    @Column(name = "storage_location")
    private String storageLocation;

    /**
     * Supplier's lot/batch number (external reference)
     */
    @Schema(description = "Supplier's lot/batch number (external reference)")
    @Column(name = "supplier_lot_number")
    private String supplierLotNumber;

    /**
     * True when quantityOnHand reaches 0
     */
    @Schema(description = "True when quantityOnHand reaches 0", required = true)
    @NotNull
    @Column(name = "is_exhausted", nullable = false)
    private Boolean isExhausted;

    @Lob
    @Column(name = "note")
    private String note;

    /**
     * Inventory & stock management
     */
    @Schema(description = "Inventory & stock management")
    @ManyToOne(optional = false)
    @NotNull
    private Material material;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "supplyOrder", "material", "batch" }, allowSetters = true)
    private SupplyOrderLine supplyOrderLine;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public InventoryLot id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLotCode() {
        return this.lotCode;
    }

    public InventoryLot lotCode(String lotCode) {
        this.setLotCode(lotCode);
        return this;
    }

    public void setLotCode(String lotCode) {
        this.lotCode = lotCode;
    }

    public LocalDate getReceivedDate() {
        return this.receivedDate;
    }

    public InventoryLot receivedDate(LocalDate receivedDate) {
        this.setReceivedDate(receivedDate);
        return this;
    }

    public void setReceivedDate(LocalDate receivedDate) {
        this.receivedDate = receivedDate;
    }

    public BigDecimal getQuantityReceived() {
        return this.quantityReceived;
    }

    public InventoryLot quantityReceived(BigDecimal quantityReceived) {
        this.setQuantityReceived(quantityReceived);
        return this;
    }

    public void setQuantityReceived(BigDecimal quantityReceived) {
        this.quantityReceived = quantityReceived;
    }

    public BigDecimal getQuantityOnHand() {
        return this.quantityOnHand;
    }

    public InventoryLot quantityOnHand(BigDecimal quantityOnHand) {
        this.setQuantityOnHand(quantityOnHand);
        return this;
    }

    public void setQuantityOnHand(BigDecimal quantityOnHand) {
        this.quantityOnHand = quantityOnHand;
    }

    public UnitOfMeasure getUnit() {
        return this.unit;
    }

    public InventoryLot unit(UnitOfMeasure unit) {
        this.setUnit(unit);
        return this;
    }

    public void setUnit(UnitOfMeasure unit) {
        this.unit = unit;
    }

    public LocalDate getExpiryDate() {
        return this.expiryDate;
    }

    public InventoryLot expiryDate(LocalDate expiryDate) {
        this.setExpiryDate(expiryDate);
        return this;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getStorageLocation() {
        return this.storageLocation;
    }

    public InventoryLot storageLocation(String storageLocation) {
        this.setStorageLocation(storageLocation);
        return this;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public String getSupplierLotNumber() {
        return this.supplierLotNumber;
    }

    public InventoryLot supplierLotNumber(String supplierLotNumber) {
        this.setSupplierLotNumber(supplierLotNumber);
        return this;
    }

    public void setSupplierLotNumber(String supplierLotNumber) {
        this.supplierLotNumber = supplierLotNumber;
    }

    public Boolean getIsExhausted() {
        return this.isExhausted;
    }

    public InventoryLot isExhausted(Boolean isExhausted) {
        this.setIsExhausted(isExhausted);
        return this;
    }

    public void setIsExhausted(Boolean isExhausted) {
        this.isExhausted = isExhausted;
    }

    public String getNote() {
        return this.note;
    }

    public InventoryLot note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Material getMaterial() {
        return this.material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public InventoryLot material(Material material) {
        this.setMaterial(material);
        return this;
    }

    public SupplyOrderLine getSupplyOrderLine() {
        return this.supplyOrderLine;
    }

    public void setSupplyOrderLine(SupplyOrderLine supplyOrderLine) {
        this.supplyOrderLine = supplyOrderLine;
    }

    public InventoryLot supplyOrderLine(SupplyOrderLine supplyOrderLine) {
        this.setSupplyOrderLine(supplyOrderLine);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InventoryLot)) {
            return false;
        }
        return getId() != null && getId().equals(((InventoryLot) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InventoryLot{" +
            "id=" + getId() +
            ", lotCode='" + getLotCode() + "'" +
            ", receivedDate='" + getReceivedDate() + "'" +
            ", quantityReceived=" + getQuantityReceived() +
            ", quantityOnHand=" + getQuantityOnHand() +
            ", unit='" + getUnit() + "'" +
            ", expiryDate='" + getExpiryDate() + "'" +
            ", storageLocation='" + getStorageLocation() + "'" +
            ", supplierLotNumber='" + getSupplierLotNumber() + "'" +
            ", isExhausted='" + getIsExhausted() + "'" +
            ", note='" + getNote() + "'" +
            "}";
    }
}
