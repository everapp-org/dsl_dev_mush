package com.mcms.web.rest;

import com.mcms.domain.Material;
import com.mcms.repository.MaterialRepository;
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
 * REST controller for managing {@link com.mcms.domain.Material}.
 */
@RestController
@RequestMapping("/api/materials")
@Transactional
public class MaterialResource {

    private static final Logger LOG = LoggerFactory.getLogger(MaterialResource.class);

    private static final String ENTITY_NAME = "material";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MaterialRepository materialRepository;

    public MaterialResource(MaterialRepository materialRepository) {
        this.materialRepository = materialRepository;
    }

    /**
     * {@code POST  /materials} : Create a new material.
     *
     * @param material the material to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new material, or with status {@code 400 (Bad Request)} if the material has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Material> createMaterial(@Valid @RequestBody Material material) throws URISyntaxException {
        LOG.debug("REST request to save Material : {}", material);
        if (material.getId() != null) {
            throw new BadRequestAlertException("A new material cannot already have an ID", ENTITY_NAME, "idexists");
        }
        material = materialRepository.save(material);
        return ResponseEntity.created(new URI("/api/materials/" + material.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, material.getId().toString()))
            .body(material);
    }

    /**
     * {@code PUT  /materials/:id} : Updates an existing material.
     *
     * @param id the id of the material to save.
     * @param material the material to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated material,
     * or with status {@code 400 (Bad Request)} if the material is not valid,
     * or with status {@code 500 (Internal Server Error)} if the material couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Material> updateMaterial(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Material material
    ) throws URISyntaxException {
        LOG.debug("REST request to update Material : {}, {}", id, material);
        if (material.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, material.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!materialRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        material = materialRepository.save(material);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, material.getId().toString()))
            .body(material);
    }

    /**
     * {@code PATCH  /materials/:id} : Partial updates given fields of an existing material, field will ignore if it is null
     *
     * @param id the id of the material to save.
     * @param material the material to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated material,
     * or with status {@code 400 (Bad Request)} if the material is not valid,
     * or with status {@code 404 (Not Found)} if the material is not found,
     * or with status {@code 500 (Internal Server Error)} if the material couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Material> partialUpdateMaterial(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Material material
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Material partially : {}, {}", id, material);
        if (material.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, material.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!materialRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Material> result = materialRepository
            .findById(material.getId())
            .map(existingMaterial -> {
                if (material.getCode() != null) {
                    existingMaterial.setCode(material.getCode());
                }
                if (material.getName() != null) {
                    existingMaterial.setName(material.getName());
                }
                if (material.getCategory() != null) {
                    existingMaterial.setCategory(material.getCategory());
                }
                if (material.getDefaultUnit() != null) {
                    existingMaterial.setDefaultUnit(material.getDefaultUnit());
                }
                if (material.getMinimumStockLevel() != null) {
                    existingMaterial.setMinimumStockLevel(material.getMinimumStockLevel());
                }
                if (material.getDescription() != null) {
                    existingMaterial.setDescription(material.getDescription());
                }
                if (material.getActive() != null) {
                    existingMaterial.setActive(material.getActive());
                }
                if (material.getNote() != null) {
                    existingMaterial.setNote(material.getNote());
                }

                return existingMaterial;
            })
            .map(materialRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, material.getId().toString())
        );
    }

    /**
     * {@code GET  /materials} : get all the materials.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of materials in body.
     */
    @GetMapping("")
    public List<Material> getAllMaterials() {
        LOG.debug("REST request to get all Materials");
        return materialRepository.findAll();
    }

    /**
     * {@code GET  /materials/:id} : get the "id" material.
     *
     * @param id the id of the material to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the material, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Material> getMaterial(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Material : {}", id);
        Optional<Material> material = materialRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(material);
    }

    /**
     * {@code DELETE  /materials/:id} : delete the "id" material.
     *
     * @param id the id of the material to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMaterial(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Material : {}", id);
        materialRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
