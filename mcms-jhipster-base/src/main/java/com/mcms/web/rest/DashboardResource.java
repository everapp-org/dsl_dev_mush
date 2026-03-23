package com.mcms.web.rest;

import com.mcms.domain.CostRecord;
import com.mcms.domain.SalesOrder;
import com.mcms.repository.BatchRepository;
import com.mcms.repository.CostRecordRepository;
import com.mcms.repository.EnvironmentalAlertRepository;
import com.mcms.repository.RoomRepository;
import com.mcms.repository.SalesOrderRepository;
import com.mcms.repository.SensorRepository;
import com.mcms.repository.StrainRepository;
import com.mcms.repository.TaskRepository;
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
    private final StrainRepository strainRepository;
    private final RoomRepository roomRepository;
    private final TaskRepository taskRepository;
    private final SensorRepository sensorRepository;
    private final EnvironmentalAlertRepository environmentalAlertRepository;

    public DashboardResource(
        CostRecordRepository costRecordRepository,
        SalesOrderRepository salesOrderRepository,
        BatchRepository batchRepository,
        StrainRepository strainRepository,
        RoomRepository roomRepository,
        TaskRepository taskRepository,
        SensorRepository sensorRepository,
        EnvironmentalAlertRepository environmentalAlertRepository
    ) {
        this.costRecordRepository = costRecordRepository;
        this.salesOrderRepository = salesOrderRepository;
        this.batchRepository = batchRepository;
        this.strainRepository = strainRepository;
        this.roomRepository = roomRepository;
        this.taskRepository = taskRepository;
        this.sensorRepository = sensorRepository;
        this.environmentalAlertRepository = environmentalAlertRepository;
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

        // Performance optimized: Calculate totals with database aggregation queries
        BigDecimal totalCosts = costRecordRepository.calculateTotalCosts();
        BigDecimal totalRevenue = salesOrderRepository.calculateTotalRevenue();

        // Calculate profit
        BigDecimal profit = totalRevenue.subtract(totalCosts);

        // Calculate profit margin (if revenue > 0)
        BigDecimal profitMargin = BigDecimal.ZERO;
        if (totalRevenue.compareTo(BigDecimal.ZERO) > 0) {
            profitMargin = profit.divide(totalRevenue, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"));
        }

        // Performance optimized: Count active batches with database query
        long activeBatches = batchRepository.countActiveBatches();
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

        // Chart data: Cost breakdown by category
        Map<String, BigDecimal> costsByCategory = new HashMap<>();
        costRecordRepository.findAll().forEach(cost -> {
            String category = cost.getCategory() != null ? cost.getCategory().toString() : "OTHER";
            costsByCategory.merge(category, cost.getAmount(), BigDecimal::add);
        });
        dashboard.put("costBreakdown", costsByCategory);

        // Chart data: Monthly revenue trend (last 12 months)
        List<Map<String, Object>> revenueByMonth = new java.util.ArrayList<>();
        Map<String, BigDecimal> monthlyRevenue = new HashMap<>();
        salesOrderRepository.findAll().stream()
            .filter(order -> order.getOrderDate() != null && order.getTotalRevenue() != null)
            .forEach(order -> {
                String month = order.getOrderDate().getYear() + "-" +
                    String.format("%02d", order.getOrderDate().getMonthValue());
                monthlyRevenue.merge(month, order.getTotalRevenue(), BigDecimal::add);
            });
        monthlyRevenue.forEach((month, revenue) -> {
            Map<String, Object> entry = new HashMap<>();
            entry.put("month", month);
            entry.put("revenue", revenue);
            revenueByMonth.add(entry);
        });
        revenueByMonth.sort((a, b) -> ((String)a.get("month")).compareTo((String)b.get("month")));
        dashboard.put("revenueTrend", revenueByMonth);

        // Chart data: Monthly profitability (revenue - costs per month)
        List<Map<String, Object>> profitabilityByMonth = new java.util.ArrayList<>();
        Map<String, BigDecimal> monthlyCosts = new HashMap<>();
        costRecordRepository.findAll().stream()
            .filter(cost -> cost.getRecordDate() != null && cost.getAmount() != null)
            .forEach(cost -> {
                String month = cost.getRecordDate().getYear() + "-" +
                    String.format("%02d", cost.getRecordDate().getMonthValue());
                monthlyCosts.merge(month, cost.getAmount(), BigDecimal::add);
            });

        // Combine revenue and costs by month
        java.util.Set<String> allMonths = new java.util.HashSet<>();
        allMonths.addAll(monthlyRevenue.keySet());
        allMonths.addAll(monthlyCosts.keySet());

        allMonths.forEach(month -> {
            Map<String, Object> entry = new HashMap<>();
            BigDecimal revenue = monthlyRevenue.getOrDefault(month, BigDecimal.ZERO);
            BigDecimal costs = monthlyCosts.getOrDefault(month, BigDecimal.ZERO);
            BigDecimal monthProfit = revenue.subtract(costs);

            entry.put("month", month);
            entry.put("revenue", revenue);
            entry.put("costs", costs);
            entry.put("profit", monthProfit);
            profitabilityByMonth.add(entry);
        });
        profitabilityByMonth.sort((a, b) -> ((String)a.get("month")).compareTo((String)b.get("month")));
        dashboard.put("profitability", profitabilityByMonth);

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

        // Performance optimized: Use database aggregation queries instead of loading all records
        long activeBatches = batchRepository.countActiveBatches();
        long totalBatches = batchRepository.count();
        long totalStrains = strainRepository.count();
        long totalRooms = roomRepository.count();
        long totalTasks = taskRepository.countPendingTasks();

        // Build response
        dashboard.put("activeBatches", activeBatches);
        dashboard.put("totalBatches", totalBatches);
        dashboard.put("totalStrains", totalStrains);
        dashboard.put("totalRooms", totalRooms);
        dashboard.put("totalTasks", totalTasks);

        return ResponseEntity.ok().body(dashboard);
    }

    /**
     * GET /api/dashboard/production : Get production dashboard with batch phase distribution.
     *
     * @return the ResponseEntity with status 200 (OK) and body containing production metrics and chart data.
     */
    @GetMapping("/production")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getProductionDashboard() {
        LOG.debug("REST request to get production dashboard");

        Map<String, Object> dashboard = new HashMap<>();

        // Performance optimized: Use database GROUP BY query instead of loading all batches
        long activeBatchCount = batchRepository.countActiveBatches();
        List<Object[]> phaseResults = batchRepository.countActiveBatchesByPhase();

        // Convert query results to Map
        Map<String, Long> batchesByPhase = new HashMap<>();
        phaseResults.forEach(result -> {
            String phase = result[0] != null ? result[0].toString() : "UNKNOWN";
            Long count = result[1] != null ? ((Number) result[1]).longValue() : 0L;
            batchesByPhase.put(phase, count);
        });

        // Build response
        dashboard.put("activeBatchCount", activeBatchCount);
        dashboard.put("batchesByPhase", batchesByPhase);

        return ResponseEntity.ok().body(dashboard);
    }

    /**
     * GET /api/dashboard/environmental : Get environmental monitoring dashboard with room and sensor status.
     *
     * @return the ResponseEntity with status 200 (OK) and body containing environmental monitoring data.
     */
    @GetMapping("/environmental")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getEnvironmentalDashboard() {
        LOG.debug("REST request to get environmental monitoring dashboard");

        Map<String, Object> dashboard = new HashMap<>();

        // Get all rooms with their associated data
        List<Map<String, Object>> roomsData = roomRepository.findAll().stream()
            .map(room -> {
                Map<String, Object> roomData = new HashMap<>();
                roomData.put("id", room.getId());
                roomData.put("name", room.getName());
                roomData.put("code", room.getCode());
                roomData.put("roomType", room.getRoomType());
                roomData.put("status", room.getStatus());
                roomData.put("capacityBags", room.getCapacityBags());
                roomData.put("currentOccupancy", room.getCurrentOccupancy());
                roomData.put("areaSqM", room.getAreaSqM());
                roomData.put("hasHVAC", room.getHasHVAC());
                roomData.put("hasMisting", room.getHasMisting());

                // Count sensors in this room
                long sensorCount = sensorRepository.findByRoomId(room.getId()).size();
                long activeSensorCount = sensorRepository.findByRoomId(room.getId()).stream()
                    .filter(sensor -> "ACTIVE".equals(sensor.getStatus()))
                    .count();
                roomData.put("sensorCount", sensorCount);
                roomData.put("activeSensorCount", activeSensorCount);

                // Count alerts for this room
                List<com.mcms.domain.EnvironmentalAlert> alerts = environmentalAlertRepository.findByRoomId(room.getId());
                long totalAlerts = alerts.size();
                long unacknowledgedAlerts = alerts.stream()
                    .filter(alert -> !alert.getAcknowledged())
                    .count();
                long criticalAlerts = alerts.stream()
                    .filter(alert -> !alert.getAcknowledged() && "CRITICAL".equals(alert.getSeverity()))
                    .count();
                roomData.put("totalAlerts", totalAlerts);
                roomData.put("unacknowledgedAlerts", unacknowledgedAlerts);
                roomData.put("criticalAlerts", criticalAlerts);

                return roomData;
            })
            .toList();

        // Summary statistics
        long totalRooms = roomRepository.count();
        long activeRooms = roomRepository.findAll().stream()
            .filter(room -> "ACTIVE".equals(room.getStatus()))
            .count();
        long totalSensors = sensorRepository.count();
        long activeSensors = sensorRepository.findAll().stream()
            .filter(sensor -> "ACTIVE".equals(sensor.getStatus()))
            .count();
        long totalAlerts = environmentalAlertRepository.count();
        long unacknowledgedAlerts = environmentalAlertRepository.findAll().stream()
            .filter(alert -> !alert.getAcknowledged())
            .count();
        long criticalAlerts = environmentalAlertRepository.findAll().stream()
            .filter(alert -> !alert.getAcknowledged() && "CRITICAL".equals(alert.getSeverity()))
            .count();

        dashboard.put("rooms", roomsData);
        dashboard.put("totalRooms", totalRooms);
        dashboard.put("activeRooms", activeRooms);
        dashboard.put("totalSensors", totalSensors);
        dashboard.put("activeSensors", activeSensors);
        dashboard.put("totalAlerts", totalAlerts);
        dashboard.put("unacknowledgedAlerts", unacknowledgedAlerts);
        dashboard.put("criticalAlerts", criticalAlerts);

        return ResponseEntity.ok().body(dashboard);
    }
}
