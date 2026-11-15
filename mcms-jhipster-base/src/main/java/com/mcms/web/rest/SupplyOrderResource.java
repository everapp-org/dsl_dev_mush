package com.mcms.web.rest;

import com.mcms.domain.SupplyOrder;
import com.mcms.repository.SupplyOrderRepository;
import com.mcms.security.AuthoritiesConstants;
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
 * REST controller for managing {@link com.mcms.domain.SupplyOrder}.
 */
@RestController
@RequestMapping("/api/supply-orders")
@Transactional
public class SupplyOrderResource {

    private static final Logger LOG = LoggerFactory.getLogger(SupplyOrderResource.class);

    private static final String ENTITY_NAME = "supplyOrder";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SupplyOrderRepository supplyOrderRepository;

    public SupplyOrderResource(SupplyOrderRepository supplyOrderRepository) {
        this.supplyOrderRepository = supplyOrderRepository;
    }

    /**
     * {@code POST  /supply-orders} : Create a new supplyOrder.
     *
     * @param supplyOrder the supplyOrder to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new supplyOrder, or with status {@code 400 (Bad Request)} if the supplyOrder has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '" + AuthoritiesConstants.MANAGER + "')")
    public ResponseEntity<SupplyOrder> createSupplyOrder(@Valid @RequestBody SupplyOrder supplyOrder) throws URISyntaxException {
        LOG.debug("REST request to save SupplyOrder : {}", supplyOrder);
        if (supplyOrder.getId() != null) {
            throw new BadRequestAlertException("A new supplyOrder cannot already have an ID", ENTITY_NAME, "idexists");
        }
        supplyOrder = supplyOrderRepository.save(supplyOrder);
        return ResponseEntity.created(new URI("/api/supply-orders/" + supplyOrder.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, supplyOrder.getId().toString()))
            .body(supplyOrder);
    }

    /**
     * {@code PUT  /supply-orders/:id} : Updates an existing supplyOrder.
     *
     * @param id the id of the supplyOrder to save.
     * @param supplyOrder the supplyOrder to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated supplyOrder,
     * or with status {@code 400 (Bad Request)} if the supplyOrder is not valid,
     * or with status {@code 500 (Internal Server Error)} if the supplyOrder couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '" + AuthoritiesConstants.MANAGER + "')")
    public ResponseEntity<SupplyOrder> updateSupplyOrder(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SupplyOrder supplyOrder
    ) throws URISyntaxException {
        LOG.debug("REST request to update SupplyOrder : {}, {}", id, supplyOrder);
        if (supplyOrder.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, supplyOrder.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!supplyOrderRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        supplyOrder = supplyOrderRepository.save(supplyOrder);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, supplyOrder.getId().toString()))
            .body(supplyOrder);
    }

    /**
     * {@code PATCH  /supply-orders/:id} : Partial updates given fields of an existing supplyOrder, field will ignore if it is null
     *
     * @param id the id of the supplyOrder to save.
     * @param supplyOrder the supplyOrder to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated supplyOrder,
     * or with status {@code 400 (Bad Request)} if the supplyOrder is not valid,
     * or with status {@code 404 (Not Found)} if the supplyOrder is not found,
     * or with status {@code 500 (Internal Server Error)} if the supplyOrder couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '" + AuthoritiesConstants.MANAGER + "')")
    public ResponseEntity<SupplyOrder> partialUpdateSupplyOrder(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SupplyOrder supplyOrder
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update SupplyOrder partially : {}, {}", id, supplyOrder);
        if (supplyOrder.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, supplyOrder.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!supplyOrderRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SupplyOrder> result = supplyOrderRepository
            .findById(supplyOrder.getId())
            .map(existingSupplyOrder -> {
                if (supplyOrder.getOrderCode() != null) {
                    existingSupplyOrder.setOrderCode(supplyOrder.getOrderCode());
                }
                if (supplyOrder.getOrderDate() != null) {
                    existingSupplyOrder.setOrderDate(supplyOrder.getOrderDate());
                }
                if (supplyOrder.getExpectedDeliveryDate() != null) {
                    existingSupplyOrder.setExpectedDeliveryDate(supplyOrder.getExpectedDeliveryDate());
                }
                if (supplyOrder.getActualDeliveryDate() != null) {
                    existingSupplyOrder.setActualDeliveryDate(supplyOrder.getActualDeliveryDate());
                }
                if (supplyOrder.getStatus() != null) {
                    existingSupplyOrder.setStatus(supplyOrder.getStatus());
                }
                if (supplyOrder.getTotalAmount() != null) {
                    existingSupplyOrder.setTotalAmount(supplyOrder.getTotalAmount());
                }
                if (supplyOrder.getCurrency() != null) {
                    existingSupplyOrder.setCurrency(supplyOrder.getCurrency());
                }
                if (supplyOrder.getShippingAddress() != null) {
                    existingSupplyOrder.setShippingAddress(supplyOrder.getShippingAddress());
                }
                if (supplyOrder.getNote() != null) {
                    existingSupplyOrder.setNote(supplyOrder.getNote());
                }

                return existingSupplyOrder;
            })
            .map(supplyOrderRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, supplyOrder.getId().toString())
        );
    }

    /**
     * {@code GET  /supply-orders} : get all the supplyOrders.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of supplyOrders in body.
     */
    @GetMapping("")
    @PreAuthorize("isAuthenticated()")
    public List<SupplyOrder> getAllSupplyOrders(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all SupplyOrders");
        if (eagerload) {
            return supplyOrderRepository.findAllWithEagerRelationships();
        } else {
            return supplyOrderRepository.findAll();
        }
    }

    /**
     * {@code GET  /supply-orders/:id} : get the "id" supplyOrder.
     *
     * @param id the id of the supplyOrder to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the supplyOrder, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SupplyOrder> getSupplyOrder(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SupplyOrder : {}", id);
        Optional<SupplyOrder> supplyOrder = supplyOrderRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(supplyOrder);
    }

    /**
     * {@code DELETE  /supply-orders/:id} : delete the "id" supplyOrder.
     *
     * @param id the id of the supplyOrder to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "', '" + AuthoritiesConstants.MANAGER + "')")
    public ResponseEntity<Void> deleteSupplyOrder(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SupplyOrder : {}", id);
        supplyOrderRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
