package com.mcms.domain;

import com.mcms.domain.enumeration.OrderStatus;
import com.mcms.domain.enumeration.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Sales order header - order from a customer.
 * Contains shared order-level information.
 * Line items are in SalesOrderLine.
 */
@Schema(
    description = "Sales order header - order from a customer.\nContains shared order-level information.\nLine items are in SalesOrderLine."
)
@Entity
@Table(name = "sales_order")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SalesOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * Sales order reference, e.g. \"SO-2025-108\"
     */
    @Schema(description = "Sales order reference, e.g. \"SO-2025-108\"", required = true)
    @NotNull
    @Column(name = "order_code", nullable = false, unique = true)
    private String orderCode;

    /**
     * Date order was placed
     */
    @Schema(description = "Date order was placed", required = true)
    @NotNull
    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;

    /**
     * Customer's requested delivery date
     */
    @Schema(description = "Customer's requested delivery date")
    @Column(name = "requested_delivery_date")
    private LocalDate requestedDeliveryDate;

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
     * Calculated total weight across all lines (kg)
     */
    @Schema(description = "Calculated total weight across all lines (kg)")
    @Column(name = "total_weight", precision = 21, scale = 2)
    private BigDecimal totalWeight;

    /**
     * Calculated total revenue across all lines
     */
    @Schema(description = "Calculated total revenue across all lines")
    @Column(name = "total_revenue", precision = 21, scale = 2)
    private BigDecimal totalRevenue;

    /**
     * Currency code
     */
    @Schema(description = "Currency code", required = true)
    @NotNull
    @Column(name = "currency", nullable = false)
    private String currency;

    /**
     * Linked invoice reference
     */
    @Schema(description = "Linked invoice reference")
    @Column(name = "invoice_number")
    private String invoiceNumber;

    /**
     * Payment tracking (replaces simple isPaid boolean)
     */
    @Schema(description = "Payment tracking (replaces simple isPaid boolean)", required = true)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    /**
     * When payment is due
     */
    @Schema(description = "When payment is due")
    @Column(name = "payment_due_date")
    private LocalDate paymentDueDate;

    /**
     * When payment was received
     */
    @Schema(description = "When payment was received")
    @Column(name = "payment_received_date")
    private LocalDate paymentReceivedDate;

    /**
     * Delivery address
     */
    @Schema(description = "Delivery address")
    @Lob
    @Column(name = "shipping_address")
    private String shippingAddress;

    @Lob
    @Column(name = "note")
    private String note;

    @ManyToOne(optional = false)
    @NotNull
    private Customer customer;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SalesOrder id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderCode() {
        return this.orderCode;
    }

    public SalesOrder orderCode(String orderCode) {
        this.setOrderCode(orderCode);
        return this;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public LocalDate getOrderDate() {
        return this.orderDate;
    }

    public SalesOrder orderDate(LocalDate orderDate) {
        this.setOrderDate(orderDate);
        return this;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public LocalDate getRequestedDeliveryDate() {
        return this.requestedDeliveryDate;
    }

    public SalesOrder requestedDeliveryDate(LocalDate requestedDeliveryDate) {
        this.setRequestedDeliveryDate(requestedDeliveryDate);
        return this;
    }

    public void setRequestedDeliveryDate(LocalDate requestedDeliveryDate) {
        this.requestedDeliveryDate = requestedDeliveryDate;
    }

    public LocalDate getActualDeliveryDate() {
        return this.actualDeliveryDate;
    }

    public SalesOrder actualDeliveryDate(LocalDate actualDeliveryDate) {
        this.setActualDeliveryDate(actualDeliveryDate);
        return this;
    }

    public void setActualDeliveryDate(LocalDate actualDeliveryDate) {
        this.actualDeliveryDate = actualDeliveryDate;
    }

    public OrderStatus getStatus() {
        return this.status;
    }

    public SalesOrder status(OrderStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalWeight() {
        return this.totalWeight;
    }

    public SalesOrder totalWeight(BigDecimal totalWeight) {
        this.setTotalWeight(totalWeight);
        return this;
    }

    public void setTotalWeight(BigDecimal totalWeight) {
        this.totalWeight = totalWeight;
    }

    public BigDecimal getTotalRevenue() {
        return this.totalRevenue;
    }

    public SalesOrder totalRevenue(BigDecimal totalRevenue) {
        this.setTotalRevenue(totalRevenue);
        return this;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public String getCurrency() {
        return this.currency;
    }

    public SalesOrder currency(String currency) {
        this.setCurrency(currency);
        return this;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getInvoiceNumber() {
        return this.invoiceNumber;
    }

    public SalesOrder invoiceNumber(String invoiceNumber) {
        this.setInvoiceNumber(invoiceNumber);
        return this;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public PaymentStatus getPaymentStatus() {
        return this.paymentStatus;
    }

    public SalesOrder paymentStatus(PaymentStatus paymentStatus) {
        this.setPaymentStatus(paymentStatus);
        return this;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDate getPaymentDueDate() {
        return this.paymentDueDate;
    }

    public SalesOrder paymentDueDate(LocalDate paymentDueDate) {
        this.setPaymentDueDate(paymentDueDate);
        return this;
    }

    public void setPaymentDueDate(LocalDate paymentDueDate) {
        this.paymentDueDate = paymentDueDate;
    }

    public LocalDate getPaymentReceivedDate() {
        return this.paymentReceivedDate;
    }

    public SalesOrder paymentReceivedDate(LocalDate paymentReceivedDate) {
        this.setPaymentReceivedDate(paymentReceivedDate);
        return this;
    }

    public void setPaymentReceivedDate(LocalDate paymentReceivedDate) {
        this.paymentReceivedDate = paymentReceivedDate;
    }

    public String getShippingAddress() {
        return this.shippingAddress;
    }

    public SalesOrder shippingAddress(String shippingAddress) {
        this.setShippingAddress(shippingAddress);
        return this;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getNote() {
        return this.note;
    }

    public SalesOrder note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public SalesOrder customer(Customer customer) {
        this.setCustomer(customer);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SalesOrder)) {
            return false;
        }
        return getId() != null && getId().equals(((SalesOrder) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SalesOrder{" +
            "id=" + getId() +
            ", orderCode='" + getOrderCode() + "'" +
            ", orderDate='" + getOrderDate() + "'" +
            ", requestedDeliveryDate='" + getRequestedDeliveryDate() + "'" +
            ", actualDeliveryDate='" + getActualDeliveryDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", totalWeight=" + getTotalWeight() +
            ", totalRevenue=" + getTotalRevenue() +
            ", currency='" + getCurrency() + "'" +
            ", invoiceNumber='" + getInvoiceNumber() + "'" +
            ", paymentStatus='" + getPaymentStatus() + "'" +
            ", paymentDueDate='" + getPaymentDueDate() + "'" +
            ", paymentReceivedDate='" + getPaymentReceivedDate() + "'" +
            ", shippingAddress='" + getShippingAddress() + "'" +
            ", note='" + getNote() + "'" +
            "}";
    }
}
