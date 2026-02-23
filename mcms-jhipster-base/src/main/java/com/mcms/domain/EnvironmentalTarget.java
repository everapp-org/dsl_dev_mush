package com.mcms.domain;

import com.mcms.domain.enumeration.PhaseName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Environmental target ranges for a room per phase.
 * Defines what the environment SHOULD be for optimal growth.
 */
@Schema(description = "Environmental target ranges for a room per phase.\nDefines what the environment SHOULD be for optimal growth.")
@Entity
@Table(name = "environmental_target")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EnvironmentalTarget implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * Target applies during this phase
     */
    @Schema(description = "Target applies during this phase", required = true)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "phase", nullable = false)
    private PhaseName phase;

    /**
     * Minimum temperature °C
     */
    @Schema(description = "Minimum temperature °C", required = true)
    @NotNull
    @Column(name = "temp_min_c", precision = 21, scale = 2, nullable = false)
    private BigDecimal tempMinC;

    /**
     * Maximum temperature °C
     */
    @Schema(description = "Maximum temperature °C", required = true)
    @NotNull
    @Column(name = "temp_max_c", precision = 21, scale = 2, nullable = false)
    private BigDecimal tempMaxC;

    /**
     * Minimum RH %
     */
    @Schema(description = "Minimum RH %", required = true)
    @NotNull
    @Column(name = "humidity_min_percent", precision = 21, scale = 2, nullable = false)
    private BigDecimal humidityMinPercent;

    /**
     * Maximum RH %
     */
    @Schema(description = "Maximum RH %", required = true)
    @NotNull
    @Column(name = "humidity_max_percent", precision = 21, scale = 2, nullable = false)
    private BigDecimal humidityMaxPercent;

    /**
     * Maximum CO₂ ppm
     */
    @Schema(description = "Maximum CO₂ ppm")
    @Column(name = "co_2_max_ppm")
    private Integer co2MaxPpm;

    /**
     * Target light level
     */
    @Schema(description = "Target light level")
    @Column(name = "light_lux")
    private Integer lightLux;

    /**
     * FAE target
     */
    @Schema(description = "FAE target")
    @Column(name = "fresh_air_exchanges_per_hour")
    private Integer freshAirExchangesPerHour;

    @ManyToOne(optional = false)
    @NotNull
    private Room room;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public EnvironmentalTarget id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PhaseName getPhase() {
        return this.phase;
    }

    public EnvironmentalTarget phase(PhaseName phase) {
        this.setPhase(phase);
        return this;
    }

    public void setPhase(PhaseName phase) {
        this.phase = phase;
    }

    public BigDecimal getTempMinC() {
        return this.tempMinC;
    }

    public EnvironmentalTarget tempMinC(BigDecimal tempMinC) {
        this.setTempMinC(tempMinC);
        return this;
    }

    public void setTempMinC(BigDecimal tempMinC) {
        this.tempMinC = tempMinC;
    }

    public BigDecimal getTempMaxC() {
        return this.tempMaxC;
    }

    public EnvironmentalTarget tempMaxC(BigDecimal tempMaxC) {
        this.setTempMaxC(tempMaxC);
        return this;
    }

    public void setTempMaxC(BigDecimal tempMaxC) {
        this.tempMaxC = tempMaxC;
    }

    public BigDecimal getHumidityMinPercent() {
        return this.humidityMinPercent;
    }

    public EnvironmentalTarget humidityMinPercent(BigDecimal humidityMinPercent) {
        this.setHumidityMinPercent(humidityMinPercent);
        return this;
    }

    public void setHumidityMinPercent(BigDecimal humidityMinPercent) {
        this.humidityMinPercent = humidityMinPercent;
    }

    public BigDecimal getHumidityMaxPercent() {
        return this.humidityMaxPercent;
    }

    public EnvironmentalTarget humidityMaxPercent(BigDecimal humidityMaxPercent) {
        this.setHumidityMaxPercent(humidityMaxPercent);
        return this;
    }

    public void setHumidityMaxPercent(BigDecimal humidityMaxPercent) {
        this.humidityMaxPercent = humidityMaxPercent;
    }

    public Integer getCo2MaxPpm() {
        return this.co2MaxPpm;
    }

    public EnvironmentalTarget co2MaxPpm(Integer co2MaxPpm) {
        this.setCo2MaxPpm(co2MaxPpm);
        return this;
    }

    public void setCo2MaxPpm(Integer co2MaxPpm) {
        this.co2MaxPpm = co2MaxPpm;
    }

    public Integer getLightLux() {
        return this.lightLux;
    }

    public EnvironmentalTarget lightLux(Integer lightLux) {
        this.setLightLux(lightLux);
        return this;
    }

    public void setLightLux(Integer lightLux) {
        this.lightLux = lightLux;
    }

    public Integer getFreshAirExchangesPerHour() {
        return this.freshAirExchangesPerHour;
    }

    public EnvironmentalTarget freshAirExchangesPerHour(Integer freshAirExchangesPerHour) {
        this.setFreshAirExchangesPerHour(freshAirExchangesPerHour);
        return this;
    }

    public void setFreshAirExchangesPerHour(Integer freshAirExchangesPerHour) {
        this.freshAirExchangesPerHour = freshAirExchangesPerHour;
    }

    public Room getRoom() {
        return this.room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public EnvironmentalTarget room(Room room) {
        this.setRoom(room);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EnvironmentalTarget)) {
            return false;
        }
        return getId() != null && getId().equals(((EnvironmentalTarget) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EnvironmentalTarget{" +
            "id=" + getId() +
            ", phase='" + getPhase() + "'" +
            ", tempMinC=" + getTempMinC() +
            ", tempMaxC=" + getTempMaxC() +
            ", humidityMinPercent=" + getHumidityMinPercent() +
            ", humidityMaxPercent=" + getHumidityMaxPercent() +
            ", co2MaxPpm=" + getCo2MaxPpm() +
            ", lightLux=" + getLightLux() +
            ", freshAirExchangesPerHour=" + getFreshAirExchangesPerHour() +
            "}";
    }
}
