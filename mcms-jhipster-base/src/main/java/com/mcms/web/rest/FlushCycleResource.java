package com.mcms.web.rest;

import com.mcms.domain.FlushCycle;
import com.mcms.repository.FlushCycleRepository;
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
 * REST controller for managing {@link com.mcms.domain.FlushCycle}.
 */
@RestController
@RequestMapping("/api/flush-cycles")
@Transactional
public class FlushCycleResource {

    private static final Logger LOG = LoggerFactory.getLogger(FlushCycleResource.class);

    private static final String ENTITY_NAME = "flushCycle";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FlushCycleRepository flushCycleRepository;

    public FlushCycleResource(FlushCycleRepository flushCycleRepository) {
        this.flushCycleRepository = flushCycleRepository;
    }

    /**
     * {@code POST  /flush-cycles} : Create a new flushCycle.
     *
     * @param flushCycle the flushCycle to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new flushCycle, or with status {@code 400 (Bad Request)} if the flushCycle has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<FlushCycle> createFlushCycle(@Valid @RequestBody FlushCycle flushCycle) throws URISyntaxException {
        LOG.debug("REST request to save FlushCycle : {}", flushCycle);
        if (flushCycle.getId() != null) {
            throw new BadRequestAlertException("A new flushCycle cannot already have an ID", ENTITY_NAME, "idexists");
        }
        flushCycle = flushCycleRepository.save(flushCycle);
        return ResponseEntity.created(new URI("/api/flush-cycles/" + flushCycle.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, flushCycle.getId().toString()))
            .body(flushCycle);
    }

    /**
     * {@code PUT  /flush-cycles/:id} : Updates an existing flushCycle.
     *
     * @param id the id of the flushCycle to save.
     * @param flushCycle the flushCycle to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated flushCycle,
     * or with status {@code 400 (Bad Request)} if the flushCycle is not valid,
     * or with status {@code 500 (Internal Server Error)} if the flushCycle couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<FlushCycle> updateFlushCycle(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody FlushCycle flushCycle
    ) throws URISyntaxException {
        LOG.debug("REST request to update FlushCycle : {}, {}", id, flushCycle);
        if (flushCycle.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, flushCycle.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!flushCycleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        flushCycle = flushCycleRepository.save(flushCycle);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, flushCycle.getId().toString()))
            .body(flushCycle);
    }

    /**
     * {@code PATCH  /flush-cycles/:id} : Partial updates given fields of an existing flushCycle, field will ignore if it is null
     *
     * @param id the id of the flushCycle to save.
     * @param flushCycle the flushCycle to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated flushCycle,
     * or with status {@code 400 (Bad Request)} if the flushCycle is not valid,
     * or with status {@code 404 (Not Found)} if the flushCycle is not found,
     * or with status {@code 500 (Internal Server Error)} if the flushCycle couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<FlushCycle> partialUpdateFlushCycle(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody FlushCycle flushCycle
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update FlushCycle partially : {}, {}", id, flushCycle);
        if (flushCycle.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, flushCycle.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!flushCycleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FlushCycle> result = flushCycleRepository
            .findById(flushCycle.getId())
            .map(existingFlushCycle -> {
                if (flushCycle.getFlushNumber() != null) {
                    existingFlushCycle.setFlushNumber(flushCycle.getFlushNumber());
                }
                if (flushCycle.getHarvestStartDate() != null) {
                    existingFlushCycle.setHarvestStartDate(flushCycle.getHarvestStartDate());
                }
                if (flushCycle.getHarvestEndDate() != null) {
                    existingFlushCycle.setHarvestEndDate(flushCycle.getHarvestEndDate());
                }
                if (flushCycle.getYieldKg() != null) {
                    existingFlushCycle.setYieldKg(flushCycle.getYieldKg());
                }
                if (flushCycle.getYieldBagsHarvested() != null) {
                    existingFlushCycle.setYieldBagsHarvested(flushCycle.getYieldBagsHarvested());
                }
                if (flushCycle.getAvgFruitBodyWeightG() != null) {
                    existingFlushCycle.setAvgFruitBodyWeightG(flushCycle.getAvgFruitBodyWeightG());
                }
                if (flushCycle.getRehydrationDone() != null) {
                    existingFlushCycle.setRehydrationDone(flushCycle.getRehydrationDone());
                }
                if (flushCycle.getRehydrationDurationHours() != null) {
                    existingFlushCycle.setRehydrationDurationHours(flushCycle.getRehydrationDurationHours());
                }
                if (flushCycle.getNote() != null) {
                    existingFlushCycle.setNote(flushCycle.getNote());
                }

                return existingFlushCycle;
            })
            .map(flushCycleRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, flushCycle.getId().toString())
        );
    }

    /**
     * {@code GET  /flush-cycles} : get all the flushCycles.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of flushCycles in body.
     */
    @GetMapping("")
    public List<FlushCycle> getAllFlushCycles(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all FlushCycles");
        if (eagerload) {
            return flushCycleRepository.findAllWithEagerRelationships();
        } else {
            return flushCycleRepository.findAll();
        }
    }

    /**
     * {@code GET  /flush-cycles/:id} : get the "id" flushCycle.
     *
     * @param id the id of the flushCycle to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the flushCycle, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<FlushCycle> getFlushCycle(@PathVariable("id") Long id) {
        LOG.debug("REST request to get FlushCycle : {}", id);
        Optional<FlushCycle> flushCycle = flushCycleRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(flushCycle);
    }

    /**
     * {@code DELETE  /flush-cycles/:id} : delete the "id" flushCycle.
     *
     * @param id the id of the flushCycle to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlushCycle(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete FlushCycle : {}", id);
        flushCycleRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code GET  /flush-cycles/by-batch/:batchId} : get all flush cycles for a batch.
     *
     * @param batchId the id of the batch.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of flushCycles in body.
     */
    @GetMapping("/by-batch/{batchId}")
    public List<FlushCycle> getFlushCyclesByBatch(@PathVariable("batchId") Long batchId) {
        LOG.debug("REST request to get FlushCycles by Batch : {}", batchId);
        return flushCycleRepository.findByBatchIdOrderByFlushNumber(batchId);
    }
}
