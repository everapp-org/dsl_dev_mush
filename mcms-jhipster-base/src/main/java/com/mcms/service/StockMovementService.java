package com.mcms.service;

import com.mcms.domain.InventoryLot;
import com.mcms.domain.StockMovement;
import com.mcms.domain.enumeration.StockMovementType;
import com.mcms.repository.InventoryLotRepository;
import com.mcms.repository.StockMovementRepository;
import com.mcms.web.rest.errors.BadRequestAlertException;
import java.math.BigDecimal;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing stock movements and automatically updating inventory lot quantities.
 *
 * Business Rules:
 * - RECEIPT: Increases quantityOnHand (material received from supplier)
 * - CONSUMPTION: Decreases quantityOnHand (material consumed by batch)
 * - WASTE: Decreases quantityOnHand (material lost/expired/discarded)
 * - ADJUSTMENT_IN: Increases quantityOnHand (inventory correction - increase)
 * - ADJUSTMENT_OUT: Decreases quantityOnHand (inventory correction - decrease)
 * - RETURN_TO_SUPPLIER: Decreases quantityOnHand (material returned to supplier)
 */
@Service
@Transactional
public class StockMovementService {

    private static final Logger LOG = LoggerFactory.getLogger(StockMovementService.class);

    private final StockMovementRepository stockMovementRepository;
    private final InventoryLotRepository inventoryLotRepository;

    public StockMovementService(
        StockMovementRepository stockMovementRepository,
        InventoryLotRepository inventoryLotRepository
    ) {
        this.stockMovementRepository = stockMovementRepository;
        this.inventoryLotRepository = inventoryLotRepository;
    }

    /**
     * Create a new stock movement and automatically update the inventory lot quantity.
     *
     * @param stockMovement the stock movement to create
     * @return the created stock movement
     */
    public StockMovement createStockMovement(StockMovement stockMovement) {
        LOG.debug("Creating stock movement: {}", stockMovement);

        // Validate that the stock movement doesn't already have an ID
        if (stockMovement.getId() != null) {
            throw new BadRequestAlertException("A new stock movement cannot already have an ID", "stockMovement", "idexists");
        }

        // Set movement date if not provided
        if (stockMovement.getMovementDate() == null) {
            stockMovement.setMovementDate(Instant.now());
        }

        // Get the inventory lot
        InventoryLot inventoryLot = stockMovement.getInventoryLot();
        if (inventoryLot == null || inventoryLot.getId() == null) {
            throw new BadRequestAlertException("Stock movement must reference a valid inventory lot", "stockMovement", "invalidlot");
        }

        // Reload the inventory lot to ensure we have the latest data
        inventoryLot = inventoryLotRepository
            .findById(inventoryLot.getId())
            .orElseThrow(() ->
                new BadRequestAlertException("Inventory lot not found", "stockMovement", "lotnotfound")
            );

        // Update the inventory lot quantity based on movement type
        BigDecimal currentQuantity = inventoryLot.getQuantityOnHand();
        BigDecimal movementQuantity = stockMovement.getQuantity();
        BigDecimal newQuantity = currentQuantity;

        StockMovementType movementType = stockMovement.getMovementType();

        if (movementType == StockMovementType.RECEIPT || movementType == StockMovementType.ADJUSTMENT_IN) {
            // Add to inventory
            newQuantity = currentQuantity.add(movementQuantity);
            LOG.debug("Adding {} to inventory lot {}. Current: {}, New: {}",
                movementQuantity, inventoryLot.getId(), currentQuantity, newQuantity);
        } else if (movementType == StockMovementType.CONSUMPTION ||
                   movementType == StockMovementType.WASTE ||
                   movementType == StockMovementType.ADJUSTMENT_OUT ||
                   movementType == StockMovementType.RETURN_TO_SUPPLIER) {
            // Subtract from inventory
            newQuantity = currentQuantity.subtract(movementQuantity);
            LOG.debug("Subtracting {} from inventory lot {}. Current: {}, New: {}",
                movementQuantity, inventoryLot.getId(), currentQuantity, newQuantity);

            // Validate sufficient quantity
            if (newQuantity.compareTo(BigDecimal.ZERO) < 0) {
                throw new BadRequestAlertException(
                    String.format("Insufficient quantity. Available: %s, Requested: %s",
                        currentQuantity, movementQuantity),
                    "stockMovement",
                    "insufficientquantity"
                );
            }
        }

        // Update the inventory lot quantity
        inventoryLot.setQuantityOnHand(newQuantity);

        // Set exhausted flag if quantity reaches zero
        if (newQuantity.compareTo(BigDecimal.ZERO) == 0) {
            inventoryLot.setIsExhausted(true);
            LOG.debug("Inventory lot {} is now exhausted", inventoryLot.getId());
        } else if (newQuantity.compareTo(BigDecimal.ZERO) > 0 && Boolean.TRUE.equals(inventoryLot.getIsExhausted())) {
            inventoryLot.setIsExhausted(false);
            LOG.debug("Inventory lot {} is no longer exhausted", inventoryLot.getId());
        }

        // Save the updated inventory lot
        inventoryLotRepository.save(inventoryLot);

        // Save the stock movement
        StockMovement result = stockMovementRepository.save(stockMovement);

        LOG.debug("Stock movement created successfully. Inventory lot {} quantity updated from {} to {}",
            inventoryLot.getId(), currentQuantity, newQuantity);

        return result;
    }

    /**
     * Get a stock movement by ID.
     *
     * @param id the ID of the stock movement
     * @return the stock movement if found
     */
    @Transactional(readOnly = true)
    public java.util.Optional<StockMovement> findOne(Long id) {
        return stockMovementRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete a stock movement by ID.
     * Note: This does NOT reverse the inventory adjustment - use with caution.
     *
     * @param id the ID of the stock movement to delete
     */
    public void delete(Long id) {
        LOG.warn("Deleting stock movement {}. Note: This does NOT reverse the inventory adjustment.", id);
        stockMovementRepository.deleteById(id);
    }
}
