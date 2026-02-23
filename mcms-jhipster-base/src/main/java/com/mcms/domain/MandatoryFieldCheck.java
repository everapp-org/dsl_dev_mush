package com.mcms.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Mandatory field check - audit trail for data completeness.
 */
@Schema(description = "Mandatory field check - audit trail for data completeness.")
@Entity
@Table(name = "mandatory_field_check")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MandatoryFieldCheck implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * Which field was checked
     */
    @Schema(description = "Which field was checked", required = true)
    @NotNull
    @Column(name = "field_name", nullable = false)
    private String fieldName;

    /**
     * Was it filled in?
     */
    @Schema(description = "Was it filled in?", required = true)
    @NotNull
    @Column(name = "is_filled", nullable = false)
    private Boolean isFilled;

    /**
     * When the check was performed
     */
    @Schema(description = "When the check was performed", required = true)
    @NotNull
    @Column(name = "check_date", nullable = false)
    private LocalDate checkDate;

    /**
     * Compliance & operations
     */
    @Schema(description = "Compliance & operations")
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "batch", "room" }, allowSetters = true)
    private PhaseExecution phaseExecution;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public MandatoryFieldCheck id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public MandatoryFieldCheck fieldName(String fieldName) {
        this.setFieldName(fieldName);
        return this;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Boolean getIsFilled() {
        return this.isFilled;
    }

    public MandatoryFieldCheck isFilled(Boolean isFilled) {
        this.setIsFilled(isFilled);
        return this;
    }

    public void setIsFilled(Boolean isFilled) {
        this.isFilled = isFilled;
    }

    public LocalDate getCheckDate() {
        return this.checkDate;
    }

    public MandatoryFieldCheck checkDate(LocalDate checkDate) {
        this.setCheckDate(checkDate);
        return this;
    }

    public void setCheckDate(LocalDate checkDate) {
        this.checkDate = checkDate;
    }

    public PhaseExecution getPhaseExecution() {
        return this.phaseExecution;
    }

    public void setPhaseExecution(PhaseExecution phaseExecution) {
        this.phaseExecution = phaseExecution;
    }

    public MandatoryFieldCheck phaseExecution(PhaseExecution phaseExecution) {
        this.setPhaseExecution(phaseExecution);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MandatoryFieldCheck)) {
            return false;
        }
        return getId() != null && getId().equals(((MandatoryFieldCheck) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MandatoryFieldCheck{" +
            "id=" + getId() +
            ", fieldName='" + getFieldName() + "'" +
            ", isFilled='" + getIsFilled() + "'" +
            ", checkDate='" + getCheckDate() + "'" +
            "}";
    }
}
