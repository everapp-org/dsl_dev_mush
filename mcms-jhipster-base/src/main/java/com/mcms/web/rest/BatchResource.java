package com.mcms.web.rest;

import com.mcms.domain.Batch;
import com.mcms.domain.BatchAuditLog;
import com.mcms.domain.BatchMaterialUsage;
import com.mcms.domain.InventoryLot;
import com.mcms.domain.SalesOrderLine;
import com.mcms.domain.SupplyOrderLine;
import com.mcms.domain.enumeration.PhaseName;
import com.mcms.repository.BatchAuditLogRepository;
import com.mcms.repository.BatchMaterialUsageRepository;
import com.mcms.repository.BatchRepository;
import com.mcms.repository.SalesOrderLineRepository;
import com.mcms.repository.UserRepository;
import com.mcms.service.BatchService;
import com.mcms.service.exception.InvalidTransitionException;
import com.mcms.web.rest.dto.DiscardBatchRequest;
import com.mcms.web.rest.dto.ForceTransitionRequest;
import com.mcms.web.rest.dto.TraceabilityReport;
import com.mcms.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mcms.domain.Batch}.
 */
@RestController
@RequestMapping("/api/batches")
@Transactional
public class BatchResource {

    private static final Logger LOG = LoggerFactory.getLogger(BatchResource.class);

    private static final String ENTITY_NAME = "batch";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BatchRepository batchRepository;
    private final BatchAuditLogRepository auditLogRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final BatchMaterialUsageRepository batchMaterialUsageRepository;
    private final SalesOrderLineRepository salesOrderLineRepository;
    private final BatchService batchService;

    public BatchResource(
        BatchRepository batchRepository,
        BatchAuditLogRepository auditLogRepository,
        PasswordEncoder passwordEncoder,
        UserRepository userRepository,
        BatchMaterialUsageRepository batchMaterialUsageRepository,
        SalesOrderLineRepository salesOrderLineRepository,
        BatchService batchService
    ) {
        this.batchRepository = batchRepository;
        this.auditLogRepository = auditLogRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.batchMaterialUsageRepository = batchMaterialUsageRepository;
        this.salesOrderLineRepository = salesOrderLineRepository;
        this.batchService = batchService;
    }

