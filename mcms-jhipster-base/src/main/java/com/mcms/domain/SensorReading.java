package com.mcms.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
 * Time-series sensor reading - the IoT data stream.
 * High-volume entity - consider TimescaleDB or partitioning for production.
 */
@Schema(
    description = "Time-series sensor reading - the IoT data stream.\nHigh-volume entity - consider TimescaleDB or partitioning for production."
)
@Entity
@Table(name = "sensor_reading")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SensorReading implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * Reading timestamp (UTC)
     */
    @Schema(description = "Reading timestamp (UTC)", required = true)
    @NotNull
    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    /**
     * Measured value
     */
    @Schema(description = "Measured value", required = true)
    @NotNull
    @Column(name = "value", precision = 21, scale = 2, nullable = false)
    private BigDecimal value;

    /**
     * Unit of measurement
     */
    @Schema(description = "Unit of measurement", required = true)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "unit", nullable = false)
    private SensorUnit unit;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "room" }, allowSetters = true)
    private Sensor sensor;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SensorReading id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getTimestamp() {
        return this.timestamp;
    }

    public SensorReading timestamp(Instant timestamp) {
        this.setTimestamp(timestamp);
        return this;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public BigDecimal getValue() {
        return this.value;
    }

    public SensorReading value(BigDecimal value) {
        this.setValue(value);
        return this;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public SensorUnit getUnit() {
        return this.unit;
    }

    public SensorReading unit(SensorUnit unit) {
        this.setUnit(unit);
        return this;
    }

    public void setUnit(SensorUnit unit) {
        this.unit = unit;
    }

    public Sensor getSensor() {
        return this.sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    public SensorReading sensor(Sensor sensor) {
        this.setSensor(sensor);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SensorReading)) {
            return false;
        }
        return getId() != null && getId().equals(((SensorReading) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SensorReading{" +
            "id=" + getId() +
            ", timestamp='" + getTimestamp() + "'" +
            ", value=" + getValue() +
            ", unit='" + getUnit() + "'" +
            "}";
    }
}
