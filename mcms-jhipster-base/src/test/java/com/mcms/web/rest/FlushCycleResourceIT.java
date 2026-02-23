package com.mcms.web.rest;

import static com.mcms.domain.FlushCycleAsserts.*;
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
import com.mcms.domain.FlushCycle;
import com.mcms.repository.FlushCycleRepository;
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
 * Integration tests for the {@link FlushCycleResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class FlushCycleResourceIT {

    private static final Integer DEFAULT_FLUSH_NUMBER = 1;
    private static final Integer UPDATED_FLUSH_NUMBER = 2;

    private static final LocalDate DEFAULT_HARVEST_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_HARVEST_START_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_HARVEST_END_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_HARVEST_END_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final BigDecimal DEFAULT_YIELD_KG = new BigDecimal(1);
    private static final BigDecimal UPDATED_YIELD_KG = new BigDecimal(2);

    private static final Integer DEFAULT_YIELD_BAGS_HARVESTED = 1;
    private static final Integer UPDATED_YIELD_BAGS_HARVESTED = 2;

    private static final BigDecimal DEFAULT_AVG_FRUIT_BODY_WEIGHT_G = new BigDecimal(1);
    private static final BigDecimal UPDATED_AVG_FRUIT_BODY_WEIGHT_G = new BigDecimal(2);

    private static final Boolean DEFAULT_REHYDRATION_DONE = false;
    private static final Boolean UPDATED_REHYDRATION_DONE = true;

    private static final Integer DEFAULT_REHYDRATION_DURATION_HOURS = 1;
    private static final Integer UPDATED_REHYDRATION_DURATION_HOURS = 2;

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/flush-cycles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private FlushCycleRepository flushCycleRepository;

    @Mock
    private FlushCycleRepository flushCycleRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFlushCycleMockMvc;

    private FlushCycle flushCycle;

    private FlushCycle insertedFlushCycle;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FlushCycle createEntity(EntityManager em) {
        FlushCycle flushCycle = new FlushCycle()
            .flushNumber(DEFAULT_FLUSH_NUMBER)
            .harvestStartDate(DEFAULT_HARVEST_START_DATE)
            .harvestEndDate(DEFAULT_HARVEST_END_DATE)
            .yieldKg(DEFAULT_YIELD_KG)
            .yieldBagsHarvested(DEFAULT_YIELD_BAGS_HARVESTED)
            .avgFruitBodyWeightG(DEFAULT_AVG_FRUIT_BODY_WEIGHT_G)
            .rehydrationDone(DEFAULT_REHYDRATION_DONE)
            .rehydrationDurationHours(DEFAULT_REHYDRATION_DURATION_HOURS)
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
        flushCycle.setBatch(batch);
        return flushCycle;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FlushCycle createUpdatedEntity(EntityManager em) {
        FlushCycle updatedFlushCycle = new FlushCycle()
            .flushNumber(UPDATED_FLUSH_NUMBER)
            .harvestStartDate(UPDATED_HARVEST_START_DATE)
            .harvestEndDate(UPDATED_HARVEST_END_DATE)
            .yieldKg(UPDATED_YIELD_KG)
            .yieldBagsHarvested(UPDATED_YIELD_BAGS_HARVESTED)
            .avgFruitBodyWeightG(UPDATED_AVG_FRUIT_BODY_WEIGHT_G)
            .rehydrationDone(UPDATED_REHYDRATION_DONE)
            .rehydrationDurationHours(UPDATED_REHYDRATION_DURATION_HOURS)
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
        updatedFlushCycle.setBatch(batch);
        return updatedFlushCycle;
    }

    @BeforeEach
    void initTest() {
        flushCycle = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedFlushCycle != null) {
            flushCycleRepository.delete(insertedFlushCycle);
            insertedFlushCycle = null;
        }
    }

    @Test
    @Transactional
    void createFlushCycle() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the FlushCycle
        var returnedFlushCycle = om.readValue(
            restFlushCycleMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(flushCycle)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            FlushCycle.class
        );

        // Validate the FlushCycle in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertFlushCycleUpdatableFieldsEquals(returnedFlushCycle, getPersistedFlushCycle(returnedFlushCycle));

        insertedFlushCycle = returnedFlushCycle;
    }

    @Test
    @Transactional
    void createFlushCycleWithExistingId() throws Exception {
        // Create the FlushCycle with an existing ID
        flushCycle.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFlushCycleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(flushCycle)))
            .andExpect(status().isBadRequest());

        // Validate the FlushCycle in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkFlushNumberIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        flushCycle.setFlushNumber(null);

        // Create the FlushCycle, which fails.

        restFlushCycleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(flushCycle)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkHarvestStartDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        flushCycle.setHarvestStartDate(null);

        // Create the FlushCycle, which fails.

        restFlushCycleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(flushCycle)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkYieldKgIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        flushCycle.setYieldKg(null);

        // Create the FlushCycle, which fails.

        restFlushCycleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(flushCycle)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllFlushCycles() throws Exception {
        // Initialize the database
        insertedFlushCycle = flushCycleRepository.saveAndFlush(flushCycle);

        // Get all the flushCycleList
        restFlushCycleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(flushCycle.getId().intValue())))
            .andExpect(jsonPath("$.[*].flushNumber").value(hasItem(DEFAULT_FLUSH_NUMBER)))
            .andExpect(jsonPath("$.[*].harvestStartDate").value(hasItem(DEFAULT_HARVEST_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].harvestEndDate").value(hasItem(DEFAULT_HARVEST_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].yieldKg").value(hasItem(sameNumber(DEFAULT_YIELD_KG))))
            .andExpect(jsonPath("$.[*].yieldBagsHarvested").value(hasItem(DEFAULT_YIELD_BAGS_HARVESTED)))
            .andExpect(jsonPath("$.[*].avgFruitBodyWeightG").value(hasItem(sameNumber(DEFAULT_AVG_FRUIT_BODY_WEIGHT_G))))
            .andExpect(jsonPath("$.[*].rehydrationDone").value(hasItem(DEFAULT_REHYDRATION_DONE)))
            .andExpect(jsonPath("$.[*].rehydrationDurationHours").value(hasItem(DEFAULT_REHYDRATION_DURATION_HOURS)))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllFlushCyclesWithEagerRelationshipsIsEnabled() throws Exception {
        when(flushCycleRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restFlushCycleMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(flushCycleRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllFlushCyclesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(flushCycleRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restFlushCycleMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(flushCycleRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getFlushCycle() throws Exception {
        // Initialize the database
        insertedFlushCycle = flushCycleRepository.saveAndFlush(flushCycle);

        // Get the flushCycle
        restFlushCycleMockMvc
            .perform(get(ENTITY_API_URL_ID, flushCycle.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(flushCycle.getId().intValue()))
            .andExpect(jsonPath("$.flushNumber").value(DEFAULT_FLUSH_NUMBER))
            .andExpect(jsonPath("$.harvestStartDate").value(DEFAULT_HARVEST_START_DATE.toString()))
            .andExpect(jsonPath("$.harvestEndDate").value(DEFAULT_HARVEST_END_DATE.toString()))
            .andExpect(jsonPath("$.yieldKg").value(sameNumber(DEFAULT_YIELD_KG)))
            .andExpect(jsonPath("$.yieldBagsHarvested").value(DEFAULT_YIELD_BAGS_HARVESTED))
            .andExpect(jsonPath("$.avgFruitBodyWeightG").value(sameNumber(DEFAULT_AVG_FRUIT_BODY_WEIGHT_G)))
            .andExpect(jsonPath("$.rehydrationDone").value(DEFAULT_REHYDRATION_DONE))
            .andExpect(jsonPath("$.rehydrationDurationHours").value(DEFAULT_REHYDRATION_DURATION_HOURS))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE));
    }

    @Test
    @Transactional
    void getNonExistingFlushCycle() throws Exception {
        // Get the flushCycle
        restFlushCycleMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFlushCycle() throws Exception {
        // Initialize the database
        insertedFlushCycle = flushCycleRepository.saveAndFlush(flushCycle);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the flushCycle
        FlushCycle updatedFlushCycle = flushCycleRepository.findById(flushCycle.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedFlushCycle are not directly saved in db
        em.detach(updatedFlushCycle);
        updatedFlushCycle
            .flushNumber(UPDATED_FLUSH_NUMBER)
            .harvestStartDate(UPDATED_HARVEST_START_DATE)
            .harvestEndDate(UPDATED_HARVEST_END_DATE)
            .yieldKg(UPDATED_YIELD_KG)
            .yieldBagsHarvested(UPDATED_YIELD_BAGS_HARVESTED)
            .avgFruitBodyWeightG(UPDATED_AVG_FRUIT_BODY_WEIGHT_G)
            .rehydrationDone(UPDATED_REHYDRATION_DONE)
            .rehydrationDurationHours(UPDATED_REHYDRATION_DURATION_HOURS)
            .note(UPDATED_NOTE);

        restFlushCycleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedFlushCycle.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedFlushCycle))
            )
            .andExpect(status().isOk());

        // Validate the FlushCycle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedFlushCycleToMatchAllProperties(updatedFlushCycle);
    }

    @Test
    @Transactional
    void putNonExistingFlushCycle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        flushCycle.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFlushCycleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, flushCycle.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(flushCycle))
            )
            .andExpect(status().isBadRequest());

        // Validate the FlushCycle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFlushCycle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        flushCycle.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFlushCycleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(flushCycle))
            )
            .andExpect(status().isBadRequest());

        // Validate the FlushCycle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFlushCycle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        flushCycle.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFlushCycleMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(flushCycle)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the FlushCycle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFlushCycleWithPatch() throws Exception {
        // Initialize the database
        insertedFlushCycle = flushCycleRepository.saveAndFlush(flushCycle);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the flushCycle using partial update
        FlushCycle partialUpdatedFlushCycle = new FlushCycle();
        partialUpdatedFlushCycle.setId(flushCycle.getId());

        partialUpdatedFlushCycle
            .flushNumber(UPDATED_FLUSH_NUMBER)
            .harvestStartDate(UPDATED_HARVEST_START_DATE)
            .yieldKg(UPDATED_YIELD_KG)
            .avgFruitBodyWeightG(UPDATED_AVG_FRUIT_BODY_WEIGHT_G)
            .rehydrationDurationHours(UPDATED_REHYDRATION_DURATION_HOURS);

        restFlushCycleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFlushCycle.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFlushCycle))
            )
            .andExpect(status().isOk());

        // Validate the FlushCycle in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFlushCycleUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedFlushCycle, flushCycle),
            getPersistedFlushCycle(flushCycle)
        );
    }

    @Test
    @Transactional
    void fullUpdateFlushCycleWithPatch() throws Exception {
        // Initialize the database
        insertedFlushCycle = flushCycleRepository.saveAndFlush(flushCycle);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the flushCycle using partial update
        FlushCycle partialUpdatedFlushCycle = new FlushCycle();
        partialUpdatedFlushCycle.setId(flushCycle.getId());

        partialUpdatedFlushCycle
            .flushNumber(UPDATED_FLUSH_NUMBER)
            .harvestStartDate(UPDATED_HARVEST_START_DATE)
            .harvestEndDate(UPDATED_HARVEST_END_DATE)
            .yieldKg(UPDATED_YIELD_KG)
            .yieldBagsHarvested(UPDATED_YIELD_BAGS_HARVESTED)
            .avgFruitBodyWeightG(UPDATED_AVG_FRUIT_BODY_WEIGHT_G)
            .rehydrationDone(UPDATED_REHYDRATION_DONE)
            .rehydrationDurationHours(UPDATED_REHYDRATION_DURATION_HOURS)
            .note(UPDATED_NOTE);

        restFlushCycleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFlushCycle.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFlushCycle))
            )
            .andExpect(status().isOk());

        // Validate the FlushCycle in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFlushCycleUpdatableFieldsEquals(partialUpdatedFlushCycle, getPersistedFlushCycle(partialUpdatedFlushCycle));
    }

    @Test
    @Transactional
    void patchNonExistingFlushCycle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        flushCycle.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFlushCycleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, flushCycle.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(flushCycle))
            )
            .andExpect(status().isBadRequest());

        // Validate the FlushCycle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFlushCycle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        flushCycle.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFlushCycleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(flushCycle))
            )
            .andExpect(status().isBadRequest());

        // Validate the FlushCycle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFlushCycle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        flushCycle.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFlushCycleMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(flushCycle)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the FlushCycle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFlushCycle() throws Exception {
        // Initialize the database
        insertedFlushCycle = flushCycleRepository.saveAndFlush(flushCycle);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the flushCycle
        restFlushCycleMockMvc
            .perform(delete(ENTITY_API_URL_ID, flushCycle.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return flushCycleRepository.count();
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

    protected FlushCycle getPersistedFlushCycle(FlushCycle flushCycle) {
        return flushCycleRepository.findById(flushCycle.getId()).orElseThrow();
    }

    protected void assertPersistedFlushCycleToMatchAllProperties(FlushCycle expectedFlushCycle) {
        assertFlushCycleAllPropertiesEquals(expectedFlushCycle, getPersistedFlushCycle(expectedFlushCycle));
    }

    protected void assertPersistedFlushCycleToMatchUpdatableProperties(FlushCycle expectedFlushCycle) {
        assertFlushCycleAllUpdatablePropertiesEquals(expectedFlushCycle, getPersistedFlushCycle(expectedFlushCycle));
    }
}
