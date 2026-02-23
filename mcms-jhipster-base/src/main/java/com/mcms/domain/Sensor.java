package com.mcms.domain;

import com.mcms.domain.enumeration.SensorStatus;
import com.mcms.domain.enumeration.SensorUnit;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * IoT sensor device installed in a room.
 */
@Schema(description = "IoT sensor device installed in a room.")
@Entity
@Table(name = "sensor")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Sensor implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * Device identifier
     */
    @Schema(description = "Device identifier", required = true)
    @NotNull
    @Column(name = "sensor_code", nullable = false, unique = true)
    private String sensorCode;

    /**
     * What it measures
     */
    @Schema(description = "What it measures", required = true)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "sensor_type", nullable = false)
    private SensorUnit sensorType;

    /**
     * Current operational status
     */
    @Schema(description = "Current operational status", required = true)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SensorStatus status;

    /**
     * Installation date
     */
    @Schema(description = "Installation date")
    @Column(name = "installed_date")
    private LocalDate installedDate;

    /**
     * Last calibration date
     */
    @Schema(description = "Last calibration date")
    @Column(name = "last_calibration_date")
    private LocalDate lastCalibrationDate;

    /**
     * Device manufacturer
     */
    @Schema(description = "Device manufacturer")
    @Column(name = "manufacturer")
    private String manufacturer;

    /**
     * Device model
     */
    @Schema(description = "Device model")
    @Column(name = "model")
    private String model;

    @Lob
    @Column(name = "note")
    private String note;

    /**
     * Facility & IoT
     */
    @Schema(description = "Facility & IoT")
    @ManyToOne(optional = false)
    @NotNull
    private Room room;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Sensor id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSensorCode() {
        return this.sensorCode;
    }

    public Sensor sensorCode(String sensorCode) {
        this.setSensorCode(sensorCode);
        return this;
    }

    public void setSensorCode(String sensorCode) {
        this.sensorCode = sensorCode;
    }

    public SensorUnit getSensorType() {
        return this.sensorType;
    }

    public Sensor sensorType(SensorUnit sensorType) {
        this.setSensorType(sensorType);
        return this;
    }

    public void setSensorType(SensorUnit sensorType) {
        this.sensorType = sensorType;
    }

    public SensorStatus getStatus() {
        return this.status;
    }

    public Sensor status(SensorStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(SensorStatus status) {
        this.status = status;
    }

    public LocalDate getInstalledDate() {
        return this.installedDate;
    }

    public Sensor installedDate(LocalDate installedDate) {
        this.setInstalledDate(installedDate);
        return this;
    }

    public void setInstalledDate(LocalDate installedDate) {
        this.installedDate = installedDate;
    }

    public LocalDate getLastCalibrationDate() {
        return this.lastCalibrationDate;
    }

    public Sensor lastCalibrationDate(LocalDate lastCalibrationDate) {
        this.setLastCalibrationDate(lastCalibrationDate);
        return this;
    }

    public void setLastCalibrationDate(LocalDate lastCalibrationDate) {
        this.lastCalibrationDate = lastCalibrationDate;
    }

    public String getManufacturer() {
        return this.manufacturer;
    }

    public Sensor manufacturer(String manufacturer) {
        this.setManufacturer(manufacturer);
        return this;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return this.model;
    }

    public Sensor model(String model) {
        this.setModel(model);
        return this;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getNote() {
        return this.note;
    }

    public Sensor note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Room getRoom() {
        return this.room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Sensor room(Room room) {
        this.setRoom(room);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Sensor)) {
            return false;
        }
        return getId() != null && getId().equals(((Sensor) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Sensor{" +
            "id=" + getId() +
            ", sensorCode='" + getSensorCode() + "'" +
            ", sensorType='" + getSensorType() + "'" +
            ", status='" + getStatus() + "'" +
            ", installedDate='" + getInstalledDate() + "'" +
            ", lastCalibrationDate='" + getLastCalibrationDate() + "'" +
            ", manufacturer='" + getManufacturer() + "'" +
            ", model='" + getModel() + "'" +
            ", note='" + getNote() + "'" +
            "}";
    }
}
