package com.mcms.domain;

import com.mcms.domain.enumeration.SubstrateBase;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Substrate recipe - the growth medium formulation.
 * Versioned to track recipe evolution and yield correlation.
 */
@Schema(description = "Substrate recipe - the growth medium formulation.\nVersioned to track recipe evolution and yield correlation.")
@Entity
@Table(name = "substrate_recipe")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SubstrateRecipe implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * e.g. \"Standard Wheat Straw v3\"
     */
    @Schema(description = "e.g. \"Standard Wheat Straw v3\"", required = true)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Recipe version identifier
     */
    @Schema(description = "Recipe version identifier", required = true)
    @NotNull
    @Column(name = "version", nullable = false)
    private String version;

    /**
     * Primary substrate material
     */
    @Schema(description = "Primary substrate material", required = true)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "base_type", nullable = false)
    private SubstrateBase baseType;

    /**
     * Full ingredient list & ratios
     */
    @Schema(description = "Full ingredient list & ratios")
    @Lob
    @Column(name = "composition_detail")
    private String compositionDetail;

    /**
     * e.g. \"Autoclave 121°C/2h\", \"Pasteurization 80°C/8h\"
     */
    @Schema(description = "e.g. \"Autoclave 121°C/2h\", \"Pasteurization 80°C/8h\"")
    @Column(name = "sterilization_method")
    private String sterilizationMethod;

    /**
     * Target moisture content %
     */
    @Schema(description = "Target moisture content %")
    @Column(name = "moisture_target_percent", precision = 21, scale = 2)
    private BigDecimal moistureTargetPercent;

    /**
     * Target pH level
     */
    @Schema(description = "Target pH level")
    @Column(name = "ph_target", precision = 21, scale = 2)
    private BigDecimal phTarget;

    /**
     * Supplements: gypsum, bran, lime, etc.
     */
    @Schema(description = "Supplements: gypsum, bran, lime, etc.")
    @Lob
    @Column(name = "supplement_notes")
    private String supplementNotes;

    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SubstrateRecipe id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public SubstrateRecipe name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return this.version;
    }

    public SubstrateRecipe version(String version) {
        this.setVersion(version);
        return this;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public SubstrateBase getBaseType() {
        return this.baseType;
    }

    public SubstrateRecipe baseType(SubstrateBase baseType) {
        this.setBaseType(baseType);
        return this;
    }

    public void setBaseType(SubstrateBase baseType) {
        this.baseType = baseType;
    }

    public String getCompositionDetail() {
        return this.compositionDetail;
    }

    public SubstrateRecipe compositionDetail(String compositionDetail) {
        this.setCompositionDetail(compositionDetail);
        return this;
    }

    public void setCompositionDetail(String compositionDetail) {
        this.compositionDetail = compositionDetail;
    }

    public String getSterilizationMethod() {
        return this.sterilizationMethod;
    }

    public SubstrateRecipe sterilizationMethod(String sterilizationMethod) {
        this.setSterilizationMethod(sterilizationMethod);
        return this;
    }

    public void setSterilizationMethod(String sterilizationMethod) {
        this.sterilizationMethod = sterilizationMethod;
    }

    public BigDecimal getMoistureTargetPercent() {
        return this.moistureTargetPercent;
    }

    public SubstrateRecipe moistureTargetPercent(BigDecimal moistureTargetPercent) {
        this.setMoistureTargetPercent(moistureTargetPercent);
        return this;
    }

    public void setMoistureTargetPercent(BigDecimal moistureTargetPercent) {
        this.moistureTargetPercent = moistureTargetPercent;
    }

    public BigDecimal getPhTarget() {
        return this.phTarget;
    }

    public SubstrateRecipe phTarget(BigDecimal phTarget) {
        this.setPhTarget(phTarget);
        return this;
    }

    public void setPhTarget(BigDecimal phTarget) {
        this.phTarget = phTarget;
    }

    public String getSupplementNotes() {
        return this.supplementNotes;
    }

    public SubstrateRecipe supplementNotes(String supplementNotes) {
        this.setSupplementNotes(supplementNotes);
        return this;
    }

    public void setSupplementNotes(String supplementNotes) {
        this.supplementNotes = supplementNotes;
    }

    public Boolean getActive() {
        return this.active;
    }

    public SubstrateRecipe active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SubstrateRecipe)) {
            return false;
        }
        return getId() != null && getId().equals(((SubstrateRecipe) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SubstrateRecipe{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", version='" + getVersion() + "'" +
            ", baseType='" + getBaseType() + "'" +
            ", compositionDetail='" + getCompositionDetail() + "'" +
            ", sterilizationMethod='" + getSterilizationMethod() + "'" +
            ", moistureTargetPercent=" + getMoistureTargetPercent() +
            ", phTarget=" + getPhTarget() +
            ", supplementNotes='" + getSupplementNotes() + "'" +
            ", active='" + getActive() + "'" +
            "}";
    }
}
