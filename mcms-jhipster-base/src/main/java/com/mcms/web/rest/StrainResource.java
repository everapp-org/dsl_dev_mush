package com.mcms.web.rest;

import com.mcms.domain.Batch;
import com.mcms.domain.Strain;
import com.mcms.repository.BatchRepository;
import com.mcms.repository.StrainRepository;
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
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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
    private final BatchRepository batchRepository;

    public StrainResource(StrainRepository strainRepository, BatchRepository batchRepository) {
        this.strainRepository = strainRepository;
        this.batchRepository = batchRepository;
    }

    /**
     * {@code POST  /strains} : Create a new strain.
     *
     * @param strain the strain to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new strain, or with status {@code 400 (Bad Request)} if the strain has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
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
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
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
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
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
     * @param search optional search term to filter strains by name, species, or variety
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of strains in body.
     */
    @GetMapping("")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_OPERATOR', 'ROLE_USER')")
    public List<Strain> getAllStrains(@RequestParam(required = false) String search) {
        LOG.debug("REST request to get all Strains with search term: {}", search);
        if (search != null && !search.trim().isEmpty()) {
            return strainRepository.searchStrains(search.trim());
        }
        return strainRepository.findAll();
    }

    /**
     * {@code GET  /strains/:id} : get the "id" strain.
     *
     * @param id the id of the strain to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the strain, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_OPERATOR', 'ROLE_USER')")
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
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<Void> deleteStrain(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Strain : {}", id);

        // Check if strain is used by any batches
        long batchCount = batchRepository.countByStrainId(id);
        if (batchCount > 0) {
            throw new BadRequestAlertException(
                "Strain is used by " + batchCount + " batches",
                ENTITY_NAME,
                "straininuse"
            );
        }

        strainRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code POST  /strains/:id/activate} : activate the "id" strain.
     *
     * @param id the id of the strain to activate.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated strain.
     */
    @PostMapping("/{id}/activate")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Strain> activateStrain(@PathVariable("id") Long id) {
        LOG.debug("REST request to activate Strain : {}", id);
        Optional<Strain> strainOptional = strainRepository.findById(id);
        if (strainOptional.isEmpty()) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        Strain strain = strainOptional.get();
        strain.setActive(true);
        strain = strainRepository.save(strain);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, strain.getId().toString()))
            .body(strain);
    }

    /**
     * {@code POST  /strains/:id/deactivate} : deactivate the "id" strain.
     *
     * @param id the id of the strain to deactivate.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated strain.
     */
    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Strain> deactivateStrain(@PathVariable("id") Long id) {
        LOG.debug("REST request to deactivate Strain : {}", id);
        Optional<Strain> strainOptional = strainRepository.findById(id);
        if (strainOptional.isEmpty()) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        Strain strain = strainOptional.get();
        strain.setActive(false);
        strain = strainRepository.save(strain);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, strain.getId().toString()))
            .body(strain);
    }

    /**
     * {@code GET  /strains/:id/batches} : get all batches using this strain.
     *
     * @param id the id of the strain.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of batches in body.
     */
    @GetMapping("/{id}/batches")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_OPERATOR', 'ROLE_USER')")
    public List<Batch> getBatchesByStrain(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Batches by Strain : {}", id);
        return batchRepository.findAllWithFilters(null, null, id, null, null, null, null, null, Pageable.unpaged()).getContent();
    }

    /**
     * {@code POST  /strains/import} : Import strains from CSV file.
     *
     * CSV format: name,species,variety (header row required)
     * Example:
     * name,species,variety
     * "Pleurotus ostreatus - Grey Dove","Pleurotus ostreatus","Grey Dove"
     * "Shiitake - 3782","Lentinula edodes","3782"
     *
     * @param file the CSV file to import.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and count of imported strains.
     */
    @PostMapping("/import")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<Map<String, Object>> importStrains(@RequestParam("file") MultipartFile file) {
        LOG.debug("REST request to import Strains from CSV file: {}", file.getOriginalFilename());

        if (file.isEmpty()) {
            throw new BadRequestAlertException("File is empty", ENTITY_NAME, "fileempty");
        }

        List<Strain> importedStrains = new ArrayList<>();
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
                    if (fields.length < 2 || !fields[0].toLowerCase().contains("name") || !fields[1].toLowerCase().contains("species")) {
                        throw new BadRequestAlertException(
                            "Invalid CSV header. Expected: name,species,variety",
                            ENTITY_NAME,
                            "invalidheader"
                        );
                    }
                    continue;
                }

                // Parse data rows
                if (fields.length < 2) {
                    errors.add("Line " + lineNumber + ": Missing required fields (name, species)");
                    continue;
                }

                try {
                    Strain strain = new Strain();
                    strain.setName(fields[0].trim());
                    strain.setSpecies(fields[1].trim());
                    strain.setVariety(fields.length > 2 && !fields[2].trim().isEmpty() ? fields[2].trim() : null);
                    strain.setActive(true); // Default to active

                    // Validate required fields
                    if (strain.getName().isEmpty() || strain.getSpecies().isEmpty()) {
                        errors.add("Line " + lineNumber + ": Name and species are required");
                        continue;
                    }

                    // Check for duplicate name+species combination
                    if (strainRepository.findOneByNameAndSpecies(strain.getName(), strain.getSpecies()).isPresent()) {
                        errors.add("Line " + lineNumber + ": Strain '" + strain.getName() + " - " + strain.getSpecies() + "' already exists (skipped)");
                        continue;
                    }

                    Strain saved = strainRepository.save(strain);
                    importedStrains.add(saved);
                } catch (Exception e) {
                    errors.add("Line " + lineNumber + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            LOG.error("Error importing strains from CSV", e);
            throw new BadRequestAlertException("Error reading CSV file: " + e.getMessage(), ENTITY_NAME, "importerror");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("imported", importedStrains.size());
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
}
