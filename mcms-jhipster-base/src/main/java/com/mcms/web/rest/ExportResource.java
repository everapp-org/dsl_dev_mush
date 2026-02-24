package com.mcms.web.rest;

import com.mcms.domain.Batch;
import com.mcms.domain.HarvestRecord;
import com.mcms.repository.BatchRepository;
import com.mcms.repository.HarvestRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
     * Only accessible to ADMIN, MANAGER, and OPERATOR roles.
     *
     * @return CSV file containing batch data
     */
    @GetMapping("/batches")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_OPERATOR')")
    public ResponseEntity<String> exportBatches() {
        LOG.debug("REST request to export batches to CSV");

        List<Batch> batches = batchRepository.findAll();

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
     * Only accessible to ADMIN, MANAGER, and OPERATOR roles.
     *
     * @return CSV file containing harvest data
     */
    @GetMapping("/harvests")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_OPERATOR')")
    public ResponseEntity<String> exportHarvests() {
        LOG.debug("REST request to export harvests to CSV");

        List<HarvestRecord> harvests = harvestRecordRepository.findAll();

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
