package com.mcms.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Mushroom strain / species being cultivated.
 * Central to yield optimization - links genetics to performance.
 */
@Schema(description = "Mushroom strain / species being cultivated.\nCentral to yield optimization - links genetics to performance.")
@Entity
@Table(name = "strain")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Strain implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * e.g. \"Pleurotus ostreatus - Grey Dove\"
     */
    @Schema(description = "e.g. \"Pleurotus ostreatus - Grey Dove\"", required = true)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Latin species name
     */
    @Schema(description = "Latin species name", required = true)
    @NotNull
    @Column(name = "species", nullable = false)
    private String species;

    /**
     * Cultivar or variant
     */
    @Schema(description = "Cultivar or variant")
    @Column(name = "variety")
    private String variety;

    /**
     * Min fruiting temp °C
     */
    @Schema(description = "Min fruiting temp °C")
    @Column(name = "optimal_temp_min_c", precision = 21, scale = 2)
    private BigDecimal optimalTempMinC;

    /**
     * Max fruiting temp °C
     */
    @Schema(description = "Max fruiting temp °C")
    @Column(name = "optimal_temp_max_c", precision = 21, scale = 2)
    private BigDecimal optimalTempMaxC;

    /**
     * Min RH %
     */
    @Schema(description = "Min RH %")
    @Column(name = "optimal_humidity_min", precision = 21, scale = 2)
    private BigDecimal optimalHumidityMin;

    /**
     * Max RH %
     */
    @Schema(description = "Max RH %")
    @Column(name = "optimal_humidity_max", precision = 21, scale = 2)
    private BigDecimal optimalHumidityMax;

    /**
     * Max CO₂ ppm for fruiting
     */
    @Schema(description = "Max CO₂ ppm for fruiting")
    @Column(name = "optimal_co_2_max_ppm")
    private Integer optimalCO2MaxPpm;

    /**
     * Typical min colonization days
     */
    @Schema(description = "Typical min colonization days")
    @Column(name = "colonization_days_min")
    private Integer colonizationDaysMin;

    /**
     * Typical max colonization days
     */
    @Schema(description = "Typical max colonization days")
    @Column(name = "colonization_days_max")
    private Integer colonizationDaysMax;

    /**
     * Expected biological efficiency %
     */
    @Schema(description = "Expected biological efficiency %")
    @Column(name = "expected_yield_percent", precision = 21, scale = 2)
    private BigDecimal expectedYieldPercent;

    /**
     * Post-harvest shelf life in days
     */
    @Schema(description = "Post-harvest shelf life in days")
    @Column(name = "shelf_life_days")
    private Integer shelfLifeDays;

    /**
     * Cultivation tips, observations
     */
    @Schema(description = "Cultivation tips, observations")
    @Lob
    @Column(name = "note")
    private String note;

    /**
     * Is this strain currently in use?
     */
    @Schema(description = "Is this strain currently in use?", required = true)
    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Strain id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Strain name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() {
        return this.species;
    }

    public Strain species(String species) {
        this.setSpecies(species);
        return this;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getVariety() {
        return this.variety;
    }

    public Strain variety(String variety) {
        this.setVariety(variety);
        return this;
    }

    public void setVariety(String variety) {
        this.variety = variety;
    }

    public BigDecimal getOptimalTempMinC() {
        return this.optimalTempMinC;
    }

    public Strain optimalTempMinC(BigDecimal optimalTempMinC) {
        this.setOptimalTempMinC(optimalTempMinC);
        return this;
    }

    public void setOptimalTempMinC(BigDecimal optimalTempMinC) {
        this.optimalTempMinC = optimalTempMinC;
    }

    public BigDecimal getOptimalTempMaxC() {
        return this.optimalTempMaxC;
    }

    public Strain optimalTempMaxC(BigDecimal optimalTempMaxC) {
        this.setOptimalTempMaxC(optimalTempMaxC);
        return this;
    }

    public void setOptimalTempMaxC(BigDecimal optimalTempMaxC) {
        this.optimalTempMaxC = optimalTempMaxC;
    }

    public BigDecimal getOptimalHumidityMin() {
        return this.optimalHumidityMin;
    }

    public Strain optimalHumidityMin(BigDecimal optimalHumidityMin) {
        this.setOptimalHumidityMin(optimalHumidityMin);
        return this;
    }

    public void setOptimalHumidityMin(BigDecimal optimalHumidityMin) {
        this.optimalHumidityMin = optimalHumidityMin;
    }

    public BigDecimal getOptimalHumidityMax() {
        return this.optimalHumidityMax;
    }

    public Strain optimalHumidityMax(BigDecimal optimalHumidityMax) {
        this.setOptimalHumidityMax(optimalHumidityMax);
        return this;
    }

    public void setOptimalHumidityMax(BigDecimal optimalHumidityMax) {
        this.optimalHumidityMax = optimalHumidityMax;
    }

    public Integer getOptimalCO2MaxPpm() {
        return this.optimalCO2MaxPpm;
    }

    public Strain optimalCO2MaxPpm(Integer optimalCO2MaxPpm) {
        this.setOptimalCO2MaxPpm(optimalCO2MaxPpm);
        return this;
    }

    public void setOptimalCO2MaxPpm(Integer optimalCO2MaxPpm) {
        this.optimalCO2MaxPpm = optimalCO2MaxPpm;
    }

    public Integer getColonizationDaysMin() {
        return this.colonizationDaysMin;
    }

    public Strain colonizationDaysMin(Integer colonizationDaysMin) {
        this.setColonizationDaysMin(colonizationDaysMin);
        return this;
    }

    public void setColonizationDaysMin(Integer colonizationDaysMin) {
        this.colonizationDaysMin = colonizationDaysMin;
    }

    public Integer getColonizationDaysMax() {
        return this.colonizationDaysMax;
    }

    public Strain colonizationDaysMax(Integer colonizationDaysMax) {
        this.setColonizationDaysMax(colonizationDaysMax);
        return this;
    }

    public void setColonizationDaysMax(Integer colonizationDaysMax) {
        this.colonizationDaysMax = colonizationDaysMax;
    }

    public BigDecimal getExpectedYieldPercent() {
        return this.expectedYieldPercent;
    }

    public Strain expectedYieldPercent(BigDecimal expectedYieldPercent) {
        this.setExpectedYieldPercent(expectedYieldPercent);
        return this;
    }

    public void setExpectedYieldPercent(BigDecimal expectedYieldPercent) {
        this.expectedYieldPercent = expectedYieldPercent;
    }

    public Integer getShelfLifeDays() {
        return this.shelfLifeDays;
    }

    public Strain shelfLifeDays(Integer shelfLifeDays) {
        this.setShelfLifeDays(shelfLifeDays);
        return this;
    }

    public void setShelfLifeDays(Integer shelfLifeDays) {
        this.shelfLifeDays = shelfLifeDays;
    }

    public String getNote() {
        return this.note;
    }

    public Strain note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Boolean getActive() {
        return this.active;
    }

    public Strain active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Strain)) {
            return false;
        }
        return getId() != null && getId().equals(((Strain) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Strain{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", species='" + getSpecies() + "'" +
            ", variety='" + getVariety() + "'" +
            ", optimalTempMinC=" + getOptimalTempMinC() +
            ", optimalTempMaxC=" + getOptimalTempMaxC() +
            ", optimalHumidityMin=" + getOptimalHumidityMin() +
            ", optimalHumidityMax=" + getOptimalHumidityMax() +
            ", optimalCO2MaxPpm=" + getOptimalCO2MaxPpm() +
            ", colonizationDaysMin=" + getColonizationDaysMin() +
            ", colonizationDaysMax=" + getColonizationDaysMax() +
            ", expectedYieldPercent=" + getExpectedYieldPercent() +
            ", shelfLifeDays=" + getShelfLifeDays() +
            ", note='" + getNote() + "'" +
            ", active='" + getActive() + "'" +
            "}";
    }
}
