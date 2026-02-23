package com.mcms.web.rest;

import static com.mcms.domain.ContaminationEventAsserts.*;
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
import com.mcms.domain.ContaminationEvent;
import com.mcms.domain.enumeration.ContaminationAction;
import com.mcms.domain.enumeration.ContaminationSeverity;
import com.mcms.domain.enumeration.ContaminationType;
import com.mcms.repository.ContaminationEventRepository;
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
 * Integration tests for the {@link ContaminationEventResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ContaminationEventResourceIT {

    private static final LocalDate DEFAULT_DETECTED_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DETECTED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final ContaminationType DEFAULT_TYPE = ContaminationType.TRICHODERMA;
    private static final ContaminationType UPDATED_TYPE = ContaminationType.COBWEB;

    private static final ContaminationSeverity DEFAULT_SEVERITY = ContaminationSeverity.LOW;
    private static final ContaminationSeverity UPDATED_SEVERITY = ContaminationSeverity.MEDIUM;

    private static final Integer DEFAULT_AFFECTED_BAGS = 1;
    private static final Integer UPDATED_AFFECTED_BAGS = 2;

    private static final BigDecimal DEFAULT_AFFECTED_PERCENTAGE = new BigDecimal(1);
    private static final BigDecimal UPDATED_AFFECTED_PERCENTAGE = new BigDecimal(2);

    private static final ContaminationAction DEFAULT_ACTION_TAKEN = ContaminationAction.ISOLATE;
    private static final ContaminationAction UPDATED_ACTION_TAKEN = ContaminationAction.REMOVE;

    private static final LocalDate DEFAULT_RESOLVED_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_RESOLVED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final BigDecimal DEFAULT_LOSS_KG = new BigDecimal(1);
    private static final BigDecimal UPDATED_LOSS_KG = new BigDecimal(2);

    private static final String DEFAULT_ROOT_CAUSE_ANALYSIS = "AAAAAAAAAA";
    private static final String UPDATED_ROOT_CAUSE_ANALYSIS = "BBBBBBBBBB";

    private static final String DEFAULT_PREVENTIVE_MEASURES = "AAAAAAAAAA";
    private static final String UPDATED_PREVENTIVE_MEASURES = "BBBBBBBBBB";

    private static final String DEFAULT_DETECTED_BY = "AAAAAAAAAA";
    private static final String UPDATED_DETECTED_BY = "BBBBBBBBBB";

    private static final String DEFAULT_PHOTOS_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_PHOTOS_REFERENCE = "BBBBBBBBBB";

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/contamination-events";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ContaminationEventRepository contaminationEventRepository;

    @Mock
    private ContaminationEventRepository contaminationEventRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restContaminationEventMockMvc;

    private ContaminationEvent contaminationEvent;

    private ContaminationEvent insertedContaminationEvent;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ContaminationEvent createEntity(EntityManager em) {
        ContaminationEvent contaminationEvent = new ContaminationEvent()
            .detectedDate(DEFAULT_DETECTED_DATE)
            .type(DEFAULT_TYPE)
            .severity(DEFAULT_SEVERITY)
            .affectedBags(DEFAULT_AFFECTED_BAGS)
            .affectedPercentage(DEFAULT_AFFECTED_PERCENTAGE)
            .actionTaken(DEFAULT_ACTION_TAKEN)
            .resolvedDate(DEFAULT_RESOLVED_DATE)
            .lossKg(DEFAULT_LOSS_KG)
            .rootCauseAnalysis(DEFAULT_ROOT_CAUSE_ANALYSIS)
            .preventiveMeasures(DEFAULT_PREVENTIVE_MEASURES)
            .detectedBy(DEFAULT_DETECTED_BY)
            .photosReference(DEFAULT_PHOTOS_REFERENCE)
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
        contaminationEvent.setBatch(batch);
        return contaminationEvent;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ContaminationEvent createUpdatedEntity(EntityManager em) {
        ContaminationEvent updatedContaminationEvent = new ContaminationEvent()
            .detectedDate(UPDATED_DETECTED_DATE)
            .type(UPDATED_TYPE)
            .severity(UPDATED_SEVERITY)
            .affectedBags(UPDATED_AFFECTED_BAGS)
            .affectedPercentage(UPDATED_AFFECTED_PERCENTAGE)
            .actionTaken(UPDATED_ACTION_TAKEN)
            .resolvedDate(UPDATED_RESOLVED_DATE)
            .lossKg(UPDATED_LOSS_KG)
            .rootCauseAnalysis(UPDATED_ROOT_CAUSE_ANALYSIS)
            .preventiveMeasures(UPDATED_PREVENTIVE_MEASURES)
            .detectedBy(UPDATED_DETECTED_BY)
            .photosReference(UPDATED_PHOTOS_REFERENCE)
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
        updatedContaminationEvent.setBatch(batch);
        return updatedContaminationEvent;
    }

    @BeforeEach
    void initTest() {
        contaminationEvent = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedContaminationEvent != null) {
            contaminationEventRepository.delete(insertedContaminationEvent);
            insertedContaminationEvent = null;
        }
    }

    @Test
    @Transactional
    void createContaminationEvent() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ContaminationEvent
        var returnedContaminationEvent = om.readValue(
            restContaminationEventMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contaminationEvent)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ContaminationEvent.class
        );

        // Validate the ContaminationEvent in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertContaminationEventUpdatableFieldsEquals(
            returnedContaminationEvent,
            getPersistedContaminationEvent(returnedContaminationEvent)
        );

        insertedContaminationEvent = returnedContaminationEvent;
    }

    @Test
    @Transactional
    void createContaminationEventWithExistingId() throws Exception {
        // Create the ContaminationEvent with an existing ID
        contaminationEvent.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restContaminationEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contaminationEvent)))
            .andExpect(status().isBadRequest());

        // Validate the ContaminationEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDetectedDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        contaminationEvent.setDetectedDate(null);

        // Create the ContaminationEvent, which fails.

        restContaminationEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contaminationEvent)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        contaminationEvent.setType(null);

        // Create the ContaminationEvent, which fails.

        restContaminationEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contaminationEvent)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSeverityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        contaminationEvent.setSeverity(null);

        // Create the ContaminationEvent, which fails.

        restContaminationEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contaminationEvent)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActionTakenIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        contaminationEvent.setActionTaken(null);

        // Create the ContaminationEvent, which fails.

        restContaminationEventMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contaminationEvent)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllContaminationEvents() throws Exception {
        // Initialize the database
        insertedContaminationEvent = contaminationEventRepository.saveAndFlush(contaminationEvent);

        // Get all the contaminationEventList
        restContaminationEventMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(contaminationEvent.getId().intValue())))
            .andExpect(jsonPath("$.[*].detectedDate").value(hasItem(DEFAULT_DETECTED_DATE.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].severity").value(hasItem(DEFAULT_SEVERITY.toString())))
            .andExpect(jsonPath("$.[*].affectedBags").value(hasItem(DEFAULT_AFFECTED_BAGS)))
            .andExpect(jsonPath("$.[*].affectedPercentage").value(hasItem(sameNumber(DEFAULT_AFFECTED_PERCENTAGE))))
            .andExpect(jsonPath("$.[*].actionTaken").value(hasItem(DEFAULT_ACTION_TAKEN.toString())))
            .andExpect(jsonPath("$.[*].resolvedDate").value(hasItem(DEFAULT_RESOLVED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lossKg").value(hasItem(sameNumber(DEFAULT_LOSS_KG))))
            .andExpect(jsonPath("$.[*].rootCauseAnalysis").value(hasItem(DEFAULT_ROOT_CAUSE_ANALYSIS)))
            .andExpect(jsonPath("$.[*].preventiveMeasures").value(hasItem(DEFAULT_PREVENTIVE_MEASURES)))
            .andExpect(jsonPath("$.[*].detectedBy").value(hasItem(DEFAULT_DETECTED_BY)))
            .andExpect(jsonPath("$.[*].photosReference").value(hasItem(DEFAULT_PHOTOS_REFERENCE)))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllContaminationEventsWithEagerRelationshipsIsEnabled() throws Exception {
        when(contaminationEventRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restContaminationEventMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(contaminationEventRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllContaminationEventsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(contaminationEventRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restContaminationEventMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(contaminationEventRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getContaminationEvent() throws Exception {
        // Initialize the database
        insertedContaminationEvent = contaminationEventRepository.saveAndFlush(contaminationEvent);

        // Get the contaminationEvent
        restContaminationEventMockMvc
            .perform(get(ENTITY_API_URL_ID, contaminationEvent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(contaminationEvent.getId().intValue()))
            .andExpect(jsonPath("$.detectedDate").value(DEFAULT_DETECTED_DATE.toString()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.severity").value(DEFAULT_SEVERITY.toString()))
            .andExpect(jsonPath("$.affectedBags").value(DEFAULT_AFFECTED_BAGS))
            .andExpect(jsonPath("$.affectedPercentage").value(sameNumber(DEFAULT_AFFECTED_PERCENTAGE)))
            .andExpect(jsonPath("$.actionTaken").value(DEFAULT_ACTION_TAKEN.toString()))
            .andExpect(jsonPath("$.resolvedDate").value(DEFAULT_RESOLVED_DATE.toString()))
            .andExpect(jsonPath("$.lossKg").value(sameNumber(DEFAULT_LOSS_KG)))
            .andExpect(jsonPath("$.rootCauseAnalysis").value(DEFAULT_ROOT_CAUSE_ANALYSIS))
            .andExpect(jsonPath("$.preventiveMeasures").value(DEFAULT_PREVENTIVE_MEASURES))
            .andExpect(jsonPath("$.detectedBy").value(DEFAULT_DETECTED_BY))
            .andExpect(jsonPath("$.photosReference").value(DEFAULT_PHOTOS_REFERENCE))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE));
    }

    @Test
    @Transactional
    void getNonExistingContaminationEvent() throws Exception {
        // Get the contaminationEvent
        restContaminationEventMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingContaminationEvent() throws Exception {
        // Initialize the database
        insertedContaminationEvent = contaminationEventRepository.saveAndFlush(contaminationEvent);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the contaminationEvent
        ContaminationEvent updatedContaminationEvent = contaminationEventRepository.findById(contaminationEvent.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedContaminationEvent are not directly saved in db
        em.detach(updatedContaminationEvent);
        updatedContaminationEvent
            .detectedDate(UPDATED_DETECTED_DATE)
            .type(UPDATED_TYPE)
            .severity(UPDATED_SEVERITY)
            .affectedBags(UPDATED_AFFECTED_BAGS)
            .affectedPercentage(UPDATED_AFFECTED_PERCENTAGE)
            .actionTaken(UPDATED_ACTION_TAKEN)
            .resolvedDate(UPDATED_RESOLVED_DATE)
            .lossKg(UPDATED_LOSS_KG)
            .rootCauseAnalysis(UPDATED_ROOT_CAUSE_ANALYSIS)
            .preventiveMeasures(UPDATED_PREVENTIVE_MEASURES)
            .detectedBy(UPDATED_DETECTED_BY)
            .photosReference(UPDATED_PHOTOS_REFERENCE)
            .note(UPDATED_NOTE);

        restContaminationEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedContaminationEvent.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedContaminationEvent))
            )
            .andExpect(status().isOk());

        // Validate the ContaminationEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedContaminationEventToMatchAllProperties(updatedContaminationEvent);
    }

    @Test
    @Transactional
    void putNonExistingContaminationEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        contaminationEvent.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restContaminationEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, contaminationEvent.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(contaminationEvent))
            )
            .andExpect(status().isBadRequest());

        // Validate the ContaminationEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchContaminationEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        contaminationEvent.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContaminationEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(contaminationEvent))
            )
            .andExpect(status().isBadRequest());

        // Validate the ContaminationEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamContaminationEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        contaminationEvent.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContaminationEventMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contaminationEvent)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ContaminationEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateContaminationEventWithPatch() throws Exception {
        // Initialize the database
        insertedContaminationEvent = contaminationEventRepository.saveAndFlush(contaminationEvent);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the contaminationEvent using partial update
        ContaminationEvent partialUpdatedContaminationEvent = new ContaminationEvent();
        partialUpdatedContaminationEvent.setId(contaminationEvent.getId());

        partialUpdatedContaminationEvent
            .affectedBags(UPDATED_AFFECTED_BAGS)
            .actionTaken(UPDATED_ACTION_TAKEN)
            .resolvedDate(UPDATED_RESOLVED_DATE)
            .lossKg(UPDATED_LOSS_KG)
            .rootCauseAnalysis(UPDATED_ROOT_CAUSE_ANALYSIS)
            .note(UPDATED_NOTE);

        restContaminationEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedContaminationEvent.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedContaminationEvent))
            )
            .andExpect(status().isOk());

        // Validate the ContaminationEvent in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertContaminationEventUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedContaminationEvent, contaminationEvent),
            getPersistedContaminationEvent(contaminationEvent)
        );
    }

    @Test
    @Transactional
    void fullUpdateContaminationEventWithPatch() throws Exception {
        // Initialize the database
        insertedContaminationEvent = contaminationEventRepository.saveAndFlush(contaminationEvent);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the contaminationEvent using partial update
        ContaminationEvent partialUpdatedContaminationEvent = new ContaminationEvent();
        partialUpdatedContaminationEvent.setId(contaminationEvent.getId());

        partialUpdatedContaminationEvent
            .detectedDate(UPDATED_DETECTED_DATE)
            .type(UPDATED_TYPE)
            .severity(UPDATED_SEVERITY)
            .affectedBags(UPDATED_AFFECTED_BAGS)
            .affectedPercentage(UPDATED_AFFECTED_PERCENTAGE)
            .actionTaken(UPDATED_ACTION_TAKEN)
            .resolvedDate(UPDATED_RESOLVED_DATE)
            .lossKg(UPDATED_LOSS_KG)
            .rootCauseAnalysis(UPDATED_ROOT_CAUSE_ANALYSIS)
            .preventiveMeasures(UPDATED_PREVENTIVE_MEASURES)
            .detectedBy(UPDATED_DETECTED_BY)
            .photosReference(UPDATED_PHOTOS_REFERENCE)
            .note(UPDATED_NOTE);

        restContaminationEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedContaminationEvent.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedContaminationEvent))
            )
            .andExpect(status().isOk());

        // Validate the ContaminationEvent in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertContaminationEventUpdatableFieldsEquals(
            partialUpdatedContaminationEvent,
            getPersistedContaminationEvent(partialUpdatedContaminationEvent)
        );
    }

    @Test
    @Transactional
    void patchNonExistingContaminationEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        contaminationEvent.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restContaminationEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, contaminationEvent.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(contaminationEvent))
            )
            .andExpect(status().isBadRequest());

        // Validate the ContaminationEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchContaminationEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        contaminationEvent.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContaminationEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(contaminationEvent))
            )
            .andExpect(status().isBadRequest());

        // Validate the ContaminationEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamContaminationEvent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        contaminationEvent.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContaminationEventMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(contaminationEvent)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ContaminationEvent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteContaminationEvent() throws Exception {
        // Initialize the database
        insertedContaminationEvent = contaminationEventRepository.saveAndFlush(contaminationEvent);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the contaminationEvent
        restContaminationEventMockMvc
            .perform(delete(ENTITY_API_URL_ID, contaminationEvent.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return contaminationEventRepository.count();
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

    protected ContaminationEvent getPersistedContaminationEvent(ContaminationEvent contaminationEvent) {
        return contaminationEventRepository.findById(contaminationEvent.getId()).orElseThrow();
    }

    protected void assertPersistedContaminationEventToMatchAllProperties(ContaminationEvent expectedContaminationEvent) {
        assertContaminationEventAllPropertiesEquals(expectedContaminationEvent, getPersistedContaminationEvent(expectedContaminationEvent));
    }

    protected void assertPersistedContaminationEventToMatchUpdatableProperties(ContaminationEvent expectedContaminationEvent) {
        assertContaminationEventAllUpdatablePropertiesEquals(
            expectedContaminationEvent,
            getPersistedContaminationEvent(expectedContaminationEvent)
        );
    }
}
