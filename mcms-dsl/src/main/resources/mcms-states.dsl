// Mushroom Cultivation Management System - State Machine Definitions
// Source of truth for Batch lifecycle behavior
// Parsed by KernelDSL.g4 grammar, generates code via McmsCodeGenerator

domain MushroomFarm {
  description "Mushroom Cultivation Management System - Production Domain"

  level production {
    model Batch {
      description "Production batch lifecycle with 10-phase enforcement. Central aggregate root linking materials, phases, harvests, sales, and costs."

      fields {
        id: Long
        batchCode: String
        currentPhase: PhaseName
        startDate: LocalDate
        endDate: LocalDate
        numberOfBags: Integer
        substrateWeightKg: BigDecimal
        spawnWeightKg: BigDecimal
        targetYieldKg: BigDecimal
        actualTotalYieldKg: BigDecimal
        biologicalEfficiencyPercent: BigDecimal
        isContaminated: Boolean
        isActive: Boolean
        flushNumber: Integer
        maxFlushes: Integer
      }

      states {
        INOCULATION
        EARLY_COLONIZATION
        FULL_COLONIZATION
        CONSOLIDATION
        FRUITING_TRIGGER
        PRIMORDIA
        FRUITING_BODY_GROWTH
        HARVEST
        REHYDRATION_PAUSE
        COMPLETED
      }

      transitions {
        // Phase 1: Inoculation complete, mycelium starts spreading
        INOCULATION -> EARLY_COLONIZATION on startColonization
          if substrateFullyInoculated
          emits ColonizationStarted
          do logPhaseStart, updatePhaseExecution

        // Phase 2: Full mycelium coverage achieved
        EARLY_COLONIZATION -> FULL_COLONIZATION on confirmFullColonization
          if myceliumCoverageComplete
          emits FullColonizationReached
          do logPhaseStart, updatePhaseExecution

        // Phase 3: Consolidation period begins
        FULL_COLONIZATION -> CONSOLIDATION on startConsolidation
          emits ConsolidationStarted
          do logPhaseStart, updatePhaseExecution

        // Phase 4: Environmental shock triggers fruiting
        CONSOLIDATION -> FRUITING_TRIGGER on triggerFruiting
          if consolidationPeriodComplete
          emits FruitingTriggered
          do logPhaseStart, updatePhaseExecution, applyEnvironmentalShock

        // Phase 5: Pin formation detected
        FRUITING_TRIGGER -> PRIMORDIA on confirmPrimordia
          if pinFormationDetected
          emits PrimordiaFormed
          do logPhaseStart, updatePhaseExecution

        // Phase 6: Fruiting bodies growing
        PRIMORDIA -> FRUITING_BODY_GROWTH on startFruitingGrowth
          emits FruitingBodyGrowthStarted
          do logPhaseStart, updatePhaseExecution

        // Phase 7: Harvest time
        FRUITING_BODY_GROWTH -> HARVEST on startHarvest
          if fruitBodiesReady
          emits HarvestReady
          do logPhaseStart, updatePhaseExecution

        // Multi-flush loop: Rehydrate for another flush
        HARVEST -> REHYDRATION_PAUSE on rehydrate
          if flushNumber < maxFlushes
          emits RehydrationStarted
          do addWater, pauseProduction, updatePhaseExecution

        // Terminal: All flushes complete
        HARVEST -> COMPLETED on endBatch
          if flushNumber >= maxFlushes
          emits BatchCompleted
          do finalizeYieldCalculations, generateReport, updatePhaseExecution

        // Resume from rehydration back to fruiting
        REHYDRATION_PAUSE -> FRUITING_TRIGGER on restartFruiting
          if rehydrationComplete
          emits FruitingRestarted
          do incrementFlushNumber, logPhaseStart, updatePhaseExecution
      }

      invariants {
        // Active batch must have a current phase that is not COMPLETED
        // Completed batch must have an end date
        // Flush number must be >= 1 when in HARVEST or later
        // Contaminated batches should be flagged but lifecycle continues
      }
    }
  }
}
