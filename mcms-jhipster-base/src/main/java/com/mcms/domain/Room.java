package com.mcms.domain;

import com.mcms.domain.enumeration.RoomStatus;
import com.mcms.domain.enumeration.RoomType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Physical room / chamber in the facility.
 * Now a full entity instead of an enum - supports multiple rooms
 * of the same type, capacity tracking, and sensor linkage.
 */
@Schema(
    description = "Physical room / chamber in the facility.\nNow a full entity instead of an enum - supports multiple rooms\nof the same type, capacity tracking, and sensor linkage."
)
@Entity
@Table(name = "room")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Room implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * e.g. \"Incubation A1\", \"Chamber B3\"
     */
    @Schema(description = "e.g. \"Incubation A1\", \"Chamber B3\"", required = true)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Short code, e.g. \"INC-A1\"
     */
    @Schema(description = "Short code, e.g. \"INC-A1\"", required = true)
    @NotNull
    @Column(name = "code", nullable = false, unique = true)
    private String code;

    /**
     * Functional classification
     */
    @Schema(description = "Functional classification", required = true)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "room_type", nullable = false)
    private RoomType roomType;

    /**
     * Current operational status
     */
    @Schema(description = "Current operational status", required = true)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RoomStatus status;

    /**
     * Max bag/block capacity
     */
    @Schema(description = "Max bag/block capacity")
    @Column(name = "capacity_bags")
    private Integer capacityBags;

    /**
     * Current number of bags/blocks
     */
    @Schema(description = "Current number of bags/blocks")
    @Column(name = "current_occupancy")
    private Integer currentOccupancy;

    /**
     * Floor area in m²
     */
    @Schema(description = "Floor area in m²")
    @Column(name = "area_sq_m", precision = 21, scale = 2)
    private BigDecimal areaSqM;

    /**
     * Automated climate control?
     */
    @Schema(description = "Automated climate control?")
    @Column(name = "has_hvac")
    private Boolean hasHVAC;

    /**
     * Automated misting/humidification?
     */
    @Schema(description = "Automated misting/humidification?")
    @Column(name = "has_misting")
    private Boolean hasMisting;

    /**
     * When was it last disinfected?
     */
    @Schema(description = "When was it last disinfected?")
    @Column(name = "last_disinfection_date")
    private LocalDate lastDisinfectionDate;

    @Lob
    @Column(name = "note")
    private String note;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Room id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Room name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return this.code;
    }

    public Room code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public RoomType getRoomType() {
        return this.roomType;
    }

    public Room roomType(RoomType roomType) {
        this.setRoomType(roomType);
        return this;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public RoomStatus getStatus() {
        return this.status;
    }

    public Room status(RoomStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }

    public Integer getCapacityBags() {
        return this.capacityBags;
    }

    public Room capacityBags(Integer capacityBags) {
        this.setCapacityBags(capacityBags);
        return this;
    }

    public void setCapacityBags(Integer capacityBags) {
        this.capacityBags = capacityBags;
    }

    public Integer getCurrentOccupancy() {
        return this.currentOccupancy;
    }

    public Room currentOccupancy(Integer currentOccupancy) {
        this.setCurrentOccupancy(currentOccupancy);
        return this;
    }

    public void setCurrentOccupancy(Integer currentOccupancy) {
        this.currentOccupancy = currentOccupancy;
    }

    public BigDecimal getAreaSqM() {
        return this.areaSqM;
    }

    public Room areaSqM(BigDecimal areaSqM) {
        this.setAreaSqM(areaSqM);
        return this;
    }

    public void setAreaSqM(BigDecimal areaSqM) {
        this.areaSqM = areaSqM;
    }

    public Boolean getHasHVAC() {
        return this.hasHVAC;
    }

    public Room hasHVAC(Boolean hasHVAC) {
        this.setHasHVAC(hasHVAC);
        return this;
    }

    public void setHasHVAC(Boolean hasHVAC) {
        this.hasHVAC = hasHVAC;
    }

    public Boolean getHasMisting() {
        return this.hasMisting;
    }

    public Room hasMisting(Boolean hasMisting) {
        this.setHasMisting(hasMisting);
        return this;
    }

    public void setHasMisting(Boolean hasMisting) {
        this.hasMisting = hasMisting;
    }

    public LocalDate getLastDisinfectionDate() {
        return this.lastDisinfectionDate;
    }

    public Room lastDisinfectionDate(LocalDate lastDisinfectionDate) {
        this.setLastDisinfectionDate(lastDisinfectionDate);
        return this;
    }

    public void setLastDisinfectionDate(LocalDate lastDisinfectionDate) {
        this.lastDisinfectionDate = lastDisinfectionDate;
    }

    public String getNote() {
        return this.note;
    }

    public Room note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Room)) {
            return false;
        }
        return getId() != null && getId().equals(((Room) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Room{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", code='" + getCode() + "'" +
            ", roomType='" + getRoomType() + "'" +
            ", status='" + getStatus() + "'" +
            ", capacityBags=" + getCapacityBags() +
            ", currentOccupancy=" + getCurrentOccupancy() +
            ", areaSqM=" + getAreaSqM() +
            ", hasHVAC='" + getHasHVAC() + "'" +
            ", hasMisting='" + getHasMisting() + "'" +
            ", lastDisinfectionDate='" + getLastDisinfectionDate() + "'" +
            ", note='" + getNote() + "'" +
            "}";
    }
}
