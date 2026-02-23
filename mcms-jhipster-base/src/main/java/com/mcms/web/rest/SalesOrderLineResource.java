package com.mcms.web.rest;

import com.mcms.domain.SalesOrderLine;
import com.mcms.repository.SalesOrderLineRepository;
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
 * REST controller for managing {@link com.mcms.domain.SalesOrderLine}.
 */
@RestController
@RequestMapping("/api/sales-order-lines")
@Transactional
public class SalesOrderLineResource {

    private static final Logger LOG = LoggerFactory.getLogger(SalesOrderLineResource.class);

    private static final String ENTITY_NAME = "salesOrderLine";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SalesOrderLineRepository salesOrderLineRepository;

    public SalesOrderLineResource(SalesOrderLineRepository salesOrderLineRepository) {
        this.salesOrderLineRepository = salesOrderLineRepository;
    }

    /**
     * {@code POST  /sales-order-lines} : Create a new salesOrderLine.
     *
     * @param salesOrderLine the salesOrderLine to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new salesOrderLine, or with status {@code 400 (Bad Request)} if the salesOrderLine has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SalesOrderLine> createSalesOrderLine(@Valid @RequestBody SalesOrderLine salesOrderLine)
        throws URISyntaxException {
        LOG.debug("REST request to save SalesOrderLine : {}", salesOrderLine);
        if (salesOrderLine.getId() != null) {
            throw new BadRequestAlertException("A new salesOrderLine cannot already have an ID", ENTITY_NAME, "idexists");
        }
        salesOrderLine = salesOrderLineRepository.save(salesOrderLine);
        return ResponseEntity.created(new URI("/api/sales-order-lines/" + salesOrderLine.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, salesOrderLine.getId().toString()))
            .body(salesOrderLine);
    }

    /**
     * {@code PUT  /sales-order-lines/:id} : Updates an existing salesOrderLine.
     *
     * @param id the id of the salesOrderLine to save.
     * @param salesOrderLine the salesOrderLine to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated salesOrderLine,
     * or with status {@code 400 (Bad Request)} if the salesOrderLine is not valid,
     * or with status {@code 500 (Internal Server Error)} if the salesOrderLine couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SalesOrderLine> updateSalesOrderLine(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SalesOrderLine salesOrderLine
    ) throws URISyntaxException {
        LOG.debug("REST request to update SalesOrderLine : {}, {}", id, salesOrderLine);
        if (salesOrderLine.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, salesOrderLine.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!salesOrderLineRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        salesOrderLine = salesOrderLineRepository.save(salesOrderLine);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, salesOrderLine.getId().toString()))
            .body(salesOrderLine);
    }

    /**
     * {@code PATCH  /sales-order-lines/:id} : Partial updates given fields of an existing salesOrderLine, field will ignore if it is null
     *
     * @param id the id of the salesOrderLine to save.
     * @param salesOrderLine the salesOrderLine to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated salesOrderLine,
     * or with status {@code 400 (Bad Request)} if the salesOrderLine is not valid,
     * or with status {@code 404 (Not Found)} if the salesOrderLine is not found,
     * or with status {@code 500 (Internal Server Error)} if the salesOrderLine couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SalesOrderLine> partialUpdateSalesOrderLine(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SalesOrderLine salesOrderLine
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update SalesOrderLine partially : {}, {}", id, salesOrderLine);
        if (salesOrderLine.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, salesOrderLine.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!salesOrderLineRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SalesOrderLine> result = salesOrderLineRepository
            .findById(salesOrderLine.getId())
            .map(existingSalesOrderLine -> {
                if (salesOrderLine.getLineNumber() != null) {
                    existingSalesOrderLine.setLineNumber(salesOrderLine.getLineNumber());
                }
                if (salesOrderLine.getWeightKg() != null) {
                    existingSalesOrderLine.setWeightKg(salesOrderLine.getWeightKg());
                }
                if (salesOrderLine.getGrade() != null) {
                    existingSalesOrderLine.setGrade(salesOrderLine.getGrade());
                }
                if (salesOrderLine.getQuantityUnits() != null) {
                    existingSalesOrderLine.setQuantityUnits(salesOrderLine.getQuantityUnits());
                }
                if (salesOrderLine.getUnit() != null) {
                    existingSalesOrderLine.setUnit(salesOrderLine.getUnit());
                }
                if (salesOrderLine.getPricePerKg() != null) {
                    existingSalesOrderLine.setPricePerKg(salesOrderLine.getPricePerKg());
                }
                if (salesOrderLine.getLineTotal() != null) {
                    existingSalesOrderLine.setLineTotal(salesOrderLine.getLineTotal());
                }
                if (salesOrderLine.getNote() != null) {
                    existingSalesOrderLine.setNote(salesOrderLine.getNote());
                }

                return existingSalesOrderLine;
            })
            .map(salesOrderLineRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, salesOrderLine.getId().toString())
        );
    }

    /**
     * {@code GET  /sales-order-lines} : get all the salesOrderLines.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of salesOrderLines in body.
     */
    @GetMapping("")
    public List<SalesOrderLine> getAllSalesOrderLines(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all SalesOrderLines");
        if (eagerload) {
            return salesOrderLineRepository.findAllWithEagerRelationships();
        } else {
            return salesOrderLineRepository.findAll();
        }
    }

    /**
     * {@code GET  /sales-order-lines/:id} : get the "id" salesOrderLine.
     *
     * @param id the id of the salesOrderLine to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the salesOrderLine, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SalesOrderLine> getSalesOrderLine(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SalesOrderLine : {}", id);
        Optional<SalesOrderLine> salesOrderLine = salesOrderLineRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(salesOrderLine);
    }

    /**
     * {@code DELETE  /sales-order-lines/:id} : delete the "id" salesOrderLine.
     *
     * @param id the id of the salesOrderLine to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSalesOrderLine(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SalesOrderLine : {}", id);
        salesOrderLineRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
