package com.mcms.web.rest;

import com.mcms.domain.BatchMaterialUsage;
import com.mcms.repository.BatchMaterialUsageRepository;
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
 * REST controller for managing {@link com.mcms.domain.BatchMaterialUsage}.
 */
@RestController
@RequestMapping("/api/batch-material-usages")
@Transactional
public class BatchMaterialUsageResource {

    private static final Logger LOG = LoggerFactory.getLogger(BatchMaterialUsageResource.class);

    private static final String ENTITY_NAME = "batchMaterialUsage";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BatchMaterialUsageRepository batchMaterialUsageRepository;

    public BatchMaterialUsageResource(BatchMaterialUsageRepository batchMaterialUsageRepository) {
        this.batchMaterialUsageRepository = batchMaterialUsageRepository;
    }

    /**
     * {@code POST  /batch-material-usages} : Create a new batchMaterialUsage.
     *
     * @param batchMaterialUsage the batchMaterialUsage to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new batchMaterialUsage, or with status {@code 400 (Bad Request)} if the batchMaterialUsage has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<BatchMaterialUsage> createBatchMaterialUsage(@Valid @RequestBody BatchMaterialUsage batchMaterialUsage)
        throws URISyntaxException {
        LOG.debug("REST request to save BatchMaterialUsage : {}", batchMaterialUsage);
        if (batchMaterialUsage.getId() != null) {
            throw new BadRequestAlertException("A new batchMaterialUsage cannot already have an ID", ENTITY_NAME, "idexists");
        }
        batchMaterialUsage = batchMaterialUsageRepository.save(batchMaterialUsage);
        return ResponseEntity.created(new URI("/api/batch-material-usages/" + batchMaterialUsage.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, batchMaterialUsage.getId().toString()))
            .body(batchMaterialUsage);
    }

    /**
     * {@code PUT  /batch-material-usages/:id} : Updates an existing batchMaterialUsage.
     *
     * @param id the id of the batchMaterialUsage to save.
     * @param batchMaterialUsage the batchMaterialUsage to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated batchMaterialUsage,
     * or with status {@code 400 (Bad Request)} if the batchMaterialUsage is not valid,
     * or with status {@code 500 (Internal Server Error)} if the batchMaterialUsage couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<BatchMaterialUsage> updateBatchMaterialUsage(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BatchMaterialUsage batchMaterialUsage
    ) throws URISyntaxException {
        LOG.debug("REST request to update BatchMaterialUsage : {}, {}", id, batchMaterialUsage);
        if (batchMaterialUsage.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, batchMaterialUsage.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!batchMaterialUsageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        batchMaterialUsage = batchMaterialUsageRepository.save(batchMaterialUsage);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, batchMaterialUsage.getId().toString()))
            .body(batchMaterialUsage);
    }

    /**
     * {@code PATCH  /batch-material-usages/:id} : Partial updates given fields of an existing batchMaterialUsage, field will ignore if it is null
     *
     * @param id the id of the batchMaterialUsage to save.
     * @param batchMaterialUsage the batchMaterialUsage to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated batchMaterialUsage,
     * or with status {@code 400 (Bad Request)} if the batchMaterialUsage is not valid,
     * or with status {@code 404 (Not Found)} if the batchMaterialUsage is not found,
     * or with status {@code 500 (Internal Server Error)} if the batchMaterialUsage couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<BatchMaterialUsage> partialUpdateBatchMaterialUsage(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BatchMaterialUsage batchMaterialUsage
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update BatchMaterialUsage partially : {}, {}", id, batchMaterialUsage);
        if (batchMaterialUsage.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, batchMaterialUsage.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!batchMaterialUsageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BatchMaterialUsage> result = batchMaterialUsageRepository
            .findById(batchMaterialUsage.getId())
            .map(existingBatchMaterialUsage -> {
                if (batchMaterialUsage.getUsageDate() != null) {
                    existingBatchMaterialUsage.setUsageDate(batchMaterialUsage.getUsageDate());
                }
                if (batchMaterialUsage.getQuantityUsed() != null) {
                    existingBatchMaterialUsage.setQuantityUsed(batchMaterialUsage.getQuantityUsed());
                }
                if (batchMaterialUsage.getUnit() != null) {
                    existingBatchMaterialUsage.setUnit(batchMaterialUsage.getUnit());
                }
                if (batchMaterialUsage.getPurpose() != null) {
                    existingBatchMaterialUsage.setPurpose(batchMaterialUsage.getPurpose());
                }
                if (batchMaterialUsage.getNote() != null) {
                    existingBatchMaterialUsage.setNote(batchMaterialUsage.getNote());
                }

                return existingBatchMaterialUsage;
            })
            .map(batchMaterialUsageRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, batchMaterialUsage.getId().toString())
        );
    }

    /**
     * {@code GET  /batch-material-usages} : get all the batchMaterialUsages.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of batchMaterialUsages in body.
     */
    @GetMapping("")
    public List<BatchMaterialUsage> getAllBatchMaterialUsages(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all BatchMaterialUsages");
        if (eagerload) {
            return batchMaterialUsageRepository.findAllWithEagerRelationships();
        } else {
            return batchMaterialUsageRepository.findAll();
        }
    }

    /**
     * {@code GET  /batch-material-usages/:id} : get the "id" batchMaterialUsage.
     *
     * @param id the id of the batchMaterialUsage to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the batchMaterialUsage, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BatchMaterialUsage> getBatchMaterialUsage(@PathVariable("id") Long id) {
        LOG.debug("REST request to get BatchMaterialUsage : {}", id);
        Optional<BatchMaterialUsage> batchMaterialUsage = batchMaterialUsageRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(batchMaterialUsage);
    }

    /**
     * {@code DELETE  /batch-material-usages/:id} : delete the "id" batchMaterialUsage.
     *
     * @param id the id of the batchMaterialUsage to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBatchMaterialUsage(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete BatchMaterialUsage : {}", id);
        batchMaterialUsageRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
