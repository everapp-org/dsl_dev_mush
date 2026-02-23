package com.mcms.web.rest;

import com.mcms.domain.InventoryLot;
import com.mcms.repository.InventoryLotRepository;
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
 * REST controller for managing {@link com.mcms.domain.InventoryLot}.
 */
@RestController
@RequestMapping("/api/inventory-lots")
@Transactional
public class InventoryLotResource {

    private static final Logger LOG = LoggerFactory.getLogger(InventoryLotResource.class);

    private static final String ENTITY_NAME = "inventoryLot";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final InventoryLotRepository inventoryLotRepository;

    public InventoryLotResource(InventoryLotRepository inventoryLotRepository) {
        this.inventoryLotRepository = inventoryLotRepository;
    }

    /**
     * {@code POST  /inventory-lots} : Create a new inventoryLot.
     *
     * @param inventoryLot the inventoryLot to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new inventoryLot, or with status {@code 400 (Bad Request)} if the inventoryLot has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<InventoryLot> createInventoryLot(@Valid @RequestBody InventoryLot inventoryLot) throws URISyntaxException {
        LOG.debug("REST request to save InventoryLot : {}", inventoryLot);
        if (inventoryLot.getId() != null) {
            throw new BadRequestAlertException("A new inventoryLot cannot already have an ID", ENTITY_NAME, "idexists");
        }
        inventoryLot = inventoryLotRepository.save(inventoryLot);
        return ResponseEntity.created(new URI("/api/inventory-lots/" + inventoryLot.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, inventoryLot.getId().toString()))
            .body(inventoryLot);
    }

    /**
     * {@code PUT  /inventory-lots/:id} : Updates an existing inventoryLot.
     *
     * @param id the id of the inventoryLot to save.
     * @param inventoryLot the inventoryLot to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated inventoryLot,
     * or with status {@code 400 (Bad Request)} if the inventoryLot is not valid,
     * or with status {@code 500 (Internal Server Error)} if the inventoryLot couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<InventoryLot> updateInventoryLot(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody InventoryLot inventoryLot
    ) throws URISyntaxException {
        LOG.debug("REST request to update InventoryLot : {}, {}", id, inventoryLot);
        if (inventoryLot.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, inventoryLot.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!inventoryLotRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        inventoryLot = inventoryLotRepository.save(inventoryLot);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, inventoryLot.getId().toString()))
            .body(inventoryLot);
    }

    /**
     * {@code PATCH  /inventory-lots/:id} : Partial updates given fields of an existing inventoryLot, field will ignore if it is null
     *
     * @param id the id of the inventoryLot to save.
     * @param inventoryLot the inventoryLot to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated inventoryLot,
     * or with status {@code 400 (Bad Request)} if the inventoryLot is not valid,
     * or with status {@code 404 (Not Found)} if the inventoryLot is not found,
     * or with status {@code 500 (Internal Server Error)} if the inventoryLot couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<InventoryLot> partialUpdateInventoryLot(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody InventoryLot inventoryLot
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update InventoryLot partially : {}, {}", id, inventoryLot);
        if (inventoryLot.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, inventoryLot.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!inventoryLotRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<InventoryLot> result = inventoryLotRepository
            .findById(inventoryLot.getId())
            .map(existingInventoryLot -> {
                if (inventoryLot.getLotCode() != null) {
                    existingInventoryLot.setLotCode(inventoryLot.getLotCode());
                }
                if (inventoryLot.getReceivedDate() != null) {
                    existingInventoryLot.setReceivedDate(inventoryLot.getReceivedDate());
                }
                if (inventoryLot.getQuantityReceived() != null) {
                    existingInventoryLot.setQuantityReceived(inventoryLot.getQuantityReceived());
                }
                if (inventoryLot.getQuantityOnHand() != null) {
                    existingInventoryLot.setQuantityOnHand(inventoryLot.getQuantityOnHand());
                }
                if (inventoryLot.getUnit() != null) {
                    existingInventoryLot.setUnit(inventoryLot.getUnit());
                }
                if (inventoryLot.getExpiryDate() != null) {
                    existingInventoryLot.setExpiryDate(inventoryLot.getExpiryDate());
                }
                if (inventoryLot.getStorageLocation() != null) {
                    existingInventoryLot.setStorageLocation(inventoryLot.getStorageLocation());
                }
                if (inventoryLot.getSupplierLotNumber() != null) {
                    existingInventoryLot.setSupplierLotNumber(inventoryLot.getSupplierLotNumber());
                }
                if (inventoryLot.getIsExhausted() != null) {
                    existingInventoryLot.setIsExhausted(inventoryLot.getIsExhausted());
                }
                if (inventoryLot.getNote() != null) {
                    existingInventoryLot.setNote(inventoryLot.getNote());
                }

                return existingInventoryLot;
            })
            .map(inventoryLotRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, inventoryLot.getId().toString())
        );
    }

    /**
     * {@code GET  /inventory-lots} : get all the inventoryLots.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of inventoryLots in body.
     */
    @GetMapping("")
    public List<InventoryLot> getAllInventoryLots(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all InventoryLots");
        if (eagerload) {
            return inventoryLotRepository.findAllWithEagerRelationships();
        } else {
            return inventoryLotRepository.findAll();
        }
    }

    /**
     * {@code GET  /inventory-lots/:id} : get the "id" inventoryLot.
     *
     * @param id the id of the inventoryLot to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the inventoryLot, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<InventoryLot> getInventoryLot(@PathVariable("id") Long id) {
        LOG.debug("REST request to get InventoryLot : {}", id);
        Optional<InventoryLot> inventoryLot = inventoryLotRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(inventoryLot);
    }

    /**
     * {@code DELETE  /inventory-lots/:id} : delete the "id" inventoryLot.
     *
     * @param id the id of the inventoryLot to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInventoryLot(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete InventoryLot : {}", id);
        inventoryLotRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
