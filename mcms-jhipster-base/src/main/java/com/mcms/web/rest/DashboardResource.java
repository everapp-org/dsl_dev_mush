package com.mcms.web.rest;

import com.mcms.domain.CostRecord;
import com.mcms.domain.SalesOrder;
import com.mcms.repository.BatchRepository;
import com.mcms.repository.CostRecordRepository;
import com.mcms.repository.SalesOrderRepository;
import com.mcms.security.AuthoritiesConstants;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for dashboard endpoints.
 * Provides aggregated KPIs and metrics for various dashboards.
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardResource {

    private static final Logger LOG = LoggerFactory.getLogger(DashboardResource.class);

    private final CostRecordRepository costRecordRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final BatchRepository batchRepository;

    public DashboardResource(
        CostRecordRepository costRecordRepository,
        SalesOrderRepository salesOrderRepository,
        BatchRepository batchRepository
    ) {
        this.costRecordRepository = costRecordRepository;
        this.salesOrderRepository = salesOrderRepository;
        this.batchRepository = batchRepository;
    }

    /**
     * GET /api/dashboard/financial : Get financial dashboard KPIs.
     *
     * @return the ResponseEntity with status 200 (OK) and body containing financial metrics.
     */
    @GetMapping("/financial")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '" + AuthoritiesConstants.MANAGER + "')")
    public ResponseEntity<Map<String, Object>> getFinancialDashboard() {
        LOG.debug("REST request to get financial dashboard");

        Map<String, Object> dashboard = new HashMap<>();

        // Calculate total costs from all cost records
        BigDecimal totalCosts = costRecordRepository.findAll().stream()
            .map(cost -> cost.getAmount())
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate total revenue from all sales orders
        BigDecimal totalRevenue = salesOrderRepository.findAll().stream()
            .filter(order -> order.getTotalRevenue() != null)
            .map(order -> order.getTotalRevenue())
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate profit
        BigDecimal profit = totalRevenue.subtract(totalCosts);

        // Calculate profit margin (if revenue > 0)
        BigDecimal profitMargin = BigDecimal.ZERO;
        if (totalRevenue.compareTo(BigDecimal.ZERO) > 0) {
            profitMargin = profit.divide(totalRevenue, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"));
        }

        // Count active batches
        long activeBatches = batchRepository.findAll().stream()
            .filter(batch -> batch.getIsActive() != null && batch.getIsActive())
            .count();

        // Count total batches
        long totalBatches = batchRepository.count();

        // Build response
        dashboard.put("totalCosts", totalCosts);
        dashboard.put("totalRevenue", totalRevenue);
        dashboard.put("profit", profit);
        dashboard.put("profitMarginPercent", profitMargin);
        dashboard.put("activeBatches", activeBatches);
        dashboard.put("totalBatches", totalBatches);
        dashboard.put("costRecordCount", costRecordRepository.count());
        dashboard.put("salesOrderCount", salesOrderRepository.count());

        return ResponseEntity.ok().body(dashboard);
    }

    /**
     * GET /api/dashboard/financial/export : Export financial dashboard data as CSV.
     *
     * @return CSV file with cost and revenue breakdown.
     */
    @GetMapping("/financial/export")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '" + AuthoritiesConstants.MANAGER + "')")
    public ResponseEntity<String> exportFinancialData() {
        LOG.debug("REST request to export financial data as CSV");

        StringBuilder csv = new StringBuilder();

        // Header section
        csv.append("Financial Report Export\n");
        csv.append("Generated,").append(java.time.LocalDateTime.now()).append("\n\n");

        // Summary section
        BigDecimal totalCosts = costRecordRepository.findAll().stream()
            .map(cost -> cost.getAmount())
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRevenue = salesOrderRepository.findAll().stream()
            .filter(order -> order.getTotalRevenue() != null)
            .map(order -> order.getTotalRevenue())
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal profit = totalRevenue.subtract(totalCosts);

        csv.append("FINANCIAL SUMMARY\n");
        csv.append("Metric,Amount (USD)\n");
        csv.append("Total Revenue,").append(totalRevenue).append("\n");
        csv.append("Total Costs,").append(totalCosts).append("\n");
        csv.append("Net Profit,").append(profit).append("\n\n");

        // Cost breakdown
        csv.append("COST BREAKDOWN\n");
        csv.append("Date,Category,Description,Batch,Amount (USD)\n");

        List<CostRecord> costs = costRecordRepository.findAll();
        for (CostRecord cost : costs) {
            csv.append(cost.getRecordDate() != null ? cost.getRecordDate().toString() : "N/A").append(",");
            csv.append(escapeCSV(cost.getCategory() != null ? cost.getCategory().toString() : "N/A")).append(",");
            csv.append(escapeCSV(cost.getDescription())).append(",");
            csv.append(cost.getBatch() != null ? escapeCSV(cost.getBatch().getBatchCode()) : "N/A").append(",");
            csv.append(cost.getAmount() != null ? cost.getAmount().toString() : "0").append("\n");
        }

        // Revenue breakdown
        csv.append("\nREVENUE BREAKDOWN\n");
        csv.append("Order Code,Order Date,Customer,Total Weight (kg),Total Revenue (USD),Status\n");

        List<SalesOrder> sales = salesOrderRepository.findAll();
        for (SalesOrder order : sales) {
            csv.append(escapeCSV(order.getOrderCode())).append(",");
            csv.append(order.getOrderDate() != null ? order.getOrderDate().toString() : "N/A").append(",");
            csv.append(order.getCustomer() != null ? escapeCSV(order.getCustomer().getName()) : "N/A").append(",");
            csv.append(order.getTotalWeight() != null ? order.getTotalWeight().toString() : "0").append(",");
            csv.append(order.getTotalRevenue() != null ? order.getTotalRevenue().toString() : "0").append(",");
            csv.append(order.getStatus() != null ? order.getStatus().toString() : "N/A").append("\n");
        }

        // Return CSV
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "financial-report-" +
            java.time.LocalDate.now().toString() + ".csv");

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
     * GET /api/dashboard/overview : Get main dashboard overview KPIs.
     *
     * @return the ResponseEntity with status 200 (OK) and body containing overview metrics.
     */
    @GetMapping("/overview")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getOverviewDashboard() {
        LOG.debug("REST request to get overview dashboard");

        Map<String, Object> dashboard = new HashMap<>();

        // Count active batches
        long activeBatches = batchRepository.findAll().stream()
            .filter(batch -> batch.getIsActive() != null && batch.getIsActive())
            .count();

        // Count total batches
        long totalBatches = batchRepository.count();

        // Build response
        dashboard.put("activeBatches", activeBatches);
        dashboard.put("totalBatches", totalBatches);

        return ResponseEntity.ok().body(dashboard);
    }
}
