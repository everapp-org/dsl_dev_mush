package com.mcms.web.rest;

import com.mcms.domain.Batch;
import com.mcms.domain.HarvestRecord;
import com.mcms.repository.BatchRepository;
import com.mcms.repository.HarvestRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for data export operations.
 * Export functionality is restricted to ADMIN, MANAGER, and OPERATOR roles only.
 * ROLE_USER is explicitly blocked from all export endpoints.
 */
@RestController
@RequestMapping("/api/export")
public class ExportResource {

    private static final Logger LOG = LoggerFactory.getLogger(ExportResource.class);

    private final BatchRepository batchRepository;
    private final HarvestRecordRepository harvestRecordRepository;

    public ExportResource(BatchRepository batchRepository, HarvestRecordRepository harvestRecordRepository) {
        this.batchRepository = batchRepository;
        this.harvestRecordRepository = harvestRecordRepository;
    }

    /**
     * GET /api/export/batches : Export batch data to CSV.
     * Supports filtering and pagination (features #333, #334).
     * Only accessible to ADMIN, MANAGER, and OPERATOR roles.
     *
     * @param search search query
     * @param phase filter by phase
     * @param strainId filter by strain ID
     * @param isActive filter by active status
     * @param startDateFrom filter by start date from
     * @param startDateTo filter by start date to
     * @param numberOfBagsMin filter by minimum number of bags
     * @param numberOfBagsMax filter by maximum number of bags
     * @param pageOnly if true, export only current page; if false, export all matching records
     * @param page page number (0-indexed, only used if pageOnly=true)
     * @param size page size (only used if pageOnly=true, default 20)
     * @return CSV file containing batch data
     */
    @GetMapping("/batches")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_OPERATOR')")
    public ResponseEntity<String> exportBatches(
        @RequestParam(name = "search", required = false) String search,
        @RequestParam(name = "phase", required = false) String phase,
        @RequestParam(name = "strainId", required = false) Long strainId,
        @RequestParam(name = "isActive", required = false) Boolean isActive,
        @RequestParam(name = "startDateFrom", required = false) LocalDate startDateFrom,
        @RequestParam(name = "startDateTo", required = false) LocalDate startDateTo,
        @RequestParam(name = "numberOfBagsMin", required = false) Integer numberOfBagsMin,
        @RequestParam(name = "numberOfBagsMax", required = false) Integer numberOfBagsMax,
        @RequestParam(name = "pageOnly", required = false, defaultValue = "false") Boolean pageOnly,
        @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
        @RequestParam(name = "size", required = false, defaultValue = "20") Integer size
    ) {
        LOG.debug("REST request to export batches to CSV: search={}, phase={}, pageOnly={}, page={}, size={}",
            search, phase, pageOnly, page, size);

        List<Batch> batches;

        // Check if any filters are applied
        boolean hasFilters = search != null || phase != null || strainId != null || isActive != null ||
            startDateFrom != null || startDateTo != null || numberOfBagsMin != null || numberOfBagsMax != null;

        if (pageOnly != null && pageOnly) {
            // Export only current page
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));
            Page<Batch> batchPage;

            if (hasFilters) {
                batchPage = batchRepository.findAllWithFilters(search, phase, strainId, isActive,
                    startDateFrom, startDateTo, numberOfBagsMin, numberOfBagsMax, pageable);
            } else {
                batchPage = batchRepository.findAllWithEagerRelationships(pageable);
            }

