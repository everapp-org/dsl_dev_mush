package com.mcms.web.rest;

import static com.mcms.domain.PhaseExecutionAsserts.*;
import static com.mcms.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcms.IntegrationTest;
import com.mcms.domain.Batch;
import com.mcms.domain.PhaseExecution;
import com.mcms.domain.enumeration.PhaseName;
import com.mcms.repository.PhaseExecutionRepository;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link PhaseExecutionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PhaseExecutionResourceIT {

    private static final PhaseName DEFAULT_PHASE = PhaseName.INOCULATION;
    private static final PhaseName UPDATED_PHASE = PhaseName.EARLY_COLONIZATION;

    private static final Integer DEFAULT_SEQUENCE_ORDER = 1;
    private static final Integer UPDATED_SEQUENCE_ORDER = 2;

    private static final LocalDate DEFAULT_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_END_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Integer DEFAULT_PLANNED_DURATION_DAYS = 1;
    private static final Integer UPDATED_PLANNED_DURATION_DAYS = 2;

    private static final Integer DEFAULT_ACTUAL_DURATION_DAYS = 1;
    private static final Integer UPDATED_ACTUAL_DURATION_DAYS = 2;

    private static final String DEFAULT_RESPONSIBLE_PERSON = "AAAAAAAAAA";
    private static final String UPDATED_RESPONSIBLE_PERSON = "BBBBBBBBBB";

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/phase-executions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PhaseExecutionRepository phaseExecutionRepository;

    @Mock
    private PhaseExecutionRepository phaseExecutionRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPhaseExecutionMockMvc;

    private PhaseExecution phaseExecution;

    private PhaseExecution insertedPhaseExecution;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PhaseExecution createEntity(EntityManager em) {
        PhaseExecution phaseExecution = new PhaseExecution()
            .phase(DEFAULT_PHASE)
            .sequenceOrder(DEFAULT_SEQUENCE_ORDER)
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
            .plannedDurationDays(DEFAULT_PLANNED_DURATION_DAYS)
            .actualDurationDays(DEFAULT_ACTUAL_DURATION_DAYS)
            .responsiblePerson(DEFAULT_RESPONSIBLE_PERSON)
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
        phaseExecution.setBatch(batch);
        return phaseExecution;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PhaseExecution createUpdatedEntity(EntityManager em) {
        PhaseExecution updatedPhaseExecution = new PhaseExecution()
            .phase(UPDATED_PHASE)
            .sequenceOrder(UPDATED_SEQUENCE_ORDER)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .plannedDurationDays(UPDATED_PLANNED_DURATION_DAYS)
            .actualDurationDays(UPDATED_ACTUAL_DURATION_DAYS)
            .responsiblePerson(UPDATED_RESPONSIBLE_PERSON)
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
        updatedPhaseExecution.setBatch(batch);
        return updatedPhaseExecution;
    }

    @BeforeEach
    void initTest() {
        phaseExecution = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedPhaseExecution != null) {
            phaseExecutionRepository.delete(insertedPhaseExecution);
            insertedPhaseExecution = null;
        }
    }

    @Test
    @Transactional
    void createPhaseExecution() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PhaseExecution
        var returnedPhaseExecution = om.readValue(
            restPhaseExecutionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(phaseExecution)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PhaseExecution.class
        );

        // Validate the PhaseExecution in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertPhaseExecutionUpdatableFieldsEquals(returnedPhaseExecution, getPersistedPhaseExecution(returnedPhaseExecution));

        insertedPhaseExecution = returnedPhaseExecution;
    }

    @Test
    @Transactional
    void createPhaseExecutionWithExistingId() throws Exception {
        // Create the PhaseExecution with an existing ID
        phaseExecution.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPhaseExecutionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(phaseExecution)))
            .andExpect(status().isBadRequest());

        // Validate the PhaseExecution in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkPhaseIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        phaseExecution.setPhase(null);

        // Create the PhaseExecution, which fails.

        restPhaseExecutionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(phaseExecution)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSequenceOrderIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        phaseExecution.setSequenceOrder(null);

        // Create the PhaseExecution, which fails.

        restPhaseExecutionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(phaseExecution)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStartDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        phaseExecution.setStartDate(null);

        // Create the PhaseExecution, which fails.

        restPhaseExecutionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(phaseExecution)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPhaseExecutions() throws Exception {
        // Initialize the database
        insertedPhaseExecution = phaseExecutionRepository.saveAndFlush(phaseExecution);

        // Get all the phaseExecutionList
        restPhaseExecutionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(phaseExecution.getId().intValue())))
            .andExpect(jsonPath("$.[*].phase").value(hasItem(DEFAULT_PHASE.toString())))
            .andExpect(jsonPath("$.[*].sequenceOrder").value(hasItem(DEFAULT_SEQUENCE_ORDER)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].plannedDurationDays").value(hasItem(DEFAULT_PLANNED_DURATION_DAYS)))
            .andExpect(jsonPath("$.[*].actualDurationDays").value(hasItem(DEFAULT_ACTUAL_DURATION_DAYS)))
            .andExpect(jsonPath("$.[*].responsiblePerson").value(hasItem(DEFAULT_RESPONSIBLE_PERSON)))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPhaseExecutionsWithEagerRelationshipsIsEnabled() throws Exception {
        when(phaseExecutionRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPhaseExecutionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(phaseExecutionRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPhaseExecutionsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(phaseExecutionRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPhaseExecutionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(phaseExecutionRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getPhaseExecution() throws Exception {
        // Initialize the database
        insertedPhaseExecution = phaseExecutionRepository.saveAndFlush(phaseExecution);

        // Get the phaseExecution
        restPhaseExecutionMockMvc
            .perform(get(ENTITY_API_URL_ID, phaseExecution.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(phaseExecution.getId().intValue()))
            .andExpect(jsonPath("$.phase").value(DEFAULT_PHASE.toString()))
            .andExpect(jsonPath("$.sequenceOrder").value(DEFAULT_SEQUENCE_ORDER))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.plannedDurationDays").value(DEFAULT_PLANNED_DURATION_DAYS))
            .andExpect(jsonPath("$.actualDurationDays").value(DEFAULT_ACTUAL_DURATION_DAYS))
            .andExpect(jsonPath("$.responsiblePerson").value(DEFAULT_RESPONSIBLE_PERSON))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE));
    }

    @Test
    @Transactional
    void getNonExistingPhaseExecution() throws Exception {
        // Get the phaseExecution
        restPhaseExecutionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPhaseExecution() throws Exception {
        // Initialize the database
        insertedPhaseExecution = phaseExecutionRepository.saveAndFlush(phaseExecution);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the phaseExecution
        PhaseExecution updatedPhaseExecution = phaseExecutionRepository.findById(phaseExecution.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPhaseExecution are not directly saved in db
        em.detach(updatedPhaseExecution);
        updatedPhaseExecution
            .phase(UPDATED_PHASE)
            .sequenceOrder(UPDATED_SEQUENCE_ORDER)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .plannedDurationDays(UPDATED_PLANNED_DURATION_DAYS)
            .actualDurationDays(UPDATED_ACTUAL_DURATION_DAYS)
            .responsiblePerson(UPDATED_RESPONSIBLE_PERSON)
            .note(UPDATED_NOTE);

        restPhaseExecutionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPhaseExecution.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedPhaseExecution))
            )
            .andExpect(status().isOk());

        // Validate the PhaseExecution in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPhaseExecutionToMatchAllProperties(updatedPhaseExecution);
    }

    @Test
    @Transactional
    void putNonExistingPhaseExecution() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        phaseExecution.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPhaseExecutionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, phaseExecution.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(phaseExecution))
            )
            .andExpect(status().isBadRequest());

        // Validate the PhaseExecution in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPhaseExecution() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        phaseExecution.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPhaseExecutionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(phaseExecution))
            )
            .andExpect(status().isBadRequest());

        // Validate the PhaseExecution in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPhaseExecution() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        phaseExecution.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPhaseExecutionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(phaseExecution)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PhaseExecution in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePhaseExecutionWithPatch() throws Exception {
        // Initialize the database
        insertedPhaseExecution = phaseExecutionRepository.saveAndFlush(phaseExecution);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the phaseExecution using partial update
        PhaseExecution partialUpdatedPhaseExecution = new PhaseExecution();
        partialUpdatedPhaseExecution.setId(phaseExecution.getId());

        partialUpdatedPhaseExecution
            .plannedDurationDays(UPDATED_PLANNED_DURATION_DAYS)
            .actualDurationDays(UPDATED_ACTUAL_DURATION_DAYS)
            .responsiblePerson(UPDATED_RESPONSIBLE_PERSON)
            .note(UPDATED_NOTE);

        restPhaseExecutionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPhaseExecution.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPhaseExecution))
            )
            .andExpect(status().isOk());

        // Validate the PhaseExecution in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPhaseExecutionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPhaseExecution, phaseExecution),
            getPersistedPhaseExecution(phaseExecution)
        );
    }

    @Test
    @Transactional
    void fullUpdatePhaseExecutionWithPatch() throws Exception {
        // Initialize the database
        insertedPhaseExecution = phaseExecutionRepository.saveAndFlush(phaseExecution);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the phaseExecution using partial update
        PhaseExecution partialUpdatedPhaseExecution = new PhaseExecution();
        partialUpdatedPhaseExecution.setId(phaseExecution.getId());

        partialUpdatedPhaseExecution
            .phase(UPDATED_PHASE)
            .sequenceOrder(UPDATED_SEQUENCE_ORDER)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .plannedDurationDays(UPDATED_PLANNED_DURATION_DAYS)
            .actualDurationDays(UPDATED_ACTUAL_DURATION_DAYS)
            .responsiblePerson(UPDATED_RESPONSIBLE_PERSON)
            .note(UPDATED_NOTE);

        restPhaseExecutionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPhaseExecution.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPhaseExecution))
            )
            .andExpect(status().isOk());

        // Validate the PhaseExecution in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPhaseExecutionUpdatableFieldsEquals(partialUpdatedPhaseExecution, getPersistedPhaseExecution(partialUpdatedPhaseExecution));
    }

    @Test
    @Transactional
    void patchNonExistingPhaseExecution() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        phaseExecution.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPhaseExecutionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, phaseExecution.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(phaseExecution))
            )
            .andExpect(status().isBadRequest());

        // Validate the PhaseExecution in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPhaseExecution() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        phaseExecution.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPhaseExecutionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(phaseExecution))
            )
            .andExpect(status().isBadRequest());

        // Validate the PhaseExecution in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPhaseExecution() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        phaseExecution.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPhaseExecutionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(phaseExecution)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PhaseExecution in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePhaseExecution() throws Exception {
        // Initialize the database
        insertedPhaseExecution = phaseExecutionRepository.saveAndFlush(phaseExecution);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the phaseExecution
        restPhaseExecutionMockMvc
            .perform(delete(ENTITY_API_URL_ID, phaseExecution.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return phaseExecutionRepository.count();
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

    protected PhaseExecution getPersistedPhaseExecution(PhaseExecution phaseExecution) {
        return phaseExecutionRepository.findById(phaseExecution.getId()).orElseThrow();
    }

    protected void assertPersistedPhaseExecutionToMatchAllProperties(PhaseExecution expectedPhaseExecution) {
        assertPhaseExecutionAllPropertiesEquals(expectedPhaseExecution, getPersistedPhaseExecution(expectedPhaseExecution));
    }

    protected void assertPersistedPhaseExecutionToMatchUpdatableProperties(PhaseExecution expectedPhaseExecution) {
        assertPhaseExecutionAllUpdatablePropertiesEquals(expectedPhaseExecution, getPersistedPhaseExecution(expectedPhaseExecution));
    }
}
