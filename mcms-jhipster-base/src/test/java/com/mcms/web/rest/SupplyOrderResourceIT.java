package com.mcms.web.rest;

import static com.mcms.domain.SupplyOrderAsserts.*;
import static com.mcms.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mcms.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcms.IntegrationTest;
import com.mcms.domain.Supplier;
import com.mcms.domain.SupplyOrder;
import com.mcms.domain.enumeration.OrderStatus;
import com.mcms.repository.SupplyOrderRepository;
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
 * Integration tests for the {@link SupplyOrderResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SupplyOrderResourceIT {

    private static final String DEFAULT_ORDER_CODE = "AAAAAAAAAA";
    private static final String UPDATED_ORDER_CODE = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_ORDER_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_ORDER_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_EXPECTED_DELIVERY_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_EXPECTED_DELIVERY_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_ACTUAL_DELIVERY_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_ACTUAL_DELIVERY_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final OrderStatus DEFAULT_STATUS = OrderStatus.ORDERED;
    private static final OrderStatus UPDATED_STATUS = OrderStatus.DELIVERED;

    private static final BigDecimal DEFAULT_TOTAL_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_TOTAL_AMOUNT = new BigDecimal(2);

    private static final String DEFAULT_CURRENCY = "AAAAAAAAAA";
    private static final String UPDATED_CURRENCY = "BBBBBBBBBB";

    private static final String DEFAULT_SHIPPING_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_SHIPPING_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/supply-orders";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SupplyOrderRepository supplyOrderRepository;

    @Mock
    private SupplyOrderRepository supplyOrderRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSupplyOrderMockMvc;

    private SupplyOrder supplyOrder;

    private SupplyOrder insertedSupplyOrder;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SupplyOrder createEntity(EntityManager em) {
        SupplyOrder supplyOrder = new SupplyOrder()
            .orderCode(DEFAULT_ORDER_CODE)
            .orderDate(DEFAULT_ORDER_DATE)
            .expectedDeliveryDate(DEFAULT_EXPECTED_DELIVERY_DATE)
            .actualDeliveryDate(DEFAULT_ACTUAL_DELIVERY_DATE)
            .status(DEFAULT_STATUS)
            .totalAmount(DEFAULT_TOTAL_AMOUNT)
            .currency(DEFAULT_CURRENCY)
            .shippingAddress(DEFAULT_SHIPPING_ADDRESS)
            .note(DEFAULT_NOTE);
        // Add required entity
        Supplier supplier;
        if (TestUtil.findAll(em, Supplier.class).isEmpty()) {
            supplier = SupplierResourceIT.createEntity();
            em.persist(supplier);
            em.flush();
        } else {
            supplier = TestUtil.findAll(em, Supplier.class).get(0);
        }
        supplyOrder.setSupplier(supplier);
        return supplyOrder;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SupplyOrder createUpdatedEntity(EntityManager em) {
        SupplyOrder updatedSupplyOrder = new SupplyOrder()
            .orderCode(UPDATED_ORDER_CODE)
            .orderDate(UPDATED_ORDER_DATE)
            .expectedDeliveryDate(UPDATED_EXPECTED_DELIVERY_DATE)
            .actualDeliveryDate(UPDATED_ACTUAL_DELIVERY_DATE)
            .status(UPDATED_STATUS)
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .currency(UPDATED_CURRENCY)
            .shippingAddress(UPDATED_SHIPPING_ADDRESS)
            .note(UPDATED_NOTE);
        // Add required entity
        Supplier supplier;
        if (TestUtil.findAll(em, Supplier.class).isEmpty()) {
            supplier = SupplierResourceIT.createUpdatedEntity();
            em.persist(supplier);
            em.flush();
        } else {
            supplier = TestUtil.findAll(em, Supplier.class).get(0);
        }
        updatedSupplyOrder.setSupplier(supplier);
        return updatedSupplyOrder;
    }

    @BeforeEach
    void initTest() {
        supplyOrder = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedSupplyOrder != null) {
            supplyOrderRepository.delete(insertedSupplyOrder);
            insertedSupplyOrder = null;
        }
    }

    @Test
    @Transactional
    void createSupplyOrder() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SupplyOrder
        var returnedSupplyOrder = om.readValue(
            restSupplyOrderMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(supplyOrder)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SupplyOrder.class
        );

        // Validate the SupplyOrder in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertSupplyOrderUpdatableFieldsEquals(returnedSupplyOrder, getPersistedSupplyOrder(returnedSupplyOrder));

        insertedSupplyOrder = returnedSupplyOrder;
    }

    @Test
    @Transactional
    void createSupplyOrderWithExistingId() throws Exception {
        // Create the SupplyOrder with an existing ID
        supplyOrder.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSupplyOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(supplyOrder)))
            .andExpect(status().isBadRequest());

        // Validate the SupplyOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkOrderCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        supplyOrder.setOrderCode(null);

        // Create the SupplyOrder, which fails.

        restSupplyOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(supplyOrder)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkOrderDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        supplyOrder.setOrderDate(null);

        // Create the SupplyOrder, which fails.

        restSupplyOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(supplyOrder)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        supplyOrder.setStatus(null);

        // Create the SupplyOrder, which fails.

        restSupplyOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(supplyOrder)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCurrencyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        supplyOrder.setCurrency(null);

        // Create the SupplyOrder, which fails.

        restSupplyOrderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(supplyOrder)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSupplyOrders() throws Exception {
        // Initialize the database
        insertedSupplyOrder = supplyOrderRepository.saveAndFlush(supplyOrder);

        // Get all the supplyOrderList
        restSupplyOrderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(supplyOrder.getId().intValue())))
            .andExpect(jsonPath("$.[*].orderCode").value(hasItem(DEFAULT_ORDER_CODE)))
            .andExpect(jsonPath("$.[*].orderDate").value(hasItem(DEFAULT_ORDER_DATE.toString())))
            .andExpect(jsonPath("$.[*].expectedDeliveryDate").value(hasItem(DEFAULT_EXPECTED_DELIVERY_DATE.toString())))
            .andExpect(jsonPath("$.[*].actualDeliveryDate").value(hasItem(DEFAULT_ACTUAL_DELIVERY_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].totalAmount").value(hasItem(sameNumber(DEFAULT_TOTAL_AMOUNT))))
            .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY)))
            .andExpect(jsonPath("$.[*].shippingAddress").value(hasItem(DEFAULT_SHIPPING_ADDRESS)))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSupplyOrdersWithEagerRelationshipsIsEnabled() throws Exception {
        when(supplyOrderRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSupplyOrderMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(supplyOrderRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSupplyOrdersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(supplyOrderRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSupplyOrderMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(supplyOrderRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getSupplyOrder() throws Exception {
        // Initialize the database
        insertedSupplyOrder = supplyOrderRepository.saveAndFlush(supplyOrder);

        // Get the supplyOrder
        restSupplyOrderMockMvc
            .perform(get(ENTITY_API_URL_ID, supplyOrder.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(supplyOrder.getId().intValue()))
            .andExpect(jsonPath("$.orderCode").value(DEFAULT_ORDER_CODE))
            .andExpect(jsonPath("$.orderDate").value(DEFAULT_ORDER_DATE.toString()))
            .andExpect(jsonPath("$.expectedDeliveryDate").value(DEFAULT_EXPECTED_DELIVERY_DATE.toString()))
            .andExpect(jsonPath("$.actualDeliveryDate").value(DEFAULT_ACTUAL_DELIVERY_DATE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.totalAmount").value(sameNumber(DEFAULT_TOTAL_AMOUNT)))
            .andExpect(jsonPath("$.currency").value(DEFAULT_CURRENCY))
            .andExpect(jsonPath("$.shippingAddress").value(DEFAULT_SHIPPING_ADDRESS))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE));
    }

    @Test
    @Transactional
    void getNonExistingSupplyOrder() throws Exception {
        // Get the supplyOrder
        restSupplyOrderMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSupplyOrder() throws Exception {
        // Initialize the database
        insertedSupplyOrder = supplyOrderRepository.saveAndFlush(supplyOrder);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the supplyOrder
        SupplyOrder updatedSupplyOrder = supplyOrderRepository.findById(supplyOrder.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSupplyOrder are not directly saved in db
        em.detach(updatedSupplyOrder);
        updatedSupplyOrder
            .orderCode(UPDATED_ORDER_CODE)
            .orderDate(UPDATED_ORDER_DATE)
            .expectedDeliveryDate(UPDATED_EXPECTED_DELIVERY_DATE)
            .actualDeliveryDate(UPDATED_ACTUAL_DELIVERY_DATE)
            .status(UPDATED_STATUS)
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .currency(UPDATED_CURRENCY)
            .shippingAddress(UPDATED_SHIPPING_ADDRESS)
            .note(UPDATED_NOTE);

        restSupplyOrderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSupplyOrder.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedSupplyOrder))
            )
            .andExpect(status().isOk());

        // Validate the SupplyOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSupplyOrderToMatchAllProperties(updatedSupplyOrder);
    }

    @Test
    @Transactional
    void putNonExistingSupplyOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        supplyOrder.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSupplyOrderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, supplyOrder.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(supplyOrder))
            )
            .andExpect(status().isBadRequest());

        // Validate the SupplyOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSupplyOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        supplyOrder.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSupplyOrderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(supplyOrder))
            )
            .andExpect(status().isBadRequest());

        // Validate the SupplyOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSupplyOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        supplyOrder.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSupplyOrderMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(supplyOrder)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SupplyOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSupplyOrderWithPatch() throws Exception {
        // Initialize the database
        insertedSupplyOrder = supplyOrderRepository.saveAndFlush(supplyOrder);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the supplyOrder using partial update
        SupplyOrder partialUpdatedSupplyOrder = new SupplyOrder();
        partialUpdatedSupplyOrder.setId(supplyOrder.getId());

        partialUpdatedSupplyOrder
            .orderCode(UPDATED_ORDER_CODE)
            .expectedDeliveryDate(UPDATED_EXPECTED_DELIVERY_DATE)
            .status(UPDATED_STATUS)
            .totalAmount(UPDATED_TOTAL_AMOUNT);

        restSupplyOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSupplyOrder.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSupplyOrder))
            )
            .andExpect(status().isOk());

        // Validate the SupplyOrder in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSupplyOrderUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSupplyOrder, supplyOrder),
            getPersistedSupplyOrder(supplyOrder)
        );
    }

    @Test
    @Transactional
    void fullUpdateSupplyOrderWithPatch() throws Exception {
        // Initialize the database
        insertedSupplyOrder = supplyOrderRepository.saveAndFlush(supplyOrder);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the supplyOrder using partial update
        SupplyOrder partialUpdatedSupplyOrder = new SupplyOrder();
        partialUpdatedSupplyOrder.setId(supplyOrder.getId());

        partialUpdatedSupplyOrder
            .orderCode(UPDATED_ORDER_CODE)
            .orderDate(UPDATED_ORDER_DATE)
            .expectedDeliveryDate(UPDATED_EXPECTED_DELIVERY_DATE)
            .actualDeliveryDate(UPDATED_ACTUAL_DELIVERY_DATE)
            .status(UPDATED_STATUS)
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .currency(UPDATED_CURRENCY)
            .shippingAddress(UPDATED_SHIPPING_ADDRESS)
            .note(UPDATED_NOTE);

        restSupplyOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSupplyOrder.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSupplyOrder))
            )
            .andExpect(status().isOk());

        // Validate the SupplyOrder in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSupplyOrderUpdatableFieldsEquals(partialUpdatedSupplyOrder, getPersistedSupplyOrder(partialUpdatedSupplyOrder));
    }

    @Test
    @Transactional
    void patchNonExistingSupplyOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        supplyOrder.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSupplyOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, supplyOrder.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(supplyOrder))
            )
            .andExpect(status().isBadRequest());

        // Validate the SupplyOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSupplyOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        supplyOrder.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSupplyOrderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(supplyOrder))
            )
            .andExpect(status().isBadRequest());

        // Validate the SupplyOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSupplyOrder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        supplyOrder.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSupplyOrderMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(supplyOrder)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SupplyOrder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSupplyOrder() throws Exception {
        // Initialize the database
        insertedSupplyOrder = supplyOrderRepository.saveAndFlush(supplyOrder);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the supplyOrder
        restSupplyOrderMockMvc
            .perform(delete(ENTITY_API_URL_ID, supplyOrder.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return supplyOrderRepository.count();
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

    protected SupplyOrder getPersistedSupplyOrder(SupplyOrder supplyOrder) {
        return supplyOrderRepository.findById(supplyOrder.getId()).orElseThrow();
    }

    protected void assertPersistedSupplyOrderToMatchAllProperties(SupplyOrder expectedSupplyOrder) {
        assertSupplyOrderAllPropertiesEquals(expectedSupplyOrder, getPersistedSupplyOrder(expectedSupplyOrder));
    }

    protected void assertPersistedSupplyOrderToMatchUpdatableProperties(SupplyOrder expectedSupplyOrder) {
        assertSupplyOrderAllUpdatablePropertiesEquals(expectedSupplyOrder, getPersistedSupplyOrder(expectedSupplyOrder));
    }
}
