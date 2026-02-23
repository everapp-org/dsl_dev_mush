package com.mcms.web.rest;

import com.mcms.domain.SupplyOrderLine;
import com.mcms.repository.SupplyOrderLineRepository;
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
 * REST controller for managing {@link com.mcms.domain.SupplyOrderLine}.
 */
@RestController
@RequestMapping("/api/supply-order-lines")
@Transactional
public class SupplyOrderLineResource {

    private static final Logger LOG = LoggerFactory.getLogger(SupplyOrderLineResource.class);

    private static final String ENTITY_NAME = "supplyOrderLine";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SupplyOrderLineRepository supplyOrderLineRepository;

    public SupplyOrderLineResource(SupplyOrderLineRepository supplyOrderLineRepository) {
        this.supplyOrderLineRepository = supplyOrderLineRepository;
    }

    /**
     * {@code POST  /supply-order-lines} : Create a new supplyOrderLine.
     *
     * @param supplyOrderLine the supplyOrderLine to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new supplyOrderLine, or with status {@code 400 (Bad Request)} if the supplyOrderLine has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SupplyOrderLine> createSupplyOrderLine(@Valid @RequestBody SupplyOrderLine supplyOrderLine)
        throws URISyntaxException {
        LOG.debug("REST request to save SupplyOrderLine : {}", supplyOrderLine);
        if (supplyOrderLine.getId() != null) {
            throw new BadRequestAlertException("A new supplyOrderLine cannot already have an ID", ENTITY_NAME, "idexists");
        }
        supplyOrderLine = supplyOrderLineRepository.save(supplyOrderLine);
        return ResponseEntity.created(new URI("/api/supply-order-lines/" + supplyOrderLine.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, supplyOrderLine.getId().toString()))
            .body(supplyOrderLine);
    }

    /**
     * {@code PUT  /supply-order-lines/:id} : Updates an existing supplyOrderLine.
     *
     * @param id the id of the supplyOrderLine to save.
     * @param supplyOrderLine the supplyOrderLine to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated supplyOrderLine,
     * or with status {@code 400 (Bad Request)} if the supplyOrderLine is not valid,
     * or with status {@code 500 (Internal Server Error)} if the supplyOrderLine couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SupplyOrderLine> updateSupplyOrderLine(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SupplyOrderLine supplyOrderLine
    ) throws URISyntaxException {
        LOG.debug("REST request to update SupplyOrderLine : {}, {}", id, supplyOrderLine);
        if (supplyOrderLine.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, supplyOrderLine.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!supplyOrderLineRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        supplyOrderLine = supplyOrderLineRepository.save(supplyOrderLine);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, supplyOrderLine.getId().toString()))
            .body(supplyOrderLine);
    }

    /**
     * {@code PATCH  /supply-order-lines/:id} : Partial updates given fields of an existing supplyOrderLine, field will ignore if it is null
     *
     * @param id the id of the supplyOrderLine to save.
     * @param supplyOrderLine the supplyOrderLine to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated supplyOrderLine,
     * or with status {@code 400 (Bad Request)} if the supplyOrderLine is not valid,
     * or with status {@code 404 (Not Found)} if the supplyOrderLine is not found,
     * or with status {@code 500 (Internal Server Error)} if the supplyOrderLine couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SupplyOrderLine> partialUpdateSupplyOrderLine(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SupplyOrderLine supplyOrderLine
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update SupplyOrderLine partially : {}, {}", id, supplyOrderLine);
        if (supplyOrderLine.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, supplyOrderLine.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!supplyOrderLineRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SupplyOrderLine> result = supplyOrderLineRepository
            .findById(supplyOrderLine.getId())
            .map(existingSupplyOrderLine -> {
                if (supplyOrderLine.getLineNumber() != null) {
                    existingSupplyOrderLine.setLineNumber(supplyOrderLine.getLineNumber());
                }
                if (supplyOrderLine.getItemDescription() != null) {
                    existingSupplyOrderLine.setItemDescription(supplyOrderLine.getItemDescription());
                }
                if (supplyOrderLine.getQuantityOrdered() != null) {
                    existingSupplyOrderLine.setQuantityOrdered(supplyOrderLine.getQuantityOrdered());
                }
                if (supplyOrderLine.getQuantityReceived() != null) {
                    existingSupplyOrderLine.setQuantityReceived(supplyOrderLine.getQuantityReceived());
                }
                if (supplyOrderLine.getUnit() != null) {
                    existingSupplyOrderLine.setUnit(supplyOrderLine.getUnit());
                }
                if (supplyOrderLine.getUnitPrice() != null) {
                    existingSupplyOrderLine.setUnitPrice(supplyOrderLine.getUnitPrice());
                }
                if (supplyOrderLine.getLineTotal() != null) {
                    existingSupplyOrderLine.setLineTotal(supplyOrderLine.getLineTotal());
                }
                if (supplyOrderLine.getLotNumber() != null) {
                    existingSupplyOrderLine.setLotNumber(supplyOrderLine.getLotNumber());
                }
                if (supplyOrderLine.getExpiryDate() != null) {
                    existingSupplyOrderLine.setExpiryDate(supplyOrderLine.getExpiryDate());
                }
                if (supplyOrderLine.getQualityOnReceipt() != null) {
                    existingSupplyOrderLine.setQualityOnReceipt(supplyOrderLine.getQualityOnReceipt());
                }
                if (supplyOrderLine.getIsReceived() != null) {
                    existingSupplyOrderLine.setIsReceived(supplyOrderLine.getIsReceived());
                }
                if (supplyOrderLine.getNote() != null) {
                    existingSupplyOrderLine.setNote(supplyOrderLine.getNote());
                }

                return existingSupplyOrderLine;
            })
            .map(supplyOrderLineRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, supplyOrderLine.getId().toString())
        );
    }

    /**
     * {@code GET  /supply-order-lines} : get all the supplyOrderLines.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of supplyOrderLines in body.
     */
    @GetMapping("")
    public List<SupplyOrderLine> getAllSupplyOrderLines(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all SupplyOrderLines");
        if (eagerload) {
            return supplyOrderLineRepository.findAllWithEagerRelationships();
        } else {
            return supplyOrderLineRepository.findAll();
        }
    }

    /**
     * {@code GET  /supply-order-lines/:id} : get the "id" supplyOrderLine.
     *
     * @param id the id of the supplyOrderLine to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the supplyOrderLine, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SupplyOrderLine> getSupplyOrderLine(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SupplyOrderLine : {}", id);
        Optional<SupplyOrderLine> supplyOrderLine = supplyOrderLineRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(supplyOrderLine);
    }

    /**
     * {@code DELETE  /supply-order-lines/:id} : delete the "id" supplyOrderLine.
     *
     * @param id the id of the supplyOrderLine to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplyOrderLine(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SupplyOrderLine : {}", id);
        supplyOrderLineRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
