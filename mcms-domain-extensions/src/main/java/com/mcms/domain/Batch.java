package com.mcms.domain;

import com.mcms.domain.enumeration.PhaseName;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Stub entity class for Batch, mirroring key fields from the JHipster-generated entity.
 * Used by the generated BatchStateMachine to compile independently of mcms-jhipster-base.
 *
 * In production, this would be replaced by the actual JHipster entity via module dependency.
 */
public class Batch {

    private Long id;
    private String batchCode;
    private PhaseName currentPhase;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer numberOfBags;
    private BigDecimal substrateWeightKg;
    private BigDecimal spawnWeightKg;
    private BigDecimal targetYieldKg;
    private BigDecimal actualTotalYieldKg;
    private BigDecimal biologicalEfficiencyPercent;
    private Boolean isContaminated;
    private Boolean isActive;
    private Integer flushNumber;
    private Integer maxFlushes;

    public Batch() {
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public PhaseName getCurrentPhase() {
        return currentPhase;
    }

    public void setCurrentPhase(PhaseName currentPhase) {
        this.currentPhase = currentPhase;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Integer getNumberOfBags() {
        return numberOfBags;
    }

    public void setNumberOfBags(Integer numberOfBags) {
        this.numberOfBags = numberOfBags;
    }

    public BigDecimal getSubstrateWeightKg() {
        return substrateWeightKg;
    }

    public void setSubstrateWeightKg(BigDecimal substrateWeightKg) {
        this.substrateWeightKg = substrateWeightKg;
    }

    public BigDecimal getSpawnWeightKg() {
        return spawnWeightKg;
    }

    public void setSpawnWeightKg(BigDecimal spawnWeightKg) {
        this.spawnWeightKg = spawnWeightKg;
    }

    public BigDecimal getTargetYieldKg() {
        return targetYieldKg;
    }

    public void setTargetYieldKg(BigDecimal targetYieldKg) {
        this.targetYieldKg = targetYieldKg;
    }

    public BigDecimal getActualTotalYieldKg() {
        return actualTotalYieldKg;
    }

    public void setActualTotalYieldKg(BigDecimal actualTotalYieldKg) {
        this.actualTotalYieldKg = actualTotalYieldKg;
    }

    public BigDecimal getBiologicalEfficiencyPercent() {
        return biologicalEfficiencyPercent;
    }

    public void setBiologicalEfficiencyPercent(BigDecimal biologicalEfficiencyPercent) {
        this.biologicalEfficiencyPercent = biologicalEfficiencyPercent;
    }

    public Boolean getIsContaminated() {
        return isContaminated;
    }

    public void setIsContaminated(Boolean isContaminated) {
        this.isContaminated = isContaminated;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Integer getFlushNumber() {
        return flushNumber;
    }

    public void setFlushNumber(Integer flushNumber) {
        this.flushNumber = flushNumber;
    }

    public Integer getMaxFlushes() {
        return maxFlushes;
    }

    public void setMaxFlushes(Integer maxFlushes) {
        this.maxFlushes = maxFlushes;
    }

    @Override
    public String toString() {
        return "Batch{" +
            "id=" + id +
            ", batchCode='" + batchCode + '\'' +
            ", currentPhase=" + currentPhase +
            ", isActive=" + isActive +
            ", flushNumber=" + flushNumber +
            '}';
    }
}
