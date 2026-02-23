package com.mcms.web.rest;

import com.mcms.repository.BatchRepository;
import com.mcms.repository.CostRecordRepository;
import com.mcms.repository.SalesOrderRepository;
import com.mcms.security.AuthoritiesConstants;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
