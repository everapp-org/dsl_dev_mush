package com.mcms.domain;

import com.mcms.domain.enumeration.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Supply order header - purchase order from a supplier.
 * Contains shared order-level information.
 * Line items are in SupplyOrderLine.
 */
@Schema(
    description = "Supply order header - purchase order from a supplier.\nContains shared order-level information.\nLine items are in SupplyOrderLine."
)
@Entity
@Table(name = "supply_order")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SupplyOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * PO reference number, e.g. \"PO-2025-042\"
     */
    @Schema(description = "PO reference number, e.g. \"PO-2025-042\"", required = true)
    @NotNull
    @Column(name = "order_code", nullable = false, unique = true)
    private String orderCode;

    /**
     * Date ordered
     */
    @Schema(description = "Date ordered", required = true)
    @NotNull
    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;

    /**
     * Expected delivery date
     */
    @Schema(description = "Expected delivery date")
    @Column(name = "expected_delivery_date")
    private LocalDate expectedDeliveryDate;

    /**
     * Actual delivery date
     */
    @Schema(description = "Actual delivery date")
    @Column(name = "actual_delivery_date")
    private LocalDate actualDeliveryDate;

    /**
     * Current order status
     */
    @Schema(description = "Current order status", required = true)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    /**
     * Calculated total across all lines
     */
    @Schema(description = "Calculated total across all lines")
    @Column(name = "total_amount", precision = 21, scale = 2)
    private BigDecimal totalAmount;

    /**
     * Currency code, e.g. \"EUR\", \"RSD\"
     */
    @Schema(description = "Currency code, e.g. \"EUR\", \"RSD\"", required = true)
    @NotNull
    @Column(name = "currency", nullable = false)
    private String currency;

    /**
     * Delivery address (if different from default)
     */
    @Schema(description = "Delivery address (if different from default)")
    @Lob
    @Column(name = "shipping_address")
    private String shippingAddress;

    @Lob
    @Column(name = "note")
    private String note;

    @ManyToOne(optional = false)
    @NotNull
    private Supplier supplier;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SupplyOrder id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderCode() {
        return this.orderCode;
    }

    public SupplyOrder orderCode(String orderCode) {
        this.setOrderCode(orderCode);
        return this;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public LocalDate getOrderDate() {
        return this.orderDate;
    }

    public SupplyOrder orderDate(LocalDate orderDate) {
        this.setOrderDate(orderDate);
        return this;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public LocalDate getExpectedDeliveryDate() {
        return this.expectedDeliveryDate;
    }

    public SupplyOrder expectedDeliveryDate(LocalDate expectedDeliveryDate) {
        this.setExpectedDeliveryDate(expectedDeliveryDate);
        return this;
    }

    public void setExpectedDeliveryDate(LocalDate expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }

    public LocalDate getActualDeliveryDate() {
        return this.actualDeliveryDate;
    }

    public SupplyOrder actualDeliveryDate(LocalDate actualDeliveryDate) {
        this.setActualDeliveryDate(actualDeliveryDate);
        return this;
    }

    public void setActualDeliveryDate(LocalDate actualDeliveryDate) {
        this.actualDeliveryDate = actualDeliveryDate;
    }

    public OrderStatus getStatus() {
        return this.status;
    }

    public SupplyOrder status(OrderStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return this.totalAmount;
    }

    public SupplyOrder totalAmount(BigDecimal totalAmount) {
        this.setTotalAmount(totalAmount);
        return this;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCurrency() {
        return this.currency;
    }

    public SupplyOrder currency(String currency) {
        this.setCurrency(currency);
        return this;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getShippingAddress() {
        return this.shippingAddress;
    }

    public SupplyOrder shippingAddress(String shippingAddress) {
        this.setShippingAddress(shippingAddress);
        return this;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getNote() {
        return this.note;
    }

    public SupplyOrder note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Supplier getSupplier() {
        return this.supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public SupplyOrder supplier(Supplier supplier) {
        this.setSupplier(supplier);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SupplyOrder)) {
            return false;
        }
        return getId() != null && getId().equals(((SupplyOrder) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SupplyOrder{" +
            "id=" + getId() +
            ", orderCode='" + getOrderCode() + "'" +
            ", orderDate='" + getOrderDate() + "'" +
            ", expectedDeliveryDate='" + getExpectedDeliveryDate() + "'" +
            ", actualDeliveryDate='" + getActualDeliveryDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", totalAmount=" + getTotalAmount() +
            ", currency='" + getCurrency() + "'" +
            ", shippingAddress='" + getShippingAddress() + "'" +
            ", note='" + getNote() + "'" +
            "}";
    }
}
