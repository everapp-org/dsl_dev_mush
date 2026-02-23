package com.mcms.domain;

import com.mcms.domain.enumeration.PhaseName;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Audit log for batch state transitions and critical operations.
 */
@Entity
@Table(name = "batch_audit_log")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class BatchAuditLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "batch_id", nullable = false)
    private Long batchId;

    @NotNull
    @Column(name = "action", nullable = false)
    private String action;

    @Column(name = "old_phase")
    @Enumerated(EnumType.STRING)
    private PhaseName oldPhase;

    @Column(name = "new_phase")
    @Enumerated(EnumType.STRING)
    private PhaseName newPhase;

    @Column(name = "performed_by")
    private String performedBy;

    @NotNull
    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    @Column(name = "reason")
    private String reason;

    @Column(name = "forced")
    private Boolean forced;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public PhaseName getOldPhase() {
        return oldPhase;
    }

    public void setOldPhase(PhaseName oldPhase) {
        this.oldPhase = oldPhase;
    }

    public PhaseName getNewPhase() {
        return newPhase;
    }

    public void setNewPhase(PhaseName newPhase) {
        this.newPhase = newPhase;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Boolean getForced() {
        return forced;
    }

    public void setForced(Boolean forced) {
        this.forced = forced;
    }
}
