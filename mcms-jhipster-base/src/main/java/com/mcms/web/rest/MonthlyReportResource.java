package com.mcms.web.rest;

import com.mcms.domain.Batch;
import com.mcms.domain.MonthlyReport;
import com.mcms.domain.ReportAuditLog;
import com.mcms.repository.BatchRepository;
import com.mcms.repository.MonthlyReportRepository;
import com.mcms.repository.ReportAuditLogRepository;
import com.mcms.web.rest.dto.GenerateReportRequest;
import com.mcms.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mcms.domain.MonthlyReport}.
 */
@RestController
@RequestMapping("/api/monthly-reports")
@Transactional
public class MonthlyReportResource {

    private static final Logger LOG = LoggerFactory.getLogger(MonthlyReportResource.class);

    private static final String ENTITY_NAME = "monthlyReport";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MonthlyReportRepository monthlyReportRepository;
    private final ReportAuditLogRepository reportAuditLogRepository;
    private final BatchRepository batchRepository;

    public MonthlyReportResource(
        MonthlyReportRepository monthlyReportRepository,
        ReportAuditLogRepository reportAuditLogRepository,
        BatchRepository batchRepository
    ) {
        this.monthlyReportRepository = monthlyReportRepository;
        this.reportAuditLogRepository = reportAuditLogRepository;
        this.batchRepository = batchRepository;
    }

    /**
     * {@code POST  /monthly-reports} : Create a new monthlyReport.
     *
     * @param monthlyReport the monthlyReport to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new monthlyReport, or with status {@code 400 (Bad Request)} if the monthlyReport has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<MonthlyReport> createMonthlyReport(@Valid @RequestBody MonthlyReport monthlyReport) throws URISyntaxException {
        LOG.debug("REST request to save MonthlyReport : {}", monthlyReport);
        if (monthlyReport.getId() != null) {
            throw new BadRequestAlertException("A new monthlyReport cannot already have an ID", ENTITY_NAME, "idexists");
        }
        monthlyReport = monthlyReportRepository.save(monthlyReport);
        return ResponseEntity.created(new URI("/api/monthly-reports/" + monthlyReport.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, monthlyReport.getId().toString()))
            .body(monthlyReport);
    }

    /**
     * {@code PUT  /monthly-reports/:id} : Updates an existing monthlyReport.
     *
     * @param id the id of the monthlyReport to save.
     * @param monthlyReport the monthlyReport to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated monthlyReport,
     * or with status {@code 400 (Bad Request)} if the monthlyReport is not valid,
     * or with status {@code 500 (Internal Server Error)} if the monthlyReport couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<MonthlyReport> updateMonthlyReport(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MonthlyReport monthlyReport
    ) throws URISyntaxException {
        LOG.debug("REST request to update MonthlyReport : {}, {}", id, monthlyReport);
        if (monthlyReport.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, monthlyReport.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!monthlyReportRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        monthlyReport = monthlyReportRepository.save(monthlyReport);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, monthlyReport.getId().toString()))
            .body(monthlyReport);
    }

    /**
     * {@code PATCH  /monthly-reports/:id} : Partial updates given fields of an existing monthlyReport, field will ignore if it is null
     *
     * @param id the id of the monthlyReport to save.
     * @param monthlyReport the monthlyReport to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated monthlyReport,
     * or with status {@code 400 (Bad Request)} if the monthlyReport is not valid,
     * or with status {@code 404 (Not Found)} if the monthlyReport is not found,
     * or with status {@code 500 (Internal Server Error)} if the monthlyReport couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<MonthlyReport> partialUpdateMonthlyReport(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MonthlyReport monthlyReport
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update MonthlyReport partially : {}, {}", id, monthlyReport);
        if (monthlyReport.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, monthlyReport.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!monthlyReportRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MonthlyReport> result = monthlyReportRepository
            .findById(monthlyReport.getId())
            .map(existingMonthlyReport -> {
                if (monthlyReport.getYear() != null) {
                    existingMonthlyReport.setYear(monthlyReport.getYear());
                }
                if (monthlyReport.getMonth() != null) {
                    existingMonthlyReport.setMonth(monthlyReport.getMonth());
                }
                if (monthlyReport.getGeneratedAt() != null) {
                    existingMonthlyReport.setGeneratedAt(monthlyReport.getGeneratedAt());
                }
                if (monthlyReport.getTotalYieldKg() != null) {
                    existingMonthlyReport.setTotalYieldKg(monthlyReport.getTotalYieldKg());
                }
                if (monthlyReport.getTotalCost() != null) {
                    existingMonthlyReport.setTotalCost(monthlyReport.getTotalCost());
                }
                if (monthlyReport.getTotalRevenue() != null) {
                    existingMonthlyReport.setTotalRevenue(monthlyReport.getTotalRevenue());
                }
                if (monthlyReport.getProfitMarginPercent() != null) {
                    existingMonthlyReport.setProfitMarginPercent(monthlyReport.getProfitMarginPercent());
                }
                if (monthlyReport.getTotalContaminationEvents() != null) {
                    existingMonthlyReport.setTotalContaminationEvents(monthlyReport.getTotalContaminationEvents());
                }
                if (monthlyReport.getTotalMissingFields() != null) {
                    existingMonthlyReport.setTotalMissingFields(monthlyReport.getTotalMissingFields());
                }
                if (monthlyReport.getSummary() != null) {
                    existingMonthlyReport.setSummary(monthlyReport.getSummary());
                }

                return existingMonthlyReport;
            })
            .map(monthlyReportRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, monthlyReport.getId().toString())
        );
    }

    /**
     * {@code GET  /monthly-reports} : get all the monthlyReports.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of monthlyReports in body.
     */
    @GetMapping("")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public List<MonthlyReport> getAllMonthlyReports(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all MonthlyReports");
        if (eagerload) {
            return monthlyReportRepository.findAllWithEagerRelationships();
        } else {
            return monthlyReportRepository.findAll();
        }
    }

