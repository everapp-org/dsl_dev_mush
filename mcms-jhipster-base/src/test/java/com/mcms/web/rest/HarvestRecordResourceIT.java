package com.mcms.web.rest;

import static com.mcms.domain.HarvestRecordAsserts.*;
import static com.mcms.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mcms.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcms.IntegrationTest;
import com.mcms.domain.FlushCycle;
import com.mcms.domain.HarvestRecord;
import com.mcms.domain.enumeration.QualityGrade;
import com.mcms.repository.HarvestRecordRepository;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link HarvestRecordResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class HarvestRecordResourceIT {

    private static final LocalDate DEFAULT_HARVEST_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_HARVEST_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final BigDecimal DEFAULT_WEIGHT_KG = new BigDecimal(1);
    private static final BigDecimal UPDATED_WEIGHT_KG = new BigDecimal(2);

    private static final QualityGrade DEFAULT_GRADE = QualityGrade.A_PREMIUM;
    private static final QualityGrade UPDATED_GRADE = QualityGrade.B_STANDARD;

    private static final String DEFAULT_PICKER_NAME = "AAAAAAAAAA";
    private static final String UPDATED_PICKER_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/harvest-records";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private HarvestRecordRepository harvestRecordRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restHarvestRecordMockMvc;

    private HarvestRecord harvestRecord;

    private HarvestRecord insertedHarvestRecord;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static HarvestRecord createEntity(EntityManager em) {
        HarvestRecord harvestRecord = new HarvestRecord()
            .harvestDate(DEFAULT_HARVEST_DATE)
            .weightKg(DEFAULT_WEIGHT_KG)
            .grade(DEFAULT_GRADE)
            .pickerName(DEFAULT_PICKER_NAME)
            .note(DEFAULT_NOTE);
        // Add required entity
        FlushCycle flushCycle;
        if (TestUtil.findAll(em, FlushCycle.class).isEmpty()) {
            flushCycle = FlushCycleResourceIT.createEntity(em);
            em.persist(flushCycle);
            em.flush();
        } else {
            flushCycle = TestUtil.findAll(em, FlushCycle.class).get(0);
        }
        harvestRecord.setFlushCycle(flushCycle);
        return harvestRecord;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static HarvestRecord createUpdatedEntity(EntityManager em) {
        HarvestRecord updatedHarvestRecord = new HarvestRecord()
            .harvestDate(UPDATED_HARVEST_DATE)
            .weightKg(UPDATED_WEIGHT_KG)
            .grade(UPDATED_GRADE)
            .pickerName(UPDATED_PICKER_NAME)
            .note(UPDATED_NOTE);
        // Add required entity
        FlushCycle flushCycle;
        if (TestUtil.findAll(em, FlushCycle.class).isEmpty()) {
            flushCycle = FlushCycleResourceIT.createUpdatedEntity(em);
            em.persist(flushCycle);
            em.flush();
        } else {
            flushCycle = TestUtil.findAll(em, FlushCycle.class).get(0);
        }
        updatedHarvestRecord.setFlushCycle(flushCycle);
        return updatedHarvestRecord;
    }

    @BeforeEach
    void initTest() {
        harvestRecord = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedHarvestRecord != null) {
            harvestRecordRepository.delete(insertedHarvestRecord);
            insertedHarvestRecord = null;
        }
    }

    @Test
    @Transactional
    void createHarvestRecord() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the HarvestRecord
        var returnedHarvestRecord = om.readValue(
            restHarvestRecordMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(harvestRecord)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            HarvestRecord.class
        );

        // Validate the HarvestRecord in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertHarvestRecordUpdatableFieldsEquals(returnedHarvestRecord, getPersistedHarvestRecord(returnedHarvestRecord));

        insertedHarvestRecord = returnedHarvestRecord;
    }

    @Test
    @Transactional
    void createHarvestRecordWithExistingId() throws Exception {
        // Create the HarvestRecord with an existing ID
        harvestRecord.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restHarvestRecordMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(harvestRecord)))
            .andExpect(status().isBadRequest());

        // Validate the HarvestRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkHarvestDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        harvestRecord.setHarvestDate(null);

        // Create the HarvestRecord, which fails.

        restHarvestRecordMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(harvestRecord)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkWeightKgIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        harvestRecord.setWeightKg(null);

        // Create the HarvestRecord, which fails.

        restHarvestRecordMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(harvestRecord)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkGradeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        harvestRecord.setGrade(null);

        // Create the HarvestRecord, which fails.

        restHarvestRecordMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(harvestRecord)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllHarvestRecords() throws Exception {
        // Initialize the database
        insertedHarvestRecord = harvestRecordRepository.saveAndFlush(harvestRecord);

        // Get all the harvestRecordList
        restHarvestRecordMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(harvestRecord.getId().intValue())))
            .andExpect(jsonPath("$.[*].harvestDate").value(hasItem(DEFAULT_HARVEST_DATE.toString())))
            .andExpect(jsonPath("$.[*].weightKg").value(hasItem(sameNumber(DEFAULT_WEIGHT_KG))))
            .andExpect(jsonPath("$.[*].grade").value(hasItem(DEFAULT_GRADE.toString())))
            .andExpect(jsonPath("$.[*].pickerName").value(hasItem(DEFAULT_PICKER_NAME)))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)));
    }

    @Test
    @Transactional
    void getHarvestRecord() throws Exception {
        // Initialize the database
        insertedHarvestRecord = harvestRecordRepository.saveAndFlush(harvestRecord);

        // Get the harvestRecord
        restHarvestRecordMockMvc
            .perform(get(ENTITY_API_URL_ID, harvestRecord.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(harvestRecord.getId().intValue()))
            .andExpect(jsonPath("$.harvestDate").value(DEFAULT_HARVEST_DATE.toString()))
            .andExpect(jsonPath("$.weightKg").value(sameNumber(DEFAULT_WEIGHT_KG)))
            .andExpect(jsonPath("$.grade").value(DEFAULT_GRADE.toString()))
            .andExpect(jsonPath("$.pickerName").value(DEFAULT_PICKER_NAME))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE));
    }

    @Test
    @Transactional
    void getNonExistingHarvestRecord() throws Exception {
        // Get the harvestRecord
        restHarvestRecordMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingHarvestRecord() throws Exception {
        // Initialize the database
        insertedHarvestRecord = harvestRecordRepository.saveAndFlush(harvestRecord);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the harvestRecord
        HarvestRecord updatedHarvestRecord = harvestRecordRepository.findById(harvestRecord.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedHarvestRecord are not directly saved in db
        em.detach(updatedHarvestRecord);
        updatedHarvestRecord
            .harvestDate(UPDATED_HARVEST_DATE)
            .weightKg(UPDATED_WEIGHT_KG)
            .grade(UPDATED_GRADE)
            .pickerName(UPDATED_PICKER_NAME)
            .note(UPDATED_NOTE);

        restHarvestRecordMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedHarvestRecord.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedHarvestRecord))
            )
            .andExpect(status().isOk());

        // Validate the HarvestRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedHarvestRecordToMatchAllProperties(updatedHarvestRecord);
    }

    @Test
    @Transactional
    void putNonExistingHarvestRecord() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        harvestRecord.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHarvestRecordMockMvc
            .perform(
                put(ENTITY_API_URL_ID, harvestRecord.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(harvestRecord))
            )
            .andExpect(status().isBadRequest());

        // Validate the HarvestRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchHarvestRecord() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        harvestRecord.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHarvestRecordMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(harvestRecord))
            )
            .andExpect(status().isBadRequest());

        // Validate the HarvestRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamHarvestRecord() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        harvestRecord.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHarvestRecordMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(harvestRecord)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the HarvestRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateHarvestRecordWithPatch() throws Exception {
        // Initialize the database
        insertedHarvestRecord = harvestRecordRepository.saveAndFlush(harvestRecord);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the harvestRecord using partial update
        HarvestRecord partialUpdatedHarvestRecord = new HarvestRecord();
        partialUpdatedHarvestRecord.setId(harvestRecord.getId());

        partialUpdatedHarvestRecord.pickerName(UPDATED_PICKER_NAME).note(UPDATED_NOTE);

        restHarvestRecordMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHarvestRecord.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedHarvestRecord))
            )
            .andExpect(status().isOk());

        // Validate the HarvestRecord in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertHarvestRecordUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedHarvestRecord, harvestRecord),
            getPersistedHarvestRecord(harvestRecord)
        );
    }

    @Test
    @Transactional
    void fullUpdateHarvestRecordWithPatch() throws Exception {
        // Initialize the database
        insertedHarvestRecord = harvestRecordRepository.saveAndFlush(harvestRecord);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the harvestRecord using partial update
        HarvestRecord partialUpdatedHarvestRecord = new HarvestRecord();
        partialUpdatedHarvestRecord.setId(harvestRecord.getId());

        partialUpdatedHarvestRecord
            .harvestDate(UPDATED_HARVEST_DATE)
            .weightKg(UPDATED_WEIGHT_KG)
            .grade(UPDATED_GRADE)
            .pickerName(UPDATED_PICKER_NAME)
            .note(UPDATED_NOTE);

        restHarvestRecordMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHarvestRecord.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedHarvestRecord))
            )
            .andExpect(status().isOk());

        // Validate the HarvestRecord in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertHarvestRecordUpdatableFieldsEquals(partialUpdatedHarvestRecord, getPersistedHarvestRecord(partialUpdatedHarvestRecord));
    }

    @Test
    @Transactional
    void patchNonExistingHarvestRecord() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        harvestRecord.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHarvestRecordMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, harvestRecord.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(harvestRecord))
            )
            .andExpect(status().isBadRequest());

        // Validate the HarvestRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchHarvestRecord() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        harvestRecord.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHarvestRecordMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(harvestRecord))
            )
            .andExpect(status().isBadRequest());

        // Validate the HarvestRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamHarvestRecord() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        harvestRecord.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHarvestRecordMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(harvestRecord)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the HarvestRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteHarvestRecord() throws Exception {
        // Initialize the database
        insertedHarvestRecord = harvestRecordRepository.saveAndFlush(harvestRecord);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the harvestRecord
        restHarvestRecordMockMvc
            .perform(delete(ENTITY_API_URL_ID, harvestRecord.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return harvestRecordRepository.count();
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

    protected HarvestRecord getPersistedHarvestRecord(HarvestRecord harvestRecord) {
        return harvestRecordRepository.findById(harvestRecord.getId()).orElseThrow();
    }

    protected void assertPersistedHarvestRecordToMatchAllProperties(HarvestRecord expectedHarvestRecord) {
        assertHarvestRecordAllPropertiesEquals(expectedHarvestRecord, getPersistedHarvestRecord(expectedHarvestRecord));
    }

    protected void assertPersistedHarvestRecordToMatchUpdatableProperties(HarvestRecord expectedHarvestRecord) {
        assertHarvestRecordAllUpdatablePropertiesEquals(expectedHarvestRecord, getPersistedHarvestRecord(expectedHarvestRecord));
    }
}
