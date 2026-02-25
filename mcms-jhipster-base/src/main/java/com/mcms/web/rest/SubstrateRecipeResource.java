package com.mcms.web.rest;

import com.mcms.domain.Batch;
import com.mcms.domain.SubstrateRecipe;
import com.mcms.repository.BatchRepository;
import com.mcms.repository.SubstrateRecipeRepository;
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
 * REST controller for managing {@link com.mcms.domain.SubstrateRecipe}.
 */
@RestController
@RequestMapping("/api/substrate-recipes")
@Transactional
public class SubstrateRecipeResource {

    private static final Logger LOG = LoggerFactory.getLogger(SubstrateRecipeResource.class);

    private static final String ENTITY_NAME = "substrateRecipe";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SubstrateRecipeRepository substrateRecipeRepository;
    private final BatchRepository batchRepository;

    public SubstrateRecipeResource(SubstrateRecipeRepository substrateRecipeRepository, BatchRepository batchRepository) {
        this.substrateRecipeRepository = substrateRecipeRepository;
        this.batchRepository = batchRepository;
    }

    /**
     * {@code POST  /substrate-recipes} : Create a new substrateRecipe.
     *
     * @param substrateRecipe the substrateRecipe to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new substrateRecipe, or with status {@code 400 (Bad Request)} if the substrateRecipe has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<SubstrateRecipe> createSubstrateRecipe(@Valid @RequestBody SubstrateRecipe substrateRecipe)
        throws URISyntaxException {
        LOG.debug("REST request to save SubstrateRecipe : {}", substrateRecipe);
        if (substrateRecipe.getId() != null) {
            throw new BadRequestAlertException("A new substrateRecipe cannot already have an ID", ENTITY_NAME, "idexists");
        }
        substrateRecipe = substrateRecipeRepository.save(substrateRecipe);
        return ResponseEntity.created(new URI("/api/substrate-recipes/" + substrateRecipe.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, substrateRecipe.getId().toString()))
            .body(substrateRecipe);
    }

    /**
     * {@code PUT  /substrate-recipes/:id} : Updates an existing substrateRecipe.
     *
     * @param id the id of the substrateRecipe to save.
     * @param substrateRecipe the substrateRecipe to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated substrateRecipe,
     * or with status {@code 400 (Bad Request)} if the substrateRecipe is not valid,
     * or with status {@code 500 (Internal Server Error)} if the substrateRecipe couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<SubstrateRecipe> updateSubstrateRecipe(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SubstrateRecipe substrateRecipe
    ) throws URISyntaxException {
        LOG.debug("REST request to update SubstrateRecipe : {}, {}", id, substrateRecipe);
        if (substrateRecipe.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, substrateRecipe.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!substrateRecipeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        substrateRecipe = substrateRecipeRepository.save(substrateRecipe);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, substrateRecipe.getId().toString()))
            .body(substrateRecipe);
    }

    /**
     * {@code PATCH  /substrate-recipes/:id} : Partial updates given fields of an existing substrateRecipe, field will ignore if it is null
     *
     * @param id the id of the substrateRecipe to save.
     * @param substrateRecipe the substrateRecipe to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated substrateRecipe,
     * or with status {@code 400 (Bad Request)} if the substrateRecipe is not valid,
     * or with status {@code 404 (Not Found)} if the substrateRecipe is not found,
     * or with status {@code 500 (Internal Server Error)} if the substrateRecipe couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<SubstrateRecipe> partialUpdateSubstrateRecipe(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SubstrateRecipe substrateRecipe
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update SubstrateRecipe partially : {}, {}", id, substrateRecipe);
        if (substrateRecipe.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, substrateRecipe.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!substrateRecipeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SubstrateRecipe> result = substrateRecipeRepository
            .findById(substrateRecipe.getId())
            .map(existingSubstrateRecipe -> {
                if (substrateRecipe.getName() != null) {
                    existingSubstrateRecipe.setName(substrateRecipe.getName());
                }
                if (substrateRecipe.getVersion() != null) {
                    existingSubstrateRecipe.setVersion(substrateRecipe.getVersion());
                }
                if (substrateRecipe.getBaseType() != null) {
                    existingSubstrateRecipe.setBaseType(substrateRecipe.getBaseType());
                }
                if (substrateRecipe.getCompositionDetail() != null) {
                    existingSubstrateRecipe.setCompositionDetail(substrateRecipe.getCompositionDetail());
                }
                if (substrateRecipe.getSterilizationMethod() != null) {
                    existingSubstrateRecipe.setSterilizationMethod(substrateRecipe.getSterilizationMethod());
                }
                if (substrateRecipe.getMoistureTargetPercent() != null) {
                    existingSubstrateRecipe.setMoistureTargetPercent(substrateRecipe.getMoistureTargetPercent());
                }
                if (substrateRecipe.getPhTarget() != null) {
                    existingSubstrateRecipe.setPhTarget(substrateRecipe.getPhTarget());
                }
                if (substrateRecipe.getSupplementNotes() != null) {
                    existingSubstrateRecipe.setSupplementNotes(substrateRecipe.getSupplementNotes());
                }
                if (substrateRecipe.getActive() != null) {
                    existingSubstrateRecipe.setActive(substrateRecipe.getActive());
                }

                return existingSubstrateRecipe;
            })
            .map(substrateRecipeRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, substrateRecipe.getId().toString())
        );
    }

    /**
     * {@code GET  /substrate-recipes} : get all the substrateRecipes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of substrateRecipes in body.
     */
    @GetMapping("")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_OPERATOR', 'ROLE_USER')")
    public List<SubstrateRecipe> getAllSubstrateRecipes() {
        LOG.debug("REST request to get all SubstrateRecipes");
        return substrateRecipeRepository.findAll();
    }

    /**
     * {@code GET  /substrate-recipes/:id} : get the "id" substrateRecipe.
     *
     * @param id the id of the substrateRecipe to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the substrateRecipe, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_OPERATOR', 'ROLE_USER')")
    public ResponseEntity<SubstrateRecipe> getSubstrateRecipe(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SubstrateRecipe : {}", id);
        Optional<SubstrateRecipe> substrateRecipe = substrateRecipeRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(substrateRecipe);
    }

    /**
     * {@code DELETE  /substrate-recipes/:id} : delete the "id" substrateRecipe.
     *
     * @param id the id of the substrateRecipe to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<Void> deleteSubstrateRecipe(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SubstrateRecipe : {}", id);
        substrateRecipeRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code GET  /substrate-recipes/:id/batches} : get all batches using this recipe.
     *
     * @param id the id of the substrateRecipe.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of batches in body.
     */
    @GetMapping("/{id}/batches")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_OPERATOR', 'ROLE_USER')")
    public ResponseEntity<List<Batch>> getBatchesForRecipe(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Batches for SubstrateRecipe : {}", id);
        List<Batch> batches = batchRepository.findByRecipeId(id);
        return ResponseEntity.ok().body(batches);
    }
}
