package com.mcms.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mcms.domain.enumeration.QualityGrade;
import com.mcms.domain.enumeration.UnitOfMeasure;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Sales order line item - individual product on a sales order.
 * Each line represents one product (strain + grade combination) being sold.
 * Links to Product catalog and source Batch for full traceability.
 */
@Schema(
    description = "Sales order line item - individual product on a sales order.\nEach line represents one product (strain + grade combination) being sold.\nLinks to Product catalog and source Batch for full traceability."
)
@Entity
@Table(name = "sales_order_line")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SalesOrderLine implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * Line sequence (1, 2, 3...)
     */
    @Schema(description = "Line sequence (1, 2, 3...)", required = true)
    @NotNull
    @Column(name = "line_number", nullable = false)
    private Integer lineNumber;

    /**
     * Weight sold on this line
     */
    @Schema(description = "Weight sold on this line", required = true)
    @NotNull
    @Column(name = "weight_kg", precision = 21, scale = 2, nullable = false)
    private BigDecimal weightKg;

    /**
     * Quality grade for this line
     */
    @Schema(description = "Quality grade for this line", required = true)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "grade", nullable = false)
    private QualityGrade grade;

    /**
     * Number of packages/units (if applicable)
     */
    @Schema(description = "Number of packages/units (if applicable)")
    @Column(name = "quantity_units")
    private Integer quantityUnits;

    /**
     * Unit of measure
     */
    @Schema(description = "Unit of measure")
    @Enumerated(EnumType.STRING)
    @Column(name = "unit")
    private UnitOfMeasure unit;

    /**
     * Selling price per kg for this line
     */
    @Schema(description = "Selling price per kg for this line", required = true)
    @NotNull
    @Column(name = "price_per_kg", precision = 21, scale = 2, nullable = false)
    private BigDecimal pricePerKg;

    /**
     * weightKg × pricePerKg
     */
    @Schema(description = "weightKg × pricePerKg", required = true)
    @NotNull
    @Column(name = "line_total", precision = 21, scale = 2, nullable = false)
    private BigDecimal lineTotal;

    @Lob
    @Column(name = "note")
    private String note;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "customer" }, allowSetters = true)
    private SalesOrder salesOrder;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "strain" }, allowSetters = true)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "strain", "recipe" }, allowSetters = true)
    private Batch batch;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SalesOrderLine id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getLineNumber() {
        return this.lineNumber;
    }

    public SalesOrderLine lineNumber(Integer lineNumber) {
        this.setLineNumber(lineNumber);
        return this;
    }

    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    public BigDecimal getWeightKg() {
        return this.weightKg;
    }

    public SalesOrderLine weightKg(BigDecimal weightKg) {
        this.setWeightKg(weightKg);
        return this;
    }

    public void setWeightKg(BigDecimal weightKg) {
        this.weightKg = weightKg;
    }

    public QualityGrade getGrade() {
        return this.grade;
    }

    public SalesOrderLine grade(QualityGrade grade) {
        this.setGrade(grade);
        return this;
    }

    public void setGrade(QualityGrade grade) {
        this.grade = grade;
    }

    public Integer getQuantityUnits() {
        return this.quantityUnits;
    }

    public SalesOrderLine quantityUnits(Integer quantityUnits) {
        this.setQuantityUnits(quantityUnits);
        return this;
    }

    public void setQuantityUnits(Integer quantityUnits) {
        this.quantityUnits = quantityUnits;
    }

    public UnitOfMeasure getUnit() {
        return this.unit;
    }

    public SalesOrderLine unit(UnitOfMeasure unit) {
        this.setUnit(unit);
        return this;
    }

    public void setUnit(UnitOfMeasure unit) {
        this.unit = unit;
    }

    public BigDecimal getPricePerKg() {
        return this.pricePerKg;
    }

    public SalesOrderLine pricePerKg(BigDecimal pricePerKg) {
        this.setPricePerKg(pricePerKg);
        return this;
    }

    public void setPricePerKg(BigDecimal pricePerKg) {
        this.pricePerKg = pricePerKg;
    }

    public BigDecimal getLineTotal() {
        return this.lineTotal;
    }

    public SalesOrderLine lineTotal(BigDecimal lineTotal) {
        this.setLineTotal(lineTotal);
        return this;
    }

    public void setLineTotal(BigDecimal lineTotal) {
        this.lineTotal = lineTotal;
    }

    public String getNote() {
        return this.note;
    }

    public SalesOrderLine note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public SalesOrder getSalesOrder() {
        return this.salesOrder;
    }

    public void setSalesOrder(SalesOrder salesOrder) {
        this.salesOrder = salesOrder;
    }

    public SalesOrderLine salesOrder(SalesOrder salesOrder) {
        this.setSalesOrder(salesOrder);
        return this;
    }

    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public SalesOrderLine product(Product product) {
        this.setProduct(product);
        return this;
    }

    public Batch getBatch() {
        return this.batch;
    }

    public void setBatch(Batch batch) {
        this.batch = batch;
    }

    public SalesOrderLine batch(Batch batch) {
        this.setBatch(batch);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SalesOrderLine)) {
            return false;
        }
        return getId() != null && getId().equals(((SalesOrderLine) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SalesOrderLine{" +
            "id=" + getId() +
            ", lineNumber=" + getLineNumber() +
            ", weightKg=" + getWeightKg() +
            ", grade='" + getGrade() + "'" +
            ", quantityUnits=" + getQuantityUnits() +
            ", unit='" + getUnit() + "'" +
            ", pricePerKg=" + getPricePerKg() +
            ", lineTotal=" + getLineTotal() +
            ", note='" + getNote() + "'" +
            "}";
    }
}
