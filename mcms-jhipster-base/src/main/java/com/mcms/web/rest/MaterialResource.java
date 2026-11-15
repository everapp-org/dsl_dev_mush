package com.mcms.web.rest;

import com.mcms.domain.Material;
import com.mcms.domain.enumeration.MaterialCategory;
import com.mcms.domain.enumeration.UnitOfMeasure;
import com.mcms.repository.MaterialRepository;
import com.mcms.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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

    /**
     * {@code POST  /materials/import} : Import materials from CSV file.
     *
     * CSV format: code,name,category,defaultUnit (header row required)
     * Example:
     * code,name,category,defaultUnit
     * "MAT-SPAWN-PO-01","Pleurotus ostreatus grain spawn","SPAWN","BAG"
     * "MAT-SUB-STRAW","Wheat straw substrate","SUBSTRATE","KG"
     *
     * @param file the CSV file to import.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and count of imported materials.
     */
    @PostMapping("/import")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<Map<String, Object>> importMaterials(@RequestParam("file") MultipartFile file) {
        LOG.debug("REST request to import Materials from CSV file: {}", file.getOriginalFilename());

        if (file.isEmpty()) {
            throw new BadRequestAlertException("File is empty", ENTITY_NAME, "fileempty");
        }

        List<Material> importedMaterials = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        int lineNumber = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            String[] headers = null;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                // Skip empty lines
                if (line.isEmpty()) {
                    continue;
                }

                // Parse CSV line (handle quoted fields)
                String[] fields = parseCsvLine(line);

                // First non-empty line is the header
                if (headers == null) {
                    headers = fields;
                    // Validate header
                    if (
                        fields.length < 4 ||
                        !fields[0].toLowerCase().contains("code") ||
                        !fields[1].toLowerCase().contains("name") ||
                        !fields[2].toLowerCase().contains("category") ||
                        !fields[3].toLowerCase().contains("unit")
                    ) {
                        throw new BadRequestAlertException(
                            "Invalid CSV header. Expected: code,name,category,defaultUnit",
                            ENTITY_NAME,
                            "invalidheader"
                        );
                    }
                    continue;
                }

                // Parse data rows
                if (fields.length < 4) {
                    errors.add("Line " + lineNumber + ": Missing required fields (code, name, category, defaultUnit)");
                    continue;
                }

                try {
                    Material material = new Material();
                    material.setCode(fields[0].trim());
                    material.setName(fields[1].trim());

                    // Parse category enum
                    try {
                        material.setCategory(MaterialCategory.valueOf(fields[2].trim().toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        errors.add("Line " + lineNumber + ": Invalid category '" + fields[2] + "'. Valid values: " + getEnumValues(MaterialCategory.class));
                        continue;
                    }

                    // Parse unit enum
                    try {
                        material.setDefaultUnit(UnitOfMeasure.valueOf(fields[3].trim().toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        errors.add("Line " + lineNumber + ": Invalid unit '" + fields[3] + "'. Valid values: " + getEnumValues(UnitOfMeasure.class));
                        continue;
                    }

                    material.setActive(true); // Default to active

                    // Validate required fields
                    if (material.getCode().isEmpty() || material.getName().isEmpty()) {
                        errors.add("Line " + lineNumber + ": Code and name are required");
                        continue;
                    }

                    // Check for duplicate code (efficient query)
                    if (materialRepository.findOneByCode(material.getCode()).isPresent()) {
                        errors.add("Line " + lineNumber + ": Material code '" + material.getCode() + "' already exists (skipped)");
                        continue;
                    }

                    Material saved = materialRepository.save(material);
                    importedMaterials.add(saved);
                } catch (Exception e) {
                    errors.add("Line " + lineNumber + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            LOG.error("Error importing materials from CSV", e);
            throw new BadRequestAlertException("Error reading CSV file: " + e.getMessage(), ENTITY_NAME, "importerror");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("imported", importedMaterials.size());
        result.put("errors", errors);
        result.put("totalLines", lineNumber);

        return ResponseEntity.ok().body(result);
    }

    /**
     * Parse a CSV line handling quoted fields.
     *
     * @param line the CSV line to parse
     * @return array of field values
     */
    private String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(currentField.toString());
                currentField = new StringBuilder();
            } else {
                currentField.append(c);
            }
        }

        // Add the last field
        fields.add(currentField.toString());

        return fields.toArray(new String[0]);
    }

    /**
     * Get all enum values as a comma-separated string.
     *
     * @param enumClass the enum class
     * @return comma-separated enum values
     */
    private <E extends Enum<E>> String getEnumValues(Class<E> enumClass) {
        E[] values = enumClass.getEnumConstants();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(values[i].name());
        }
        return sb.toString();
    }
}
