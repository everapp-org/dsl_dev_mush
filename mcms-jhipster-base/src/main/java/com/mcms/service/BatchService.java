package com.mcms.service;

import com.mcms.domain.Batch;
import com.mcms.domain.FlushCycle;
import com.mcms.domain.PhaseExecution;
import com.mcms.domain.enumeration.PhaseName;
import com.mcms.repository.BatchRepository;
import com.mcms.repository.FlushCycleRepository;
import com.mcms.repository.PhaseExecutionRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing batch lifecycle operations.
 */
@Service
@Transactional
public class BatchService {

    private static final Logger LOG = LoggerFactory.getLogger(BatchService.class);

    private final BatchRepository batchRepository;
    private final PhaseExecutionRepository phaseExecutionRepository;
    private final FlushCycleRepository flushCycleRepository;

    public BatchService(
        BatchRepository batchRepository,
        PhaseExecutionRepository phaseExecutionRepository,
        FlushCycleRepository flushCycleRepository
    ) {
        this.batchRepository = batchRepository;
        this.phaseExecutionRepository = phaseExecutionRepository;
        this.flushCycleRepository = flushCycleRepository;
    }

    /**
     * Transition a batch to the next phase, creating a PhaseExecution record.
     *
     * @param batchId the batch ID
     * @param targetPhase the target phase to transition to
     * @return the updated batch
     */
    public Batch transitionPhase(Long batchId, PhaseName targetPhase) {
        LOG.debug("Transitioning batch {} to phase {}", batchId, targetPhase);

        Optional<Batch> batchOpt = batchRepository.findById(batchId);
        if (batchOpt.isEmpty()) {
            throw new IllegalArgumentException("Batch not found: " + batchId);
        }

        Batch batch = batchOpt.get();
        PhaseName oldPhase = batch.getCurrentPhase();

        // Close previous phase execution if exists
        List<PhaseExecution> activePhases = phaseExecutionRepository.findByBatchAndEndDateIsNull(batch);
        for (PhaseExecution pe : activePhases) {
            pe.setEndDate(LocalDate.now());
            // Calculate actual duration if start date exists
            if (pe.getStartDate() != null) {
                long days = java.time.temporal.ChronoUnit.DAYS.between(pe.getStartDate(), LocalDate.now());
                pe.setActualDurationDays((int) days);
            }
            phaseExecutionRepository.save(pe);
        }

        // Update batch current phase
        batch.setCurrentPhase(targetPhase);
        batch = batchRepository.save(batch);

        // Create new phase execution record
        PhaseExecution newPhase = new PhaseExecution();
        newPhase.setBatch(batch);
        newPhase.setPhase(targetPhase);
        newPhase.setStartDate(LocalDate.now());

        // Calculate sequence order (count existing phases + 1)
        long existingPhaseCount = phaseExecutionRepository.countByBatch(batch);
        newPhase.setSequenceOrder((int) existingPhaseCount + 1);

        phaseExecutionRepository.save(newPhase);

        LOG.info("Batch {} transitioned from {} to {}", batchId, oldPhase, targetPhase);
        return batch;
    }

    /**
     * Complete a batch, calculating final yields and marking it inactive.
     *
     * @param batchId the batch ID
     * @param completionNote optional completion notes
     * @return the completed batch
     */
    public Batch completeBatch(Long batchId, String completionNote) {
        LOG.debug("Completing batch {}", batchId);

        Optional<Batch> batchOpt = batchRepository.findById(batchId);
        if (batchOpt.isEmpty()) {
            throw new IllegalArgumentException("Batch not found: " + batchId);
        }

        Batch batch = batchOpt.get();

        // Calculate cumulative yield from all flush cycles
        BigDecimal totalYield = calculateCumulativeYield(batchId);
        batch.setActualTotalYieldKg(totalYield);

        // Calculate biological efficiency
        BigDecimal biologicalEfficiency = calculateBiologicalEfficiency(batch);
        batch.setBiologicalEfficiencyPercent(biologicalEfficiency);

        // Set completion fields
        batch.setEndDate(LocalDate.now());
        batch.setIsActive(false);
        batch.setCurrentPhase(PhaseName.COMPLETED);

        if (completionNote != null && !completionNote.isEmpty()) {
            batch.setCompletionNote(completionNote);
        }

        // Close any open phase executions
        List<PhaseExecution> activePhases = phaseExecutionRepository.findByBatchAndEndDateIsNull(batch);
        for (PhaseExecution pe : activePhases) {
            pe.setEndDate(LocalDate.now());
            if (pe.getStartDate() != null) {
                long days = java.time.temporal.ChronoUnit.DAYS.between(pe.getStartDate(), LocalDate.now());
                pe.setActualDurationDays((int) days);
            }
            phaseExecutionRepository.save(pe);
        }

        batch = batchRepository.save(batch);

        LOG.info("Batch {} completed. Total yield: {} kg, Biological efficiency: {}%",
                 batchId, totalYield, biologicalEfficiency);
        return batch;
    }

    /**
     * Calculate cumulative yield from all flush cycles for a batch.
     *
     * @param batchId the batch ID
     * @return the total yield in kg
     */
    public BigDecimal calculateCumulativeYield(Long batchId) {
        List<FlushCycle> flushCycles = flushCycleRepository.findByBatchId(batchId);

        BigDecimal totalYield = BigDecimal.ZERO;
        for (FlushCycle flush : flushCycles) {
            if (flush.getYieldKg() != null) {
                totalYield = totalYield.add(flush.getYieldKg());
            }
        }

        LOG.debug("Calculated cumulative yield for batch {}: {} kg from {} flush cycles",
                  batchId, totalYield, flushCycles.size());
        return totalYield;
    }

    /**
     * Calculate biological efficiency: (actual yield / dry substrate weight) × 100
     *
     * @param batch the batch
     * @return the biological efficiency percentage
     */
    public BigDecimal calculateBiologicalEfficiency(Batch batch) {
        if (batch.getSubstrateWeightKg() == null ||
            batch.getSubstrateWeightKg().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal actualYield = batch.getActualTotalYieldKg();
        if (actualYield == null) {
            actualYield = BigDecimal.ZERO;
        }

        // Formula: (actual yield / substrate weight) × 100
        BigDecimal efficiency = actualYield
            .divide(batch.getSubstrateWeightKg(), 4, RoundingMode.HALF_UP)
            .multiply(new BigDecimal("100"))
            .setScale(2, RoundingMode.HALF_UP);

        LOG.debug("Calculated biological efficiency for batch {}: {}% (yield: {} kg, substrate: {} kg)",
                  batch.getId(), efficiency, actualYield, batch.getSubstrateWeightKg());
        return efficiency;
    }

    /**
     * Update cumulative yield for a batch (called when flush cycles are modified).
     *
     * @param batchId the batch ID
     * @return the updated batch
     */
    public Batch updateCumulativeYield(Long batchId) {
        Optional<Batch> batchOpt = batchRepository.findById(batchId);
        if (batchOpt.isEmpty()) {
            throw new IllegalArgumentException("Batch not found: " + batchId);
        }

        Batch batch = batchOpt.get();
        BigDecimal totalYield = calculateCumulativeYield(batchId);
        batch.setActualTotalYieldKg(totalYield);

        // Recalculate biological efficiency with new yield
        BigDecimal biologicalEfficiency = calculateBiologicalEfficiency(batch);
        batch.setBiologicalEfficiencyPercent(biologicalEfficiency);

        return batchRepository.save(batch);
    }
}
