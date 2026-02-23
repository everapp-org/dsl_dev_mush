package com.mcms.web.rest;

import com.mcms.domain.HarvestRecord;
import com.mcms.repository.HarvestRecordRepository;
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
 * REST controller for managing {@link com.mcms.domain.HarvestRecord}.
 */
@RestController
@RequestMapping("/api/harvest-records")
@Transactional
public class HarvestRecordResource {

    private static final Logger LOG = LoggerFactory.getLogger(HarvestRecordResource.class);

    private static final String ENTITY_NAME = "harvestRecord";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final HarvestRecordRepository harvestRecordRepository;

    public HarvestRecordResource(HarvestRecordRepository harvestRecordRepository) {
        this.harvestRecordRepository = harvestRecordRepository;
    }

    /**
     * {@code POST  /harvest-records} : Create a new harvestRecord.
     *
     * @param harvestRecord the harvestRecord to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new harvestRecord, or with status {@code 400 (Bad Request)} if the harvestRecord has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<HarvestRecord> createHarvestRecord(@Valid @RequestBody HarvestRecord harvestRecord) throws URISyntaxException {
        LOG.debug("REST request to save HarvestRecord : {}", harvestRecord);
        if (harvestRecord.getId() != null) {
            throw new BadRequestAlertException("A new harvestRecord cannot already have an ID", ENTITY_NAME, "idexists");
        }
        harvestRecord = harvestRecordRepository.save(harvestRecord);
        return ResponseEntity.created(new URI("/api/harvest-records/" + harvestRecord.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, harvestRecord.getId().toString()))
            .body(harvestRecord);
    }

    /**
     * {@code PUT  /harvest-records/:id} : Updates an existing harvestRecord.
     *
     * @param id the id of the harvestRecord to save.
     * @param harvestRecord the harvestRecord to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated harvestRecord,
     * or with status {@code 400 (Bad Request)} if the harvestRecord is not valid,
     * or with status {@code 500 (Internal Server Error)} if the harvestRecord couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<HarvestRecord> updateHarvestRecord(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody HarvestRecord harvestRecord
    ) throws URISyntaxException {
        LOG.debug("REST request to update HarvestRecord : {}, {}", id, harvestRecord);
        if (harvestRecord.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, harvestRecord.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!harvestRecordRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        harvestRecord = harvestRecordRepository.save(harvestRecord);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, harvestRecord.getId().toString()))
            .body(harvestRecord);
    }

    /**
     * {@code PATCH  /harvest-records/:id} : Partial updates given fields of an existing harvestRecord, field will ignore if it is null
     *
     * @param id the id of the harvestRecord to save.
     * @param harvestRecord the harvestRecord to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated harvestRecord,
     * or with status {@code 400 (Bad Request)} if the harvestRecord is not valid,
     * or with status {@code 404 (Not Found)} if the harvestRecord is not found,
     * or with status {@code 500 (Internal Server Error)} if the harvestRecord couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<HarvestRecord> partialUpdateHarvestRecord(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody HarvestRecord harvestRecord
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update HarvestRecord partially : {}, {}", id, harvestRecord);
        if (harvestRecord.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, harvestRecord.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!harvestRecordRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<HarvestRecord> result = harvestRecordRepository
            .findById(harvestRecord.getId())
            .map(existingHarvestRecord -> {
                if (harvestRecord.getHarvestDate() != null) {
                    existingHarvestRecord.setHarvestDate(harvestRecord.getHarvestDate());
                }
                if (harvestRecord.getWeightKg() != null) {
                    existingHarvestRecord.setWeightKg(harvestRecord.getWeightKg());
                }
                if (harvestRecord.getGrade() != null) {
                    existingHarvestRecord.setGrade(harvestRecord.getGrade());
                }
                if (harvestRecord.getPickerName() != null) {
                    existingHarvestRecord.setPickerName(harvestRecord.getPickerName());
                }
                if (harvestRecord.getNote() != null) {
                    existingHarvestRecord.setNote(harvestRecord.getNote());
                }

                return existingHarvestRecord;
            })
            .map(harvestRecordRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, harvestRecord.getId().toString())
        );
    }

    /**
     * {@code GET  /harvest-records} : get all the harvestRecords.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of harvestRecords in body.
     */
    @GetMapping("")
    public List<HarvestRecord> getAllHarvestRecords() {
        LOG.debug("REST request to get all HarvestRecords");
        return harvestRecordRepository.findAll();
    }

    /**
     * {@code GET  /harvest-records/:id} : get the "id" harvestRecord.
     *
     * @param id the id of the harvestRecord to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the harvestRecord, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<HarvestRecord> getHarvestRecord(@PathVariable("id") Long id) {
        LOG.debug("REST request to get HarvestRecord : {}", id);
        Optional<HarvestRecord> harvestRecord = harvestRecordRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(harvestRecord);
    }

    /**
     * {@code DELETE  /harvest-records/:id} : delete the "id" harvestRecord.
     *
     * @param id the id of the harvestRecord to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHarvestRecord(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete HarvestRecord : {}", id);
        harvestRecordRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
