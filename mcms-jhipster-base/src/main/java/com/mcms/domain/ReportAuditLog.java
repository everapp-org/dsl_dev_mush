package com.mcms.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Audit log for report generation events.
 */
@Schema(description = "Audit log for report generation events.")
@Entity
@Table(name = "report_audit_log")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ReportAuditLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * Type of report generated
     */
    @Schema(description = "Type of report generated", required = true)
    @NotNull
    @Column(name = "report_type", nullable = false)
    private String reportType;

    /**
     * Username of person who generated the report
     */
    @Schema(description = "Username of person who generated the report", required = true)
    @NotNull
    @Column(name = "generated_by", nullable = false)
    private String generatedBy;

    /**
     * Timestamp of report generation
     */
    @Schema(description = "Timestamp of report generation", required = true)
    @NotNull
    @Column(name = "generated_at", nullable = false)
    private Instant generatedAt;

    /**
     * Year of the report
     */
    @Schema(description = "Year of the report")
    @Column(name = "report_year")
    private Integer reportYear;

    /**
     * Month of the report (1-12)
     */
    @Schema(description = "Month of the report (1-12)")
    @Min(value = 1)
    @Max(value = 12)
    @Column(name = "report_month")
    private Integer reportMonth;

    /**
     * ID of the generated report
     */
    @Schema(description = "ID of the generated report")
    @Column(name = "report_id")
    private Long reportId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ReportAuditLog id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReportType() {
        return this.reportType;
    }

    public ReportAuditLog reportType(String reportType) {
        this.setReportType(reportType);
        return this;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getGeneratedBy() {
        return this.generatedBy;
    }

    public ReportAuditLog generatedBy(String generatedBy) {
        this.setGeneratedBy(generatedBy);
        return this;
    }

    public void setGeneratedBy(String generatedBy) {
        this.generatedBy = generatedBy;
    }

    public Instant getGeneratedAt() {
        return this.generatedAt;
    }

    public ReportAuditLog generatedAt(Instant generatedAt) {
        this.setGeneratedAt(generatedAt);
        return this;
    }

    public void setGeneratedAt(Instant generatedAt) {
        this.generatedAt = generatedAt;
    }

    public Integer getReportYear() {
        return this.reportYear;
    }

    public ReportAuditLog reportYear(Integer reportYear) {
        this.setReportYear(reportYear);
        return this;
    }

    public void setReportYear(Integer reportYear) {
        this.reportYear = reportYear;
    }

    public Integer getReportMonth() {
        return this.reportMonth;
    }

    public ReportAuditLog reportMonth(Integer reportMonth) {
        this.setReportMonth(reportMonth);
        return this;
    }

    public void setReportMonth(Integer reportMonth) {
        this.reportMonth = reportMonth;
    }

    public Long getReportId() {
        return this.reportId;
    }

    public ReportAuditLog reportId(Long reportId) {
        this.setReportId(reportId);
        return this;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReportAuditLog)) {
            return false;
        }
        return getId() != null && getId().equals(((ReportAuditLog) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReportAuditLog{" +
            "id=" + getId() +
            ", reportType='" + getReportType() + "'" +
            ", generatedBy='" + getGeneratedBy() + "'" +
            ", generatedAt='" + getGeneratedAt() + "'" +
            ", reportYear=" + getReportYear() +
            ", reportMonth=" + getReportMonth() +
            ", reportId=" + getReportId() +
            "}";
    }
}
