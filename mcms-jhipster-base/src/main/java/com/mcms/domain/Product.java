package com.mcms.domain;

import com.mcms.domain.enumeration.QualityGrade;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Product - a sellable mushroom product.
 * Normalizes what is being sold across sales order lines.
 * Combines strain + grade into a marketable product definition.
 */
@Schema(
    description = "Product - a sellable mushroom product.\nNormalizes what is being sold across sales order lines.\nCombines strain + grade into a marketable product definition."
)
@Entity
@Table(name = "product")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * SKU / product code, e.g. \"OYS-PREM-500\"
     */
    @Schema(description = "SKU / product code, e.g. \"OYS-PREM-500\"", required = true)
    @NotNull
    @Column(name = "code", nullable = false, unique = true)
    private String code;

    /**
     * Display name, e.g. \"Premium Oyster Mushroom 500g\"
     */
    @Schema(description = "Display name, e.g. \"Premium Oyster Mushroom 500g\"", required = true)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Product description
     */
    @Schema(description = "Product description")
    @Lob
    @Column(name = "description")
    private String description;

    /**
     * Default quality grade for this product
     */
    @Schema(description = "Default quality grade for this product", required = true)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "default_grade", nullable = false)
    private QualityGrade defaultGrade;

    /**
     * Standard package weight in kg
     */
    @Schema(description = "Standard package weight in kg")
    @Column(name = "default_weight_kg", precision = 21, scale = 2)
    private BigDecimal defaultWeightKg;

    /**
     * Standard selling price per kg
     */
    @Schema(description = "Standard selling price per kg")
    @Column(name = "default_price_per_kg", precision = 21, scale = 2)
    private BigDecimal defaultPricePerKg;

    /**
     * Product shelf life in days
     */
    @Schema(description = "Product shelf life in days")
    @Column(name = "shelf_life_days")
    private Integer shelfLifeDays;

    /**
     * Currently offered?
     */
    @Schema(description = "Currently offered?", required = true)
    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active;

    @Lob
    @Column(name = "note")
    private String note;

    /**
     * Product catalog
     */
    @Schema(description = "Product catalog")
    @ManyToOne(optional = false)
    @NotNull
    private Strain strain;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Product id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public Product code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public Product name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Product description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public QualityGrade getDefaultGrade() {
        return this.defaultGrade;
    }

    public Product defaultGrade(QualityGrade defaultGrade) {
        this.setDefaultGrade(defaultGrade);
        return this;
    }

    public void setDefaultGrade(QualityGrade defaultGrade) {
        this.defaultGrade = defaultGrade;
    }

    public BigDecimal getDefaultWeightKg() {
        return this.defaultWeightKg;
    }

    public Product defaultWeightKg(BigDecimal defaultWeightKg) {
        this.setDefaultWeightKg(defaultWeightKg);
        return this;
    }

    public void setDefaultWeightKg(BigDecimal defaultWeightKg) {
        this.defaultWeightKg = defaultWeightKg;
    }

    public BigDecimal getDefaultPricePerKg() {
        return this.defaultPricePerKg;
    }

    public Product defaultPricePerKg(BigDecimal defaultPricePerKg) {
        this.setDefaultPricePerKg(defaultPricePerKg);
        return this;
    }

    public void setDefaultPricePerKg(BigDecimal defaultPricePerKg) {
        this.defaultPricePerKg = defaultPricePerKg;
    }

    public Integer getShelfLifeDays() {
        return this.shelfLifeDays;
    }

    public Product shelfLifeDays(Integer shelfLifeDays) {
        this.setShelfLifeDays(shelfLifeDays);
        return this;
    }

    public void setShelfLifeDays(Integer shelfLifeDays) {
        this.shelfLifeDays = shelfLifeDays;
    }

    public Boolean getActive() {
        return this.active;
    }

    public Product active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getNote() {
        return this.note;
    }

    public Product note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Strain getStrain() {
        return this.strain;
    }

    public void setStrain(Strain strain) {
        this.strain = strain;
    }

    public Product strain(Strain strain) {
        this.setStrain(strain);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Product)) {
            return false;
        }
        return getId() != null && getId().equals(((Product) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Product{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", defaultGrade='" + getDefaultGrade() + "'" +
            ", defaultWeightKg=" + getDefaultWeightKg() +
            ", defaultPricePerKg=" + getDefaultPricePerKg() +
            ", shelfLifeDays=" + getShelfLifeDays() +
            ", active='" + getActive() + "'" +
            ", note='" + getNote() + "'" +
            "}";
    }
}
