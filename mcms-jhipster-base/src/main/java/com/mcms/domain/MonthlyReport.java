package com.mcms.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Monthly report - operational summary per batch per month.
 */
@Schema(description = "Monthly report - operational summary per batch per month.")
@Entity
@Table(name = "monthly_report")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MonthlyReport implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * Year
     */
    @Schema(description = "Year", required = true)
    @NotNull
    @Column(name = "year", nullable = false)
    private Integer year;

    /**
     * Month (1-12)
     */
    @Schema(description = "Month (1-12)", required = true)
    @NotNull
    @Min(value = 1)
    @Max(value = 12)
    @Column(name = "month", nullable = false)
    private Integer month;

    /**
     * Report generation timestamp
     */
    @Schema(description = "Report generation timestamp", required = true)
    @NotNull
    @Column(name = "generated_at", nullable = false)
    private Instant generatedAt;

    /**
     * Total yield in reporting period
     */
    @Schema(description = "Total yield in reporting period")
    @Column(name = "total_yield_kg", precision = 21, scale = 2)
    private BigDecimal totalYieldKg;

    /**
     * Total costs in reporting period
     */
    @Schema(description = "Total costs in reporting period")
    @Column(name = "total_cost", precision = 21, scale = 2)
    private BigDecimal totalCost;

    /**
     * Total revenue in reporting period
     */
    @Schema(description = "Total revenue in reporting period")
    @Column(name = "total_revenue", precision = 21, scale = 2)
    private BigDecimal totalRevenue;

    /**
     * Calculated profit margin
     */
    @Schema(description = "Calculated profit margin")
    @Column(name = "profit_margin_percent", precision = 21, scale = 2)
    private BigDecimal profitMarginPercent;

    /**
     * Count of contamination events
     */
    @Schema(description = "Count of contamination events")
    @Column(name = "total_contamination_events")
    private Integer totalContaminationEvents;

    /**
     * Count of incomplete mandatory fields
     */
    @Schema(description = "Count of incomplete mandatory fields")
    @Column(name = "total_missing_fields")
    private Integer totalMissingFields;

    /**
     * Executive summary text
     */
    @Schema(description = "Executive summary text")
    @Lob
    @Column(name = "summary")
    private String summary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "strain", "recipe" }, allowSetters = true)
    private Batch batch;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public MonthlyReport id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getYear() {
        return this.year;
    }

    public MonthlyReport year(Integer year) {
        this.setYear(year);
        return this;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return this.month;
    }

    public MonthlyReport month(Integer month) {
        this.setMonth(month);
        return this;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Instant getGeneratedAt() {
        return this.generatedAt;
    }

    public MonthlyReport generatedAt(Instant generatedAt) {
        this.setGeneratedAt(generatedAt);
        return this;
    }

    public void setGeneratedAt(Instant generatedAt) {
        this.generatedAt = generatedAt;
    }

    public BigDecimal getTotalYieldKg() {
        return this.totalYieldKg;
    }

    public MonthlyReport totalYieldKg(BigDecimal totalYieldKg) {
        this.setTotalYieldKg(totalYieldKg);
        return this;
    }

    public void setTotalYieldKg(BigDecimal totalYieldKg) {
        this.totalYieldKg = totalYieldKg;
    }

    public BigDecimal getTotalCost() {
        return this.totalCost;
    }

    public MonthlyReport totalCost(BigDecimal totalCost) {
        this.setTotalCost(totalCost);
        return this;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public BigDecimal getTotalRevenue() {
        return this.totalRevenue;
    }

    public MonthlyReport totalRevenue(BigDecimal totalRevenue) {
        this.setTotalRevenue(totalRevenue);
        return this;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimal getProfitMarginPercent() {
        return this.profitMarginPercent;
    }

    public MonthlyReport profitMarginPercent(BigDecimal profitMarginPercent) {
        this.setProfitMarginPercent(profitMarginPercent);
        return this;
    }

    public void setProfitMarginPercent(BigDecimal profitMarginPercent) {
        this.profitMarginPercent = profitMarginPercent;
    }

    public Integer getTotalContaminationEvents() {
        return this.totalContaminationEvents;
    }

    public MonthlyReport totalContaminationEvents(Integer totalContaminationEvents) {
        this.setTotalContaminationEvents(totalContaminationEvents);
        return this;
    }

    public void setTotalContaminationEvents(Integer totalContaminationEvents) {
        this.totalContaminationEvents = totalContaminationEvents;
    }

    public Integer getTotalMissingFields() {
        return this.totalMissingFields;
    }

    public MonthlyReport totalMissingFields(Integer totalMissingFields) {
        this.setTotalMissingFields(totalMissingFields);
        return this;
    }

    public void setTotalMissingFields(Integer totalMissingFields) {
        this.totalMissingFields = totalMissingFields;
    }

    public String getSummary() {
        return this.summary;
    }

    public MonthlyReport summary(String summary) {
        this.setSummary(summary);
        return this;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Batch getBatch() {
        return this.batch;
    }

    public void setBatch(Batch batch) {
        this.batch = batch;
    }

    public MonthlyReport batch(Batch batch) {
        this.setBatch(batch);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MonthlyReport)) {
            return false;
        }
        return getId() != null && getId().equals(((MonthlyReport) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MonthlyReport{" +
            "id=" + getId() +
            ", year=" + getYear() +
            ", month=" + getMonth() +
            ", generatedAt='" + getGeneratedAt() + "'" +
            ", totalYieldKg=" + getTotalYieldKg() +
            ", totalCost=" + getTotalCost() +
            ", totalRevenue=" + getTotalRevenue() +
            ", profitMarginPercent=" + getProfitMarginPercent() +
            ", totalContaminationEvents=" + getTotalContaminationEvents() +
            ", totalMissingFields=" + getTotalMissingFields() +
            ", summary='" + getSummary() + "'" +
            "}";
    }
}
