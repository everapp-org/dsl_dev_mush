package com.mcms.web.rest;

import com.mcms.domain.ReportAuditLog;
import com.mcms.repository.ReportAuditLogRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mcms.domain.ReportAuditLog}.
 */
@RestController
@RequestMapping("/api/report-audit-logs")
public class ReportAuditLogResource {

    private static final Logger LOG = LoggerFactory.getLogger(ReportAuditLogResource.class);

    private final ReportAuditLogRepository reportAuditLogRepository;

    public ReportAuditLogResource(ReportAuditLogRepository reportAuditLogRepository) {
        this.reportAuditLogRepository = reportAuditLogRepository;
    }

    /**
     * {@code GET  /report-audit-logs} : get all the reportAuditLogs.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of reportAuditLogs in body.
     */
    @GetMapping("")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public List<ReportAuditLog> getAllReportAuditLogs() {
        LOG.debug("REST request to get all ReportAuditLogs");
        return reportAuditLogRepository.findAll();
    }

    /**
     * {@code GET  /report-audit-logs/:id} : get the "id" reportAuditLog.
     *
     * @param id the id of the reportAuditLog to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the reportAuditLog, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<ReportAuditLog> getReportAuditLog(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ReportAuditLog : {}", id);
        Optional<ReportAuditLog> reportAuditLog = reportAuditLogRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(reportAuditLog);
    }
}
