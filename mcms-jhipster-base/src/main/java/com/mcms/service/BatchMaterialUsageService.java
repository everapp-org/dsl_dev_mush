package com.mcms.service;

import com.mcms.domain.BatchMaterialUsage;
import com.mcms.domain.InventoryLot;
import com.mcms.domain.StockMovement;
import com.mcms.domain.enumeration.StockMovementType;
import com.mcms.repository.BatchMaterialUsageRepository;
import com.mcms.web.rest.errors.BadRequestAlertException;
import java.time.Instant;
import java.time.ZoneId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing batch material usages and automatically creating stock movements.
 *
 * When a batch material usage is recorded, this service automatically:
 * 1. Creates a corresponding CONSUMPTION stock movement
 * 2. Updates the inventory lot quantity (via StockMovementService)
 */
@Service
@Transactional
public class BatchMaterialUsageService {

    private static final Logger LOG = LoggerFactory.getLogger(BatchMaterialUsageService.class);

    private final BatchMaterialUsageRepository batchMaterialUsageRepository;
    private final StockMovementService stockMovementService;

    public BatchMaterialUsageService(
        BatchMaterialUsageRepository batchMaterialUsageRepository,
        StockMovementService stockMovementService
    ) {
        this.batchMaterialUsageRepository = batchMaterialUsageRepository;
        this.stockMovementService = stockMovementService;
    }

    /**
     * Create a new batch material usage and automatically create a consumption stock movement.
     *
     * @param batchMaterialUsage the batch material usage to create
     * @return the created batch material usage
     */
    public BatchMaterialUsage createBatchMaterialUsage(BatchMaterialUsage batchMaterialUsage) {
        LOG.debug("Creating batch material usage: {}", batchMaterialUsage);

        // Validate that the batch material usage doesn't already have an ID
        if (batchMaterialUsage.getId() != null) {
            throw new BadRequestAlertException("A new batch material usage cannot already have an ID", "batchMaterialUsage", "idexists");
        }

        // Validate required fields
        if (batchMaterialUsage.getBatch() == null || batchMaterialUsage.getBatch().getId() == null) {
            throw new BadRequestAlertException("Batch material usage must reference a valid batch", "batchMaterialUsage", "invalidbatch");
        }
        if (batchMaterialUsage.getInventoryLot() == null || batchMaterialUsage.getInventoryLot().getId() == null) {
            throw new BadRequestAlertException("Batch material usage must reference a valid inventory lot", "batchMaterialUsage", "invalidlot");
        }
        if (batchMaterialUsage.getMaterial() == null || batchMaterialUsage.getMaterial().getId() == null) {
            throw new BadRequestAlertException("Batch material usage must reference a valid material", "batchMaterialUsage", "invalidmaterial");
        }

        // Save the batch material usage first
        BatchMaterialUsage result = batchMaterialUsageRepository.save(batchMaterialUsage);

        // Create a corresponding CONSUMPTION stock movement to update inventory
        StockMovement stockMovement = new StockMovement();
        stockMovement.setMovementDate(
            batchMaterialUsage.getUsageDate() != null
                ? batchMaterialUsage.getUsageDate().atStartOfDay(ZoneId.systemDefault()).toInstant()
                : Instant.now()
        );
        stockMovement.setMovementType(StockMovementType.CONSUMPTION);
        stockMovement.setQuantity(batchMaterialUsage.getQuantityUsed());
        stockMovement.setUnit(batchMaterialUsage.getUnit());
        stockMovement.setReference("BATCH-" + batchMaterialUsage.getBatch().getId());
        stockMovement.setReason(batchMaterialUsage.getPurpose());
        stockMovement.setPerformedBy("SYSTEM");
        stockMovement.setNote("Auto-generated from batch material usage #" + result.getId());

        // Set relationships
        InventoryLot inventoryLot = new InventoryLot();
        inventoryLot.setId(batchMaterialUsage.getInventoryLot().getId());
        stockMovement.setInventoryLot(inventoryLot);
        stockMovement.setBatch(batchMaterialUsage.getBatch());

        // Create the stock movement (which will automatically update the inventory lot quantity)
        stockMovementService.createStockMovement(stockMovement);

        LOG.debug("Batch material usage created successfully with automatic stock movement");

        return result;
    }

    /**
     * Get a batch material usage by ID.
     *
     * @param id the ID of the batch material usage
     * @return the batch material usage if found
     */
    @Transactional(readOnly = true)
    public java.util.Optional<BatchMaterialUsage> findOne(Long id) {
        return batchMaterialUsageRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete a batch material usage by ID.
     * Note: This does NOT reverse the stock movement or inventory adjustment.
     *
     * @param id the ID of the batch material usage to delete
     */
    public void delete(Long id) {
        LOG.warn("Deleting batch material usage {}. Note: This does NOT reverse the stock movement.", id);
        batchMaterialUsageRepository.deleteById(id);
    }
}
