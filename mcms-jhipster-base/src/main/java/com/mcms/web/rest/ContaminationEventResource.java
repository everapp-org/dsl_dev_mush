package com.mcms.web.rest;

import com.mcms.domain.ContaminationEvent;
import com.mcms.repository.ContaminationEventRepository;
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
 * REST controller for managing {@link com.mcms.domain.ContaminationEvent}.
 */
@RestController
@RequestMapping("/api/contamination-events")
@Transactional
public class ContaminationEventResource {

    private static final Logger LOG = LoggerFactory.getLogger(ContaminationEventResource.class);

    private static final String ENTITY_NAME = "contaminationEvent";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ContaminationEventRepository contaminationEventRepository;

    public ContaminationEventResource(ContaminationEventRepository contaminationEventRepository) {
        this.contaminationEventRepository = contaminationEventRepository;
    }

    /**
     * {@code POST  /contamination-events} : Create a new contaminationEvent.
     *
     * @param contaminationEvent the contaminationEvent to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new contaminationEvent, or with status {@code 400 (Bad Request)} if the contaminationEvent has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ContaminationEvent> createContaminationEvent(@Valid @RequestBody ContaminationEvent contaminationEvent)
        throws URISyntaxException {
        LOG.debug("REST request to save ContaminationEvent : {}", contaminationEvent);
        if (contaminationEvent.getId() != null) {
            throw new BadRequestAlertException("A new contaminationEvent cannot already have an ID", ENTITY_NAME, "idexists");
        }
        contaminationEvent = contaminationEventRepository.save(contaminationEvent);
        return ResponseEntity.created(new URI("/api/contamination-events/" + contaminationEvent.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, contaminationEvent.getId().toString()))
            .body(contaminationEvent);
    }

    /**
     * {@code PUT  /contamination-events/:id} : Updates an existing contaminationEvent.
     *
     * @param id the id of the contaminationEvent to save.
     * @param contaminationEvent the contaminationEvent to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated contaminationEvent,
     * or with status {@code 400 (Bad Request)} if the contaminationEvent is not valid,
     * or with status {@code 500 (Internal Server Error)} if the contaminationEvent couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ContaminationEvent> updateContaminationEvent(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ContaminationEvent contaminationEvent
    ) throws URISyntaxException {
        LOG.debug("REST request to update ContaminationEvent : {}, {}", id, contaminationEvent);
        if (contaminationEvent.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, contaminationEvent.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!contaminationEventRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        contaminationEvent = contaminationEventRepository.save(contaminationEvent);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, contaminationEvent.getId().toString()))
            .body(contaminationEvent);
    }

    /**
     * {@code PATCH  /contamination-events/:id} : Partial updates given fields of an existing contaminationEvent, field will ignore if it is null
     *
     * @param id the id of the contaminationEvent to save.
     * @param contaminationEvent the contaminationEvent to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated contaminationEvent,
     * or with status {@code 400 (Bad Request)} if the contaminationEvent is not valid,
     * or with status {@code 404 (Not Found)} if the contaminationEvent is not found,
     * or with status {@code 500 (Internal Server Error)} if the contaminationEvent couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ContaminationEvent> partialUpdateContaminationEvent(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ContaminationEvent contaminationEvent
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ContaminationEvent partially : {}, {}", id, contaminationEvent);
        if (contaminationEvent.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, contaminationEvent.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!contaminationEventRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ContaminationEvent> result = contaminationEventRepository
            .findById(contaminationEvent.getId())
            .map(existingContaminationEvent -> {
                if (contaminationEvent.getDetectedDate() != null) {
                    existingContaminationEvent.setDetectedDate(contaminationEvent.getDetectedDate());
                }
                if (contaminationEvent.getType() != null) {
                    existingContaminationEvent.setType(contaminationEvent.getType());
                }
                if (contaminationEvent.getSeverity() != null) {
                    existingContaminationEvent.setSeverity(contaminationEvent.getSeverity());
                }
                if (contaminationEvent.getAffectedBags() != null) {
                    existingContaminationEvent.setAffectedBags(contaminationEvent.getAffectedBags());
                }
                if (contaminationEvent.getAffectedPercentage() != null) {
                    existingContaminationEvent.setAffectedPercentage(contaminationEvent.getAffectedPercentage());
                }
                if (contaminationEvent.getActionTaken() != null) {
                    existingContaminationEvent.setActionTaken(contaminationEvent.getActionTaken());
                }
                if (contaminationEvent.getResolvedDate() != null) {
                    existingContaminationEvent.setResolvedDate(contaminationEvent.getResolvedDate());
                }
                if (contaminationEvent.getLossKg() != null) {
                    existingContaminationEvent.setLossKg(contaminationEvent.getLossKg());
                }
                if (contaminationEvent.getRootCauseAnalysis() != null) {
                    existingContaminationEvent.setRootCauseAnalysis(contaminationEvent.getRootCauseAnalysis());
                }
                if (contaminationEvent.getPreventiveMeasures() != null) {
                    existingContaminationEvent.setPreventiveMeasures(contaminationEvent.getPreventiveMeasures());
                }
                if (contaminationEvent.getDetectedBy() != null) {
                    existingContaminationEvent.setDetectedBy(contaminationEvent.getDetectedBy());
                }
                if (contaminationEvent.getPhotosReference() != null) {
                    existingContaminationEvent.setPhotosReference(contaminationEvent.getPhotosReference());
                }
                if (contaminationEvent.getNote() != null) {
                    existingContaminationEvent.setNote(contaminationEvent.getNote());
                }

                return existingContaminationEvent;
            })
            .map(contaminationEventRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, contaminationEvent.getId().toString())
        );
    }

    /**
     * {@code GET  /contamination-events} : get all the contaminationEvents.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of contaminationEvents in body.
     */
    @GetMapping("")
    public List<ContaminationEvent> getAllContaminationEvents(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all ContaminationEvents");
        if (eagerload) {
            return contaminationEventRepository.findAllWithEagerRelationships();
        } else {
            return contaminationEventRepository.findAll();
        }
    }

    /**
     * {@code GET  /contamination-events/:id} : get the "id" contaminationEvent.
     *
     * @param id the id of the contaminationEvent to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the contaminationEvent, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ContaminationEvent> getContaminationEvent(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ContaminationEvent : {}", id);
        Optional<ContaminationEvent> contaminationEvent = contaminationEventRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(contaminationEvent);
    }

    /**
     * {@code DELETE  /contamination-events/:id} : delete the "id" contaminationEvent.
     *
     * @param id the id of the contaminationEvent to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContaminationEvent(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ContaminationEvent : {}", id);
        contaminationEventRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
