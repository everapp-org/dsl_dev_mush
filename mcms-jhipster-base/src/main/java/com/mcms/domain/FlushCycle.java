package com.mcms.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Flush cycle - each harvest cycle within a batch.
 * Supports unlimited flushes (typical: 2-5 depending on species).
 */
@Schema(description = "Flush cycle - each harvest cycle within a batch.\nSupports unlimited flushes (typical: 2-5 depending on species).")
@Entity
@Table(name = "flush_cycle")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FlushCycle implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * 1st, 2nd, 3rd flush, etc.
     */
    @Schema(description = "1st, 2nd, 3rd flush, etc.", required = true)
    @NotNull
    @Column(name = "flush_number", nullable = false)
    private Integer flushNumber;

    /**
     * When picking began
     */
    @Schema(description = "When picking began", required = true)
    @NotNull
    @Column(name = "harvest_start_date", nullable = false)
    private LocalDate harvestStartDate;

    /**
     * When picking ended
     */
    @Schema(description = "When picking ended")
    @Column(name = "harvest_end_date")
    private LocalDate harvestEndDate;

    /**
     * Total yield for this flush
     */
    @Schema(description = "Total yield for this flush", required = true)
    @NotNull
    @Column(name = "yield_kg", precision = 21, scale = 2, nullable = false)
    private BigDecimal yieldKg;

    /**
     * Number of bags that produced
     */
    @Schema(description = "Number of bags that produced")
    @Column(name = "yield_bags_harvested")
    private Integer yieldBagsHarvested;

    /**
     * Average individual mushroom weight in grams
     */
    @Schema(description = "Average individual mushroom weight in grams")
    @Column(name = "avg_fruit_body_weight_g", precision = 21, scale = 2)
    private BigDecimal avgFruitBodyWeightG;

    /**
     * Was rehydration performed after this flush?
     */
    @Schema(description = "Was rehydration performed after this flush?")
    @Column(name = "rehydration_done")
    private Boolean rehydrationDone;

    /**
     * Soaking time in hours
     */
    @Schema(description = "Soaking time in hours")
    @Column(name = "rehydration_duration_hours")
    private Integer rehydrationDurationHours;

    /**
     * Flush-specific observations
     */
    @Schema(description = "Flush-specific observations")
    @Lob
    @Column(name = "note")
    private String note;

    /**
     * Multi-flush harvest
     */
    @Schema(description = "Multi-flush harvest")
    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "strain", "recipe" }, allowSetters = true)
    private Batch batch;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public FlushCycle id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getFlushNumber() {
        return this.flushNumber;
    }

    public FlushCycle flushNumber(Integer flushNumber) {
        this.setFlushNumber(flushNumber);
        return this;
    }

    public void setFlushNumber(Integer flushNumber) {
        this.flushNumber = flushNumber;
    }

    public LocalDate getHarvestStartDate() {
        return this.harvestStartDate;
    }

    public FlushCycle harvestStartDate(LocalDate harvestStartDate) {
        this.setHarvestStartDate(harvestStartDate);
        return this;
    }

    public void setHarvestStartDate(LocalDate harvestStartDate) {
        this.harvestStartDate = harvestStartDate;
    }

    public LocalDate getHarvestEndDate() {
        return this.harvestEndDate;
    }

    public FlushCycle harvestEndDate(LocalDate harvestEndDate) {
        this.setHarvestEndDate(harvestEndDate);
        return this;
    }

    public void setHarvestEndDate(LocalDate harvestEndDate) {
        this.harvestEndDate = harvestEndDate;
    }

    public BigDecimal getYieldKg() {
        return this.yieldKg;
    }

    public FlushCycle yieldKg(BigDecimal yieldKg) {
        this.setYieldKg(yieldKg);
        return this;
    }

    public void setYieldKg(BigDecimal yieldKg) {
        this.yieldKg = yieldKg;
    }

    public Integer getYieldBagsHarvested() {
        return this.yieldBagsHarvested;
    }

    public FlushCycle yieldBagsHarvested(Integer yieldBagsHarvested) {
        this.setYieldBagsHarvested(yieldBagsHarvested);
        return this;
    }

    public void setYieldBagsHarvested(Integer yieldBagsHarvested) {
        this.yieldBagsHarvested = yieldBagsHarvested;
    }

    public BigDecimal getAvgFruitBodyWeightG() {
        return this.avgFruitBodyWeightG;
    }

    public FlushCycle avgFruitBodyWeightG(BigDecimal avgFruitBodyWeightG) {
        this.setAvgFruitBodyWeightG(avgFruitBodyWeightG);
        return this;
    }

    public void setAvgFruitBodyWeightG(BigDecimal avgFruitBodyWeightG) {
        this.avgFruitBodyWeightG = avgFruitBodyWeightG;
    }

    public Boolean getRehydrationDone() {
        return this.rehydrationDone;
    }

    public FlushCycle rehydrationDone(Boolean rehydrationDone) {
        this.setRehydrationDone(rehydrationDone);
        return this;
    }

    public void setRehydrationDone(Boolean rehydrationDone) {
        this.rehydrationDone = rehydrationDone;
    }

    public Integer getRehydrationDurationHours() {
        return this.rehydrationDurationHours;
    }

    public FlushCycle rehydrationDurationHours(Integer rehydrationDurationHours) {
        this.setRehydrationDurationHours(rehydrationDurationHours);
        return this;
    }

    public void setRehydrationDurationHours(Integer rehydrationDurationHours) {
        this.rehydrationDurationHours = rehydrationDurationHours;
    }

    public String getNote() {
        return this.note;
    }

    public FlushCycle note(String note) {
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

    public FlushCycle batch(Batch batch) {
        this.setBatch(batch);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FlushCycle)) {
            return false;
        }
        return getId() != null && getId().equals(((FlushCycle) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FlushCycle{" +
            "id=" + getId() +
            ", flushNumber=" + getFlushNumber() +
            ", harvestStartDate='" + getHarvestStartDate() + "'" +
            ", harvestEndDate='" + getHarvestEndDate() + "'" +
            ", yieldKg=" + getYieldKg() +
            ", yieldBagsHarvested=" + getYieldBagsHarvested() +
            ", avgFruitBodyWeightG=" + getAvgFruitBodyWeightG() +
            ", rehydrationDone='" + getRehydrationDone() + "'" +
            ", rehydrationDurationHours=" + getRehydrationDurationHours() +
            ", note='" + getNote() + "'" +
            "}";
    }
}
