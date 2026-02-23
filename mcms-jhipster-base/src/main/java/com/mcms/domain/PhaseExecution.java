package com.mcms.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mcms.domain.enumeration.PhaseName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Phase execution - a single lifecycle phase within a batch.
 * State machine validates transitions (enforced at service layer):
 * INOCULATION -> EARLY_COLONIZATION -> FULL_COLONIZATION ->
 * CONSOLIDATION -> FRUITING_TRIGGER -> PRIMORDIA ->
 * FRUITING_BODY_GROWTH -> HARVEST -> (REHYDRATION_PAUSE -> HARVEST)* -> COMPLETED
 */
@Schema(
    description = "Phase execution - a single lifecycle phase within a batch.\nState machine validates transitions (enforced at service layer):\nINOCULATION -> EARLY_COLONIZATION -> FULL_COLONIZATION ->\nCONSOLIDATION -> FRUITING_TRIGGER -> PRIMORDIA ->\nFRUITING_BODY_GROWTH -> HARVEST -> (REHYDRATION_PAUSE -> HARVEST)* -> COMPLETED"
)
@Entity
@Table(name = "phase_execution")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PhaseExecution implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * Which lifecycle phase
     */
    @Schema(description = "Which lifecycle phase", required = true)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "phase", nullable = false)
    private PhaseName phase;

    /**
     * Execution order within the batch (1, 2, 3...)
     */
    @Schema(description = "Execution order within the batch (1, 2, 3...)", required = true)
    @NotNull
    @Column(name = "sequence_order", nullable = false)
    private Integer sequenceOrder;

    /**
     * Phase start date
     */
    @Schema(description = "Phase start date", required = true)
    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    /**
     * Phase end date (null if ongoing)
     */
    @Schema(description = "Phase end date (null if ongoing)")
    @Column(name = "end_date")
    private LocalDate endDate;

    /**
     * Planned duration in days
     */
    @Schema(description = "Planned duration in days")
    @Column(name = "planned_duration_days")
    private Integer plannedDurationDays;

    /**
     * Actual duration in days
     */
    @Schema(description = "Actual duration in days")
    @Column(name = "actual_duration_days")
    private Integer actualDurationDays;

    /**
     * Who managed this phase (display name)
     */
    @Schema(description = "Who managed this phase (display name)")
    @Column(name = "responsible_person")
    private String responsiblePerson;

    /**
     * Phase observations & notes
     */
    @Schema(description = "Phase observations & notes")
    @Lob
    @Column(name = "note")
    private String note;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "strain", "recipe" }, allowSetters = true)
    private Batch batch;

    @ManyToOne(fetch = FetchType.LAZY)
    private Room room;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PhaseExecution id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PhaseName getPhase() {
        return this.phase;
    }

    public PhaseExecution phase(PhaseName phase) {
        this.setPhase(phase);
        return this;
    }

    public void setPhase(PhaseName phase) {
        this.phase = phase;
    }

    public Integer getSequenceOrder() {
        return this.sequenceOrder;
    }

    public PhaseExecution sequenceOrder(Integer sequenceOrder) {
        this.setSequenceOrder(sequenceOrder);
        return this;
    }

    public void setSequenceOrder(Integer sequenceOrder) {
        this.sequenceOrder = sequenceOrder;
    }

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public PhaseExecution startDate(LocalDate startDate) {
        this.setStartDate(startDate);
        return this;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }

    public PhaseExecution endDate(LocalDate endDate) {
        this.setEndDate(endDate);
        return this;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Integer getPlannedDurationDays() {
        return this.plannedDurationDays;
    }

    public PhaseExecution plannedDurationDays(Integer plannedDurationDays) {
        this.setPlannedDurationDays(plannedDurationDays);
        return this;
    }

    public void setPlannedDurationDays(Integer plannedDurationDays) {
        this.plannedDurationDays = plannedDurationDays;
    }

    public Integer getActualDurationDays() {
        return this.actualDurationDays;
    }

    public PhaseExecution actualDurationDays(Integer actualDurationDays) {
        this.setActualDurationDays(actualDurationDays);
        return this;
    }

    public void setActualDurationDays(Integer actualDurationDays) {
        this.actualDurationDays = actualDurationDays;
    }

    public String getResponsiblePerson() {
        return this.responsiblePerson;
    }

    public PhaseExecution responsiblePerson(String responsiblePerson) {
        this.setResponsiblePerson(responsiblePerson);
        return this;
    }

    public void setResponsiblePerson(String responsiblePerson) {
        this.responsiblePerson = responsiblePerson;
    }

    public String getNote() {
        return this.note;
    }

    public PhaseExecution note(String note) {
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

    public PhaseExecution batch(Batch batch) {
        this.setBatch(batch);
        return this;
    }

    public Room getRoom() {
        return this.room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public PhaseExecution room(Room room) {
        this.setRoom(room);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PhaseExecution)) {
            return false;
        }
        return getId() != null && getId().equals(((PhaseExecution) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PhaseExecution{" +
            "id=" + getId() +
            ", phase='" + getPhase() + "'" +
            ", sequenceOrder=" + getSequenceOrder() +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", plannedDurationDays=" + getPlannedDurationDays() +
            ", actualDurationDays=" + getActualDurationDays() +
            ", responsiblePerson='" + getResponsiblePerson() + "'" +
            ", note='" + getNote() + "'" +
            "}";
    }
}
