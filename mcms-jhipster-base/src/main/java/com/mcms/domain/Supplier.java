package com.mcms.domain;

import com.mcms.domain.enumeration.SupplierType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Supplier / vendor master data.
 */
@Schema(description = "Supplier / vendor master data.")
@Entity
@Table(name = "supplier")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Supplier implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * Company or individual name
     */
    @Schema(description = "Company or individual name", required = true)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * What they supply
     */
    @Schema(description = "What they supply", required = true)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private SupplierType type;

    /**
     * Primary contact name
     */
    @Schema(description = "Primary contact name")
    @Column(name = "contact_person")
    private String contactPerson;

    /**
     * Email
     */
    @Schema(description = "Email")
    @Column(name = "email")
    private String email;

    /**
     * Phone number
     */
    @Schema(description = "Phone number")
    @Column(name = "phone")
    private String phone;

    /**
     * Full address
     */
    @Schema(description = "Full address")
    @Lob
    @Column(name = "address")
    private String address;

    /**
     * Internal quality rating 1-5
     */
    @Schema(description = "Internal quality rating 1-5")
    @Min(value = 1)
    @Max(value = 5)
    @Column(name = "rating")
    private Integer rating;

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

    public Supplier id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Supplier name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SupplierType getType() {
        return this.type;
    }

    public Supplier type(SupplierType type) {
        this.setType(type);
        return this;
    }

    public void setType(SupplierType type) {
        this.type = type;
    }

    public String getContactPerson() {
        return this.contactPerson;
    }

    public Supplier contactPerson(String contactPerson) {
        this.setContactPerson(contactPerson);
        return this;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getEmail() {
        return this.email;
    }

    public Supplier email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return this.phone;
    }

    public Supplier phone(String phone) {
        this.setPhone(phone);
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return this.address;
    }

    public Supplier address(String address) {
        this.setAddress(address);
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getRating() {
        return this.rating;
    }

    public Supplier rating(Integer rating) {
        this.setRating(rating);
        return this;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Boolean getActive() {
        return this.active;
    }

    public Supplier active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getNote() {
        return this.note;
    }

    public Supplier note(String note) {
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
        if (!(o instanceof Supplier)) {
            return false;
        }
        return getId() != null && getId().equals(((Supplier) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Supplier{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", type='" + getType() + "'" +
            ", contactPerson='" + getContactPerson() + "'" +
            ", email='" + getEmail() + "'" +
            ", phone='" + getPhone() + "'" +
            ", address='" + getAddress() + "'" +
            ", rating=" + getRating() +
            ", active='" + getActive() + "'" +
            ", note='" + getNote() + "'" +
            "}";
    }
}
