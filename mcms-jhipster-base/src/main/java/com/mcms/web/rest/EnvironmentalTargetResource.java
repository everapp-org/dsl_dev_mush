package com.mcms.web.rest;

import com.mcms.domain.EnvironmentalTarget;
import com.mcms.repository.EnvironmentalTargetRepository;
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
 * REST controller for managing {@link com.mcms.domain.EnvironmentalTarget}.
 */
@RestController
@RequestMapping("/api/environmental-targets")
@Transactional
public class EnvironmentalTargetResource {

    private static final Logger LOG = LoggerFactory.getLogger(EnvironmentalTargetResource.class);

    private static final String ENTITY_NAME = "environmentalTarget";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EnvironmentalTargetRepository environmentalTargetRepository;

    public EnvironmentalTargetResource(EnvironmentalTargetRepository environmentalTargetRepository) {
        this.environmentalTargetRepository = environmentalTargetRepository;
    }

    /**
     * {@code POST  /environmental-targets} : Create a new environmentalTarget.
     *
     * @param environmentalTarget the environmentalTarget to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new environmentalTarget, or with status {@code 400 (Bad Request)} if the environmentalTarget has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<EnvironmentalTarget> createEnvironmentalTarget(@Valid @RequestBody EnvironmentalTarget environmentalTarget)
        throws URISyntaxException {
        LOG.debug("REST request to save EnvironmentalTarget : {}", environmentalTarget);
        if (environmentalTarget.getId() != null) {
            throw new BadRequestAlertException("A new environmentalTarget cannot already have an ID", ENTITY_NAME, "idexists");
        }
        environmentalTarget = environmentalTargetRepository.save(environmentalTarget);
        return ResponseEntity.created(new URI("/api/environmental-targets/" + environmentalTarget.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, environmentalTarget.getId().toString()))
            .body(environmentalTarget);
    }

    /**
     * {@code PUT  /environmental-targets/:id} : Updates an existing environmentalTarget.
     *
     * @param id the id of the environmentalTarget to save.
     * @param environmentalTarget the environmentalTarget to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated environmentalTarget,
     * or with status {@code 400 (Bad Request)} if the environmentalTarget is not valid,
     * or with status {@code 500 (Internal Server Error)} if the environmentalTarget couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<EnvironmentalTarget> updateEnvironmentalTarget(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody EnvironmentalTarget environmentalTarget
    ) throws URISyntaxException {
        LOG.debug("REST request to update EnvironmentalTarget : {}, {}", id, environmentalTarget);
        if (environmentalTarget.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, environmentalTarget.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!environmentalTargetRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        environmentalTarget = environmentalTargetRepository.save(environmentalTarget);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, environmentalTarget.getId().toString()))
            .body(environmentalTarget);
    }

    /**
     * {@code PATCH  /environmental-targets/:id} : Partial updates given fields of an existing environmentalTarget, field will ignore if it is null
     *
     * @param id the id of the environmentalTarget to save.
     * @param environmentalTarget the environmentalTarget to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated environmentalTarget,
     * or with status {@code 400 (Bad Request)} if the environmentalTarget is not valid,
     * or with status {@code 404 (Not Found)} if the environmentalTarget is not found,
     * or with status {@code 500 (Internal Server Error)} if the environmentalTarget couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<EnvironmentalTarget> partialUpdateEnvironmentalTarget(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody EnvironmentalTarget environmentalTarget
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update EnvironmentalTarget partially : {}, {}", id, environmentalTarget);
        if (environmentalTarget.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, environmentalTarget.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!environmentalTargetRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<EnvironmentalTarget> result = environmentalTargetRepository
            .findById(environmentalTarget.getId())
            .map(existingEnvironmentalTarget -> {
                if (environmentalTarget.getPhase() != null) {
                    existingEnvironmentalTarget.setPhase(environmentalTarget.getPhase());
                }
                if (environmentalTarget.getTempMinC() != null) {
                    existingEnvironmentalTarget.setTempMinC(environmentalTarget.getTempMinC());
                }
                if (environmentalTarget.getTempMaxC() != null) {
                    existingEnvironmentalTarget.setTempMaxC(environmentalTarget.getTempMaxC());
                }
                if (environmentalTarget.getHumidityMinPercent() != null) {
                    existingEnvironmentalTarget.setHumidityMinPercent(environmentalTarget.getHumidityMinPercent());
                }
                if (environmentalTarget.getHumidityMaxPercent() != null) {
                    existingEnvironmentalTarget.setHumidityMaxPercent(environmentalTarget.getHumidityMaxPercent());
                }
                if (environmentalTarget.getCo2MaxPpm() != null) {
                    existingEnvironmentalTarget.setCo2MaxPpm(environmentalTarget.getCo2MaxPpm());
                }
                if (environmentalTarget.getLightLux() != null) {
                    existingEnvironmentalTarget.setLightLux(environmentalTarget.getLightLux());
                }
                if (environmentalTarget.getFreshAirExchangesPerHour() != null) {
                    existingEnvironmentalTarget.setFreshAirExchangesPerHour(environmentalTarget.getFreshAirExchangesPerHour());
                }

                return existingEnvironmentalTarget;
            })
            .map(environmentalTargetRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, environmentalTarget.getId().toString())
        );
    }

    /**
     * {@code GET  /environmental-targets} : get all the environmentalTargets.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of environmentalTargets in body.
     */
    @GetMapping("")
    public List<EnvironmentalTarget> getAllEnvironmentalTargets(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all EnvironmentalTargets");
        if (eagerload) {
            return environmentalTargetRepository.findAllWithEagerRelationships();
        } else {
            return environmentalTargetRepository.findAll();
        }
    }

    /**
     * {@code GET  /environmental-targets/:id} : get the "id" environmentalTarget.
     *
     * @param id the id of the environmentalTarget to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the environmentalTarget, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<EnvironmentalTarget> getEnvironmentalTarget(@PathVariable("id") Long id) {
        LOG.debug("REST request to get EnvironmentalTarget : {}", id);
        Optional<EnvironmentalTarget> environmentalTarget = environmentalTargetRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(environmentalTarget);
    }

    /**
     * {@code DELETE  /environmental-targets/:id} : delete the "id" environmentalTarget.
     *
     * @param id the id of the environmentalTarget to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnvironmentalTarget(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete EnvironmentalTarget : {}", id);
        environmentalTargetRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
