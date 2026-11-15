package com.mcms.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mcms.domain.enumeration.ContaminationAction;
import com.mcms.domain.enumeration.ContaminationSeverity;
import com.mcms.domain.enumeration.ContaminationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Structured contamination event - replaces free-text notes.
 * Enables trend analysis, root cause tracking, and loss forecasting.
 */
@Schema(
    description = "Structured contamination event - replaces free-text notes.\nEnables trend analysis, root cause tracking, and loss forecasting."
)
@Entity
@Table(name = "contamination_event")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ContaminationEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * When contamination was first observed
     */
    @Schema(description = "When contamination was first observed", required = true)
    @NotNull
    @Column(name = "detected_date", nullable = false)
    private LocalDate detectedDate;

    /**
     * What type of contamination
     */
    @Schema(description = "What type of contamination", required = true)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ContaminationType type;

    /**
     * How severe
     */
    @Schema(description = "How severe", required = true)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false)
    private ContaminationSeverity severity;

    /**
     * How many bags/blocks affected
     */
    @Schema(description = "How many bags/blocks affected")
    @Column(name = "affected_bags")
    private Integer affectedBags;

    /**
     * % of batch affected
     */
    @Schema(description = "% of batch affected")
    @Column(name = "affected_percentage", precision = 21, scale = 2)
    private BigDecimal affectedPercentage;

    /**
     * Primary response action
     */
    @Schema(description = "Primary response action", required = true)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "action_taken", nullable = false)
    private ContaminationAction actionTaken;

    /**
     * When issue was resolved (null if ongoing)
     */
    @Schema(description = "When issue was resolved (null if ongoing)")
    @Column(name = "resolved_date")
    private LocalDate resolvedDate;

    /**
     * Estimated yield loss in kg
     */
    @Schema(description = "Estimated yield loss in kg")
    @Column(name = "loss_kg", precision = 21, scale = 2)
    private BigDecimal lossKg;

    /**
     * Root cause investigation notes
     */
    @Schema(description = "Root cause investigation notes")
    @Lob
    @Column(name = "root_cause_analysis")
    private String rootCauseAnalysis;

    /**
     * Preventive actions for future
     */
    @Schema(description = "Preventive actions for future")
    @Lob
    @Column(name = "preventive_measures")
    private String preventiveMeasures;

    /**
     * Who discovered it
     */
    @Schema(description = "Who discovered it")
    @Column(name = "detected_by")
    private String detectedBy;

    /**
     * Path/URL to photo evidence
     */
    @Schema(description = "Path/URL to photo evidence")
    @Column(name = "photos_reference")
    private String photosReference;

    @Lob
    @Column(name = "note")
    private String note;

    /**
     * Contamination
     */
    @Schema(description = "Contamination")
    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "strain", "recipe" }, allowSetters = true)
    private Batch batch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "batch", "room" }, allowSetters = true)
    private PhaseExecution phaseExecution;

    @ManyToOne(fetch = FetchType.LAZY)
    private Room room;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ContaminationEvent id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDetectedDate() {
        return this.detectedDate;
    }

    public ContaminationEvent detectedDate(LocalDate detectedDate) {
        this.setDetectedDate(detectedDate);
        return this;
    }

    public void setDetectedDate(LocalDate detectedDate) {
        this.detectedDate = detectedDate;
    }

    public ContaminationType getType() {
        return this.type;
    }

    public ContaminationEvent type(ContaminationType type) {
        this.setType(type);
        return this;
    }

    public void setType(ContaminationType type) {
        this.type = type;
    }

    public ContaminationSeverity getSeverity() {
        return this.severity;
    }

    public ContaminationEvent severity(ContaminationSeverity severity) {
        this.setSeverity(severity);
        return this;
    }

    public void setSeverity(ContaminationSeverity severity) {
        this.severity = severity;
    }

    public Integer getAffectedBags() {
        return this.affectedBags;
    }

    public ContaminationEvent affectedBags(Integer affectedBags) {
        this.setAffectedBags(affectedBags);
        return this;
    }

    public void setAffectedBags(Integer affectedBags) {
        this.affectedBags = affectedBags;
    }

    public BigDecimal getAffectedPercentage() {
        return this.affectedPercentage;
    }

    public ContaminationEvent affectedPercentage(BigDecimal affectedPercentage) {
        this.setAffectedPercentage(affectedPercentage);
        return this;
    }

    public void setAffectedPercentage(BigDecimal affectedPercentage) {
        this.affectedPercentage = affectedPercentage;
    }

    public ContaminationAction getActionTaken() {
        return this.actionTaken;
    }

    public ContaminationEvent actionTaken(ContaminationAction actionTaken) {
        this.setActionTaken(actionTaken);
        return this;
    }

    public void setActionTaken(ContaminationAction actionTaken) {
        this.actionTaken = actionTaken;
    }

    public LocalDate getResolvedDate() {
        return this.resolvedDate;
    }

    public ContaminationEvent resolvedDate(LocalDate resolvedDate) {
        this.setResolvedDate(resolvedDate);
        return this;
    }

    public void setResolvedDate(LocalDate resolvedDate) {
        this.resolvedDate = resolvedDate;
    }

    public BigDecimal getLossKg() {
        return this.lossKg;
    }

    public ContaminationEvent lossKg(BigDecimal lossKg) {
        this.setLossKg(lossKg);
        return this;
    }

    public void setLossKg(BigDecimal lossKg) {
        this.lossKg = lossKg;
    }

    public String getRootCauseAnalysis() {
        return this.rootCauseAnalysis;
    }

    public ContaminationEvent rootCauseAnalysis(String rootCauseAnalysis) {
        this.setRootCauseAnalysis(rootCauseAnalysis);
        return this;
    }

    public void setRootCauseAnalysis(String rootCauseAnalysis) {
        this.rootCauseAnalysis = rootCauseAnalysis;
    }

    public String getPreventiveMeasures() {
        return this.preventiveMeasures;
    }

    public ContaminationEvent preventiveMeasures(String preventiveMeasures) {
        this.setPreventiveMeasures(preventiveMeasures);
        return this;
    }

    public void setPreventiveMeasures(String preventiveMeasures) {
        this.preventiveMeasures = preventiveMeasures;
    }

    public String getDetectedBy() {
        return this.detectedBy;
    }

    public ContaminationEvent detectedBy(String detectedBy) {
        this.setDetectedBy(detectedBy);
        return this;
    }

    public void setDetectedBy(String detectedBy) {
        this.detectedBy = detectedBy;
    }

    public String getPhotosReference() {
        return this.photosReference;
    }

    public ContaminationEvent photosReference(String photosReference) {
        this.setPhotosReference(photosReference);
        return this;
    }

    public void setPhotosReference(String photosReference) {
        this.photosReference = photosReference;
    }

    public String getNote() {
        return this.note;
    }

    public ContaminationEvent note(String note) {
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

    public ContaminationEvent batch(Batch batch) {
        this.setBatch(batch);
        return this;
    }

    public PhaseExecution getPhaseExecution() {
        return this.phaseExecution;
    }

    public void setPhaseExecution(PhaseExecution phaseExecution) {
        this.phaseExecution = phaseExecution;
    }

    public ContaminationEvent phaseExecution(PhaseExecution phaseExecution) {
        this.setPhaseExecution(phaseExecution);
        return this;
    }

    public Room getRoom() {
        return this.room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public ContaminationEvent room(Room room) {
        this.setRoom(room);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ContaminationEvent)) {
            return false;
        }
        return getId() != null && getId().equals(((ContaminationEvent) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ContaminationEvent{" +
            "id=" + getId() +
            ", detectedDate='" + getDetectedDate() + "'" +
            ", type='" + getType() + "'" +
            ", severity='" + getSeverity() + "'" +
            ", affectedBags=" + getAffectedBags() +
            ", affectedPercentage=" + getAffectedPercentage() +
            ", actionTaken='" + getActionTaken() + "'" +
            ", resolvedDate='" + getResolvedDate() + "'" +
            ", lossKg=" + getLossKg() +
            ", rootCauseAnalysis='" + getRootCauseAnalysis() + "'" +
            ", preventiveMeasures='" + getPreventiveMeasures() + "'" +
            ", detectedBy='" + getDetectedBy() + "'" +
            ", photosReference='" + getPhotosReference() + "'" +
            ", note='" + getNote() + "'" +
            "}";
    }
}
