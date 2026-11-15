package com.mcms.web.rest;

import static com.mcms.domain.BatchMaterialUsageAsserts.*;
import static com.mcms.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mcms.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcms.IntegrationTest;
import com.mcms.domain.Batch;
import com.mcms.domain.BatchMaterialUsage;
import com.mcms.domain.InventoryLot;
import com.mcms.domain.Material;
import com.mcms.domain.enumeration.UnitOfMeasure;
import com.mcms.repository.BatchMaterialUsageRepository;
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
 * Integration tests for the {@link BatchMaterialUsageResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class BatchMaterialUsageResourceIT {

    private static final LocalDate DEFAULT_USAGE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_USAGE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final BigDecimal DEFAULT_QUANTITY_USED = new BigDecimal(1);
    private static final BigDecimal UPDATED_QUANTITY_USED = new BigDecimal(2);

    private static final UnitOfMeasure DEFAULT_UNIT = UnitOfMeasure.KG;
    private static final UnitOfMeasure UPDATED_UNIT = UnitOfMeasure.GRAM;

    private static final String DEFAULT_PURPOSE = "AAAAAAAAAA";
    private static final String UPDATED_PURPOSE = "BBBBBBBBBB";

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/batch-material-usages";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BatchMaterialUsageRepository batchMaterialUsageRepository;

    @Mock
    private BatchMaterialUsageRepository batchMaterialUsageRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBatchMaterialUsageMockMvc;

    private BatchMaterialUsage batchMaterialUsage;

    private BatchMaterialUsage insertedBatchMaterialUsage;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BatchMaterialUsage createEntity(EntityManager em) {
        BatchMaterialUsage batchMaterialUsage = new BatchMaterialUsage()
            .usageDate(DEFAULT_USAGE_DATE)
            .quantityUsed(DEFAULT_QUANTITY_USED)
            .unit(DEFAULT_UNIT)
            .purpose(DEFAULT_PURPOSE)
            .note(DEFAULT_NOTE);
        // Add required entity
        Batch batch;
        if (TestUtil.findAll(em, Batch.class).isEmpty()) {
            batch = BatchResourceIT.createEntity(em);
            em.persist(batch);
            em.flush();
        } else {
            batch = TestUtil.findAll(em, Batch.class).get(0);
        }
        batchMaterialUsage.setBatch(batch);
        // Add required entity
        InventoryLot inventoryLot;
        if (TestUtil.findAll(em, InventoryLot.class).isEmpty()) {
            inventoryLot = InventoryLotResourceIT.createEntity(em);
            em.persist(inventoryLot);
            em.flush();
        } else {
            inventoryLot = TestUtil.findAll(em, InventoryLot.class).get(0);
        }
        batchMaterialUsage.setInventoryLot(inventoryLot);
        // Add required entity
        Material material;
        if (TestUtil.findAll(em, Material.class).isEmpty()) {
            material = MaterialResourceIT.createEntity();
            em.persist(material);
            em.flush();
        } else {
            material = TestUtil.findAll(em, Material.class).get(0);
        }
        batchMaterialUsage.setMaterial(material);
        return batchMaterialUsage;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BatchMaterialUsage createUpdatedEntity(EntityManager em) {
        BatchMaterialUsage updatedBatchMaterialUsage = new BatchMaterialUsage()
            .usageDate(UPDATED_USAGE_DATE)
            .quantityUsed(UPDATED_QUANTITY_USED)
            .unit(UPDATED_UNIT)
            .purpose(UPDATED_PURPOSE)
            .note(UPDATED_NOTE);
        // Add required entity
        Batch batch;
        if (TestUtil.findAll(em, Batch.class).isEmpty()) {
            batch = BatchResourceIT.createUpdatedEntity(em);
            em.persist(batch);
            em.flush();
        } else {
            batch = TestUtil.findAll(em, Batch.class).get(0);
        }
        updatedBatchMaterialUsage.setBatch(batch);
        // Add required entity
        InventoryLot inventoryLot;
        if (TestUtil.findAll(em, InventoryLot.class).isEmpty()) {
            inventoryLot = InventoryLotResourceIT.createUpdatedEntity(em);
            em.persist(inventoryLot);
            em.flush();
        } else {
            inventoryLot = TestUtil.findAll(em, InventoryLot.class).get(0);
        }
        updatedBatchMaterialUsage.setInventoryLot(inventoryLot);
        // Add required entity
        Material material;
        if (TestUtil.findAll(em, Material.class).isEmpty()) {
            material = MaterialResourceIT.createUpdatedEntity();
            em.persist(material);
            em.flush();
        } else {
            material = TestUtil.findAll(em, Material.class).get(0);
        }
        updatedBatchMaterialUsage.setMaterial(material);
        return updatedBatchMaterialUsage;
    }

    @BeforeEach
    void initTest() {
        batchMaterialUsage = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedBatchMaterialUsage != null) {
            batchMaterialUsageRepository.delete(insertedBatchMaterialUsage);
            insertedBatchMaterialUsage = null;
        }
    }

    @Test
    @Transactional
    void createBatchMaterialUsage() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the BatchMaterialUsage
        var returnedBatchMaterialUsage = om.readValue(
            restBatchMaterialUsageMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(batchMaterialUsage)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            BatchMaterialUsage.class
        );

        // Validate the BatchMaterialUsage in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertBatchMaterialUsageUpdatableFieldsEquals(
            returnedBatchMaterialUsage,
            getPersistedBatchMaterialUsage(returnedBatchMaterialUsage)
        );

        insertedBatchMaterialUsage = returnedBatchMaterialUsage;
    }

    @Test
    @Transactional
    void createBatchMaterialUsageWithExistingId() throws Exception {
        // Create the BatchMaterialUsage with an existing ID
        batchMaterialUsage.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBatchMaterialUsageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(batchMaterialUsage)))
            .andExpect(status().isBadRequest());

        // Validate the BatchMaterialUsage in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkUsageDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        batchMaterialUsage.setUsageDate(null);

        // Create the BatchMaterialUsage, which fails.

        restBatchMaterialUsageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(batchMaterialUsage)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkQuantityUsedIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        batchMaterialUsage.setQuantityUsed(null);

        // Create the BatchMaterialUsage, which fails.

        restBatchMaterialUsageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(batchMaterialUsage)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUnitIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        batchMaterialUsage.setUnit(null);

        // Create the BatchMaterialUsage, which fails.

        restBatchMaterialUsageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(batchMaterialUsage)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBatchMaterialUsages() throws Exception {
        // Initialize the database
        insertedBatchMaterialUsage = batchMaterialUsageRepository.saveAndFlush(batchMaterialUsage);

        // Get all the batchMaterialUsageList
        restBatchMaterialUsageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(batchMaterialUsage.getId().intValue())))
            .andExpect(jsonPath("$.[*].usageDate").value(hasItem(DEFAULT_USAGE_DATE.toString())))
            .andExpect(jsonPath("$.[*].quantityUsed").value(hasItem(sameNumber(DEFAULT_QUANTITY_USED))))
            .andExpect(jsonPath("$.[*].unit").value(hasItem(DEFAULT_UNIT.toString())))
            .andExpect(jsonPath("$.[*].purpose").value(hasItem(DEFAULT_PURPOSE)))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBatchMaterialUsagesWithEagerRelationshipsIsEnabled() throws Exception {
        when(batchMaterialUsageRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBatchMaterialUsageMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(batchMaterialUsageRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBatchMaterialUsagesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(batchMaterialUsageRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBatchMaterialUsageMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(batchMaterialUsageRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getBatchMaterialUsage() throws Exception {
        // Initialize the database
        insertedBatchMaterialUsage = batchMaterialUsageRepository.saveAndFlush(batchMaterialUsage);

        // Get the batchMaterialUsage
        restBatchMaterialUsageMockMvc
            .perform(get(ENTITY_API_URL_ID, batchMaterialUsage.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(batchMaterialUsage.getId().intValue()))
            .andExpect(jsonPath("$.usageDate").value(DEFAULT_USAGE_DATE.toString()))
            .andExpect(jsonPath("$.quantityUsed").value(sameNumber(DEFAULT_QUANTITY_USED)))
            .andExpect(jsonPath("$.unit").value(DEFAULT_UNIT.toString()))
            .andExpect(jsonPath("$.purpose").value(DEFAULT_PURPOSE))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE));
    }

    @Test
    @Transactional
    void getNonExistingBatchMaterialUsage() throws Exception {
        // Get the batchMaterialUsage
        restBatchMaterialUsageMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBatchMaterialUsage() throws Exception {
        // Initialize the database
        insertedBatchMaterialUsage = batchMaterialUsageRepository.saveAndFlush(batchMaterialUsage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the batchMaterialUsage
        BatchMaterialUsage updatedBatchMaterialUsage = batchMaterialUsageRepository.findById(batchMaterialUsage.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedBatchMaterialUsage are not directly saved in db
        em.detach(updatedBatchMaterialUsage);
        updatedBatchMaterialUsage
            .usageDate(UPDATED_USAGE_DATE)
            .quantityUsed(UPDATED_QUANTITY_USED)
            .unit(UPDATED_UNIT)
            .purpose(UPDATED_PURPOSE)
            .note(UPDATED_NOTE);

        restBatchMaterialUsageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedBatchMaterialUsage.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedBatchMaterialUsage))
            )
            .andExpect(status().isOk());

        // Validate the BatchMaterialUsage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedBatchMaterialUsageToMatchAllProperties(updatedBatchMaterialUsage);
    }

    @Test
    @Transactional
    void putNonExistingBatchMaterialUsage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        batchMaterialUsage.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBatchMaterialUsageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, batchMaterialUsage.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(batchMaterialUsage))
            )
            .andExpect(status().isBadRequest());

        // Validate the BatchMaterialUsage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBatchMaterialUsage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        batchMaterialUsage.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBatchMaterialUsageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(batchMaterialUsage))
            )
            .andExpect(status().isBadRequest());

        // Validate the BatchMaterialUsage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBatchMaterialUsage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        batchMaterialUsage.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBatchMaterialUsageMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(batchMaterialUsage)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BatchMaterialUsage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBatchMaterialUsageWithPatch() throws Exception {
        // Initialize the database
        insertedBatchMaterialUsage = batchMaterialUsageRepository.saveAndFlush(batchMaterialUsage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the batchMaterialUsage using partial update
        BatchMaterialUsage partialUpdatedBatchMaterialUsage = new BatchMaterialUsage();
        partialUpdatedBatchMaterialUsage.setId(batchMaterialUsage.getId());

        partialUpdatedBatchMaterialUsage.usageDate(UPDATED_USAGE_DATE).purpose(UPDATED_PURPOSE);

        restBatchMaterialUsageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBatchMaterialUsage.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBatchMaterialUsage))
            )
            .andExpect(status().isOk());

        // Validate the BatchMaterialUsage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBatchMaterialUsageUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedBatchMaterialUsage, batchMaterialUsage),
            getPersistedBatchMaterialUsage(batchMaterialUsage)
        );
    }

    @Test
    @Transactional
    void fullUpdateBatchMaterialUsageWithPatch() throws Exception {
        // Initialize the database
        insertedBatchMaterialUsage = batchMaterialUsageRepository.saveAndFlush(batchMaterialUsage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the batchMaterialUsage using partial update
        BatchMaterialUsage partialUpdatedBatchMaterialUsage = new BatchMaterialUsage();
        partialUpdatedBatchMaterialUsage.setId(batchMaterialUsage.getId());

        partialUpdatedBatchMaterialUsage
            .usageDate(UPDATED_USAGE_DATE)
            .quantityUsed(UPDATED_QUANTITY_USED)
            .unit(UPDATED_UNIT)
            .purpose(UPDATED_PURPOSE)
            .note(UPDATED_NOTE);

        restBatchMaterialUsageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBatchMaterialUsage.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBatchMaterialUsage))
            )
            .andExpect(status().isOk());

        // Validate the BatchMaterialUsage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBatchMaterialUsageUpdatableFieldsEquals(
            partialUpdatedBatchMaterialUsage,
            getPersistedBatchMaterialUsage(partialUpdatedBatchMaterialUsage)
        );
    }

    @Test
    @Transactional
    void patchNonExistingBatchMaterialUsage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        batchMaterialUsage.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBatchMaterialUsageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, batchMaterialUsage.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(batchMaterialUsage))
            )
            .andExpect(status().isBadRequest());

        // Validate the BatchMaterialUsage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBatchMaterialUsage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        batchMaterialUsage.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBatchMaterialUsageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(batchMaterialUsage))
            )
            .andExpect(status().isBadRequest());

        // Validate the BatchMaterialUsage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBatchMaterialUsage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        batchMaterialUsage.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBatchMaterialUsageMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(batchMaterialUsage)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BatchMaterialUsage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBatchMaterialUsage() throws Exception {
        // Initialize the database
        insertedBatchMaterialUsage = batchMaterialUsageRepository.saveAndFlush(batchMaterialUsage);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the batchMaterialUsage
        restBatchMaterialUsageMockMvc
            .perform(delete(ENTITY_API_URL_ID, batchMaterialUsage.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return batchMaterialUsageRepository.count();
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

    protected BatchMaterialUsage getPersistedBatchMaterialUsage(BatchMaterialUsage batchMaterialUsage) {
        return batchMaterialUsageRepository.findById(batchMaterialUsage.getId()).orElseThrow();
    }

    protected void assertPersistedBatchMaterialUsageToMatchAllProperties(BatchMaterialUsage expectedBatchMaterialUsage) {
        assertBatchMaterialUsageAllPropertiesEquals(expectedBatchMaterialUsage, getPersistedBatchMaterialUsage(expectedBatchMaterialUsage));
    }

    protected void assertPersistedBatchMaterialUsageToMatchUpdatableProperties(BatchMaterialUsage expectedBatchMaterialUsage) {
        assertBatchMaterialUsageAllUpdatablePropertiesEquals(
            expectedBatchMaterialUsage,
            getPersistedBatchMaterialUsage(expectedBatchMaterialUsage)
        );
    }
}
