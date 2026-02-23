package com.mcms.web.rest;

import com.mcms.domain.StockMovement;
import com.mcms.repository.StockMovementRepository;
import com.mcms.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mcms.domain.StockMovement}.
 */
@RestController
@RequestMapping("/api/stock-movements")
@Transactional
public class StockMovementResource {

    private static final Logger LOG = LoggerFactory.getLogger(StockMovementResource.class);

    private static final String ENTITY_NAME = "stockMovement";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StockMovementRepository stockMovementRepository;

    public StockMovementResource(StockMovementRepository stockMovementRepository) {
        this.stockMovementRepository = stockMovementRepository;
    }

    /**
     * {@code POST  /stock-movements} : Create a new stockMovement.
     *
     * @param stockMovement the stockMovement to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new stockMovement, or with status {@code 400 (Bad Request)} if the stockMovement has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<StockMovement> createStockMovement(@Valid @RequestBody StockMovement stockMovement) throws URISyntaxException {
        LOG.debug("REST request to save StockMovement : {}", stockMovement);
        if (stockMovement.getId() != null) {
            throw new BadRequestAlertException("A new stockMovement cannot already have an ID", ENTITY_NAME, "idexists");
        }
        stockMovement = stockMovementRepository.save(stockMovement);
        return ResponseEntity.created(new URI("/api/stock-movements/" + stockMovement.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, stockMovement.getId().toString()))
            .body(stockMovement);
    }

    /**
     * {@code PUT  /stock-movements/:id} : Updates an existing stockMovement.
     *
     * @param id the id of the stockMovement to save.
     * @param stockMovement the stockMovement to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stockMovement,
     * or with status {@code 400 (Bad Request)} if the stockMovement is not valid,
     * or with status {@code 500 (Internal Server Error)} if the stockMovement couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<StockMovement> updateStockMovement(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody StockMovement stockMovement
    ) throws URISyntaxException {
        LOG.debug("REST request to update StockMovement : {}, {}", id, stockMovement);
        if (stockMovement.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stockMovement.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stockMovementRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        stockMovement = stockMovementRepository.save(stockMovement);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, stockMovement.getId().toString()))
            .body(stockMovement);
    }

    /**
     * {@code PATCH  /stock-movements/:id} : Partial updates given fields of an existing stockMovement, field will ignore if it is null
     *
     * @param id the id of the stockMovement to save.
     * @param stockMovement the stockMovement to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stockMovement,
     * or with status {@code 400 (Bad Request)} if the stockMovement is not valid,
     * or with status {@code 404 (Not Found)} if the stockMovement is not found,
     * or with status {@code 500 (Internal Server Error)} if the stockMovement couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<StockMovement> partialUpdateStockMovement(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody StockMovement stockMovement
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update StockMovement partially : {}, {}", id, stockMovement);
        if (stockMovement.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stockMovement.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stockMovementRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<StockMovement> result = stockMovementRepository
            .findById(stockMovement.getId())
            .map(existingStockMovement -> {
                if (stockMovement.getMovementDate() != null) {
                    existingStockMovement.setMovementDate(stockMovement.getMovementDate());
                }
                if (stockMovement.getMovementType() != null) {
                    existingStockMovement.setMovementType(stockMovement.getMovementType());
                }
                if (stockMovement.getQuantity() != null) {
                    existingStockMovement.setQuantity(stockMovement.getQuantity());
                }
                if (stockMovement.getUnit() != null) {
                    existingStockMovement.setUnit(stockMovement.getUnit());
                }
                if (stockMovement.getReference() != null) {
                    existingStockMovement.setReference(stockMovement.getReference());
                }
                if (stockMovement.getReason() != null) {
                    existingStockMovement.setReason(stockMovement.getReason());
                }
                if (stockMovement.getPerformedBy() != null) {
                    existingStockMovement.setPerformedBy(stockMovement.getPerformedBy());
                }
                if (stockMovement.getNote() != null) {
                    existingStockMovement.setNote(stockMovement.getNote());
                }

                return existingStockMovement;
            })
            .map(stockMovementRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, stockMovement.getId().toString())
        );
    }

    /**
     * {@code GET  /stock-movements} : get all the stockMovements.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of stockMovements in body.
     */
    @GetMapping("")
    public List<StockMovement> getAllStockMovements(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all StockMovements");
        if (eagerload) {
            return stockMovementRepository.findAllWithEagerRelationships();
        } else {
            return stockMovementRepository.findAll();
        }
    }

    /**
     * {@code GET  /stock-movements/:id} : get the "id" stockMovement.
     *
     * @param id the id of the stockMovement to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the stockMovement, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<StockMovement> getStockMovement(@PathVariable("id") Long id) {
        LOG.debug("REST request to get StockMovement : {}", id);
        Optional<StockMovement> stockMovement = stockMovementRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(stockMovement);
    }

    /**
     * {@code DELETE  /stock-movements/:id} : delete the "id" stockMovement.
     *
     * @param id the id of the stockMovement to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStockMovement(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete StockMovement : {}", id);
        stockMovementRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