    /**
     * {@code POST  /batches} : Create a new batch.
     *
     * @param batch the batch to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new batch, or with status {@code 400 (Bad Request)} if the batch has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_OPERATOR')")
    public ResponseEntity<Batch> createBatch(@Valid @RequestBody Batch batch) throws URISyntaxException {
        LOG.debug("REST request to save Batch : {}", batch);
        if (batch.getId() != null) {
            throw new BadRequestAlertException("A new batch cannot already have an ID", ENTITY_NAME, "idexists");
        }
        batch = batchRepository.save(batch);
        return ResponseEntity.created(new URI("/api/batches/" + batch.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, batch.getId().toString()))
            .body(batch);
    }

    /**
     * {@code PUT  /batches/:id} : Updates an existing batch.
     *
     * @param id the id of the batch to save.
     * @param batch the batch to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated batch,
     * or with status {@code 400 (Bad Request)} if the batch is not valid,
     * or with status {@code 500 (Internal Server Error)} if the batch couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_OPERATOR')")
    public ResponseEntity<Batch> updateBatch(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Batch batch)
        throws URISyntaxException {
        LOG.debug("REST request to update Batch : {}, {}", id, batch);
        if (batch.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, batch.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!batchRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        batch = batchRepository.save(batch);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, batch.getId().toString()))
            .body(batch);
    }

    /**
     * {@code PATCH  /batches/:id} : Partial updates given fields of an existing batch, field will ignore if it is null
     *
     * @param id the id of the batch to save.
     * @param batch the batch to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated batch,
     * or with status {@code 400 (Bad Request)} if the batch is not valid,
     * or with status {@code 404 (Not Found)} if the batch is not found,
     * or with status {@code 500 (Internal Server Error)} if the batch couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_OPERATOR')")
    public ResponseEntity<Batch> partialUpdateBatch(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Batch batch
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Batch partially : {}, {}", id, batch);
        if (batch.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, batch.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!batchRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Batch> result = batchRepository
            .findById(batch.getId())
            .map(existingBatch -> {
                if (batch.getBatchCode() != null) {
                    existingBatch.setBatchCode(batch.getBatchCode());
                }
                if (batch.getStartDate() != null) {
                    existingBatch.setStartDate(batch.getStartDate());
                }
                if (batch.getEndDate() != null) {
                    existingBatch.setEndDate(batch.getEndDate());
                }
                if (batch.getCurrentPhase() != null) {
                    existingBatch.setCurrentPhase(batch.getCurrentPhase());
                }
                if (batch.getNumberOfBags() != null) {
                    existingBatch.setNumberOfBags(batch.getNumberOfBags());
                }
                if (batch.getSubstrateWeightKg() != null) {
                    existingBatch.setSubstrateWeightKg(batch.getSubstrateWeightKg());
                }
                if (batch.getSpawnWeightKg() != null) {
                    existingBatch.setSpawnWeightKg(batch.getSpawnWeightKg());
                }
                if (batch.getTargetYieldKg() != null) {
                    existingBatch.setTargetYieldKg(batch.getTargetYieldKg());
                }
                if (batch.getActualTotalYieldKg() != null) {
                    existingBatch.setActualTotalYieldKg(batch.getActualTotalYieldKg());
                }
                if (batch.getBiologicalEfficiencyPercent() != null) {
                    existingBatch.setBiologicalEfficiencyPercent(batch.getBiologicalEfficiencyPercent());
                }
                if (batch.getIsContaminated() != null) {
                    existingBatch.setIsContaminated(batch.getIsContaminated());
                }
                if (batch.getIsActive() != null) {
                    existingBatch.setIsActive(batch.getIsActive());
                }
                if (batch.getCompletionNote() != null) {
                    existingBatch.setCompletionNote(batch.getCompletionNote());
                }
                if (batch.getNote() != null) {
                    existingBatch.setNote(batch.getNote());
                }

                return existingBatch;
            })
            .map(batchRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, batch.getId().toString())
        );
    }

    /**
     * {@code GET  /batches} : get all the batches.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of batches in body.
     */
    @GetMapping("")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_OPERATOR', 'ROLE_USER')")
    public ResponseEntity<List<Batch>> getAllBatches(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload,
        @RequestParam(name = "search", required = false) String search,
        @RequestParam(name = "phase", required = false) String phase,
        @RequestParam(name = "strainId", required = false) Long strainId,
        @RequestParam(name = "isActive", required = false) Boolean isActive,
        @RequestParam(name = "startDateFrom", required = false) LocalDate startDateFrom,
        @RequestParam(name = "startDateTo", required = false) LocalDate startDateTo
    ) {
        LOG.debug("REST request to get a page of Batches with filters: search={}, phase={}, strainId={}, isActive={}, startDateFrom={}, startDateTo={}",
            search, phase, strainId, isActive, startDateFrom, startDateTo);

        Page<Batch> page;

        // Check if any filters are applied
        boolean hasFilters = search != null || phase != null || strainId != null || isActive != null || startDateFrom != null || startDateTo != null;

        if (hasFilters) {
            // Use filtered query
            page = batchRepository.findAllWithFilters(search, phase, strainId, isActive, startDateFrom, startDateTo, pageable);
        } else if (eagerload) {
            // Use eager loading without filters
            page = batchRepository.findAllWithEagerRelationships(pageable);
        } else {
            // Simple query without filters or eager loading
            page = batchRepository.findAll(pageable);
        }

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /batches/:id} : get the "id" batch.
     *
     * @param id the id of the batch to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the batch, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_OPERATOR', 'ROLE_USER')")
    public ResponseEntity<Batch> getBatch(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Batch : {}", id);
        Optional<Batch> batch = batchRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(batch);
    }

