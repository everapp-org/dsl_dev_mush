package com.mcms.web.rest.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for batch traceability report (supplier to customer chain).
 */
public class TraceabilityReport {

    private String batchCode;
    private LocalDate startDate;
    private String currentPhase;
    private List<MaterialUsageInfo> materialsUsed = new ArrayList<>();
    private List<SalesInfo> sales = new ArrayList<>();

    public TraceabilityReport() {
    }

    public TraceabilityReport(String batchCode, LocalDate startDate, String currentPhase) {
        this.batchCode = batchCode;
        this.startDate = startDate;
        this.currentPhase = currentPhase;
    }

    // Getters and setters
    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public String getCurrentPhase() {
        return currentPhase;
    }

    public void setCurrentPhase(String currentPhase) {
        this.currentPhase = currentPhase;
    }

    public List<MaterialUsageInfo> getMaterialsUsed() {
        return materialsUsed;
    }

    public void setMaterialsUsed(List<MaterialUsageInfo> materialsUsed) {
        this.materialsUsed = materialsUsed;
    }

    public List<SalesInfo> getSales() {
        return sales;
    }

    public void setSales(List<SalesInfo> sales) {
        this.sales = sales;
    }

    /**
     * Information about material usage (from suppliers).
     */
    public static class MaterialUsageInfo {
        private String materialName;
        private String supplierName;
        private String supplyOrderCode;
        private String lotCode;
        private java.math.BigDecimal quantityUsed;
        private String unit;
        private LocalDate usageDate;

        public MaterialUsageInfo() {
        }

        // Getters and setters
        public String getMaterialName() {
            return materialName;
        }

        public void setMaterialName(String materialName) {
            this.materialName = materialName;
        }

        public String getSupplierName() {
            return supplierName;
        }

        public void setSupplierName(String supplierName) {
            this.supplierName = supplierName;
        }

        public String getSupplyOrderCode() {
            return supplyOrderCode;
        }

        public void setSupplyOrderCode(String supplyOrderCode) {
            this.supplyOrderCode = supplyOrderCode;
        }

        public String getLotCode() {
            return lotCode;
        }

        public void setLotCode(String lotCode) {
            this.lotCode = lotCode;
        }

        public java.math.BigDecimal getQuantityUsed() {
            return quantityUsed;
        }

        public void setQuantityUsed(java.math.BigDecimal quantityUsed) {
            this.quantityUsed = quantityUsed;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public LocalDate getUsageDate() {
            return usageDate;
        }

        public void setUsageDate(LocalDate usageDate) {
            this.usageDate = usageDate;
        }
    }

    /**
     * Information about sales (to customers).
     */
    public static class SalesInfo {
        private String customerName;
        private String salesOrderCode;
        private String productName;
        private java.math.BigDecimal quantity;
        private String unit;
        private LocalDate orderDate;
        private LocalDate deliveryDate;

        public SalesInfo() {
        }

        // Getters and setters
        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public String getSalesOrderCode() {
            return salesOrderCode;
        }

        public void setSalesOrderCode(String salesOrderCode) {
            this.salesOrderCode = salesOrderCode;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public java.math.BigDecimal getQuantity() {
            return quantity;
        }

        public void setQuantity(java.math.BigDecimal quantity) {
            this.quantity = quantity;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public LocalDate getOrderDate() {
            return orderDate;
        }

        public void setOrderDate(LocalDate orderDate) {
            this.orderDate = orderDate;
        }

        public LocalDate getDeliveryDate() {
            return deliveryDate;
        }

        public void setDeliveryDate(LocalDate deliveryDate) {
            this.deliveryDate = deliveryDate;
        }
    }
}
