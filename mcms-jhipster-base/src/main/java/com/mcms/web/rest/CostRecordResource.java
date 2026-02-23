package com.mcms.web.rest;

import com.mcms.domain.CostRecord;
import com.mcms.repository.CostRecordRepository;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mcms.domain.CostRecord}.
 */
@RestController
@RequestMapping("/api/cost-records")
@Transactional
public class CostRecordResource {

    private static final Logger LOG = LoggerFactory.getLogger(CostRecordResource.class);

    private static final String ENTITY_NAME = "costRecord";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CostRecordRepository costRecordRepository;

    public CostRecordResource(CostRecordRepository costRecordRepository) {
        this.costRecordRepository = costRecordRepository;
    }

    /**
     * {@code POST  /cost-records} : Create a new costRecord.
     *
     * @param costRecord the costRecord to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new costRecord, or with status {@code 400 (Bad Request)} if the costRecord has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<CostRecord> createCostRecord(@Valid @RequestBody CostRecord costRecord) throws URISyntaxException {
        LOG.debug("REST request to save CostRecord : {}", costRecord);
        if (costRecord.getId() != null) {
            throw new BadRequestAlertException("A new costRecord cannot already have an ID", ENTITY_NAME, "idexists");
        }
        costRecord = costRecordRepository.save(costRecord);
        return ResponseEntity.created(new URI("/api/cost-records/" + costRecord.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, costRecord.getId().toString()))
            .body(costRecord);
    }

    /**
     * {@code PUT  /cost-records/:id} : Updates an existing costRecord.
     *
     * @param id the id of the costRecord to save.
     * @param costRecord the costRecord to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated costRecord,
     * or with status {@code 400 (Bad Request)} if the costRecord is not valid,
     * or with status {@code 500 (Internal Server Error)} if the costRecord couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<CostRecord> updateCostRecord(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CostRecord costRecord
    ) throws URISyntaxException {
        LOG.debug("REST request to update CostRecord : {}, {}", id, costRecord);
        if (costRecord.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, costRecord.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!costRecordRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        costRecord = costRecordRepository.save(costRecord);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, costRecord.getId().toString()))
            .body(costRecord);
    }

    /**
     * {@code PATCH  /cost-records/:id} : Partial updates given fields of an existing costRecord, field will ignore if it is null
     *
     * @param id the id of the costRecord to save.
     * @param costRecord the costRecord to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated costRecord,
     * or with status {@code 400 (Bad Request)} if the costRecord is not valid,
     * or with status {@code 404 (Not Found)} if the costRecord is not found,
     * or with status {@code 500 (Internal Server Error)} if the costRecord couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<CostRecord> partialUpdateCostRecord(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CostRecord costRecord
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update CostRecord partially : {}, {}", id, costRecord);
        if (costRecord.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, costRecord.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!costRecordRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CostRecord> result = costRecordRepository
            .findById(costRecord.getId())
            .map(existingCostRecord -> {
                if (costRecord.getRecordDate() != null) {
                    existingCostRecord.setRecordDate(costRecord.getRecordDate());
                }
                if (costRecord.getCategory() != null) {
                    existingCostRecord.setCategory(costRecord.getCategory());
                }
                if (costRecord.getDescription() != null) {
                    existingCostRecord.setDescription(costRecord.getDescription());
                }
                if (costRecord.getAmount() != null) {
                    existingCostRecord.setAmount(costRecord.getAmount());
                }
                if (costRecord.getCurrency() != null) {
                    existingCostRecord.setCurrency(costRecord.getCurrency());
                }
                if (costRecord.getNote() != null) {
                    existingCostRecord.setNote(costRecord.getNote());
                }

                return existingCostRecord;
            })
            .map(costRecordRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, costRecord.getId().toString())
        );
    }

    /**
     * {@code GET  /cost-records} : get all the costRecords.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of costRecords in body.
     */
    @GetMapping("")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public List<CostRecord> getAllCostRecords(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all CostRecords");
        if (eagerload) {
            return costRecordRepository.findAllWithEagerRelationships();
        } else {
            return costRecordRepository.findAll();
        }
    }

    /**
     * {@code GET  /cost-records/:id} : get the "id" costRecord.
     *
     * @param id the id of the costRecord to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the costRecord, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<CostRecord> getCostRecord(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CostRecord : {}", id);
        Optional<CostRecord> costRecord = costRecordRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(costRecord);
    }

    /**
     * {@code DELETE  /cost-records/:id} : delete the "id" costRecord.
     *
     * @param id the id of the costRecord to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<Void> deleteCostRecord(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CostRecord : {}", id);
        costRecordRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