    /**
     * {@code GET  /batches/:id/traceability} : Get traceability report for a batch (supplier to customer chain).
     *
     * @param id the id of the batch.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the traceability report in JSON format.
     */
    @GetMapping("/{id}/traceability")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_OPERATOR', 'ROLE_USER')")
    public ResponseEntity<TraceabilityReport> getTraceability(@PathVariable("id") Long id) {
        LOG.debug("REST request to get traceability for Batch : {}", id);

        Optional<Batch> batchOptional = batchRepository.findOneWithEagerRelationships(id);
        if (batchOptional.isEmpty()) {
            throw new BadRequestAlertException("Batch not found", ENTITY_NAME, "notfound");
        }

        Batch batch = batchOptional.get();

        // Build traceability report
        TraceabilityReport report = new TraceabilityReport(
            batch.getBatchCode(),
            batch.getStartDate(),
            batch.getCurrentPhase() != null ? batch.getCurrentPhase().toString() : null
        );

        // Section 1: Materials used (from suppliers)
        List<BatchMaterialUsage> materialUsages = batchMaterialUsageRepository.findAll().stream()
            .filter(usage -> usage.getBatch() != null && usage.getBatch().getId().equals(id))
            .collect(Collectors.toList());

        for (BatchMaterialUsage usage : materialUsages) {
            TraceabilityReport.MaterialUsageInfo info = new TraceabilityReport.MaterialUsageInfo();
            info.setMaterialName(usage.getMaterial() != null ? usage.getMaterial().getName() : null);
            info.setQuantityUsed(usage.getQuantityUsed());
            info.setUnit(usage.getUnit() != null ? usage.getUnit().toString() : null);
            info.setUsageDate(usage.getUsageDate());

            InventoryLot lot = usage.getInventoryLot();
            if (lot != null) {
                info.setLotCode(lot.getLotCode());
                if (lot.getSupplyOrderLine() != null) {
                    SupplyOrderLine supplyLine = lot.getSupplyOrderLine();
                    if (supplyLine.getSupplyOrder() != null) {
                        info.setSupplyOrderCode(supplyLine.getSupplyOrder().getOrderCode());
                        if (supplyLine.getSupplyOrder().getSupplier() != null) {
                            info.setSupplierName(supplyLine.getSupplyOrder().getSupplier().getName());
                        }
                    }
                }
            }

            report.getMaterialsUsed().add(info);
        }

        // Section 2: Sales (to customers)
        List<SalesOrderLine> salesLines = salesOrderLineRepository.findAll().stream()
            .filter(line -> line.getBatch() != null && line.getBatch().getId().equals(id))
            .collect(Collectors.toList());

        for (SalesOrderLine line : salesLines) {
            TraceabilityReport.SalesInfo info = new TraceabilityReport.SalesInfo();
            info.setProductName(line.getProduct() != null ? line.getProduct().getName() : null);
            info.setQuantity(line.getWeightKg());
            info.setUnit(line.getUnit() != null ? line.getUnit().toString() : "kg");

            if (line.getSalesOrder() != null) {
                info.setSalesOrderCode(line.getSalesOrder().getOrderCode());
                info.setOrderDate(line.getSalesOrder().getOrderDate());
                info.setDeliveryDate(line.getSalesOrder().getActualDeliveryDate());
                if (line.getSalesOrder().getCustomer() != null) {
                    info.setCustomerName(line.getSalesOrder().getCustomer().getName());
                }
            }

            report.getSales().add(info);
        }

        return ResponseEntity.ok().body(report);
    }

    /**
     * {@code POST  /batches/:id/force-transition} : Force a batch to transition to a new phase (admin only).
     *
     * @param id the id of the batch to transition.
     * @param request the force transition request with target phase and admin password.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated batch,
     * or with status {@code 401 (Unauthorized)} if password is incorrect,
     * or with status {@code 404 (Not Found)} if the batch is not found.
     */
    @PostMapping("/{id}/force-transition")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Batch> forceTransition(
        @PathVariable("id") Long id,
        @Valid @RequestBody ForceTransitionRequest request
    ) {
        LOG.debug("REST request to force transition Batch {} to phase {}", id, request.getTargetPhase());

        // Get current user login
        String currentUserLogin = SecurityContextHolder.getContext().getAuthentication().getName();

        // Verify admin password
        Optional<com.mcms.domain.User> adminUserOpt = userRepository.findOneByLogin(currentUserLogin);
        if (adminUserOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        com.mcms.domain.User adminUser = adminUserOpt.get();
        if (!passwordEncoder.matches(request.getAdminPassword(), adminUser.getPassword())) {
            LOG.warn("Failed force transition attempt - invalid password for user {}", currentUserLogin);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Get batch
        Optional<Batch> batchOpt = batchRepository.findById(id);
        if (batchOpt.isEmpty()) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Batch batch = batchOpt.get();
        var oldPhase = batch.getCurrentPhase();
        var newPhase = request.getTargetPhase();

        // Force transition (bypass state machine guards)
        batch.setCurrentPhase(newPhase);
        batch = batchRepository.save(batch);

        // Create audit log entry
        BatchAuditLog auditLog = new BatchAuditLog();
        auditLog.setBatchId(batch.getId());
        auditLog.setAction("FORCE_TRANSITION");
        auditLog.setOldPhase(oldPhase);
        auditLog.setNewPhase(newPhase);
        auditLog.setPerformedBy(currentUserLogin);
        auditLog.setTimestamp(Instant.now());
        auditLog.setReason(request.getReason());
        auditLog.setForced(true);
        auditLogRepository.save(auditLog);

        LOG.info("Admin {} forced batch {} transition from {} to {}", currentUserLogin, batch.getId(), oldPhase, newPhase);

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, batch.getId().toString()))
            .body(batch);
    }

