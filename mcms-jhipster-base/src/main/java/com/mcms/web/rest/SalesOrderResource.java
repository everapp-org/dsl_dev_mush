package com.mcms.web.rest;

import com.mcms.domain.SalesOrder;
import com.mcms.repository.SalesOrderRepository;
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
 * REST controller for managing {@link com.mcms.domain.SalesOrder}.
 */
@RestController
@RequestMapping("/api/sales-orders")
@Transactional
public class SalesOrderResource {

    private static final Logger LOG = LoggerFactory.getLogger(SalesOrderResource.class);

    private static final String ENTITY_NAME = "salesOrder";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SalesOrderRepository salesOrderRepository;

    public SalesOrderResource(SalesOrderRepository salesOrderRepository) {
        this.salesOrderRepository = salesOrderRepository;
    }

    /**
     * {@code POST  /sales-orders} : Create a new salesOrder.
     *
     * @param salesOrder the salesOrder to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new salesOrder, or with status {@code 400 (Bad Request)} if the salesOrder has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SalesOrder> createSalesOrder(@Valid @RequestBody SalesOrder salesOrder) throws URISyntaxException {
        LOG.debug("REST request to save SalesOrder : {}", salesOrder);
        if (salesOrder.getId() != null) {
            throw new BadRequestAlertException("A new salesOrder cannot already have an ID", ENTITY_NAME, "idexists");
        }
        salesOrder = salesOrderRepository.save(salesOrder);
        return ResponseEntity.created(new URI("/api/sales-orders/" + salesOrder.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, salesOrder.getId().toString()))
            .body(salesOrder);
    }

    /**
     * {@code PUT  /sales-orders/:id} : Updates an existing salesOrder.
     *
     * @param id the id of the salesOrder to save.
     * @param salesOrder the salesOrder to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated salesOrder,
     * or with status {@code 400 (Bad Request)} if the salesOrder is not valid,
     * or with status {@code 500 (Internal Server Error)} if the salesOrder couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SalesOrder> updateSalesOrder(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SalesOrder salesOrder
    ) throws URISyntaxException {
        LOG.debug("REST request to update SalesOrder : {}, {}", id, salesOrder);
        if (salesOrder.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, salesOrder.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!salesOrderRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        salesOrder = salesOrderRepository.save(salesOrder);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, salesOrder.getId().toString()))
            .body(salesOrder);
    }

    /**
     * {@code PATCH  /sales-orders/:id} : Partial updates given fields of an existing salesOrder, field will ignore if it is null
     *
     * @param id the id of the salesOrder to save.
     * @param salesOrder the salesOrder to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated salesOrder,
     * or with status {@code 400 (Bad Request)} if the salesOrder is not valid,
     * or with status {@code 404 (Not Found)} if the salesOrder is not found,
     * or with status {@code 500 (Internal Server Error)} if the salesOrder couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SalesOrder> partialUpdateSalesOrder(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SalesOrder salesOrder
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update SalesOrder partially : {}, {}", id, salesOrder);
        if (salesOrder.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, salesOrder.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!salesOrderRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SalesOrder> result = salesOrderRepository
            .findById(salesOrder.getId())
            .map(existingSalesOrder -> {
                if (salesOrder.getOrderCode() != null) {
                    existingSalesOrder.setOrderCode(salesOrder.getOrderCode());
                }
                if (salesOrder.getOrderDate() != null) {
                    existingSalesOrder.setOrderDate(salesOrder.getOrderDate());
                }
                if (salesOrder.getRequestedDeliveryDate() != null) {
                    existingSalesOrder.setRequestedDeliveryDate(salesOrder.getRequestedDeliveryDate());
                }
                if (salesOrder.getActualDeliveryDate() != null) {
                    existingSalesOrder.setActualDeliveryDate(salesOrder.getActualDeliveryDate());
                }
                if (salesOrder.getStatus() != null) {
                    existingSalesOrder.setStatus(salesOrder.getStatus());
                }
                if (salesOrder.getTotalWeight() != null) {
                    existingSalesOrder.setTotalWeight(salesOrder.getTotalWeight());
                }
                if (salesOrder.getTotalRevenue() != null) {
                    existingSalesOrder.setTotalRevenue(salesOrder.getTotalRevenue());
                }
                if (salesOrder.getCurrency() != null) {
                    existingSalesOrder.setCurrency(salesOrder.getCurrency());
                }
                if (salesOrder.getInvoiceNumber() != null) {
                    existingSalesOrder.setInvoiceNumber(salesOrder.getInvoiceNumber());
                }
                if (salesOrder.getPaymentStatus() != null) {
                    existingSalesOrder.setPaymentStatus(salesOrder.getPaymentStatus());
                }
                if (salesOrder.getPaymentDueDate() != null) {
                    existingSalesOrder.setPaymentDueDate(salesOrder.getPaymentDueDate());
                }
                if (salesOrder.getPaymentReceivedDate() != null) {
                    existingSalesOrder.setPaymentReceivedDate(salesOrder.getPaymentReceivedDate());
                }
                if (salesOrder.getShippingAddress() != null) {
                    existingSalesOrder.setShippingAddress(salesOrder.getShippingAddress());
                }
                if (salesOrder.getNote() != null) {
                    existingSalesOrder.setNote(salesOrder.getNote());
                }

                return existingSalesOrder;
            })
            .map(salesOrderRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, salesOrder.getId().toString())
        );
    }

    /**
     * {@code GET  /sales-orders} : get all the salesOrders.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of salesOrders in body.
     */
    @GetMapping("")
    public List<SalesOrder> getAllSalesOrders(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all SalesOrders");
        if (eagerload) {
            return salesOrderRepository.findAllWithEagerRelationships();
        } else {
            return salesOrderRepository.findAll();
        }
    }

    /**
     * {@code GET  /sales-orders/:id} : get the "id" salesOrder.
     *
     * @param id the id of the salesOrder to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the salesOrder, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SalesOrder> getSalesOrder(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SalesOrder : {}", id);
        Optional<SalesOrder> salesOrder = salesOrderRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(salesOrder);
    }

    /**
     * {@code DELETE  /sales-orders/:id} : delete the "id" salesOrder.
     *
     * @param id the id of the salesOrder to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSalesOrder(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SalesOrder : {}", id);
        salesOrderRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
