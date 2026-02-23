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
 * Task / action item for farm staff.
 * Lightweight task management for daily operations.
 */
@Schema(description = "Task / action item for farm staff.\nLightweight task management for daily operations.")
@Entity
@Table(name = "task")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Task implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * Task title
     */
    @Schema(description = "Task title", required = true)
    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    /**
     * Detailed instructions
     */
    @Schema(description = "Detailed instructions")
    @Lob
    @Column(name = "description")
    private String description;

    /**
     * When it's due
     */
    @Schema(description = "When it's due", required = true)
    @NotNull
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    /**
     * Is it done?
     */
    @Schema(description = "Is it done?", required = true)
    @NotNull
    @Column(name = "completed", nullable = false)
    private Boolean completed;

    /**
     * When it was completed
     */
    @Schema(description = "When it was completed")
    @Column(name = "completed_date")
    private LocalDate completedDate;

    /**
     * Priority: 1=highest, 5=lowest
     */
    @Schema(description = "Priority: 1=highest, 5=lowest")
    @Min(value = 1)
    @Max(value = 5)
    @Column(name = "priority")
    private Integer priority;

    /**
     * Display name of assignee
     */
    @Schema(description = "Display name of assignee")
    @Column(name = "assigned_to")
    private String assignedTo;

    @Lob
    @Column(name = "note")
    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "strain", "recipe" }, allowSetters = true)
    private Batch batch;

    @ManyToOne(fetch = FetchType.LAZY)
    private Room room;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Task id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Task title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public Task description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDueDate() {
        return this.dueDate;
    }

    public Task dueDate(LocalDate dueDate) {
        this.setDueDate(dueDate);
        return this;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Boolean getCompleted() {
        return this.completed;
    }

    public Task completed(Boolean completed) {
        this.setCompleted(completed);
        return this;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public LocalDate getCompletedDate() {
        return this.completedDate;
    }

    public Task completedDate(LocalDate completedDate) {
        this.setCompletedDate(completedDate);
        return this;
    }

    public void setCompletedDate(LocalDate completedDate) {
        this.completedDate = completedDate;
    }

    public Integer getPriority() {
        return this.priority;
    }

    public Task priority(Integer priority) {
        this.setPriority(priority);
        return this;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getAssignedTo() {
        return this.assignedTo;
    }

    public Task assignedTo(String assignedTo) {
        this.setAssignedTo(assignedTo);
        return this;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getNote() {
        return this.note;
    }

    public Task note(String note) {
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

    public Task batch(Batch batch) {
        this.setBatch(batch);
        return this;
    }

    public Room getRoom() {
        return this.room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Task room(Room room) {
        this.setRoom(room);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Task)) {
            return false;
        }
        return getId() != null && getId().equals(((Task) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Task{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", dueDate='" + getDueDate() + "'" +
            ", completed='" + getCompleted() + "'" +
            ", completedDate='" + getCompletedDate() + "'" +
            ", priority=" + getPriority() +
            ", assignedTo='" + getAssignedTo() + "'" +
            ", note='" + getNote() + "'" +
            "}";
    }
}
