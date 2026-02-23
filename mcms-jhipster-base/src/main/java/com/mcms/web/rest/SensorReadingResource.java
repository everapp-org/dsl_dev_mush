package com.mcms.web.rest;

import com.mcms.domain.SensorReading;
import com.mcms.repository.SensorReadingRepository;
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
 * REST controller for managing {@link com.mcms.domain.SensorReading}.
 */
@RestController
@RequestMapping("/api/sensor-readings")
@Transactional
public class SensorReadingResource {

    private static final Logger LOG = LoggerFactory.getLogger(SensorReadingResource.class);

    private static final String ENTITY_NAME = "sensorReading";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SensorReadingRepository sensorReadingRepository;

    public SensorReadingResource(SensorReadingRepository sensorReadingRepository) {
        this.sensorReadingRepository = sensorReadingRepository;
    }

    /**
     * {@code POST  /sensor-readings} : Create a new sensorReading.
     *
     * @param sensorReading the sensorReading to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new sensorReading, or with status {@code 400 (Bad Request)} if the sensorReading has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_OPERATOR')")
    public ResponseEntity<SensorReading> createSensorReading(@Valid @RequestBody SensorReading sensorReading) throws URISyntaxException {
        LOG.debug("REST request to save SensorReading : {}", sensorReading);
        if (sensorReading.getId() != null) {
            throw new BadRequestAlertException("A new sensorReading cannot already have an ID", ENTITY_NAME, "idexists");
        }
        sensorReading = sensorReadingRepository.save(sensorReading);
        return ResponseEntity.created(new URI("/api/sensor-readings/" + sensorReading.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, sensorReading.getId().toString()))
            .body(sensorReading);
    }

    /**
     * {@code PUT  /sensor-readings/:id} : Updates an existing sensorReading.
     *
     * @param id the id of the sensorReading to save.
     * @param sensorReading the sensorReading to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sensorReading,
     * or with status {@code 400 (Bad Request)} if the sensorReading is not valid,
     * or with status {@code 500 (Internal Server Error)} if the sensorReading couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<SensorReading> updateSensorReading(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SensorReading sensorReading
    ) throws URISyntaxException {
        LOG.debug("REST request to update SensorReading : {}, {}", id, sensorReading);
        if (sensorReading.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sensorReading.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sensorReadingRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        sensorReading = sensorReadingRepository.save(sensorReading);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, sensorReading.getId().toString()))
            .body(sensorReading);
    }

    /**
     * {@code PATCH  /sensor-readings/:id} : Partial updates given fields of an existing sensorReading, field will ignore if it is null
     *
     * @param id the id of the sensorReading to save.
     * @param sensorReading the sensorReading to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sensorReading,
     * or with status {@code 400 (Bad Request)} if the sensorReading is not valid,
     * or with status {@code 404 (Not Found)} if the sensorReading is not found,
     * or with status {@code 500 (Internal Server Error)} if the sensorReading couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<SensorReading> partialUpdateSensorReading(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SensorReading sensorReading
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update SensorReading partially : {}, {}", id, sensorReading);
        if (sensorReading.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sensorReading.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sensorReadingRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SensorReading> result = sensorReadingRepository
            .findById(sensorReading.getId())
            .map(existingSensorReading -> {
                if (sensorReading.getTimestamp() != null) {
                    existingSensorReading.setTimestamp(sensorReading.getTimestamp());
                }
                if (sensorReading.getValue() != null) {
                    existingSensorReading.setValue(sensorReading.getValue());
                }
                if (sensorReading.getUnit() != null) {
                    existingSensorReading.setUnit(sensorReading.getUnit());
                }

                return existingSensorReading;
            })
            .map(sensorReadingRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, sensorReading.getId().toString())
        );
    }

    /**
     * {@code GET  /sensor-readings} : get all the sensorReadings.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of sensorReadings in body.
     */
    @GetMapping("")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_OPERATOR', 'ROLE_USER')")
    public List<SensorReading> getAllSensorReadings(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all SensorReadings");
        if (eagerload) {
            return sensorReadingRepository.findAllWithEagerRelationships();
        } else {
            return sensorReadingRepository.findAll();
        }
    }

    /**
     * {@code GET  /sensor-readings/:id} : get the "id" sensorReading.
     *
     * @param id the id of the sensorReading to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the sensorReading, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_OPERATOR', 'ROLE_USER')")
    public ResponseEntity<SensorReading> getSensorReading(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SensorReading : {}", id);
        Optional<SensorReading> sensorReading = sensorReadingRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(sensorReading);
    }

    /**
     * {@code DELETE  /sensor-readings/:id} : delete the "id" sensorReading.
     *
     * @param id the id of the sensorReading to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<Void> deleteSensorReading(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SensorReading : {}", id);
        sensorReadingRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
