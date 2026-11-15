package com.mcms.web.rest;

import com.mcms.domain.MandatoryFieldCheck;
import com.mcms.repository.MandatoryFieldCheckRepository;
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
 * REST controller for managing {@link com.mcms.domain.MandatoryFieldCheck}.
 */
@RestController
@RequestMapping("/api/mandatory-field-checks")
@Transactional
public class MandatoryFieldCheckResource {

    private static final Logger LOG = LoggerFactory.getLogger(MandatoryFieldCheckResource.class);

    private static final String ENTITY_NAME = "mandatoryFieldCheck";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MandatoryFieldCheckRepository mandatoryFieldCheckRepository;

    public MandatoryFieldCheckResource(MandatoryFieldCheckRepository mandatoryFieldCheckRepository) {
        this.mandatoryFieldCheckRepository = mandatoryFieldCheckRepository;
    }

    /**
     * {@code POST  /mandatory-field-checks} : Create a new mandatoryFieldCheck.
     *
     * @param mandatoryFieldCheck the mandatoryFieldCheck to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new mandatoryFieldCheck, or with status {@code 400 (Bad Request)} if the mandatoryFieldCheck has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<MandatoryFieldCheck> createMandatoryFieldCheck(@Valid @RequestBody MandatoryFieldCheck mandatoryFieldCheck)
        throws URISyntaxException {
        LOG.debug("REST request to save MandatoryFieldCheck : {}", mandatoryFieldCheck);
        if (mandatoryFieldCheck.getId() != null) {
            throw new BadRequestAlertException("A new mandatoryFieldCheck cannot already have an ID", ENTITY_NAME, "idexists");
        }
        mandatoryFieldCheck = mandatoryFieldCheckRepository.save(mandatoryFieldCheck);
        return ResponseEntity.created(new URI("/api/mandatory-field-checks/" + mandatoryFieldCheck.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, mandatoryFieldCheck.getId().toString()))
            .body(mandatoryFieldCheck);
    }

    /**
     * {@code PUT  /mandatory-field-checks/:id} : Updates an existing mandatoryFieldCheck.
     *
     * @param id the id of the mandatoryFieldCheck to save.
     * @param mandatoryFieldCheck the mandatoryFieldCheck to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mandatoryFieldCheck,
     * or with status {@code 400 (Bad Request)} if the mandatoryFieldCheck is not valid,
     * or with status {@code 500 (Internal Server Error)} if the mandatoryFieldCheck couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MandatoryFieldCheck> updateMandatoryFieldCheck(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MandatoryFieldCheck mandatoryFieldCheck
    ) throws URISyntaxException {
        LOG.debug("REST request to update MandatoryFieldCheck : {}, {}", id, mandatoryFieldCheck);
        if (mandatoryFieldCheck.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mandatoryFieldCheck.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!mandatoryFieldCheckRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        mandatoryFieldCheck = mandatoryFieldCheckRepository.save(mandatoryFieldCheck);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, mandatoryFieldCheck.getId().toString()))
            .body(mandatoryFieldCheck);
    }

    /**
     * {@code PATCH  /mandatory-field-checks/:id} : Partial updates given fields of an existing mandatoryFieldCheck, field will ignore if it is null
     *
     * @param id the id of the mandatoryFieldCheck to save.
     * @param mandatoryFieldCheck the mandatoryFieldCheck to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mandatoryFieldCheck,
     * or with status {@code 400 (Bad Request)} if the mandatoryFieldCheck is not valid,
     * or with status {@code 404 (Not Found)} if the mandatoryFieldCheck is not found,
     * or with status {@code 500 (Internal Server Error)} if the mandatoryFieldCheck couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MandatoryFieldCheck> partialUpdateMandatoryFieldCheck(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MandatoryFieldCheck mandatoryFieldCheck
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update MandatoryFieldCheck partially : {}, {}", id, mandatoryFieldCheck);
        if (mandatoryFieldCheck.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mandatoryFieldCheck.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!mandatoryFieldCheckRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MandatoryFieldCheck> result = mandatoryFieldCheckRepository
            .findById(mandatoryFieldCheck.getId())
            .map(existingMandatoryFieldCheck -> {
                if (mandatoryFieldCheck.getFieldName() != null) {
                    existingMandatoryFieldCheck.setFieldName(mandatoryFieldCheck.getFieldName());
                }
                if (mandatoryFieldCheck.getIsFilled() != null) {
                    existingMandatoryFieldCheck.setIsFilled(mandatoryFieldCheck.getIsFilled());
                }
                if (mandatoryFieldCheck.getCheckDate() != null) {
                    existingMandatoryFieldCheck.setCheckDate(mandatoryFieldCheck.getCheckDate());
                }

                return existingMandatoryFieldCheck;
            })
            .map(mandatoryFieldCheckRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, mandatoryFieldCheck.getId().toString())
        );
    }

    /**
     * {@code GET  /mandatory-field-checks} : get all the mandatoryFieldChecks.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of mandatoryFieldChecks in body.
     */
    @GetMapping("")
    public List<MandatoryFieldCheck> getAllMandatoryFieldChecks() {
        LOG.debug("REST request to get all MandatoryFieldChecks");
        return mandatoryFieldCheckRepository.findAll();
    }

    /**
     * {@code GET  /mandatory-field-checks/:id} : get the "id" mandatoryFieldCheck.
     *
     * @param id the id of the mandatoryFieldCheck to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the mandatoryFieldCheck, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MandatoryFieldCheck> getMandatoryFieldCheck(@PathVariable("id") Long id) {
        LOG.debug("REST request to get MandatoryFieldCheck : {}", id);
        Optional<MandatoryFieldCheck> mandatoryFieldCheck = mandatoryFieldCheckRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(mandatoryFieldCheck);
    }

    /**
     * {@code DELETE  /mandatory-field-checks/:id} : delete the "id" mandatoryFieldCheck.
     *
     * @param id the id of the mandatoryFieldCheck to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMandatoryFieldCheck(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete MandatoryFieldCheck : {}", id);
        mandatoryFieldCheckRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
