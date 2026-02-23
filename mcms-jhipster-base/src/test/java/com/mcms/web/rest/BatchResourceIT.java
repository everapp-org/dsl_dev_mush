package com.mcms.web.rest;

import static com.mcms.domain.BatchAsserts.*;
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
import com.mcms.domain.Strain;
import com.mcms.domain.SubstrateRecipe;
import com.mcms.domain.enumeration.PhaseName;
import com.mcms.repository.BatchRepository;
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
 * Integration tests for the {@link BatchResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class BatchResourceIT {

    private static final String DEFAULT_BATCH_CODE = "AAAAAAAAAA";
    private static final String UPDATED_BATCH_CODE = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_END_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final PhaseName DEFAULT_CURRENT_PHASE = PhaseName.INOCULATION;
    private static final PhaseName UPDATED_CURRENT_PHASE = PhaseName.EARLY_COLONIZATION;

    private static final Integer DEFAULT_NUMBER_OF_BAGS = 1;
    private static final Integer UPDATED_NUMBER_OF_BAGS = 2;

    private static final BigDecimal DEFAULT_SUBSTRATE_WEIGHT_KG = new BigDecimal(1);
    private static final BigDecimal UPDATED_SUBSTRATE_WEIGHT_KG = new BigDecimal(2);

    private static final BigDecimal DEFAULT_SPAWN_WEIGHT_KG = new BigDecimal(1);
    private static final BigDecimal UPDATED_SPAWN_WEIGHT_KG = new BigDecimal(2);

    private static final BigDecimal DEFAULT_TARGET_YIELD_KG = new BigDecimal(1);
    private static final BigDecimal UPDATED_TARGET_YIELD_KG = new BigDecimal(2);

    private static final BigDecimal DEFAULT_ACTUAL_TOTAL_YIELD_KG = new BigDecimal(1);
    private static final BigDecimal UPDATED_ACTUAL_TOTAL_YIELD_KG = new BigDecimal(2);

    private static final BigDecimal DEFAULT_BIOLOGICAL_EFFICIENCY_PERCENT = new BigDecimal(1);
    private static final BigDecimal UPDATED_BIOLOGICAL_EFFICIENCY_PERCENT = new BigDecimal(2);

    private static final Boolean DEFAULT_IS_CONTAMINATED = false;
    private static final Boolean UPDATED_IS_CONTAMINATED = true;

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final String DEFAULT_COMPLETION_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_COMPLETION_NOTE = "BBBBBBBBBB";

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/batches";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BatchRepository batchRepository;

    @Mock
    private BatchRepository batchRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBatchMockMvc;

    private Batch batch;

    private Batch insertedBatch;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Batch createEntity(EntityManager em) {
        Batch batch = new Batch()
            .batchCode(DEFAULT_BATCH_CODE)
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
            .currentPhase(DEFAULT_CURRENT_PHASE)
            .numberOfBags(DEFAULT_NUMBER_OF_BAGS)
            .substrateWeightKg(DEFAULT_SUBSTRATE_WEIGHT_KG)
            .spawnWeightKg(DEFAULT_SPAWN_WEIGHT_KG)
            .targetYieldKg(DEFAULT_TARGET_YIELD_KG)
            .actualTotalYieldKg(DEFAULT_ACTUAL_TOTAL_YIELD_KG)
            .biologicalEfficiencyPercent(DEFAULT_BIOLOGICAL_EFFICIENCY_PERCENT)
            .isContaminated(DEFAULT_IS_CONTAMINATED)
            .isActive(DEFAULT_IS_ACTIVE)
            .completionNote(DEFAULT_COMPLETION_NOTE)
            .note(DEFAULT_NOTE);
        // Add required entity
        Strain strain;
        if (TestUtil.findAll(em, Strain.class).isEmpty()) {
            strain = StrainResourceIT.createEntity();
            em.persist(strain);
            em.flush();
        } else {
            strain = TestUtil.findAll(em, Strain.class).get(0);
        }
        batch.setStrain(strain);
        // Add required entity
        SubstrateRecipe substrateRecipe;
        if (TestUtil.findAll(em, SubstrateRecipe.class).isEmpty()) {
            substrateRecipe = SubstrateRecipeResourceIT.createEntity();
            em.persist(substrateRecipe);
            em.flush();
        } else {
            substrateRecipe = TestUtil.findAll(em, SubstrateRecipe.class).get(0);
        }
        batch.setRecipe(substrateRecipe);
        return batch;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Batch createUpdatedEntity(EntityManager em) {
        Batch updatedBatch = new Batch()
            .batchCode(UPDATED_BATCH_CODE)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .currentPhase(UPDATED_CURRENT_PHASE)
            .numberOfBags(UPDATED_NUMBER_OF_BAGS)
            .substrateWeightKg(UPDATED_SUBSTRATE_WEIGHT_KG)
            .spawnWeightKg(UPDATED_SPAWN_WEIGHT_KG)
            .targetYieldKg(UPDATED_TARGET_YIELD_KG)
            .actualTotalYieldKg(UPDATED_ACTUAL_TOTAL_YIELD_KG)
            .biologicalEfficiencyPercent(UPDATED_BIOLOGICAL_EFFICIENCY_PERCENT)
            .isContaminated(UPDATED_IS_CONTAMINATED)
            .isActive(UPDATED_IS_ACTIVE)
            .completionNote(UPDATED_COMPLETION_NOTE)
            .note(UPDATED_NOTE);
        // Add required entity
        Strain strain;
        if (TestUtil.findAll(em, Strain.class).isEmpty()) {
            strain = StrainResourceIT.createUpdatedEntity();
            em.persist(strain);
            em.flush();
        } else {
            strain = TestUtil.findAll(em, Strain.class).get(0);
        }
        updatedBatch.setStrain(strain);
        // Add required entity
        SubstrateRecipe substrateRecipe;
        if (TestUtil.findAll(em, SubstrateRecipe.class).isEmpty()) {
            substrateRecipe = SubstrateRecipeResourceIT.createUpdatedEntity();
            em.persist(substrateRecipe);
            em.flush();
        } else {
            substrateRecipe = TestUtil.findAll(em, SubstrateRecipe.class).get(0);
        }
        updatedBatch.setRecipe(substrateRecipe);
        return updatedBatch;
    }

    @BeforeEach
    void initTest() {
        batch = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedBatch != null) {
            batchRepository.delete(insertedBatch);
            insertedBatch = null;
        }
    }

    @Test
    @Transactional
    void createBatch() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Batch
        var returnedBatch = om.readValue(
            restBatchMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(batch)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Batch.class
        );

        // Validate the Batch in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertBatchUpdatableFieldsEquals(returnedBatch, getPersistedBatch(returnedBatch));

        insertedBatch = returnedBatch;
    }

    @Test
    @Transactional
    void createBatchWithExistingId() throws Exception {
        // Create the Batch with an existing ID
        batch.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBatchMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(batch)))
            .andExpect(status().isBadRequest());

        // Validate the Batch in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkBatchCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        batch.setBatchCode(null);

        // Create the Batch, which fails.

        restBatchMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(batch)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStartDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        batch.setStartDate(null);

        // Create the Batch, which fails.

        restBatchMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(batch)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCurrentPhaseIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        batch.setCurrentPhase(null);

        // Create the Batch, which fails.

        restBatchMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(batch)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        batch.setIsActive(null);

        // Create the Batch, which fails.

        restBatchMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(batch)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBatches() throws Exception {
        // Initialize the database
        insertedBatch = batchRepository.saveAndFlush(batch);

        // Get all the batchList
        restBatchMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(batch.getId().intValue())))
            .andExpect(jsonPath("$.[*].batchCode").value(hasItem(DEFAULT_BATCH_CODE)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].currentPhase").value(hasItem(DEFAULT_CURRENT_PHASE.toString())))
            .andExpect(jsonPath("$.[*].numberOfBags").value(hasItem(DEFAULT_NUMBER_OF_BAGS)))
            .andExpect(jsonPath("$.[*].substrateWeightKg").value(hasItem(sameNumber(DEFAULT_SUBSTRATE_WEIGHT_KG))))
            .andExpect(jsonPath("$.[*].spawnWeightKg").value(hasItem(sameNumber(DEFAULT_SPAWN_WEIGHT_KG))))
            .andExpect(jsonPath("$.[*].targetYieldKg").value(hasItem(sameNumber(DEFAULT_TARGET_YIELD_KG))))
            .andExpect(jsonPath("$.[*].actualTotalYieldKg").value(hasItem(sameNumber(DEFAULT_ACTUAL_TOTAL_YIELD_KG))))
            .andExpect(jsonPath("$.[*].biologicalEfficiencyPercent").value(hasItem(sameNumber(DEFAULT_BIOLOGICAL_EFFICIENCY_PERCENT))))
            .andExpect(jsonPath("$.[*].isContaminated").value(hasItem(DEFAULT_IS_CONTAMINATED)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE)))
            .andExpect(jsonPath("$.[*].completionNote").value(hasItem(DEFAULT_COMPLETION_NOTE)))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBatchesWithEagerRelationshipsIsEnabled() throws Exception {
        when(batchRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBatchMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(batchRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBatchesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(batchRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBatchMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(batchRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getBatch() throws Exception {
        // Initialize the database
        insertedBatch = batchRepository.saveAndFlush(batch);

        // Get the batch
        restBatchMockMvc
            .perform(get(ENTITY_API_URL_ID, batch.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(batch.getId().intValue()))
            .andExpect(jsonPath("$.batchCode").value(DEFAULT_BATCH_CODE))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.currentPhase").value(DEFAULT_CURRENT_PHASE.toString()))
            .andExpect(jsonPath("$.numberOfBags").value(DEFAULT_NUMBER_OF_BAGS))
            .andExpect(jsonPath("$.substrateWeightKg").value(sameNumber(DEFAULT_SUBSTRATE_WEIGHT_KG)))
            .andExpect(jsonPath("$.spawnWeightKg").value(sameNumber(DEFAULT_SPAWN_WEIGHT_KG)))
            .andExpect(jsonPath("$.targetYieldKg").value(sameNumber(DEFAULT_TARGET_YIELD_KG)))
            .andExpect(jsonPath("$.actualTotalYieldKg").value(sameNumber(DEFAULT_ACTUAL_TOTAL_YIELD_KG)))
            .andExpect(jsonPath("$.biologicalEfficiencyPercent").value(sameNumber(DEFAULT_BIOLOGICAL_EFFICIENCY_PERCENT)))
            .andExpect(jsonPath("$.isContaminated").value(DEFAULT_IS_CONTAMINATED))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE))
            .andExpect(jsonPath("$.completionNote").value(DEFAULT_COMPLETION_NOTE))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE));
    }

    @Test
    @Transactional
    void getNonExistingBatch() throws Exception {
        // Get the batch
        restBatchMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBatch() throws Exception {
        // Initialize the database
        insertedBatch = batchRepository.saveAndFlush(batch);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the batch
        Batch updatedBatch = batchRepository.findById(batch.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedBatch are not directly saved in db
        em.detach(updatedBatch);
        updatedBatch
            .batchCode(UPDATED_BATCH_CODE)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .currentPhase(UPDATED_CURRENT_PHASE)
            .numberOfBags(UPDATED_NUMBER_OF_BAGS)
            .substrateWeightKg(UPDATED_SUBSTRATE_WEIGHT_KG)
            .spawnWeightKg(UPDATED_SPAWN_WEIGHT_KG)
            .targetYieldKg(UPDATED_TARGET_YIELD_KG)
            .actualTotalYieldKg(UPDATED_ACTUAL_TOTAL_YIELD_KG)
            .biologicalEfficiencyPercent(UPDATED_BIOLOGICAL_EFFICIENCY_PERCENT)
            .isContaminated(UPDATED_IS_CONTAMINATED)
            .isActive(UPDATED_IS_ACTIVE)
            .completionNote(UPDATED_COMPLETION_NOTE)
            .note(UPDATED_NOTE);

        restBatchMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedBatch.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedBatch))
            )
            .andExpect(status().isOk());

        // Validate the Batch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedBatchToMatchAllProperties(updatedBatch);
    }

    @Test
    @Transactional
    void putNonExistingBatch() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        batch.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBatchMockMvc
            .perform(put(ENTITY_API_URL_ID, batch.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(batch)))
            .andExpect(status().isBadRequest());

        // Validate the Batch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBatch() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        batch.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBatchMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(batch))
            )
            .andExpect(status().isBadRequest());

        // Validate the Batch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBatch() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        batch.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBatchMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(batch)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Batch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBatchWithPatch() throws Exception {
        // Initialize the database
        insertedBatch = batchRepository.saveAndFlush(batch);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the batch using partial update
        Batch partialUpdatedBatch = new Batch();
        partialUpdatedBatch.setId(batch.getId());

        partialUpdatedBatch
            .batchCode(UPDATED_BATCH_CODE)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .substrateWeightKg(UPDATED_SUBSTRATE_WEIGHT_KG)
            .actualTotalYieldKg(UPDATED_ACTUAL_TOTAL_YIELD_KG)
            .completionNote(UPDATED_COMPLETION_NOTE);

        restBatchMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBatch.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBatch))
            )
            .andExpect(status().isOk());

        // Validate the Batch in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBatchUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedBatch, batch), getPersistedBatch(batch));
    }

    @Test
    @Transactional
    void fullUpdateBatchWithPatch() throws Exception {
        // Initialize the database
        insertedBatch = batchRepository.saveAndFlush(batch);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the batch using partial update
        Batch partialUpdatedBatch = new Batch();
        partialUpdatedBatch.setId(batch.getId());

        partialUpdatedBatch
            .batchCode(UPDATED_BATCH_CODE)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .currentPhase(UPDATED_CURRENT_PHASE)
            .numberOfBags(UPDATED_NUMBER_OF_BAGS)
            .substrateWeightKg(UPDATED_SUBSTRATE_WEIGHT_KG)
            .spawnWeightKg(UPDATED_SPAWN_WEIGHT_KG)
            .targetYieldKg(UPDATED_TARGET_YIELD_KG)
            .actualTotalYieldKg(UPDATED_ACTUAL_TOTAL_YIELD_KG)
            .biologicalEfficiencyPercent(UPDATED_BIOLOGICAL_EFFICIENCY_PERCENT)
            .isContaminated(UPDATED_IS_CONTAMINATED)
            .isActive(UPDATED_IS_ACTIVE)
            .completionNote(UPDATED_COMPLETION_NOTE)
            .note(UPDATED_NOTE);

        restBatchMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBatch.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBatch))
            )
            .andExpect(status().isOk());

        // Validate the Batch in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBatchUpdatableFieldsEquals(partialUpdatedBatch, getPersistedBatch(partialUpdatedBatch));
    }

    @Test
    @Transactional
    void patchNonExistingBatch() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        batch.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBatchMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, batch.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(batch))
            )
            .andExpect(status().isBadRequest());

        // Validate the Batch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBatch() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        batch.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBatchMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(batch))
            )
            .andExpect(status().isBadRequest());

        // Validate the Batch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBatch() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        batch.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBatchMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(batch)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Batch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBatch() throws Exception {
        // Initialize the database
        insertedBatch = batchRepository.saveAndFlush(batch);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the batch
        restBatchMockMvc
            .perform(delete(ENTITY_API_URL_ID, batch.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return batchRepository.count();
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

    protected Batch getPersistedBatch(Batch batch) {
        return batchRepository.findById(batch.getId()).orElseThrow();
    }

    protected void assertPersistedBatchToMatchAllProperties(Batch expectedBatch) {
        assertBatchAllPropertiesEquals(expectedBatch, getPersistedBatch(expectedBatch));
    }

    protected void assertPersistedBatchToMatchUpdatableProperties(Batch expectedBatch) {
        assertBatchAllUpdatablePropertiesEquals(expectedBatch, getPersistedBatch(expectedBatch));
    }
}
