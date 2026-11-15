package com.mcms.web.rest;

import com.mcms.domain.EnvironmentalAlert;
import com.mcms.repository.EnvironmentalAlertRepository;
import com.mcms.security.AuthoritiesConstants;
import com.mcms.web.rest.dto.AcknowledgeAlertRequest;
import com.mcms.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.time.Instant;
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
 * REST controller for managing {@link com.mcms.domain.EnvironmentalAlert}.
 */
@RestController
@RequestMapping("/api/environmental-alerts")
@Transactional
public class EnvironmentalAlertResource {

    private static final Logger LOG = LoggerFactory.getLogger(EnvironmentalAlertResource.class);

    private static final String ENTITY_NAME = "environmentalAlert";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EnvironmentalAlertRepository environmentalAlertRepository;

    public EnvironmentalAlertResource(EnvironmentalAlertRepository environmentalAlertRepository) {
        this.environmentalAlertRepository = environmentalAlertRepository;
    }

    /**
     * {@code POST  /environmental-alerts} : Create a new environmentalAlert.
     *
     * @param environmentalAlert the environmentalAlert to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new environmentalAlert, or with status {@code 400 (Bad Request)} if the environmentalAlert has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '" + AuthoritiesConstants.MANAGER + "')")
    public ResponseEntity<EnvironmentalAlert> createEnvironmentalAlert(@Valid @RequestBody EnvironmentalAlert environmentalAlert)
        throws URISyntaxException {
        LOG.debug("REST request to save EnvironmentalAlert : {}", environmentalAlert);
        if (environmentalAlert.getId() != null) {
            throw new BadRequestAlertException("A new environmentalAlert cannot already have an ID", ENTITY_NAME, "idexists");
        }
        environmentalAlert = environmentalAlertRepository.save(environmentalAlert);
        return ResponseEntity.created(new URI("/api/environmental-alerts/" + environmentalAlert.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, environmentalAlert.getId().toString()))
            .body(environmentalAlert);
    }

    /**
     * {@code PUT  /environmental-alerts/:id} : Updates an existing environmentalAlert.
     *
     * @param id the id of the environmentalAlert to save.
     * @param environmentalAlert the environmentalAlert to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated environmentalAlert,
     * or with status {@code 400 (Bad Request)} if the environmentalAlert is not valid,
     * or with status {@code 500 (Internal Server Error)} if the environmentalAlert couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '" + AuthoritiesConstants.MANAGER + "')")
    public ResponseEntity<EnvironmentalAlert> updateEnvironmentalAlert(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody EnvironmentalAlert environmentalAlert
    ) throws URISyntaxException {
        LOG.debug("REST request to update EnvironmentalAlert : {}, {}", id, environmentalAlert);
        if (environmentalAlert.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, environmentalAlert.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!environmentalAlertRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        environmentalAlert = environmentalAlertRepository.save(environmentalAlert);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, environmentalAlert.getId().toString()))
            .body(environmentalAlert);
    }

    /**
     * {@code PATCH  /environmental-alerts/:id} : Partial updates given fields of an existing environmentalAlert, field will ignore if it is null
     *
     * @param id the id of the environmentalAlert to save.
     * @param environmentalAlert the environmentalAlert to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated environmentalAlert,
     * or with status {@code 400 (Bad Request)} if the environmentalAlert is not valid,
     * or with status {@code 404 (Not Found)} if the environmentalAlert is not found,
     * or with status {@code 500 (Internal Server Error)} if the environmentalAlert couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '" + AuthoritiesConstants.MANAGER + "')")
    public ResponseEntity<EnvironmentalAlert> partialUpdateEnvironmentalAlert(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody EnvironmentalAlert environmentalAlert
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update EnvironmentalAlert partially : {}, {}", id, environmentalAlert);
        if (environmentalAlert.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, environmentalAlert.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!environmentalAlertRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<EnvironmentalAlert> result = environmentalAlertRepository
            .findById(environmentalAlert.getId())
            .map(existingEnvironmentalAlert -> {
                if (environmentalAlert.getAlertTime() != null) {
                    existingEnvironmentalAlert.setAlertTime(environmentalAlert.getAlertTime());
                }
                if (environmentalAlert.getSeverity() != null) {
                    existingEnvironmentalAlert.setSeverity(environmentalAlert.getSeverity());
                }
                if (environmentalAlert.getParameter() != null) {
                    existingEnvironmentalAlert.setParameter(environmentalAlert.getParameter());
                }
                if (environmentalAlert.getActualValue() != null) {
                    existingEnvironmentalAlert.setActualValue(environmentalAlert.getActualValue());
                }
                if (environmentalAlert.getThresholdValue() != null) {
                    existingEnvironmentalAlert.setThresholdValue(environmentalAlert.getThresholdValue());
                }
                if (environmentalAlert.getMessage() != null) {
                    existingEnvironmentalAlert.setMessage(environmentalAlert.getMessage());
                }
                if (environmentalAlert.getAcknowledged() != null) {
                    existingEnvironmentalAlert.setAcknowledged(environmentalAlert.getAcknowledged());
                }
                if (environmentalAlert.getAcknowledgedBy() != null) {
                    existingEnvironmentalAlert.setAcknowledgedBy(environmentalAlert.getAcknowledgedBy());
                }
                if (environmentalAlert.getAcknowledgedAt() != null) {
                    existingEnvironmentalAlert.setAcknowledgedAt(environmentalAlert.getAcknowledgedAt());
                }
                if (environmentalAlert.getResolutionNote() != null) {
                    existingEnvironmentalAlert.setResolutionNote(environmentalAlert.getResolutionNote());
                }

                return existingEnvironmentalAlert;
            })
            .map(environmentalAlertRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, environmentalAlert.getId().toString())
        );
    }

    /**
     * {@code GET  /environmental-alerts} : get all the environmentalAlerts.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of environmentalAlerts in body.
     */
    @GetMapping("")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '" + AuthoritiesConstants.MANAGER + "', '" + AuthoritiesConstants.OPERATOR + "')")
    public List<EnvironmentalAlert> getAllEnvironmentalAlerts(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all EnvironmentalAlerts");
        if (eagerload) {
            return environmentalAlertRepository.findAllWithEagerRelationships();
        } else {
            return environmentalAlertRepository.findAll();
        }
    }

    /**
     * {@code GET  /environmental-alerts/:id} : get the "id" environmentalAlert.
     *
     * @param id the id of the environmentalAlert to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the environmentalAlert, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '" + AuthoritiesConstants.MANAGER + "', '" + AuthoritiesConstants.OPERATOR + "')")
    public ResponseEntity<EnvironmentalAlert> getEnvironmentalAlert(@PathVariable("id") Long id) {
        LOG.debug("REST request to get EnvironmentalAlert : {}", id);
        Optional<EnvironmentalAlert> environmentalAlert = environmentalAlertRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(environmentalAlert);
    }

    /**
     * {@code DELETE  /environmental-alerts/:id} : delete the "id" environmentalAlert.
     *
     * @param id the id of the environmentalAlert to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '" + AuthoritiesConstants.MANAGER + "')")
    public ResponseEntity<Void> deleteEnvironmentalAlert(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete EnvironmentalAlert : {}", id);
        environmentalAlertRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code PUT  /environmental-alerts/:id/acknowledge} : Acknowledge an environmental alert.
     * Operators can acknowledge alerts for their rooms.
     *
     * @param id the id of the environmentalAlert to acknowledge.
     * @param request the acknowledgement request with resolution note.
     * @param principal the current user.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated environmentalAlert.
     */
    @PutMapping("/{id}/acknowledge")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '" + AuthoritiesConstants.MANAGER + "', '" + AuthoritiesConstants.OPERATOR + "')")
    public ResponseEntity<EnvironmentalAlert> acknowledgeAlert(
        @PathVariable("id") Long id,
        @Valid @RequestBody AcknowledgeAlertRequest request,
        Principal principal
    ) {
        LOG.debug("REST request to acknowledge EnvironmentalAlert : {}", id);

        Optional<EnvironmentalAlert> result = environmentalAlertRepository
            .findById(id)
            .map(alert -> {
                alert.setAcknowledged(true);
                alert.setAcknowledgedBy(principal.getName());
                alert.setAcknowledgedAt(Instant.now());
                alert.setResolutionNote(request.getResolutionNote());
                return environmentalAlertRepository.save(alert);
            });

        if (result.isEmpty()) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .body(result.get());
    }
}
