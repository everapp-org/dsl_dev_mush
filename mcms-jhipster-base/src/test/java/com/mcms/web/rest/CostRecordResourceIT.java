package com.mcms.web.rest;

import static com.mcms.domain.CostRecordAsserts.*;
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
import com.mcms.domain.CostRecord;
import com.mcms.domain.enumeration.CostCategory;
import com.mcms.repository.CostRecordRepository;
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
 * Integration tests for the {@link CostRecordResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CostRecordResourceIT {

    private static final LocalDate DEFAULT_RECORD_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_RECORD_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final CostCategory DEFAULT_CATEGORY = CostCategory.RAW_MATERIALS;
    private static final CostCategory UPDATED_CATEGORY = CostCategory.ENERGY;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);

    private static final String DEFAULT_CURRENCY = "AAAAAAAAAA";
    private static final String UPDATED_CURRENCY = "BBBBBBBBBB";

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/cost-records";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CostRecordRepository costRecordRepository;

    @Mock
    private CostRecordRepository costRecordRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCostRecordMockMvc;

    private CostRecord costRecord;

    private CostRecord insertedCostRecord;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CostRecord createEntity(EntityManager em) {
        CostRecord costRecord = new CostRecord()
            .recordDate(DEFAULT_RECORD_DATE)
            .category(DEFAULT_CATEGORY)
            .description(DEFAULT_DESCRIPTION)
            .amount(DEFAULT_AMOUNT)
            .currency(DEFAULT_CURRENCY)
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
        costRecord.setBatch(batch);
        return costRecord;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CostRecord createUpdatedEntity(EntityManager em) {
        CostRecord updatedCostRecord = new CostRecord()
            .recordDate(UPDATED_RECORD_DATE)
            .category(UPDATED_CATEGORY)
            .description(UPDATED_DESCRIPTION)
            .amount(UPDATED_AMOUNT)
            .currency(UPDATED_CURRENCY)
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
        updatedCostRecord.setBatch(batch);
        return updatedCostRecord;
    }

    @BeforeEach
    void initTest() {
        costRecord = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedCostRecord != null) {
            costRecordRepository.delete(insertedCostRecord);
            insertedCostRecord = null;
        }
    }

    @Test
    @Transactional
    void createCostRecord() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CostRecord
        var returnedCostRecord = om.readValue(
            restCostRecordMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(costRecord)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CostRecord.class
        );

        // Validate the CostRecord in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertCostRecordUpdatableFieldsEquals(returnedCostRecord, getPersistedCostRecord(returnedCostRecord));

        insertedCostRecord = returnedCostRecord;
    }

    @Test
    @Transactional
    void createCostRecordWithExistingId() throws Exception {
        // Create the CostRecord with an existing ID
        costRecord.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCostRecordMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(costRecord)))
            .andExpect(status().isBadRequest());

        // Validate the CostRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkRecordDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        costRecord.setRecordDate(null);

        // Create the CostRecord, which fails.

        restCostRecordMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(costRecord)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCategoryIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        costRecord.setCategory(null);

        // Create the CostRecord, which fails.

        restCostRecordMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(costRecord)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDescriptionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        costRecord.setDescription(null);

        // Create the CostRecord, which fails.

        restCostRecordMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(costRecord)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        costRecord.setAmount(null);

        // Create the CostRecord, which fails.

        restCostRecordMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(costRecord)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCurrencyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        costRecord.setCurrency(null);

        // Create the CostRecord, which fails.

        restCostRecordMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(costRecord)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCostRecords() throws Exception {
        // Initialize the database
        insertedCostRecord = costRecordRepository.saveAndFlush(costRecord);

        // Get all the costRecordList
        restCostRecordMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(costRecord.getId().intValue())))
            .andExpect(jsonPath("$.[*].recordDate").value(hasItem(DEFAULT_RECORD_DATE.toString())))
            .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY)))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCostRecordsWithEagerRelationshipsIsEnabled() throws Exception {
        when(costRecordRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCostRecordMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(costRecordRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCostRecordsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(costRecordRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCostRecordMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(costRecordRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getCostRecord() throws Exception {
        // Initialize the database
        insertedCostRecord = costRecordRepository.saveAndFlush(costRecord);

        // Get the costRecord
        restCostRecordMockMvc
            .perform(get(ENTITY_API_URL_ID, costRecord.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(costRecord.getId().intValue()))
            .andExpect(jsonPath("$.recordDate").value(DEFAULT_RECORD_DATE.toString()))
            .andExpect(jsonPath("$.category").value(DEFAULT_CATEGORY.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.amount").value(sameNumber(DEFAULT_AMOUNT)))
            .andExpect(jsonPath("$.currency").value(DEFAULT_CURRENCY))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE));
    }

    @Test
    @Transactional
    void getNonExistingCostRecord() throws Exception {
        // Get the costRecord
        restCostRecordMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCostRecord() throws Exception {
        // Initialize the database
        insertedCostRecord = costRecordRepository.saveAndFlush(costRecord);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the costRecord
        CostRecord updatedCostRecord = costRecordRepository.findById(costRecord.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCostRecord are not directly saved in db
        em.detach(updatedCostRecord);
        updatedCostRecord
            .recordDate(UPDATED_RECORD_DATE)
            .category(UPDATED_CATEGORY)
            .description(UPDATED_DESCRIPTION)
            .amount(UPDATED_AMOUNT)
            .currency(UPDATED_CURRENCY)
            .note(UPDATED_NOTE);

        restCostRecordMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCostRecord.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedCostRecord))
            )
            .andExpect(status().isOk());

        // Validate the CostRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCostRecordToMatchAllProperties(updatedCostRecord);
    }

    @Test
    @Transactional
    void putNonExistingCostRecord() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        costRecord.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCostRecordMockMvc
            .perform(
                put(ENTITY_API_URL_ID, costRecord.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(costRecord))
            )
            .andExpect(status().isBadRequest());

        // Validate the CostRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCostRecord() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        costRecord.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCostRecordMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(costRecord))
            )
            .andExpect(status().isBadRequest());

        // Validate the CostRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCostRecord() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        costRecord.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCostRecordMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(costRecord)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CostRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCostRecordWithPatch() throws Exception {
        // Initialize the database
        insertedCostRecord = costRecordRepository.saveAndFlush(costRecord);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the costRecord using partial update
        CostRecord partialUpdatedCostRecord = new CostRecord();
        partialUpdatedCostRecord.setId(costRecord.getId());

        partialUpdatedCostRecord.category(UPDATED_CATEGORY).description(UPDATED_DESCRIPTION).currency(UPDATED_CURRENCY).note(UPDATED_NOTE);

        restCostRecordMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCostRecord.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCostRecord))
            )
            .andExpect(status().isOk());

        // Validate the CostRecord in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCostRecordUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCostRecord, costRecord),
            getPersistedCostRecord(costRecord)
        );
    }

    @Test
    @Transactional
    void fullUpdateCostRecordWithPatch() throws Exception {
        // Initialize the database
        insertedCostRecord = costRecordRepository.saveAndFlush(costRecord);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the costRecord using partial update
        CostRecord partialUpdatedCostRecord = new CostRecord();
        partialUpdatedCostRecord.setId(costRecord.getId());

        partialUpdatedCostRecord
            .recordDate(UPDATED_RECORD_DATE)
            .category(UPDATED_CATEGORY)
            .description(UPDATED_DESCRIPTION)
            .amount(UPDATED_AMOUNT)
            .currency(UPDATED_CURRENCY)
            .note(UPDATED_NOTE);

        restCostRecordMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCostRecord.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCostRecord))
            )
            .andExpect(status().isOk());

        // Validate the CostRecord in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCostRecordUpdatableFieldsEquals(partialUpdatedCostRecord, getPersistedCostRecord(partialUpdatedCostRecord));
    }

    @Test
    @Transactional
    void patchNonExistingCostRecord() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        costRecord.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCostRecordMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, costRecord.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(costRecord))
            )
            .andExpect(status().isBadRequest());

        // Validate the CostRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCostRecord() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        costRecord.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCostRecordMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(costRecord))
            )
            .andExpect(status().isBadRequest());

        // Validate the CostRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCostRecord() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        costRecord.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCostRecordMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(costRecord)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CostRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCostRecord() throws Exception {
        // Initialize the database
        insertedCostRecord = costRecordRepository.saveAndFlush(costRecord);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the costRecord
        restCostRecordMockMvc
            .perform(delete(ENTITY_API_URL_ID, costRecord.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return costRecordRepository.count();
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

    protected CostRecord getPersistedCostRecord(CostRecord costRecord) {
        return costRecordRepository.findById(costRecord.getId()).orElseThrow();
    }

    protected void assertPersistedCostRecordToMatchAllProperties(CostRecord expectedCostRecord) {
        assertCostRecordAllPropertiesEquals(expectedCostRecord, getPersistedCostRecord(expectedCostRecord));
    }

    protected void assertPersistedCostRecordToMatchUpdatableProperties(CostRecord expectedCostRecord) {
        assertCostRecordAllUpdatablePropertiesEquals(expectedCostRecord, getPersistedCostRecord(expectedCostRecord));
    }
}