            batches = batchPage.getContent();
        } else {
            // Export all matching records
            if (hasFilters) {
                // Use unpaged query with filters
                Pageable unpaged = Pageable.unpaged();
                Page<Batch> batchPage = batchRepository.findAllWithFilters(search, phase, strainId, isActive,
                    startDateFrom, startDateTo, numberOfBagsMin, numberOfBagsMax, unpaged);
                batches = batchPage.getContent();
            } else {
                // Export all records without filters
                batches = batchRepository.findAllWithEagerRelationships();
            }
        }

        StringBuilder csv = new StringBuilder();
        csv.append("ID,Batch Code,Strain,Current Phase,Start Date,End Date,Active\n");

        for (Batch batch : batches) {
            csv.append(batch.getId()).append(",");
            csv.append(escapeCSV(batch.getBatchCode())).append(",");
            csv.append(escapeCSV(batch.getStrain() != null ? batch.getStrain().getName() : "")).append(",");
            csv.append(escapeCSV(batch.getCurrentPhase() != null ? batch.getCurrentPhase().toString() : "")).append(",");
            csv.append(batch.getStartDate() != null ? batch.getStartDate().toString() : "").append(",");
            csv.append(batch.getEndDate() != null ? batch.getEndDate().toString() : "").append(",");
            csv.append(batch.getIsActive() != null ? batch.getIsActive().toString() : "").append("\n");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "batches.csv");

        return ResponseEntity.ok()
            .headers(headers)
            .body(csv.toString());
    }

    /**
     * GET /api/export/harvests : Export harvest records to CSV.
     * Supports pagination (feature #334).
     * Only accessible to ADMIN, MANAGER, and OPERATOR roles.
     *
     * @param pageOnly if true, export only current page; if false, export all records
     * @param page page number (0-indexed, only used if pageOnly=true)
     * @param size page size (only used if pageOnly=true, default 20)
     * @return CSV file containing harvest data
     */
    @GetMapping("/harvests")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_OPERATOR')")
    public ResponseEntity<String> exportHarvests(
        @RequestParam(name = "pageOnly", required = false, defaultValue = "false") Boolean pageOnly,
        @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
        @RequestParam(name = "size", required = false, defaultValue = "20") Integer size
    ) {
        LOG.debug("REST request to export harvests to CSV: pageOnly={}, page={}, size={}",
            pageOnly, page, size);

        List<HarvestRecord> harvests;

        if (pageOnly != null && pageOnly) {
            // Export only current page
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "harvestDate"));
            Page<HarvestRecord> harvestPage = harvestRecordRepository.findAll(pageable);
            harvests = harvestPage.getContent();
        } else {
            // Export all records
            harvests = harvestRecordRepository.findAll(Sort.by(Sort.Direction.DESC, "harvestDate"));
        }

        StringBuilder csv = new StringBuilder();
        csv.append("ID,Batch Code,Harvest Date,Flush Number,Weight (kg),Quality Grade\n");

        for (HarvestRecord harvest : harvests) {
            csv.append(harvest.getId()).append(",");
            // Get batch code via flushCycle -> batch relationship
            String batchCode = "";
            if (harvest.getFlushCycle() != null && harvest.getFlushCycle().getBatch() != null) {
                batchCode = harvest.getFlushCycle().getBatch().getBatchCode();
            }
            csv.append(escapeCSV(batchCode)).append(",");
            csv.append(harvest.getHarvestDate() != null ? harvest.getHarvestDate().toString() : "").append(",");
            csv.append(harvest.getFlushCycle() != null && harvest.getFlushCycle().getFlushNumber() != null ?
                harvest.getFlushCycle().getFlushNumber() : "").append(",");
            csv.append(harvest.getWeightKg() != null ? harvest.getWeightKg() : "").append(",");
            csv.append(escapeCSV(harvest.getGrade() != null ? harvest.getGrade().toString() : "")).append("\n");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "harvests.csv");

        return ResponseEntity.ok()
            .headers(headers)
            .body(csv.toString());
    }

    /**
     * GET /api/export/financials : Export financial summary to CSV.
     * Only accessible to ADMIN and MANAGER roles (financial data is sensitive).
     *
     * @return CSV file containing financial data
     */
    @GetMapping("/financials")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<String> exportFinancials() {
        LOG.debug("REST request to export financial summary to CSV");

        // For now, return a simple placeholder
        // In production, this would query CostRecord, SalesOrder, etc.
        StringBuilder csv = new StringBuilder();
        csv.append("Category,Amount\n");
        csv.append("Total Revenue,0.00\n");
        csv.append("Total Costs,0.00\n");
        csv.append("Net Profit,0.00\n");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "financials.csv");

        return ResponseEntity.ok()
            .headers(headers)
            .body(csv.toString());
    }

    /**
     * Escape CSV values to prevent injection and handle commas/quotes.
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
}
