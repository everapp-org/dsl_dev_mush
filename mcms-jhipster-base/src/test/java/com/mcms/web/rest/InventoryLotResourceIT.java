package com.mcms.web.rest;

import static com.mcms.domain.InventoryLotAsserts.*;
import static com.mcms.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mcms.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcms.IntegrationTest;
import com.mcms.domain.InventoryLot;
import com.mcms.domain.Material;
import com.mcms.domain.SupplyOrderLine;
import com.mcms.domain.enumeration.UnitOfMeasure;
import com.mcms.repository.InventoryLotRepository;
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
 * Integration tests for the {@link InventoryLotResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class InventoryLotResourceIT {

    private static final String DEFAULT_LOT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_LOT_CODE = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_RECEIVED_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_RECEIVED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final BigDecimal DEFAULT_QUANTITY_RECEIVED = new BigDecimal(1);
    private static final BigDecimal UPDATED_QUANTITY_RECEIVED = new BigDecimal(2);

    private static final BigDecimal DEFAULT_QUANTITY_ON_HAND = new BigDecimal(1);
    private static final BigDecimal UPDATED_QUANTITY_ON_HAND = new BigDecimal(2);

    private static final UnitOfMeasure DEFAULT_UNIT = UnitOfMeasure.KG;
    private static final UnitOfMeasure UPDATED_UNIT = UnitOfMeasure.GRAM;

    private static final LocalDate DEFAULT_EXPIRY_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_EXPIRY_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_STORAGE_LOCATION = "AAAAAAAAAA";
    private static final String UPDATED_STORAGE_LOCATION = "BBBBBBBBBB";

    private static final String DEFAULT_SUPPLIER_LOT_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_SUPPLIER_LOT_NUMBER = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_EXHAUSTED = false;
    private static final Boolean UPDATED_IS_EXHAUSTED = true;

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/inventory-lots";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private InventoryLotRepository inventoryLotRepository;

    @Mock
    private InventoryLotRepository inventoryLotRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restInventoryLotMockMvc;

    private InventoryLot inventoryLot;

    private InventoryLot insertedInventoryLot;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InventoryLot createEntity(EntityManager em) {
        InventoryLot inventoryLot = new InventoryLot()
            .lotCode(DEFAULT_LOT_CODE)
            .receivedDate(DEFAULT_RECEIVED_DATE)
            .quantityReceived(DEFAULT_QUANTITY_RECEIVED)
            .quantityOnHand(DEFAULT_QUANTITY_ON_HAND)
            .unit(DEFAULT_UNIT)
            .expiryDate(DEFAULT_EXPIRY_DATE)
            .storageLocation(DEFAULT_STORAGE_LOCATION)
            .supplierLotNumber(DEFAULT_SUPPLIER_LOT_NUMBER)
            .isExhausted(DEFAULT_IS_EXHAUSTED)
            .note(DEFAULT_NOTE);
        // Add required entity
        Material material;
        if (TestUtil.findAll(em, Material.class).isEmpty()) {
            material = MaterialResourceIT.createEntity();
            em.persist(material);
            em.flush();
        } else {
            material = TestUtil.findAll(em, Material.class).get(0);
        }
        inventoryLot.setMaterial(material);
        // Add required entity
        SupplyOrderLine supplyOrderLine;
        if (TestUtil.findAll(em, SupplyOrderLine.class).isEmpty()) {
            supplyOrderLine = SupplyOrderLineResourceIT.createEntity(em);
            em.persist(supplyOrderLine);
            em.flush();
        } else {
            supplyOrderLine = TestUtil.findAll(em, SupplyOrderLine.class).get(0);
        }
        inventoryLot.setSupplyOrderLine(supplyOrderLine);
        return inventoryLot;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InventoryLot createUpdatedEntity(EntityManager em) {
        InventoryLot updatedInventoryLot = new InventoryLot()
            .lotCode(UPDATED_LOT_CODE)
            .receivedDate(UPDATED_RECEIVED_DATE)
            .quantityReceived(UPDATED_QUANTITY_RECEIVED)
            .quantityOnHand(UPDATED_QUANTITY_ON_HAND)
            .unit(UPDATED_UNIT)
            .expiryDate(UPDATED_EXPIRY_DATE)
            .storageLocation(UPDATED_STORAGE_LOCATION)
            .supplierLotNumber(UPDATED_SUPPLIER_LOT_NUMBER)
            .isExhausted(UPDATED_IS_EXHAUSTED)
            .note(UPDATED_NOTE);
        // Add required entity
        Material material;
        if (TestUtil.findAll(em, Material.class).isEmpty()) {
            material = MaterialResourceIT.createUpdatedEntity();
            em.persist(material);
            em.flush();
        } else {
            material = TestUtil.findAll(em, Material.class).get(0);
        }
        updatedInventoryLot.setMaterial(material);
        // Add required entity
        SupplyOrderLine supplyOrderLine;
        if (TestUtil.findAll(em, SupplyOrderLine.class).isEmpty()) {
            supplyOrderLine = SupplyOrderLineResourceIT.createUpdatedEntity(em);
            em.persist(supplyOrderLine);
            em.flush();
        } else {
            supplyOrderLine = TestUtil.findAll(em, SupplyOrderLine.class).get(0);
        }
        updatedInventoryLot.setSupplyOrderLine(supplyOrderLine);
        return updatedInventoryLot;
    }

    @BeforeEach
    void initTest() {
        inventoryLot = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedInventoryLot != null) {
            inventoryLotRepository.delete(insertedInventoryLot);
            insertedInventoryLot = null;
        }
    }

    @Test
    @Transactional
    void createInventoryLot() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the InventoryLot
        var returnedInventoryLot = om.readValue(
            restInventoryLotMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inventoryLot)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            InventoryLot.class
        );

        // Validate the InventoryLot in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertInventoryLotUpdatableFieldsEquals(returnedInventoryLot, getPersistedInventoryLot(returnedInventoryLot));

        insertedInventoryLot = returnedInventoryLot;
    }

    @Test
    @Transactional
    void createInventoryLotWithExistingId() throws Exception {
        // Create the InventoryLot with an existing ID
        inventoryLot.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restInventoryLotMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inventoryLot)))
            .andExpect(status().isBadRequest());

        // Validate the InventoryLot in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkLotCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        inventoryLot.setLotCode(null);

        // Create the InventoryLot, which fails.

        restInventoryLotMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inventoryLot)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkReceivedDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        inventoryLot.setReceivedDate(null);

        // Create the InventoryLot, which fails.

        restInventoryLotMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inventoryLot)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkQuantityReceivedIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        inventoryLot.setQuantityReceived(null);

        // Create the InventoryLot, which fails.

        restInventoryLotMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inventoryLot)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkQuantityOnHandIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        inventoryLot.setQuantityOnHand(null);

        // Create the InventoryLot, which fails.

        restInventoryLotMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inventoryLot)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUnitIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        inventoryLot.setUnit(null);

        // Create the InventoryLot, which fails.

        restInventoryLotMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inventoryLot)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsExhaustedIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        inventoryLot.setIsExhausted(null);

        // Create the InventoryLot, which fails.

        restInventoryLotMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inventoryLot)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllInventoryLots() throws Exception {
        // Initialize the database
        insertedInventoryLot = inventoryLotRepository.saveAndFlush(inventoryLot);

        // Get all the inventoryLotList
        restInventoryLotMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(inventoryLot.getId().intValue())))
            .andExpect(jsonPath("$.[*].lotCode").value(hasItem(DEFAULT_LOT_CODE)))
            .andExpect(jsonPath("$.[*].receivedDate").value(hasItem(DEFAULT_RECEIVED_DATE.toString())))
            .andExpect(jsonPath("$.[*].quantityReceived").value(hasItem(sameNumber(DEFAULT_QUANTITY_RECEIVED))))
            .andExpect(jsonPath("$.[*].quantityOnHand").value(hasItem(sameNumber(DEFAULT_QUANTITY_ON_HAND))))
            .andExpect(jsonPath("$.[*].unit").value(hasItem(DEFAULT_UNIT.toString())))
            .andExpect(jsonPath("$.[*].expiryDate").value(hasItem(DEFAULT_EXPIRY_DATE.toString())))
            .andExpect(jsonPath("$.[*].storageLocation").value(hasItem(DEFAULT_STORAGE_LOCATION)))
            .andExpect(jsonPath("$.[*].supplierLotNumber").value(hasItem(DEFAULT_SUPPLIER_LOT_NUMBER)))
            .andExpect(jsonPath("$.[*].isExhausted").value(hasItem(DEFAULT_IS_EXHAUSTED)))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInventoryLotsWithEagerRelationshipsIsEnabled() throws Exception {
        when(inventoryLotRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restInventoryLotMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(inventoryLotRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInventoryLotsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(inventoryLotRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restInventoryLotMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(inventoryLotRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getInventoryLot() throws Exception {
        // Initialize the database
        insertedInventoryLot = inventoryLotRepository.saveAndFlush(inventoryLot);

        // Get the inventoryLot
        restInventoryLotMockMvc
            .perform(get(ENTITY_API_URL_ID, inventoryLot.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(inventoryLot.getId().intValue()))
            .andExpect(jsonPath("$.lotCode").value(DEFAULT_LOT_CODE))
            .andExpect(jsonPath("$.receivedDate").value(DEFAULT_RECEIVED_DATE.toString()))
            .andExpect(jsonPath("$.quantityReceived").value(sameNumber(DEFAULT_QUANTITY_RECEIVED)))
            .andExpect(jsonPath("$.quantityOnHand").value(sameNumber(DEFAULT_QUANTITY_ON_HAND)))
            .andExpect(jsonPath("$.unit").value(DEFAULT_UNIT.toString()))
            .andExpect(jsonPath("$.expiryDate").value(DEFAULT_EXPIRY_DATE.toString()))
            .andExpect(jsonPath("$.storageLocation").value(DEFAULT_STORAGE_LOCATION))
            .andExpect(jsonPath("$.supplierLotNumber").value(DEFAULT_SUPPLIER_LOT_NUMBER))
            .andExpect(jsonPath("$.isExhausted").value(DEFAULT_IS_EXHAUSTED))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE));
    }

    @Test
    @Transactional
    void getNonExistingInventoryLot() throws Exception {
        // Get the inventoryLot
        restInventoryLotMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingInventoryLot() throws Exception {
        // Initialize the database
        insertedInventoryLot = inventoryLotRepository.saveAndFlush(inventoryLot);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the inventoryLot
        InventoryLot updatedInventoryLot = inventoryLotRepository.findById(inventoryLot.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedInventoryLot are not directly saved in db
        em.detach(updatedInventoryLot);
        updatedInventoryLot
            .lotCode(UPDATED_LOT_CODE)
            .receivedDate(UPDATED_RECEIVED_DATE)
            .quantityReceived(UPDATED_QUANTITY_RECEIVED)
            .quantityOnHand(UPDATED_QUANTITY_ON_HAND)
            .unit(UPDATED_UNIT)
            .expiryDate(UPDATED_EXPIRY_DATE)
            .storageLocation(UPDATED_STORAGE_LOCATION)
            .supplierLotNumber(UPDATED_SUPPLIER_LOT_NUMBER)
            .isExhausted(UPDATED_IS_EXHAUSTED)
            .note(UPDATED_NOTE);

        restInventoryLotMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedInventoryLot.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedInventoryLot))
            )
            .andExpect(status().isOk());

        // Validate the InventoryLot in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedInventoryLotToMatchAllProperties(updatedInventoryLot);
    }

    @Test
    @Transactional
    void putNonExistingInventoryLot() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inventoryLot.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInventoryLotMockMvc
            .perform(
                put(ENTITY_API_URL_ID, inventoryLot.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(inventoryLot))
            )
            .andExpect(status().isBadRequest());

        // Validate the InventoryLot in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchInventoryLot() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inventoryLot.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInventoryLotMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(inventoryLot))
            )
            .andExpect(status().isBadRequest());

        // Validate the InventoryLot in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamInventoryLot() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inventoryLot.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInventoryLotMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inventoryLot)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the InventoryLot in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateInventoryLotWithPatch() throws Exception {
        // Initialize the database
        insertedInventoryLot = inventoryLotRepository.saveAndFlush(inventoryLot);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the inventoryLot using partial update
        InventoryLot partialUpdatedInventoryLot = new InventoryLot();
        partialUpdatedInventoryLot.setId(inventoryLot.getId());

        partialUpdatedInventoryLot.lotCode(UPDATED_LOT_CODE).quantityReceived(UPDATED_QUANTITY_RECEIVED);

        restInventoryLotMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInventoryLot.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInventoryLot))
            )
            .andExpect(status().isOk());

        // Validate the InventoryLot in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInventoryLotUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedInventoryLot, inventoryLot),
            getPersistedInventoryLot(inventoryLot)
        );
    }

    @Test
    @Transactional
    void fullUpdateInventoryLotWithPatch() throws Exception {
        // Initialize the database
        insertedInventoryLot = inventoryLotRepository.saveAndFlush(inventoryLot);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the inventoryLot using partial update
        InventoryLot partialUpdatedInventoryLot = new InventoryLot();
        partialUpdatedInventoryLot.setId(inventoryLot.getId());

        partialUpdatedInventoryLot
            .lotCode(UPDATED_LOT_CODE)
            .receivedDate(UPDATED_RECEIVED_DATE)
            .quantityReceived(UPDATED_QUANTITY_RECEIVED)
            .quantityOnHand(UPDATED_QUANTITY_ON_HAND)
            .unit(UPDATED_UNIT)
            .expiryDate(UPDATED_EXPIRY_DATE)
            .storageLocation(UPDATED_STORAGE_LOCATION)
            .supplierLotNumber(UPDATED_SUPPLIER_LOT_NUMBER)
            .isExhausted(UPDATED_IS_EXHAUSTED)
            .note(UPDATED_NOTE);

        restInventoryLotMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInventoryLot.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInventoryLot))
            )
            .andExpect(status().isOk());

        // Validate the InventoryLot in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInventoryLotUpdatableFieldsEquals(partialUpdatedInventoryLot, getPersistedInventoryLot(partialUpdatedInventoryLot));
    }

    @Test
    @Transactional
    void patchNonExistingInventoryLot() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inventoryLot.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInventoryLotMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, inventoryLot.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(inventoryLot))
            )
            .andExpect(status().isBadRequest());

        // Validate the InventoryLot in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchInventoryLot() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inventoryLot.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInventoryLotMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(inventoryLot))
            )
            .andExpect(status().isBadRequest());

        // Validate the InventoryLot in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamInventoryLot() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inventoryLot.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInventoryLotMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(inventoryLot)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the InventoryLot in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteInventoryLot() throws Exception {
        // Initialize the database
        insertedInventoryLot = inventoryLotRepository.saveAndFlush(inventoryLot);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the inventoryLot
        restInventoryLotMockMvc
            .perform(delete(ENTITY_API_URL_ID, inventoryLot.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return inventoryLotRepository.count();
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

    protected InventoryLot getPersistedInventoryLot(InventoryLot inventoryLot) {
        return inventoryLotRepository.findById(inventoryLot.getId()).orElseThrow();
    }

    protected void assertPersistedInventoryLotToMatchAllProperties(InventoryLot expectedInventoryLot) {
        assertInventoryLotAllPropertiesEquals(expectedInventoryLot, getPersistedInventoryLot(expectedInventoryLot));
    }

    protected void assertPersistedInventoryLotToMatchUpdatableProperties(InventoryLot expectedInventoryLot) {
        assertInventoryLotAllUpdatablePropertiesEquals(expectedInventoryLot, getPersistedInventoryLot(expectedInventoryLot));
    }
}
