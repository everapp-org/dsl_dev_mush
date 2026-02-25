package com.mcms.web.rest;

import com.mcms.domain.PhaseExecution;
import com.mcms.repository.PhaseExecutionRepository;
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
 * REST controller for managing {@link com.mcms.domain.PhaseExecution}.
 */
@RestController
@RequestMapping("/api/phase-executions")
@Transactional
public class PhaseExecutionResource {

    private static final Logger LOG = LoggerFactory.getLogger(PhaseExecutionResource.class);

    private static final String ENTITY_NAME = "phaseExecution";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PhaseExecutionRepository phaseExecutionRepository;

    public PhaseExecutionResource(PhaseExecutionRepository phaseExecutionRepository) {
        this.phaseExecutionRepository = phaseExecutionRepository;
    }

    /**
     * {@code POST  /phase-executions} : Create a new phaseExecution.
     *
     * @param phaseExecution the phaseExecution to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new phaseExecution, or with status {@code 400 (Bad Request)} if the phaseExecution has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PhaseExecution> createPhaseExecution(@Valid @RequestBody PhaseExecution phaseExecution)
        throws URISyntaxException {
        LOG.debug("REST request to save PhaseExecution : {}", phaseExecution);
        if (phaseExecution.getId() != null) {
            throw new BadRequestAlertException("A new phaseExecution cannot already have an ID", ENTITY_NAME, "idexists");
        }
        phaseExecution = phaseExecutionRepository.save(phaseExecution);
        return ResponseEntity.created(new URI("/api/phase-executions/" + phaseExecution.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, phaseExecution.getId().toString()))
            .body(phaseExecution);
    }

    /**
     * {@code PUT  /phase-executions/:id} : Updates an existing phaseExecution.
     *
     * @param id the id of the phaseExecution to save.
     * @param phaseExecution the phaseExecution to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated phaseExecution,
     * or with status {@code 400 (Bad Request)} if the phaseExecution is not valid,
     * or with status {@code 500 (Internal Server Error)} if the phaseExecution couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PhaseExecution> updatePhaseExecution(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PhaseExecution phaseExecution
    ) throws URISyntaxException {
        LOG.debug("REST request to update PhaseExecution : {}, {}", id, phaseExecution);
        if (phaseExecution.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, phaseExecution.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!phaseExecutionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        phaseExecution = phaseExecutionRepository.save(phaseExecution);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, phaseExecution.getId().toString()))
            .body(phaseExecution);
    }

    /**
     * {@code PATCH  /phase-executions/:id} : Partial updates given fields of an existing phaseExecution, field will ignore if it is null
     *
     * @param id the id of the phaseExecution to save.
     * @param phaseExecution the phaseExecution to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated phaseExecution,
     * or with status {@code 400 (Bad Request)} if the phaseExecution is not valid,
     * or with status {@code 404 (Not Found)} if the phaseExecution is not found,
     * or with status {@code 500 (Internal Server Error)} if the phaseExecution couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PhaseExecution> partialUpdatePhaseExecution(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PhaseExecution phaseExecution
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update PhaseExecution partially : {}, {}", id, phaseExecution);
        if (phaseExecution.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, phaseExecution.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!phaseExecutionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PhaseExecution> result = phaseExecutionRepository
            .findById(phaseExecution.getId())
            .map(existingPhaseExecution -> {
                if (phaseExecution.getPhase() != null) {
                    existingPhaseExecution.setPhase(phaseExecution.getPhase());
                }
                if (phaseExecution.getSequenceOrder() != null) {
                    existingPhaseExecution.setSequenceOrder(phaseExecution.getSequenceOrder());
                }
                if (phaseExecution.getStartDate() != null) {
                    existingPhaseExecution.setStartDate(phaseExecution.getStartDate());
                }
                if (phaseExecution.getEndDate() != null) {
                    existingPhaseExecution.setEndDate(phaseExecution.getEndDate());
                }
                if (phaseExecution.getPlannedDurationDays() != null) {
                    existingPhaseExecution.setPlannedDurationDays(phaseExecution.getPlannedDurationDays());
                }
                if (phaseExecution.getActualDurationDays() != null) {
                    existingPhaseExecution.setActualDurationDays(phaseExecution.getActualDurationDays());
                }
                if (phaseExecution.getResponsiblePerson() != null) {
                    existingPhaseExecution.setResponsiblePerson(phaseExecution.getResponsiblePerson());
                }
                if (phaseExecution.getNote() != null) {
                    existingPhaseExecution.setNote(phaseExecution.getNote());
                }

                return existingPhaseExecution;
            })
            .map(phaseExecutionRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, phaseExecution.getId().toString())
        );
    }

    /**
     * {@code GET  /phase-executions} : get all the phaseExecutions.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of phaseExecutions in body.
     */
    @GetMapping("")
    public List<PhaseExecution> getAllPhaseExecutions(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all PhaseExecutions");
        if (eagerload) {
            return phaseExecutionRepository.findAllWithEagerRelationships();
        } else {
            return phaseExecutionRepository.findAll();
        }
    }

    /**
     * {@code GET  /phase-executions/:id} : get the "id" phaseExecution.
     *
     * @param id the id of the phaseExecution to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the phaseExecution, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PhaseExecution> getPhaseExecution(@PathVariable("id") Long id) {
        LOG.debug("REST request to get PhaseExecution : {}", id);
        Optional<PhaseExecution> phaseExecution = phaseExecutionRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(phaseExecution);
    }

    /**
     * {@code DELETE  /phase-executions/:id} : delete the "id" phaseExecution.
     *
     * @param id the id of the phaseExecution to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePhaseExecution(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete PhaseExecution : {}", id);
        phaseExecutionRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code GET  /phase-executions/by-batch/:batchId} : get all phase executions for a batch.
     *
     * @param batchId the id of the batch.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of phaseExecutions in body.
     */
    @GetMapping("/by-batch/{batchId}")
    public List<PhaseExecution> getPhaseExecutionsByBatch(@PathVariable("batchId") Long batchId) {
        LOG.debug("REST request to get PhaseExecutions by Batch : {}", batchId);
        return phaseExecutionRepository.findByBatchIdOrderBySequenceOrder(batchId);
    }
}
