package com.mcms.domain;

import com.mcms.domain.enumeration.MaterialCategory;
import com.mcms.domain.enumeration.UnitOfMeasure;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Material - master catalog of all materials the farm uses.
 * Centralizes material definitions so they can be referenced
 * consistently across supply orders, inventory, and batch consumption.
 */
@Schema(
    description = "Material - master catalog of all materials the farm uses.\nCentralizes material definitions so they can be referenced\nconsistently across supply orders, inventory, and batch consumption."
)
@Entity
@Table(name = "material")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Material implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * Material code, e.g. \"MAT-SPAWN-PO-01\"
     */
    @Schema(description = "Material code, e.g. \"MAT-SPAWN-PO-01\"", required = true)
    @NotNull
    @Column(name = "code", nullable = false, unique = true)
    private String code;

    /**
     * Display name, e.g. \"Pleurotus ostreatus grain spawn\"
     */
    @Schema(description = "Display name, e.g. \"Pleurotus ostreatus grain spawn\"", required = true)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Material classification
     */
    @Schema(description = "Material classification", required = true)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private MaterialCategory category;

    /**
     * Default unit of measure
     */
    @Schema(description = "Default unit of measure", required = true)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "default_unit", nullable = false)
    private UnitOfMeasure defaultUnit;

    /**
     * Reorder threshold - alert when stock falls below
     */
    @Schema(description = "Reorder threshold - alert when stock falls below")
    @Column(name = "minimum_stock_level", precision = 21, scale = 2)
    private BigDecimal minimumStockLevel;

    /**
     * Detailed description / specifications
     */
    @Schema(description = "Detailed description / specifications")
    @Lob
    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active;

    @Lob
    @Column(name = "note")
    private String note;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Material id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public Material code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public Material name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MaterialCategory getCategory() {
        return this.category;
    }

    public Material category(MaterialCategory category) {
        this.setCategory(category);
        return this;
    }

    public void setCategory(MaterialCategory category) {
        this.category = category;
    }

    public UnitOfMeasure getDefaultUnit() {
        return this.defaultUnit;
    }

    public Material defaultUnit(UnitOfMeasure defaultUnit) {
        this.setDefaultUnit(defaultUnit);
        return this;
    }

    public void setDefaultUnit(UnitOfMeasure defaultUnit) {
        this.defaultUnit = defaultUnit;
    }

    public BigDecimal getMinimumStockLevel() {
        return this.minimumStockLevel;
    }

    public Material minimumStockLevel(BigDecimal minimumStockLevel) {
        this.setMinimumStockLevel(minimumStockLevel);
        return this;
    }

    public void setMinimumStockLevel(BigDecimal minimumStockLevel) {
        this.minimumStockLevel = minimumStockLevel;
    }

    public String getDescription() {
        return this.description;
    }

    public Material description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActive() {
        return this.active;
    }

    public Material active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getNote() {
        return this.note;
    }

    public Material note(String note) {
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
        if (!(o instanceof Material)) {
            return false;
        }
        return getId() != null && getId().equals(((Material) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Material{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", name='" + getName() + "'" +
            ", category='" + getCategory() + "'" +
            ", defaultUnit='" + getDefaultUnit() + "'" +
            ", minimumStockLevel=" + getMinimumStockLevel() +
            ", description='" + getDescription() + "'" +
            ", active='" + getActive() + "'" +
            ", note='" + getNote() + "'" +
            "}";
    }
}