    /**
     * {@code GET  /monthly-reports/:id} : get the "id" monthlyReport.
     *
     * @param id the id of the monthlyReport to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the monthlyReport, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<MonthlyReport> getMonthlyReport(@PathVariable("id") Long id) {
        LOG.debug("REST request to get MonthlyReport : {}", id);
        Optional<MonthlyReport> monthlyReport = monthlyReportRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(monthlyReport);
    }

    /**
     * {@code POST  /monthly-reports/generate} : Generate a new monthly report.
     *
     * @param request the report generation request (year, month).
     * @param principal the current authenticated user.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new monthlyReport.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/generate")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<MonthlyReport> generateMonthlyReport(@Valid @RequestBody GenerateReportRequest request, Principal principal)
        throws URISyntaxException {
        LOG.debug("REST request to generate MonthlyReport for year={}, month={}", request.getYear(), request.getMonth());

        String username = principal != null ? principal.getName() : "system";
        Instant now = Instant.now();

        // Create the monthly report with aggregated data
        MonthlyReport monthlyReport = new MonthlyReport();
        monthlyReport.setYear(request.getYear());
        monthlyReport.setMonth(request.getMonth());
        monthlyReport.setGeneratedAt(now);

        // TODO: Aggregate data from batches, costs, revenues, contamination events, etc.
        // For now, set placeholder values
        monthlyReport.setTotalYieldKg(BigDecimal.ZERO);
        monthlyReport.setTotalCost(BigDecimal.ZERO);
        monthlyReport.setTotalRevenue(BigDecimal.ZERO);
        monthlyReport.setProfitMarginPercent(BigDecimal.ZERO);
        monthlyReport.setTotalContaminationEvents(0);
        monthlyReport.setTotalMissingFields(0);
        monthlyReport.setSummary("Monthly report for " + request.getYear() + "-" + request.getMonth());

        // Save the report
        monthlyReport = monthlyReportRepository.save(monthlyReport);

        // Create audit log entry
        ReportAuditLog auditLog = new ReportAuditLog();
        auditLog.setReportType("MONTHLY_REPORT");
        auditLog.setGeneratedBy(username);
        auditLog.setGeneratedAt(now);
        auditLog.setReportYear(request.getYear());
        auditLog.setReportMonth(request.getMonth());
        auditLog.setReportId(monthlyReport.getId());
        reportAuditLogRepository.save(auditLog);

        LOG.info("Monthly report generated: id={}, year={}, month={}, generatedBy={}", monthlyReport.getId(), request.getYear(), request.getMonth(), username);

        return ResponseEntity.created(new URI("/api/monthly-reports/" + monthlyReport.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, monthlyReport.getId().toString()))
            .body(monthlyReport);
    }

    /**
     * {@code GET  /monthly-reports/:id/batches} : get batches for the monthly report period.
     *
     * @param id the id of the monthlyReport.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and list of batches in body.
     */
    @GetMapping("/{id}/batches")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_USER')")
    public ResponseEntity<List<Batch>> getBatchesForReport(@PathVariable("id") Long id) {
        LOG.debug("REST request to get batches for MonthlyReport : {}", id);
        Optional<MonthlyReport> monthlyReportOpt = monthlyReportRepository.findById(id);
        if (monthlyReportOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        MonthlyReport report = monthlyReportOpt.get();
        List<Batch> batches = batchRepository.findByYearAndMonth(report.getYear(), report.getMonth());
        return ResponseEntity.ok(batches);
    }

    /**
     * {@code DELETE  /monthly-reports/:id} : delete the "id" monthlyReport.
     *
     * @param id the id of the monthlyReport to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<Void> deleteMonthlyReport(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete MonthlyReport : {}", id);
        monthlyReportRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
