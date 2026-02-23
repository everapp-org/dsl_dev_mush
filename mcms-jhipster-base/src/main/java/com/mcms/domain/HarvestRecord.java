package com.mcms.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mcms.domain.enumeration.QualityGrade;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Harvest detail - individual harvest event within a flush.
 * Enables daily picking records and quality grading.
 */
@Schema(description = "Harvest detail - individual harvest event within a flush.\nEnables daily picking records and quality grading.")
@Entity
@Table(name = "harvest_record")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class HarvestRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * Date of picking
     */
    @Schema(description = "Date of picking", required = true)
    @NotNull
    @Column(name = "harvest_date", nullable = false)
    private LocalDate harvestDate;

    /**
     * Weight harvested
     */
    @Schema(description = "Weight harvested", required = true)
    @NotNull
    @Column(name = "weight_kg", precision = 21, scale = 2, nullable = false)
    private BigDecimal weightKg;

    /**
     * Quality classification
     */
    @Schema(description = "Quality classification", required = true)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "grade", nullable = false)
    private QualityGrade grade;

    /**
     * Who harvested
     */
    @Schema(description = "Who harvested")
    @Column(name = "picker_name")
    private String pickerName;

    @Lob
    @Column(name = "note")
    private String note;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "batch" }, allowSetters = true)
    private FlushCycle flushCycle;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public HarvestRecord id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getHarvestDate() {
        return this.harvestDate;
    }

    public HarvestRecord harvestDate(LocalDate harvestDate) {
        this.setHarvestDate(harvestDate);
        return this;
    }

    public void setHarvestDate(LocalDate harvestDate) {
        this.harvestDate = harvestDate;
    }

    public BigDecimal getWeightKg() {
        return this.weightKg;
    }

    public HarvestRecord weightKg(BigDecimal weightKg) {
        this.setWeightKg(weightKg);
        return this;
    }

    public void setWeightKg(BigDecimal weightKg) {
        this.weightKg = weightKg;
    }

    public QualityGrade getGrade() {
        return this.grade;
    }

    public HarvestRecord grade(QualityGrade grade) {
        this.setGrade(grade);
        return this;
    }

    public void setGrade(QualityGrade grade) {
        this.grade = grade;
    }

    public String getPickerName() {
        return this.pickerName;
    }

    public HarvestRecord pickerName(String pickerName) {
        this.setPickerName(pickerName);
        return this;
    }

    public void setPickerName(String pickerName) {
        this.pickerName = pickerName;
    }

    public String getNote() {
        return this.note;
    }

    public HarvestRecord note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public FlushCycle getFlushCycle() {
        return this.flushCycle;
    }

    public void setFlushCycle(FlushCycle flushCycle) {
        this.flushCycle = flushCycle;
    }

    public HarvestRecord flushCycle(FlushCycle flushCycle) {
        this.setFlushCycle(flushCycle);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HarvestRecord)) {
            return false;
        }
        return getId() != null && getId().equals(((HarvestRecord) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "HarvestRecord{" +
            "id=" + getId() +
            ", harvestDate='" + getHarvestDate() + "'" +
            ", weightKg=" + getWeightKg() +
            ", grade='" + getGrade() + "'" +
            ", pickerName='" + getPickerName() + "'" +
            ", note='" + getNote() + "'" +
            "}";
    }
}
