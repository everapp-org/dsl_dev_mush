package com.mcms.domain;

import com.mcms.domain.enumeration.PhaseName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Production batch - the central tracking entity.
 * Represents a single production run from inoculation to final harvest.
 */
@Schema(
    description = "Production batch - the central tracking entity.\nRepresents a single production run from inoculation to final harvest."
)
@Entity
@Table(name = "batch")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Batch implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * Unique identifier, e.g. \"PO-2025-001\"
     */
    @Schema(description = "Unique identifier, e.g. \"PO-2025-001\"", required = true)
    @NotNull
    @Column(name = "batch_code", nullable = false, unique = true)
    private String batchCode;

    /**
     * Inoculation date
     */
    @Schema(description = "Inoculation date", required = true)
    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    /**
     * Batch completion date
     */
    @Schema(description = "Batch completion date")
    @Column(name = "end_date")
    private LocalDate endDate;

    /**
     * Current lifecycle phase (state machine)
     */
    @Schema(description = "Current lifecycle phase (state machine)", required = true)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "current_phase", nullable = false)
    private PhaseName currentPhase;

    /**
     * Total substrate bags/blocks in batch
     */
    @Schema(description = "Total substrate bags/blocks in batch")
    @Column(name = "number_of_bags")
    private Integer numberOfBags;

    /**
     * Total substrate weight in kg
     */
    @Schema(description = "Total substrate weight in kg")
    @Column(name = "substrate_weight_kg", precision = 21, scale = 2)
    private BigDecimal substrateWeightKg;

    /**
     * Total spawn weight used in kg
     */
    @Schema(description = "Total spawn weight used in kg")
    @Column(name = "spawn_weight_kg", precision = 21, scale = 2)
    private BigDecimal spawnWeightKg;

    /**
     * Expected total yield in kg
     */
    @Schema(description = "Expected total yield in kg")
    @Column(name = "target_yield_kg", precision = 21, scale = 2)
    private BigDecimal targetYieldKg;

    /**
     * Accumulated actual yield across all flushes
     */
    @Schema(description = "Accumulated actual yield across all flushes")
    @Column(name = "actual_total_yield_kg", precision = 21, scale = 2)
    private BigDecimal actualTotalYieldKg;

    /**
     * (actual yield / dry substrate weight) × 100
     */
    @Schema(description = "(actual yield / dry substrate weight) × 100")
    @Column(name = "biological_efficiency_percent", precision = 21, scale = 2)
    private BigDecimal biologicalEfficiencyPercent;

    /**
     * Flag: any contamination detected?
     */
    @Schema(description = "Flag: any contamination detected?")
    @Column(name = "is_contaminated")
    private Boolean isContaminated;

    /**
     * Batch still in production?
     */
    @Schema(description = "Batch still in production?", required = true)
    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    /**
     * Final notes upon batch closure
     */
    @Schema(description = "Final notes upon batch closure")
    @Lob
    @Column(name = "completion_note")
    private String completionNote;

    /**
     * General batch notes
     */
    @Schema(description = "General batch notes")
    @Lob
    @Column(name = "note")
    private String note;

    /**
     * Production core
     */
    @Schema(description = "Production core")
    @ManyToOne(optional = false)
    @NotNull
    private Strain strain;

    @ManyToOne(optional = false)
    @NotNull
    private SubstrateRecipe recipe;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Batch id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBatchCode() {
        return this.batchCode;
    }

    public Batch batchCode(String batchCode) {
        this.setBatchCode(batchCode);
        return this;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public Batch startDate(LocalDate startDate) {
        this.setStartDate(startDate);
        return this;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }

    public Batch endDate(LocalDate endDate) {
        this.setEndDate(endDate);
        return this;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public PhaseName getCurrentPhase() {
        return this.currentPhase;
    }

    public Batch currentPhase(PhaseName currentPhase) {
        this.setCurrentPhase(currentPhase);
        return this;
    }

    public void setCurrentPhase(PhaseName currentPhase) {
        this.currentPhase = currentPhase;
    }

    public Integer getNumberOfBags() {
        return this.numberOfBags;
    }

    public Batch numberOfBags(Integer numberOfBags) {
        this.setNumberOfBags(numberOfBags);
        return this;
    }

    public void setNumberOfBags(Integer numberOfBags) {
        this.numberOfBags = numberOfBags;
    }

    public BigDecimal getSubstrateWeightKg() {
        return this.substrateWeightKg;
    }

    public Batch substrateWeightKg(BigDecimal substrateWeightKg) {
        this.setSubstrateWeightKg(substrateWeightKg);
        return this;
    }

    public void setSubstrateWeightKg(BigDecimal substrateWeightKg) {
        this.substrateWeightKg = substrateWeightKg;
    }

    public BigDecimal getSpawnWeightKg() {
        return this.spawnWeightKg;
    }

    public Batch spawnWeightKg(BigDecimal spawnWeightKg) {
        this.setSpawnWeightKg(spawnWeightKg);
        return this;
    }

    public void setSpawnWeightKg(BigDecimal spawnWeightKg) {
        this.spawnWeightKg = spawnWeightKg;
    }

    public BigDecimal getTargetYieldKg() {
        return this.targetYieldKg;
    }

    public Batch targetYieldKg(BigDecimal targetYieldKg) {
        this.setTargetYieldKg(targetYieldKg);
        return this;
    }

    public void setTargetYieldKg(BigDecimal targetYieldKg) {
        this.targetYieldKg = targetYieldKg;
    }

    public BigDecimal getActualTotalYieldKg() {
        return this.actualTotalYieldKg;
    }

    public Batch actualTotalYieldKg(BigDecimal actualTotalYieldKg) {
        this.setActualTotalYieldKg(actualTotalYieldKg);
        return this;
    }

    public void setActualTotalYieldKg(BigDecimal actualTotalYieldKg) {
        this.actualTotalYieldKg = actualTotalYieldKg;
    }

    public BigDecimal getBiologicalEfficiencyPercent() {
        return this.biologicalEfficiencyPercent;
    }

    public Batch biologicalEfficiencyPercent(BigDecimal biologicalEfficiencyPercent) {
        this.setBiologicalEfficiencyPercent(biologicalEfficiencyPercent);
        return this;
    }

    public void setBiologicalEfficiencyPercent(BigDecimal biologicalEfficiencyPercent) {
        this.biologicalEfficiencyPercent = biologicalEfficiencyPercent;
    }

    public Boolean getIsContaminated() {
        return this.isContaminated;
    }

    public Batch isContaminated(Boolean isContaminated) {
        this.setIsContaminated(isContaminated);
        return this;
    }

    public void setIsContaminated(Boolean isContaminated) {
        this.isContaminated = isContaminated;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public Batch isActive(Boolean isActive) {
        this.setIsActive(isActive);
        return this;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getCompletionNote() {
        return this.completionNote;
    }

    public Batch completionNote(String completionNote) {
        this.setCompletionNote(completionNote);
        return this;
    }

    public void setCompletionNote(String completionNote) {
        this.completionNote = completionNote;
    }

    public String getNote() {
        return this.note;
    }

    public Batch note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Strain getStrain() {
        return this.strain;
    }

    public void setStrain(Strain strain) {
        this.strain = strain;
    }

    public Batch strain(Strain strain) {
        this.setStrain(strain);
        return this;
    }

    public SubstrateRecipe getRecipe() {
        return this.recipe;
    }

    public void setRecipe(SubstrateRecipe substrateRecipe) {
        this.recipe = substrateRecipe;
    }

    public Batch recipe(SubstrateRecipe substrateRecipe) {
        this.setRecipe(substrateRecipe);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Batch)) {
            return false;
        }
        return getId() != null && getId().equals(((Batch) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Batch{" +
            "id=" + getId() +
            ", batchCode='" + getBatchCode() + "'" +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", currentPhase='" + getCurrentPhase() + "'" +
            ", numberOfBags=" + getNumberOfBags() +
            ", substrateWeightKg=" + getSubstrateWeightKg() +
            ", spawnWeightKg=" + getSpawnWeightKg() +
            ", targetYieldKg=" + getTargetYieldKg() +
            ", actualTotalYieldKg=" + getActualTotalYieldKg() +
            ", biologicalEfficiencyPercent=" + getBiologicalEfficiencyPercent() +
            ", isContaminated='" + getIsContaminated() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", completionNote='" + getCompletionNote() + "'" +
            ", note='" + getNote() + "'" +
            "}";
    }
}
