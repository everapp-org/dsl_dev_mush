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
 * Supply order line item - individual item on a purchase order.
 * Each line represents one material being procured.
 * Links to Material catalog for standardized identification.
 * Supports partial deliveries and per-line quality tracking.
 */
@Schema(
    description = "Supply order line item - individual item on a purchase order.\nEach line represents one material being procured.\nLinks to Material catalog for standardized identification.\nSupports partial deliveries and per-line quality tracking."
)
@Entity
@Table(name = "supply_order_line")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SupplyOrderLine implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * Line sequence (1, 2, 3...)
     */
    @Schema(description = "Line sequence (1, 2, 3...)", required = true)
    @NotNull
    @Column(name = "line_number", nullable = false)
    private Integer lineNumber;

    /**
     * Free-text description of what is being ordered
     */
    @Schema(description = "Free-text description of what is being ordered", required = true)
    @NotNull
    @Column(name = "item_description", nullable = false)
    private String itemDescription;

    /**
     * Amount ordered
     */
    @Schema(description = "Amount ordered", required = true)
    @NotNull
    @Column(name = "quantity_ordered", precision = 21, scale = 2, nullable = false)
    private BigDecimal quantityOrdered;

    /**
     * Amount actually received (supports partial delivery)
     */
    @Schema(description = "Amount actually received (supports partial delivery)")
    @Column(name = "quantity_received", precision = 21, scale = 2)
    private BigDecimal quantityReceived;

    /**
     * Unit of measure
     */
    @Schema(description = "Unit of measure", required = true)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "unit", nullable = false)
    private UnitOfMeasure unit;

    /**
     * Price per unit
     */
    @Schema(description = "Price per unit", required = true)
    @NotNull
    @Column(name = "unit_price", precision = 21, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    /**
     * quantityOrdered × unitPrice
     */
    @Schema(description = "quantityOrdered × unitPrice", required = true)
    @NotNull
    @Column(name = "line_total", precision = 21, scale = 2, nullable = false)
    private BigDecimal lineTotal;

    /**
     * Supplier lot/batch number for traceability
     */
    @Schema(description = "Supplier lot/batch number for traceability")
    @Column(name = "lot_number")
    private String lotNumber;

    /**
     * Expiry date of supplied material
     */
    @Schema(description = "Expiry date of supplied material")
    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    /**
     * Quality assessment upon delivery
     */
    @Schema(description = "Quality assessment upon delivery")
    @Column(name = "quality_on_receipt")
    private String qualityOnReceipt;

    /**
     * Has this line been received?
     */
    @Schema(description = "Has this line been received?", required = true)
    @NotNull
    @Column(name = "is_received", nullable = false)
    private Boolean isReceived;

    @Lob
    @Column(name = "note")
    private String note;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "supplier" }, allowSetters = true)
    private SupplyOrder supplyOrder;

    @ManyToOne(optional = false)
    @NotNull
    private Material material;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "strain", "recipe" }, allowSetters = true)
    private Batch batch;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SupplyOrderLine id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getLineNumber() {
        return this.lineNumber;
    }

    public SupplyOrderLine lineNumber(Integer lineNumber) {
        this.setLineNumber(lineNumber);
        return this;
    }

    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getItemDescription() {
        return this.itemDescription;
    }

    public SupplyOrderLine itemDescription(String itemDescription) {
        this.setItemDescription(itemDescription);
        return this;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public BigDecimal getQuantityOrdered() {
        return this.quantityOrdered;
    }

    public SupplyOrderLine quantityOrdered(BigDecimal quantityOrdered) {
        this.setQuantityOrdered(quantityOrdered);
        return this;
    }

    public void setQuantityOrdered(BigDecimal quantityOrdered) {
        this.quantityOrdered = quantityOrdered;
    }

    public BigDecimal getQuantityReceived() {
        return this.quantityReceived;
    }

    public SupplyOrderLine quantityReceived(BigDecimal quantityReceived) {
        this.setQuantityReceived(quantityReceived);
        return this;
    }

    public void setQuantityReceived(BigDecimal quantityReceived) {
        this.quantityReceived = quantityReceived;
    }

    public UnitOfMeasure getUnit() {
        return this.unit;
    }

    public SupplyOrderLine unit(UnitOfMeasure unit) {
        this.setUnit(unit);
        return this;
    }

    public void setUnit(UnitOfMeasure unit) {
        this.unit = unit;
    }

    public BigDecimal getUnitPrice() {
        return this.unitPrice;
    }

    public SupplyOrderLine unitPrice(BigDecimal unitPrice) {
        this.setUnitPrice(unitPrice);
        return this;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getLineTotal() {
        return this.lineTotal;
    }

    public SupplyOrderLine lineTotal(BigDecimal lineTotal) {
        this.setLineTotal(lineTotal);
        return this;
    }

    public void setLineTotal(BigDecimal lineTotal) {
        this.lineTotal = lineTotal;
    }

    public String getLotNumber() {
        return this.lotNumber;
    }

    public SupplyOrderLine lotNumber(String lotNumber) {
        this.setLotNumber(lotNumber);
        return this;
    }

    public void setLotNumber(String lotNumber) {
        this.lotNumber = lotNumber;
    }

    public LocalDate getExpiryDate() {
        return this.expiryDate;
    }

    public SupplyOrderLine expiryDate(LocalDate expiryDate) {
        this.setExpiryDate(expiryDate);
        return this;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getQualityOnReceipt() {
        return this.qualityOnReceipt;
    }

    public SupplyOrderLine qualityOnReceipt(String qualityOnReceipt) {
        this.setQualityOnReceipt(qualityOnReceipt);
        return this;
    }

    public void setQualityOnReceipt(String qualityOnReceipt) {
        this.qualityOnReceipt = qualityOnReceipt;
    }

    public Boolean getIsReceived() {
        return this.isReceived;
    }

    public SupplyOrderLine isReceived(Boolean isReceived) {
        this.setIsReceived(isReceived);
        return this;
    }

    public void setIsReceived(Boolean isReceived) {
        this.isReceived = isReceived;
    }

    public String getNote() {
        return this.note;
    }

    public SupplyOrderLine note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public SupplyOrder getSupplyOrder() {
        return this.supplyOrder;
    }

    public void setSupplyOrder(SupplyOrder supplyOrder) {
        this.supplyOrder = supplyOrder;
    }

    public SupplyOrderLine supplyOrder(SupplyOrder supplyOrder) {
        this.setSupplyOrder(supplyOrder);
        return this;
    }

    public Material getMaterial() {
        return this.material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public SupplyOrderLine material(Material material) {
        this.setMaterial(material);
        return this;
    }

    public Batch getBatch() {
        return this.batch;
    }

    public void setBatch(Batch batch) {
        this.batch = batch;
    }

    public SupplyOrderLine batch(Batch batch) {
        this.setBatch(batch);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SupplyOrderLine)) {
            return false;
        }
        return getId() != null && getId().equals(((SupplyOrderLine) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SupplyOrderLine{" +
            "id=" + getId() +
            ", lineNumber=" + getLineNumber() +
            ", itemDescription='" + getItemDescription() + "'" +
            ", quantityOrdered=" + getQuantityOrdered() +
            ", quantityReceived=" + getQuantityReceived() +
            ", unit='" + getUnit() + "'" +
            ", unitPrice=" + getUnitPrice() +
            ", lineTotal=" + getLineTotal() +
            ", lotNumber='" + getLotNumber() + "'" +
            ", expiryDate='" + getExpiryDate() + "'" +
            ", qualityOnReceipt='" + getQualityOnReceipt() + "'" +
            ", isReceived='" + getIsReceived() + "'" +
            ", note='" + getNote() + "'" +
            "}";
    }
}