    /**
     * {@code GET  /batches/:id/audit-log} : Get audit log for a batch.
     *
     * @param id the id of the batch.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of audit log entries.
     */
    @GetMapping("/{id}/audit-log")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<BatchAuditLog>> getBatchAuditLog(@PathVariable("id") Long id) {
        LOG.debug("REST request to get audit log for Batch : {}", id);
        List<BatchAuditLog> auditLog = auditLogRepository.findByBatchIdOrderByTimestampDesc(id);
        return ResponseEntity.ok().body(auditLog);
    }

    /**
     * {@code POST  /batches/:id/discard} : Discard a contaminated batch with a reason.
     *
     * @param id the id of the batch to discard.
     * @param request the discard request containing the reason.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the discarded batch.
     */
    @PostMapping("/{id}/discard")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<Batch> discardBatch(@PathVariable("id") Long id, @Valid @RequestBody DiscardBatchRequest request) {
        LOG.debug("REST request to discard Batch : {} with reason: {}", id, request.getReason());

        Optional<Batch> batchOptional = batchRepository.findById(id);
        if (!batchOptional.isPresent()) {
            throw new BadRequestAlertException("Batch not found", ENTITY_NAME, "idnotfound");
        }

        Batch batch = batchOptional.get();

        // Mark batch as contaminated and inactive
        batch.setIsContaminated(true);
        batch.setIsActive(false);
        batch.setEndDate(LocalDate.now());
        batch.setCompletionNote("DISCARDED - " + request.getReason());

        batch = batchRepository.save(batch);

        LOG.info("Batch {} discarded by {} - Reason: {}", id, SecurityContextHolder.getContext().getAuthentication().getName(), request.getReason());

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, batch.getId().toString()))
            .body(batch);
    }

    /**
     * {@code GET  /batches/:id/export-traceability} : export traceability report for batch.
     *
     * @param id the id of the batch.
     * @return CSV file with full supply chain traceability.
     */
    @GetMapping("/{id}/export-traceability")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> exportTraceability(@PathVariable("id") Long id) {
        LOG.debug("REST request to export traceability for Batch : {}", id);

        Optional<Batch> batchOptional = batchRepository.findById(id);
        if (batchOptional.isEmpty()) {
            throw new BadRequestAlertException("Batch not found", ENTITY_NAME, "notfound");
        }

        Batch batch = batchOptional.get();

        // Build CSV content
        StringBuilder csv = new StringBuilder();
        csv.append("Batch Traceability Report\n");
        csv.append("Batch Code,").append(batch.getBatchCode()).append("\n");
        csv.append("Start Date,").append(batch.getStartDate()).append("\n");
        csv.append("Current Phase,").append(batch.getCurrentPhase()).append("\n\n");

        // Section 1: Materials used (suppliers)
        csv.append("MATERIALS USED (FROM SUPPLIERS)\n");
        csv.append("Material,Supplier,Supply Order,Lot Number,Quantity Used,Unit,Usage Date\n");

        List<BatchMaterialUsage> materialUsages = batchMaterialUsageRepository.findAll().stream()
            .filter(usage -> usage.getBatch() != null && usage.getBatch().getId().equals(id))
            .collect(Collectors.toList());

        for (BatchMaterialUsage usage : materialUsages) {
            String materialName = usage.getMaterial() != null ? usage.getMaterial().getName() : "N/A";
            InventoryLot lot = usage.getInventoryLot();
            String lotCode = lot != null ? lot.getLotCode() : "N/A";

            String supplierName = "N/A";
            String supplyOrderCode = "N/A";
            if (lot != null && lot.getSupplyOrderLine() != null) {
                SupplyOrderLine supplyLine = lot.getSupplyOrderLine();
                if (supplyLine.getSupplyOrder() != null) {
                    supplyOrderCode = supplyLine.getSupplyOrder().getOrderCode();
                    if (supplyLine.getSupplyOrder().getSupplier() != null) {
                        supplierName = supplyLine.getSupplyOrder().getSupplier().getName();
                    }
                }
            }

            csv.append(escapeCSV(materialName)).append(",");
            csv.append(escapeCSV(supplierName)).append(",");
            csv.append(escapeCSV(supplyOrderCode)).append(",");
            csv.append(escapeCSV(lotCode)).append(",");
            csv.append(usage.getQuantityUsed()).append(",");
            csv.append(usage.getUnit()).append(",");
            csv.append(usage.getUsageDate()).append("\n");
        }

        // Section 2: Sales (to customers)
        csv.append("\nSALES (TO CUSTOMERS)\n");
        csv.append("Customer,Sales Order,Product,Quantity,Unit,Order Date,Delivery Date\n");

        List<SalesOrderLine> salesLines = salesOrderLineRepository.findAll().stream()
            .filter(line -> line.getBatch() != null && line.getBatch().getId().equals(id))
            .collect(Collectors.toList());

        for (SalesOrderLine line : salesLines) {
            String customerName = "N/A";
            String salesOrderCode = "N/A";
            String orderDate = "N/A";
            String deliveryDate = "N/A";

            if (line.getSalesOrder() != null) {
                salesOrderCode = line.getSalesOrder().getOrderCode();
                orderDate = line.getSalesOrder().getOrderDate() != null ?
                    line.getSalesOrder().getOrderDate().toString() : "N/A";
                deliveryDate = line.getSalesOrder().getActualDeliveryDate() != null ?
                    line.getSalesOrder().getActualDeliveryDate().toString() : "N/A";

                if (line.getSalesOrder().getCustomer() != null) {
                    customerName = line.getSalesOrder().getCustomer().getName();
                }
            }

            String productName = line.getProduct() != null ? line.getProduct().getName() : "N/A";

            csv.append(escapeCSV(customerName)).append(",");
            csv.append(escapeCSV(salesOrderCode)).append(",");
            csv.append(escapeCSV(productName)).append(",");
            csv.append(line.getWeightKg()).append(",");
            csv.append(line.getUnit() != null ? line.getUnit().toString() : "kg").append(",");
            csv.append(escapeCSV(orderDate)).append(",");
            csv.append(escapeCSV(deliveryDate)).append("\n");
        }

        // Return CSV file
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "traceability-" + batch.getBatchCode() + ".csv");

        return ResponseEntity.ok()
            .headers(headers)
            .body(csv.toString());
    }

    /**
     * Escape CSV values to handle commas and quotes.
     */
    private String escapeCSV(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    /**
     * {@code POST  /batches/:id/transition} : Transition a batch to a new phase with phase execution logging.
     *
     * @param id the id of the batch to transition.
     * @param request the force transition request with target phase.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated batch.
     */
    @PostMapping("/{id}/transition")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_OPERATOR')")
    public ResponseEntity<Batch> transitionPhase(
        @PathVariable("id") Long id,
        @Valid @RequestBody ForceTransitionRequest request
    ) {
        LOG.debug("REST request to transition Batch {} to phase {}", id, request.getTargetPhase());

        try {
            Batch batch = batchService.transitionPhase(id, request.getTargetPhase());
            return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, batch.getId().toString()))
                .body(batch);
        } catch (InvalidTransitionException e) {
            throw new BadRequestAlertException(e.getMessage(), ENTITY_NAME, "invalidtransition");
        } catch (IllegalArgumentException e) {
            throw new BadRequestAlertException(e.getMessage(), ENTITY_NAME, "transitionfailed");
        }
    }

    /**
     * {@code POST  /batches/:id/complete} : Complete a batch, calculating yields and marking inactive.
     *
     * @param id the id of the batch to complete.
     * @param request the discard request (reused for completion note).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the completed batch.
     */
    @PostMapping("/{id}/complete")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_OPERATOR')")
    public ResponseEntity<Batch> completeBatch(
        @PathVariable("id") Long id,
        @RequestBody(required = false) DiscardBatchRequest request
    ) {
        LOG.debug("REST request to complete Batch : {}", id);

        try {
            String completionNote = request != null ? request.getReason() : null;
            Batch batch = batchService.completeBatch(id, completionNote);
            return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, batch.getId().toString()))
                .body(batch);
        } catch (IllegalArgumentException e) {
            throw new BadRequestAlertException(e.getMessage(), ENTITY_NAME, "completionfailed");
        }
    }

    /**
     * {@code GET  /batches/:id/cumulative-yield} : Get cumulative yield from all flush cycles.
     *
     * @param id the id of the batch.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the cumulative yield.
     */
    @GetMapping("/{id}/cumulative-yield")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_OPERATOR', 'ROLE_USER')")
    public ResponseEntity<java.math.BigDecimal> getCumulativeYield(@PathVariable("id") Long id) {
        LOG.debug("REST request to get cumulative yield for Batch : {}", id);
        java.math.BigDecimal yield = batchService.calculateCumulativeYield(id);
        return ResponseEntity.ok().body(yield);
    }

    /**
     * {@code DELETE  /batches/:id} : delete the "id" batch.
     *
     * @param id the id of the batch to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<Void> deleteBatch(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Batch : {}", id);
        batchRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
