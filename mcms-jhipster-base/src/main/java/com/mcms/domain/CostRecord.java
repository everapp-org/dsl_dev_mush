package com.mcms.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mcms.domain.enumeration.CostCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Cost record - tracks expenses per batch per category.
 * Enables full batch profitability analysis.
 */
@Schema(description = "Cost record - tracks expenses per batch per category.\nEnables full batch profitability analysis.")
@Entity
@Table(name = "cost_record")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CostRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * Date of expense
     */
    @Schema(description = "Date of expense", required = true)
    @NotNull
    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    /**
     * What type of cost
     */
    @Schema(description = "What type of cost", required = true)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private CostCategory category;

    /**
     * Brief description
     */
    @Schema(description = "Brief description", required = true)
    @NotNull
    @Column(name = "description", nullable = false)
    private String description;

    /**
     * Cost amount in local currency
     */
    @Schema(description = "Cost amount in local currency", required = true)
    @NotNull
    @Column(name = "amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal amount;

    /**
     * Currency code, e.g. \"RSD\", \"EUR\"
     */
    @Schema(description = "Currency code, e.g. \"RSD\", \"EUR\"", required = true)
    @NotNull
    @Column(name = "currency", nullable = false)
    private String currency;

    @Lob
    @Column(name = "note")
    private String note;

    /**
     * Financials
     */
    @Schema(description = "Financials")
    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "strain", "recipe" }, allowSetters = true)
    private Batch batch;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CostRecord id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getRecordDate() {
        return this.recordDate;
    }

    public CostRecord recordDate(LocalDate recordDate) {
        this.setRecordDate(recordDate);
        return this;
    }

    public void setRecordDate(LocalDate recordDate) {
        this.recordDate = recordDate;
    }

    public CostCategory getCategory() {
        return this.category;
    }

    public CostRecord category(CostCategory category) {
        this.setCategory(category);
        return this;
    }

    public void setCategory(CostCategory category) {
        this.category = category;
    }

    public String getDescription() {
        return this.description;
    }

    public CostRecord description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public CostRecord amount(BigDecimal amount) {
        this.setAmount(amount);
        return this;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return this.currency;
    }

    public CostRecord currency(String currency) {
        this.setCurrency(currency);
        return this;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getNote() {
        return this.note;
    }

    public CostRecord note(String note) {
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

    public CostRecord batch(Batch batch) {
        this.setBatch(batch);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CostRecord)) {
            return false;
        }
        return getId() != null && getId().equals(((CostRecord) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CostRecord{" +
            "id=" + getId() +
            ", recordDate='" + getRecordDate() + "'" +
            ", category='" + getCategory() + "'" +
            ", description='" + getDescription() + "'" +
            ", amount=" + getAmount() +
            ", currency='" + getCurrency() + "'" +
            ", note='" + getNote() + "'" +
            "}";
    }
}
