package com.mcms.web.rest;

import static com.mcms.domain.SalesOrderAsserts.*;
import static com.mcms.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mcms.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcms.IntegrationTest;
import com.mcms.domain.Customer;
import com.mcms.domain.SalesOrder;
import com.mcms.domain.enumeration.OrderStatus;
import com.mcms.domain.enumeration.PaymentStatus;
import com.mcms.repository.SalesOrderRepository;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link SalesOrderResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SalesOrderResourceIT {

    private static final String DEFAULT_ORDER_CODE = "AAAAAAAAAA";
    private static final String UPDATED_ORDER_CODE = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_ORDER_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_ORDER_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_REQUESTED_DELIVERY_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_REQUESTED_DELIVERY_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_ACTUAL_DELIVERY_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_ACTUAL_DELIVERY_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final OrderStatus DEFAULT_STATUS = OrderStatus.ORDERED;
    private static final OrderStatus UPDATED_STATUS = OrderStatus.DELIVERED;

    private static final BigDecimal DEFAULT_TOTAL_WEIGHT = new BigDecimal(1);
    private static final BigDecimal UPDATED_TOTAL_WEIGHT = new BigDecimal(2);

    private static final BigDecimal DEFAULT_TOTAL_REVENUE = new BigDecimal(1);
    private static final BigDecimal UPDATED_TOTAL_REVENUE = new BigDecimal(2);

    private static final String DEFAULT_CURRENCY = "AAAAAAAAAA";
    private static final String UPDATED_CURRENCY = "BBBBBBBBBB";

    private static final String DEFAULT_INVOICE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_INVOICE_NUMBER = "BBBBBBBBBB";

    private static final PaymentStatus DEFAULT_PAYMENT_STATUS = PaymentStatus.UNPAID;
    private static final PaymentStatus UPDATED_PAYMENT_STATUS = PaymentStatus.PARTIALLY_PAID;

    private static final LocalDate DEFAULT_PAYMENT_DUE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_PAYMENT_DUE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_PAYMENT_RECEIVED_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_PAYMENT_RECEIVED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_SHIPPING_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_SHIPPING_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/sales-orders";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SalesOrderRepository salesOrderRepository;

    @Mock
    private SalesOrderRepository salesOrderRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSalesOrderMockMvc;

    private SalesOrder salesOrder;

    private SalesOrder insertedSalesOrder;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SalesOrder createEntity(EntityManager em) {
        SalesOrder salesOrder = new SalesOrder()
            .orderCode(DEFAULT_ORDER_CODE)
            .orderDate(DEFAULT_ORDER_DATE)
            .requestedDeliveryDate(DEFAULT_REQUESTED_DELIVERY_DATE)
            .actualDeliveryDate(DEFAULT_ACTUAL_DELIVERY_DATE)
            .status(DEFAULT_STATUS)
            .totalWeight(DEFAULT_TOTAL_WEIGHT)
            .totalRevenue(DEFAULT_TOTAL_REVENUE)
            .currency(DEFAULT_CURRENCY)
            .invoiceNumber(DEFAULT_INVOICE_NUMBER)
            .paymentStatus(DEFAULT_PAYMENT_STATUS)
            .paymentDueDate(DEFAULT_PAYMENT_DUE_DATE)
            .paymentReceivedDate(DEFAULT_PAYMENT_RECEIVED_DATE)
            .shippingAddress(DEFAULT_SHIPPING_ADDRESS)
            .note(DEFAULT_NOTE);
        // Add required entity
        Customer customer;
        if (TestUtil.findAll(em, Customer.class).isEmpty()) {
            customer = CustomerResourceIT.createEntity();
            em.persist(customer);
            em.flush();
        } else {
            customer = TestUtil.findAll(em, Customer.class).get(0);
        }
        salesOrder.setCustomer(customer);
        return salesOrder;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SalesOrder createUpdatedEntity(EntityManager em) {
        SalesOrder updatedSalesOrder = new SalesOrder()
            .orderCode(UPDATED_ORDER_CODE)
            .orderDate(UPDATED_ORDER_DATE)
            .requestedDeliveryDate(UPDATED_REQUESTED_DELIVERY_DATE)
            .actualDeliveryDate(UPDATED_ACTUAL_DELIVERY_DATE)
            .status(UPDATED_STATUS)
            .totalWeight(UPDATED_TOTAL_WEIGHT)
            .totalRevenue(UPDATED_TOTAL_REVENUE)
            .currency(UPDATED_CURRENCY)
            .invoiceNumber(UPDATED_INVOICE_NUMBER)
            .paymentStatus(UPDATED_PAYMENT_STATUS)
            .paymentDueDate(UPDATED_PAYMENT_DUE_DATE)
            .paymentReceivedDate(UPDATED_PAYMENT_RECEIVED_DATE)
            .shippingAddress(UPDATED_SHIPPING_ADDRESS)
            .note(UPDATED_NOTE);
        // Add required entity
        Customer customer;
        if (TestUtil.findAll(em, Customer.class).isEmpty()) {
            customer = CustomerResourceIT.createUpdatedEntity();
            em.persist(customer);
            em.flush();
        } else {
            customer = TestUtil.findAll(em, Customer.class).get(0);
        }
        updatedSalesOrder.setCustomer(customer);
        return updatedSalesOrder;
    }

    @BeforeEach
    void initTest() {
        salesOrder = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedSalesOrder != null) {
            salesOrderRepository.delete(insertedSalesOrder);
            insertedSalesOrder = null;
        }
    }

    @Test
    @Transactional
    void createSalesOrder() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SalesOrder
        var returnedSalesOrder = om.readValue(
            restSalesOrderMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(salesOrder)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SalesOrder.class
        );

        // Validate the SalesOrder in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertSalesOrderUpdatableFieldsEquals(returnedSalesOrder, getPersistedSalesOrder(returnedSalesOrder));

        insertedSalesOrder = returnedSalesOrder;
    }

    @Test
    @Transactional
    void createSalesOrderWithExistingId() throws Exception {
        // Create the SalesOrder with an existing ID
        salesOrder.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSalesOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(salesOrder)))
            .andExpect(status().isBadRequest());

        // Validate the SalesOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkOrderCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        salesOrder.setOrderCode(null);

        // Create the SalesOrder, which fails.

        restSalesOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(salesOrder)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkOrderDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        salesOrder.setOrderDate(null);

        // Create the SalesOrder, which fails.

        restSalesOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(salesOrder)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        salesOrder.setStatus(null);

        // Create the SalesOrder, which fails.

        restSalesOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(salesOrder)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCurrencyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        salesOrder.setCurrency(null);

        // Create the SalesOrder, which fails.

        restSalesOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(salesOrder)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPaymentStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        salesOrder.setPaymentStatus(null);

        // Create the SalesOrder, which fails.

        restSalesOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(salesOrder)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSalesOrders() throws Exception {
        // Initialize the database
        insertedSalesOrder = salesOrderRepository.saveAndFlush(salesOrder);

        // Get all the salesOrderList
        restSalesOrderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(salesOrder.getId().intValue())))
            .andExpect(jsonPath("$.[*].orderCode").value(hasItem(DEFAULT_ORDER_CODE)))
            .andExpect(jsonPath("$.[*].orderDate").value(hasItem(DEFAULT_ORDER_DATE.toString())))
            .andExpect(jsonPath("$.[*].requestedDeliveryDate").value(hasItem(DEFAULT_REQUESTED_DELIVERY_DATE.toString())))
            .andExpect(jsonPath("$.[*].actualDeliveryDate").value(hasItem(DEFAULT_ACTUAL_DELIVERY_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].totalWeight").value(hasItem(sameNumber(DEFAULT_TOTAL_WEIGHT))))
            .andExpect(jsonPath("$.[*].totalRevenue").value(hasItem(sameNumber(DEFAULT_TOTAL_REVENUE))))
            .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY)))
            .andExpect(jsonPath("$.[*].invoiceNumber").value(hasItem(DEFAULT_INVOICE_NUMBER)))
            .andExpect(jsonPath("$.[*].paymentStatus").value(hasItem(DEFAULT_PAYMENT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].paymentDueDate").value(hasItem(DEFAULT_PAYMENT_DUE_DATE.toString())))
            .andExpect(jsonPath("$.[*].paymentReceivedDate").value(hasItem(DEFAULT_PAYMENT_RECEIVED_DATE.toString())))
            .andExpect(jsonPath("$.[*].shippingAddress").value(hasItem(DEFAULT_SHIPPING_ADDRESS)))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSalesOrdersWithEagerRelationshipsIsEnabled() throws Exception {
        when(salesOrderRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSalesOrderMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(salesOrderRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSalesOrdersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(salesOrderRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSalesOrderMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(salesOrderRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getSalesOrder() throws Exception {
        // Initialize the database
        insertedSalesOrder = salesOrderRepository.saveAndFlush(salesOrder);

        // Get the salesOrder
        restSalesOrderMockMvc
            .perform(get(ENTITY_API_URL_ID, salesOrder.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(salesOrder.getId().intValue()))
            .andExpect(jsonPath("$.orderCode").value(DEFAULT_ORDER_CODE))
            .andExpect(jsonPath("$.orderDate").value(DEFAULT_ORDER_DATE.toString()))
            .andExpect(jsonPath("$.requestedDeliveryDate").value(DEFAULT_REQUESTED_DELIVERY_DATE.toString()))
            .andExpect(jsonPath("$.actualDeliveryDate").value(DEFAULT_ACTUAL_DELIVERY_DATE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.totalWeight").value(sameNumber(DEFAULT_TOTAL_WEIGHT)))
            .andExpect(jsonPath("$.totalRevenue").value(sameNumber(DEFAULT_TOTAL_REVENUE)))
            .andExpect(jsonPath("$.currency").value(DEFAULT_CURRENCY))
            .andExpect(jsonPath("$.invoiceNumber").value(DEFAULT_INVOICE_NUMBER))
            .andExpect(jsonPath("$.paymentStatus").value(DEFAULT_PAYMENT_STATUS.toString()))
            .andExpect(jsonPath("$.paymentDueDate").value(DEFAULT_PAYMENT_DUE_DATE.toString()))
            .andExpect(jsonPath("$.paymentReceivedDate").value(DEFAULT_PAYMENT_RECEIVED_DATE.toString()))
            .andExpect(jsonPath("$.shippingAddress").value(DEFAULT_SHIPPING_ADDRESS))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE));
    }

    @Test
    @Transactional
    void getNonExistingSalesOrder() throws Exception {
        // Get the salesOrder
        restSalesOrderMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSalesOrder() throws Exception {
        // Initialize the database
        insertedSalesOrder = salesOrderRepository.saveAndFlush(salesOrder);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the salesOrder
        SalesOrder updatedSalesOrder = salesOrderRepository.findById(salesOrder.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSalesOrder are not directly saved in db
        em.detach(updatedSalesOrder);
        updatedSalesOrder
            .orderCode(UPDATED_ORDER_CODE)
            .orderDate(UPDATED_ORDER_DATE)
            .requestedDeliveryDate(UPDATED_REQUESTED_DELIVERY_DATE)
            .actualDeliveryDate(UPDATED_ACTUAL_DELIVERY_DATE)
            .status(UPDATED_STATUS)
            .totalWeight(UPDATED_TOTAL_WEIGHT)
            .totalRevenue(UPDATED_TOTAL_REVENUE)
            .currency(UPDATED_CURRENCY)
            .invoiceNumber(UPDATED_INVOICE_NUMBER)
            .paymentStatus(UPDATED_PAYMENT_STATUS)
            .paymentDueDate(UPDATED_PAYMENT_DUE_DATE)
            .paymentReceivedDate(UPDATED_PAYMENT_RECEIVED_DATE)
            .shippingAddress(UPDATED_SHIPPING_ADDRESS)
            .note(UPDATED_NOTE);

        restSalesOrderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSalesOrder.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedSalesOrder))
            )
            .andExpect(status().isOk());

        // Validate the SalesOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSalesOrderToMatchAllProperties(updatedSalesOrder);
    }

    @Test
    @Transactional
    void putNonExistingSalesOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        salesOrder.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSalesOrderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, salesOrder.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(salesOrder))
            )
            .andExpect(status().isBadRequest());

        // Validate the SalesOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSalesOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        salesOrder.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalesOrderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(salesOrder))
            )
            .andExpect(status().isBadRequest());

        // Validate the SalesOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSalesOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        salesOrder.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalesOrderMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(salesOrder)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SalesOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSalesOrderWithPatch() throws Exception {
        // Initialize the database
        insertedSalesOrder = salesOrderRepository.saveAndFlush(salesOrder);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the salesOrder using partial update
        SalesOrder partialUpdatedSalesOrder = new SalesOrder();
        partialUpdatedSalesOrder.setId(salesOrder.getId());

        partialUpdatedSalesOrder
            .orderCode(UPDATED_ORDER_CODE)
            .status(UPDATED_STATUS)
            .currency(UPDATED_CURRENCY)
            .invoiceNumber(UPDATED_INVOICE_NUMBER)
            .note(UPDATED_NOTE);

        restSalesOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSalesOrder.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSalesOrder))
            )
            .andExpect(status().isOk());

        // Validate the SalesOrder in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSalesOrderUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSalesOrder, salesOrder),
            getPersistedSalesOrder(salesOrder)
        );
    }

    @Test
    @Transactional
    void fullUpdateSalesOrderWithPatch() throws Exception {
        // Initialize the database
        insertedSalesOrder = salesOrderRepository.saveAndFlush(salesOrder);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the salesOrder using partial update
        SalesOrder partialUpdatedSalesOrder = new SalesOrder();
        partialUpdatedSalesOrder.setId(salesOrder.getId());

        partialUpdatedSalesOrder
            .orderCode(UPDATED_ORDER_CODE)
            .orderDate(UPDATED_ORDER_DATE)
            .requestedDeliveryDate(UPDATED_REQUESTED_DELIVERY_DATE)
            .actualDeliveryDate(UPDATED_ACTUAL_DELIVERY_DATE)
            .status(UPDATED_STATUS)
            .totalWeight(UPDATED_TOTAL_WEIGHT)
            .totalRevenue(UPDATED_TOTAL_REVENUE)
            .currency(UPDATED_CURRENCY)
            .invoiceNumber(UPDATED_INVOICE_NUMBER)
            .paymentStatus(UPDATED_PAYMENT_STATUS)
            .paymentDueDate(UPDATED_PAYMENT_DUE_DATE)
            .paymentReceivedDate(UPDATED_PAYMENT_RECEIVED_DATE)
            .shippingAddress(UPDATED_SHIPPING_ADDRESS)
            .note(UPDATED_NOTE);

        restSalesOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSalesOrder.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSalesOrder))
            )
            .andExpect(status().isOk());

        // Validate the SalesOrder in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSalesOrderUpdatableFieldsEquals(partialUpdatedSalesOrder, getPersistedSalesOrder(partialUpdatedSalesOrder));
    }

    @Test
    @Transactional
    void patchNonExistingSalesOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        salesOrder.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSalesOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, salesOrder.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(salesOrder))
            )
            .andExpect(status().isBadRequest());

        // Validate the SalesOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSalesOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        salesOrder.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalesOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(salesOrder))
            )
            .andExpect(status().isBadRequest());

        // Validate the SalesOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSalesOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        salesOrder.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalesOrderMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(salesOrder)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SalesOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSalesOrder() throws Exception {
        // Initialize the database
        insertedSalesOrder = salesOrderRepository.saveAndFlush(salesOrder);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the salesOrder
        restSalesOrderMockMvc
            .perform(delete(ENTITY_API_URL_ID, salesOrder.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return salesOrderRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected SalesOrder getPersistedSalesOrder(SalesOrder salesOrder) {
        return salesOrderRepository.findById(salesOrder.getId()).orElseThrow();
    }

    protected void assertPersistedSalesOrderToMatchAllProperties(SalesOrder expectedSalesOrder) {
        assertSalesOrderAllPropertiesEquals(expectedSalesOrder, getPersistedSalesOrder(expectedSalesOrder));
    }

    protected void assertPersistedSalesOrderToMatchUpdatableProperties(SalesOrder expectedSalesOrder) {
        assertSalesOrderAllUpdatablePropertiesEquals(expectedSalesOrder, getPersistedSalesOrder(expectedSalesOrder));
    }
}
