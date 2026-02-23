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
 * Batch material usage - the key traceability link.
 * Records exactly which materials (from which inventory lots,
 * from which supply orders, from which suppliers) were consumed
 * in each production batch.
 *
 * Answers: \"Batch PO-2025-001 used 200kg of wheat straw from
 * LOT-2025-0042, which came from SupplyOrder PO-2025-018
 * line 2, supplied by Agro-Straw d.o.o.\"
 */
@Schema(
    description = "Batch material usage - the key traceability link.\nRecords exactly which materials (from which inventory lots,\nfrom which supply orders, from which suppliers) were consumed\nin each production batch.\n\nAnswers: \"Batch PO-2025-001 used 200kg of wheat straw from\nLOT-2025-0042, which came from SupplyOrder PO-2025-018\nline 2, supplied by Agro-Straw d.o.o.\""
)
@Entity
@Table(name = "batch_material_usage")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BatchMaterialUsage implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * When the material was used
     */
    @Schema(description = "When the material was used", required = true)
    @NotNull
    @Column(name = "usage_date", nullable = false)
    private LocalDate usageDate;

    /**
     * How much was consumed
     */
    @Schema(description = "How much was consumed", required = true)
    @NotNull
    @Column(name = "quantity_used", precision = 21, scale = 2, nullable = false)
    private BigDecimal quantityUsed;

    /**
     * Unit of measure
     */
    @Schema(description = "Unit of measure", required = true)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "unit", nullable = false)
    private UnitOfMeasure unit;

    /**
     * What it was used for, e.g. \"Substrate preparation\", \"Supplementation\"
     */
    @Schema(description = "What it was used for, e.g. \"Substrate preparation\", \"Supplementation\"")
    @Column(name = "purpose")
    private String purpose;

    @Lob
    @Column(name = "note")
    private String note;

    /**
     * Batch material usage - THE KEY TRACEABILITY LINK
     */
    @Schema(description = "Batch material usage - THE KEY TRACEABILITY LINK")
    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "strain", "recipe" }, allowSetters = true)
    private Batch batch;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "material", "supplyOrderLine" }, allowSetters = true)
    private InventoryLot inventoryLot;

    @ManyToOne(optional = false)
    @NotNull
    private Material material;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public BatchMaterialUsage id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getUsageDate() {
        return this.usageDate;
    }

    public BatchMaterialUsage usageDate(LocalDate usageDate) {
        this.setUsageDate(usageDate);
        return this;
    }

    public void setUsageDate(LocalDate usageDate) {
        this.usageDate = usageDate;
    }

    public BigDecimal getQuantityUsed() {
        return this.quantityUsed;
    }

    public BatchMaterialUsage quantityUsed(BigDecimal quantityUsed) {
        this.setQuantityUsed(quantityUsed);
        return this;
    }

    public void setQuantityUsed(BigDecimal quantityUsed) {
        this.quantityUsed = quantityUsed;
    }

    public UnitOfMeasure getUnit() {
        return this.unit;
    }

    public BatchMaterialUsage unit(UnitOfMeasure unit) {
        this.setUnit(unit);
        return this;
    }

    public void setUnit(UnitOfMeasure unit) {
        this.unit = unit;
    }

    public String getPurpose() {
        return this.purpose;
    }

    public BatchMaterialUsage purpose(String purpose) {
        this.setPurpose(purpose);
        return this;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getNote() {
        return this.note;
    }

    public BatchMaterialUsage note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Batch getBatch() {
        return this.batch;
    }

    public void setBatch(Batch batch) {
        this.batch = batch;
    }

    public BatchMaterialUsage batch(Batch batch) {
        this.setBatch(batch);
        return this;
    }

    public InventoryLot getInventoryLot() {
        return this.inventoryLot;
    }

    public void setInventoryLot(InventoryLot inventoryLot) {
        this.inventoryLot = inventoryLot;
    }

    public BatchMaterialUsage inventoryLot(InventoryLot inventoryLot) {
        this.setInventoryLot(inventoryLot);
        return this;
    }

    public Material getMaterial() {
        return this.material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public BatchMaterialUsage material(Material material) {
        this.setMaterial(material);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BatchMaterialUsage)) {
            return false;
        }
        return getId() != null && getId().equals(((BatchMaterialUsage) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BatchMaterialUsage{" +
            "id=" + getId() +
            ", usageDate='" + getUsageDate() + "'" +
            ", quantityUsed=" + getQuantityUsed() +
            ", unit='" + getUnit() + "'" +
            ", purpose='" + getPurpose() + "'" +
            ", note='" + getNote() + "'" +
            "}";
    }
}
