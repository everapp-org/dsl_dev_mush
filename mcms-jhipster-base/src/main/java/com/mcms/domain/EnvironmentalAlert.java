package com.mcms.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mcms.domain.enumeration.AlertSeverity;
import com.mcms.domain.enumeration.SensorUnit;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Environmental alert - triggered when readings breach thresholds.
 */
@Schema(description = "Environmental alert - triggered when readings breach thresholds.")
@Entity
@Table(name = "environmental_alert")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EnvironmentalAlert implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * When the alert was triggered
     */
    @Schema(description = "When the alert was triggered", required = true)
    @NotNull
    @Column(name = "alert_time", nullable = false)
    private Instant alertTime;

    /**
     * INFO / WARNING / CRITICAL
     */
    @Schema(description = "INFO / WARNING / CRITICAL", required = true)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false)
    private AlertSeverity severity;

    /**
     * Which environmental parameter
     */
    @Schema(description = "Which environmental parameter", required = true)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "parameter", nullable = false)
    private SensorUnit parameter;

    /**
     * The value that triggered the alert
     */
    @Schema(description = "The value that triggered the alert", required = true)
    @NotNull
    @Column(name = "actual_value", precision = 21, scale = 2, nullable = false)
    private BigDecimal actualValue;

    /**
     * The threshold that was breached
     */
    @Schema(description = "The threshold that was breached", required = true)
    @NotNull
    @Column(name = "threshold_value", precision = 21, scale = 2, nullable = false)
    private BigDecimal thresholdValue;

    /**
     * Human-readable alert description
     */
    @Schema(description = "Human-readable alert description", required = true)
    @NotNull
    @Column(name = "message", nullable = false)
    private String message;

    /**
     * Has someone acknowledged this?
     */
    @Schema(description = "Has someone acknowledged this?", required = true)
    @NotNull
    @Column(name = "acknowledged", nullable = false)
    private Boolean acknowledged;

    /**
     * Who acknowledged it
     */
    @Schema(description = "Who acknowledged it")
    @Column(name = "acknowledged_by")
    private String acknowledgedBy;

    /**
     * When it was acknowledged
     */
    @Schema(description = "When it was acknowledged")
    @Column(name = "acknowledged_at")
    private Instant acknowledgedAt;

    /**
     * What was done about it
     */
    @Schema(description = "What was done about it")
    @Lob
    @Column(name = "resolution_note")
    private String resolutionNote;

    @ManyToOne(optional = false)
    @NotNull
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "room" }, allowSetters = true)
    private Sensor sensor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "strain", "recipe" }, allowSetters = true)
    private Batch batch;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public EnvironmentalAlert id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getAlertTime() {
        return this.alertTime;
    }

    public EnvironmentalAlert alertTime(Instant alertTime) {
        this.setAlertTime(alertTime);
        return this;
    }

    public void setAlertTime(Instant alertTime) {
        this.alertTime = alertTime;
    }

    public AlertSeverity getSeverity() {
        return this.severity;
    }

    public EnvironmentalAlert severity(AlertSeverity severity) {
        this.setSeverity(severity);
        return this;
    }

    public void setSeverity(AlertSeverity severity) {
        this.severity = severity;
    }

    public SensorUnit getParameter() {
        return this.parameter;
    }

    public EnvironmentalAlert parameter(SensorUnit parameter) {
        this.setParameter(parameter);
        return this;
    }

    public void setParameter(SensorUnit parameter) {
        this.parameter = parameter;
    }

    public BigDecimal getActualValue() {
        return this.actualValue;
    }

    public EnvironmentalAlert actualValue(BigDecimal actualValue) {
        this.setActualValue(actualValue);
        return this;
    }

    public void setActualValue(BigDecimal actualValue) {
        this.actualValue = actualValue;
    }

    public BigDecimal getThresholdValue() {
        return this.thresholdValue;
    }

    public EnvironmentalAlert thresholdValue(BigDecimal thresholdValue) {
        this.setThresholdValue(thresholdValue);
        return this;
    }

    public void setThresholdValue(BigDecimal thresholdValue) {
        this.thresholdValue = thresholdValue;
    }

    public String getMessage() {
        return this.message;
    }

    public EnvironmentalAlert message(String message) {
        this.setMessage(message);
        return this;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getAcknowledged() {
        return this.acknowledged;
    }

    public EnvironmentalAlert acknowledged(Boolean acknowledged) {
        this.setAcknowledged(acknowledged);
        return this;
    }

    public void setAcknowledged(Boolean acknowledged) {
        this.acknowledged = acknowledged;
    }

    public String getAcknowledgedBy() {
        return this.acknowledgedBy;
    }

    public EnvironmentalAlert acknowledgedBy(String acknowledgedBy) {
        this.setAcknowledgedBy(acknowledgedBy);
        return this;
    }

    public void setAcknowledgedBy(String acknowledgedBy) {
        this.acknowledgedBy = acknowledgedBy;
    }

    public Instant getAcknowledgedAt() {
        return this.acknowledgedAt;
    }

    public EnvironmentalAlert acknowledgedAt(Instant acknowledgedAt) {
        this.setAcknowledgedAt(acknowledgedAt);
        return this;
    }

    public void setAcknowledgedAt(Instant acknowledgedAt) {
        this.acknowledgedAt = acknowledgedAt;
    }

    public String getResolutionNote() {
        return this.resolutionNote;
    }

    public EnvironmentalAlert resolutionNote(String resolutionNote) {
        this.setResolutionNote(resolutionNote);
        return this;
    }

    public void setResolutionNote(String resolutionNote) {
        this.resolutionNote = resolutionNote;
    }

    public Room getRoom() {
        return this.room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public EnvironmentalAlert room(Room room) {
        this.setRoom(room);
        return this;
    }

    public Sensor getSensor() {
        return this.sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    public EnvironmentalAlert sensor(Sensor sensor) {
        this.setSensor(sensor);
        return this;
    }

    public Batch getBatch() {
        return this.batch;
    }

    public void setBatch(Batch batch) {
        this.batch = batch;
    }

    public EnvironmentalAlert batch(Batch batch) {
        this.setBatch(batch);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EnvironmentalAlert)) {
            return false;
        }
        return getId() != null && getId().equals(((EnvironmentalAlert) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EnvironmentalAlert{" +
            "id=" + getId() +
            ", alertTime='" + getAlertTime() + "'" +
            ", severity='" + getSeverity() + "'" +
            ", parameter='" + getParameter() + "'" +
            ", actualValue=" + getActualValue() +
            ", thresholdValue=" + getThresholdValue() +
            ", message='" + getMessage() + "'" +
            ", acknowledged='" + getAcknowledged() + "'" +
            ", acknowledgedBy='" + getAcknowledgedBy() + "'" +
            ", acknowledgedAt='" + getAcknowledgedAt() + "'" +
            ", resolutionNote='" + getResolutionNote() + "'" +
            "}";
    }
}
