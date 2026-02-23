package com.mcms.web.rest;

import com.mcms.domain.Strain;
import com.mcms.repository.StrainRepository;
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
 * REST controller for managing {@link com.mcms.domain.Strain}.
 */
@RestController
@RequestMapping("/api/strains")
@Transactional
public class StrainResource {

    private static final Logger LOG = LoggerFactory.getLogger(StrainResource.class);

    private static final String ENTITY_NAME = "strain";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StrainRepository strainRepository;

    public StrainResource(StrainRepository strainRepository) {
        this.strainRepository = strainRepository;
    }

    /**
     * {@code POST  /strains} : Create a new strain.
     *
     * @param strain the strain to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new strain, or with status {@code 400 (Bad Request)} if the strain has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Strain> createStrain(@Valid @RequestBody Strain strain) throws URISyntaxException {
        LOG.debug("REST request to save Strain : {}", strain);
        if (strain.getId() != null) {
            throw new BadRequestAlertException("A new strain cannot already have an ID", ENTITY_NAME, "idexists");
        }
        strain = strainRepository.save(strain);
        return ResponseEntity.created(new URI("/api/strains/" + strain.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, strain.getId().toString()))
            .body(strain);
    }

    /**
     * {@code PUT  /strains/:id} : Updates an existing strain.
     *
     * @param id the id of the strain to save.
     * @param strain the strain to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated strain,
     * or with status {@code 400 (Bad Request)} if the strain is not valid,
     * or with status {@code 500 (Internal Server Error)} if the strain couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Strain> updateStrain(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Strain strain
    ) throws URISyntaxException {
        LOG.debug("REST request to update Strain : {}, {}", id, strain);
        if (strain.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, strain.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!strainRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        strain = strainRepository.save(strain);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, strain.getId().toString()))
            .body(strain);
    }

    /**
     * {@code PATCH  /strains/:id} : Partial updates given fields of an existing strain, field will ignore if it is null
     *
     * @param id the id of the strain to save.
     * @param strain the strain to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated strain,
     * or with status {@code 400 (Bad Request)} if the strain is not valid,
     * or with status {@code 404 (Not Found)} if the strain is not found,
     * or with status {@code 500 (Internal Server Error)} if the strain couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Strain> partialUpdateStrain(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Strain strain
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Strain partially : {}, {}", id, strain);
        if (strain.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, strain.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!strainRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Strain> result = strainRepository
            .findById(strain.getId())
            .map(existingStrain -> {
                if (strain.getName() != null) {
                    existingStrain.setName(strain.getName());
                }
                if (strain.getSpecies() != null) {
                    existingStrain.setSpecies(strain.getSpecies());
                }
                if (strain.getVariety() != null) {
                    existingStrain.setVariety(strain.getVariety());
                }
                if (strain.getOptimalTempMinC() != null) {
                    existingStrain.setOptimalTempMinC(strain.getOptimalTempMinC());
                }
                if (strain.getOptimalTempMaxC() != null) {
                    existingStrain.setOptimalTempMaxC(strain.getOptimalTempMaxC());
                }
                if (strain.getOptimalHumidityMin() != null) {
                    existingStrain.setOptimalHumidityMin(strain.getOptimalHumidityMin());
                }
                if (strain.getOptimalHumidityMax() != null) {
                    existingStrain.setOptimalHumidityMax(strain.getOptimalHumidityMax());
                }
                if (strain.getOptimalCO2MaxPpm() != null) {
                    existingStrain.setOptimalCO2MaxPpm(strain.getOptimalCO2MaxPpm());
                }
                if (strain.getColonizationDaysMin() != null) {
                    existingStrain.setColonizationDaysMin(strain.getColonizationDaysMin());
                }
                if (strain.getColonizationDaysMax() != null) {
                    existingStrain.setColonizationDaysMax(strain.getColonizationDaysMax());
                }
                if (strain.getExpectedYieldPercent() != null) {
                    existingStrain.setExpectedYieldPercent(strain.getExpectedYieldPercent());
                }
                if (strain.getShelfLifeDays() != null) {
                    existingStrain.setShelfLifeDays(strain.getShelfLifeDays());
                }
                if (strain.getNote() != null) {
                    existingStrain.setNote(strain.getNote());
                }
                if (strain.getActive() != null) {
                    existingStrain.setActive(strain.getActive());
                }

                return existingStrain;
            })
            .map(strainRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, strain.getId().toString())
        );
    }

    /**
     * {@code GET  /strains} : get all the strains.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of strains in body.
     */
    @GetMapping("")
    public List<Strain> getAllStrains() {
        LOG.debug("REST request to get all Strains");
        return strainRepository.findAll();
    }

    /**
     * {@code GET  /strains/:id} : get the "id" strain.
     *
     * @param id the id of the strain to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the strain, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Strain> getStrain(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Strain : {}", id);
        Optional<Strain> strain = strainRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(strain);
    }

    /**
     * {@code DELETE  /strains/:id} : delete the "id" strain.
     *
     * @param id the id of the strain to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStrain(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Strain : {}", id);
        strainRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
