package com.mcms.web.rest;

import static com.mcms.domain.EnvironmentalAlertAsserts.*;
import static com.mcms.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mcms.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcms.IntegrationTest;
import com.mcms.domain.EnvironmentalAlert;
import com.mcms.domain.Room;
import com.mcms.domain.enumeration.AlertSeverity;
import com.mcms.domain.enumeration.SensorUnit;
import com.mcms.repository.EnvironmentalAlertRepository;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link EnvironmentalAlertResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class EnvironmentalAlertResourceIT {

    private static final Instant DEFAULT_ALERT_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ALERT_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final AlertSeverity DEFAULT_SEVERITY = AlertSeverity.INFO;
    private static final AlertSeverity UPDATED_SEVERITY = AlertSeverity.WARNING;

    private static final SensorUnit DEFAULT_PARAMETER = SensorUnit.CELSIUS;
    private static final SensorUnit UPDATED_PARAMETER = SensorUnit.HUMIDITY_PERCENT;

    private static final BigDecimal DEFAULT_ACTUAL_VALUE = new BigDecimal(1);
    private static final BigDecimal UPDATED_ACTUAL_VALUE = new BigDecimal(2);

    private static final BigDecimal DEFAULT_THRESHOLD_VALUE = new BigDecimal(1);
    private static final BigDecimal UPDATED_THRESHOLD_VALUE = new BigDecimal(2);

    private static final String DEFAULT_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_MESSAGE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACKNOWLEDGED = false;
    private static final Boolean UPDATED_ACKNOWLEDGED = true;

    private static final String DEFAULT_ACKNOWLEDGED_BY = "AAAAAAAAAA";
    private static final String UPDATED_ACKNOWLEDGED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_ACKNOWLEDGED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ACKNOWLEDGED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_RESOLUTION_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_RESOLUTION_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/environmental-alerts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EnvironmentalAlertRepository environmentalAlertRepository;

    @Mock
    private EnvironmentalAlertRepository environmentalAlertRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEnvironmentalAlertMockMvc;

    private EnvironmentalAlert environmentalAlert;

    private EnvironmentalAlert insertedEnvironmentalAlert;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EnvironmentalAlert createEntity(EntityManager em) {
        EnvironmentalAlert environmentalAlert = new EnvironmentalAlert()
            .alertTime(DEFAULT_ALERT_TIME)
            .severity(DEFAULT_SEVERITY)
            .parameter(DEFAULT_PARAMETER)
            .actualValue(DEFAULT_ACTUAL_VALUE)
            .thresholdValue(DEFAULT_THRESHOLD_VALUE)
            .message(DEFAULT_MESSAGE)
            .acknowledged(DEFAULT_ACKNOWLEDGED)
            .acknowledgedBy(DEFAULT_ACKNOWLEDGED_BY)
            .acknowledgedAt(DEFAULT_ACKNOWLEDGED_AT)
            .resolutionNote(DEFAULT_RESOLUTION_NOTE);
        // Add required entity
        Room room;
        if (TestUtil.findAll(em, Room.class).isEmpty()) {
            room = RoomResourceIT.createEntity();
            em.persist(room);
            em.flush();
        } else {
            room = TestUtil.findAll(em, Room.class).get(0);
        }
        environmentalAlert.setRoom(room);
        return environmentalAlert;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EnvironmentalAlert createUpdatedEntity(EntityManager em) {
        EnvironmentalAlert updatedEnvironmentalAlert = new EnvironmentalAlert()
            .alertTime(UPDATED_ALERT_TIME)
            .severity(UPDATED_SEVERITY)
            .parameter(UPDATED_PARAMETER)
            .actualValue(UPDATED_ACTUAL_VALUE)
            .thresholdValue(UPDATED_THRESHOLD_VALUE)
            .message(UPDATED_MESSAGE)
            .acknowledged(UPDATED_ACKNOWLEDGED)
            .acknowledgedBy(UPDATED_ACKNOWLEDGED_BY)
            .acknowledgedAt(UPDATED_ACKNOWLEDGED_AT)
            .resolutionNote(UPDATED_RESOLUTION_NOTE);
        // Add required entity
        Room room;
        if (TestUtil.findAll(em, Room.class).isEmpty()) {
            room = RoomResourceIT.createUpdatedEntity();
            em.persist(room);
            em.flush();
        } else {
            room = TestUtil.findAll(em, Room.class).get(0);
        }
        updatedEnvironmentalAlert.setRoom(room);
        return updatedEnvironmentalAlert;
    }

    @BeforeEach
    void initTest() {
        environmentalAlert = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedEnvironmentalAlert != null) {
            environmentalAlertRepository.delete(insertedEnvironmentalAlert);
            insertedEnvironmentalAlert = null;
        }
    }

    @Test
    @Transactional
    void createEnvironmentalAlert() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the EnvironmentalAlert
        var returnedEnvironmentalAlert = om.readValue(
            restEnvironmentalAlertMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(environmentalAlert)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            EnvironmentalAlert.class
        );

        // Validate the EnvironmentalAlert in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertEnvironmentalAlertUpdatableFieldsEquals(
            returnedEnvironmentalAlert,
            getPersistedEnvironmentalAlert(returnedEnvironmentalAlert)
        );

        insertedEnvironmentalAlert = returnedEnvironmentalAlert;
    }

    @Test
    @Transactional
    void createEnvironmentalAlertWithExistingId() throws Exception {
        // Create the EnvironmentalAlert with an existing ID
        environmentalAlert.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEnvironmentalAlertMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(environmentalAlert)))
            .andExpect(status().isBadRequest());

        // Validate the EnvironmentalAlert in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkAlertTimeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        environmentalAlert.setAlertTime(null);

        // Create the EnvironmentalAlert, which fails.

        restEnvironmentalAlertMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(environmentalAlert)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSeverityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        environmentalAlert.setSeverity(null);

        // Create the EnvironmentalAlert, which fails.

        restEnvironmentalAlertMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(environmentalAlert)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkParameterIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        environmentalAlert.setParameter(null);

        // Create the EnvironmentalAlert, which fails.

        restEnvironmentalAlertMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(environmentalAlert)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActualValueIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        environmentalAlert.setActualValue(null);

        // Create the EnvironmentalAlert, which fails.

        restEnvironmentalAlertMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(environmentalAlert)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkThresholdValueIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        environmentalAlert.setThresholdValue(null);

        // Create the EnvironmentalAlert, which fails.

        restEnvironmentalAlertMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(environmentalAlert)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMessageIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        environmentalAlert.setMessage(null);

        // Create the EnvironmentalAlert, which fails.

        restEnvironmentalAlertMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(environmentalAlert)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAcknowledgedIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        environmentalAlert.setAcknowledged(null);

        // Create the EnvironmentalAlert, which fails.

        restEnvironmentalAlertMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(environmentalAlert)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllEnvironmentalAlerts() throws Exception {
        // Initialize the database
        insertedEnvironmentalAlert = environmentalAlertRepository.saveAndFlush(environmentalAlert);

        // Get all the environmentalAlertList
        restEnvironmentalAlertMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(environmentalAlert.getId().intValue())))
            .andExpect(jsonPath("$.[*].alertTime").value(hasItem(DEFAULT_ALERT_TIME.toString())))
            .andExpect(jsonPath("$.[*].severity").value(hasItem(DEFAULT_SEVERITY.toString())))
            .andExpect(jsonPath("$.[*].parameter").value(hasItem(DEFAULT_PARAMETER.toString())))
            .andExpect(jsonPath("$.[*].actualValue").value(hasItem(sameNumber(DEFAULT_ACTUAL_VALUE))))
            .andExpect(jsonPath("$.[*].thresholdValue").value(hasItem(sameNumber(DEFAULT_THRESHOLD_VALUE))))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE)))
            .andExpect(jsonPath("$.[*].acknowledged").value(hasItem(DEFAULT_ACKNOWLEDGED)))
            .andExpect(jsonPath("$.[*].acknowledgedBy").value(hasItem(DEFAULT_ACKNOWLEDGED_BY)))
            .andExpect(jsonPath("$.[*].acknowledgedAt").value(hasItem(DEFAULT_ACKNOWLEDGED_AT.toString())))
            .andExpect(jsonPath("$.[*].resolutionNote").value(hasItem(DEFAULT_RESOLUTION_NOTE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEnvironmentalAlertsWithEagerRelationshipsIsEnabled() throws Exception {
        when(environmentalAlertRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEnvironmentalAlertMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(environmentalAlertRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEnvironmentalAlertsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(environmentalAlertRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEnvironmentalAlertMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(environmentalAlertRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getEnvironmentalAlert() throws Exception {
        // Initialize the database
        insertedEnvironmentalAlert = environmentalAlertRepository.saveAndFlush(environmentalAlert);

        // Get the environmentalAlert
        restEnvironmentalAlertMockMvc
            .perform(get(ENTITY_API_URL_ID, environmentalAlert.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(environmentalAlert.getId().intValue()))
            .andExpect(jsonPath("$.alertTime").value(DEFAULT_ALERT_TIME.toString()))
            .andExpect(jsonPath("$.severity").value(DEFAULT_SEVERITY.toString()))
            .andExpect(jsonPath("$.parameter").value(DEFAULT_PARAMETER.toString()))
            .andExpect(jsonPath("$.actualValue").value(sameNumber(DEFAULT_ACTUAL_VALUE)))
            .andExpect(jsonPath("$.thresholdValue").value(sameNumber(DEFAULT_THRESHOLD_VALUE)))
            .andExpect(jsonPath("$.message").value(DEFAULT_MESSAGE))
            .andExpect(jsonPath("$.acknowledged").value(DEFAULT_ACKNOWLEDGED))
            .andExpect(jsonPath("$.acknowledgedBy").value(DEFAULT_ACKNOWLEDGED_BY))
            .andExpect(jsonPath("$.acknowledgedAt").value(DEFAULT_ACKNOWLEDGED_AT.toString()))
            .andExpect(jsonPath("$.resolutionNote").value(DEFAULT_RESOLUTION_NOTE));
    }

    @Test
    @Transactional
    void getNonExistingEnvironmentalAlert() throws Exception {
        // Get the environmentalAlert
        restEnvironmentalAlertMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingEnvironmentalAlert() throws Exception {
        // Initialize the database
        insertedEnvironmentalAlert = environmentalAlertRepository.saveAndFlush(environmentalAlert);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the environmentalAlert
        EnvironmentalAlert updatedEnvironmentalAlert = environmentalAlertRepository.findById(environmentalAlert.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedEnvironmentalAlert are not directly saved in db
        em.detach(updatedEnvironmentalAlert);
        updatedEnvironmentalAlert
            .alertTime(UPDATED_ALERT_TIME)
            .severity(UPDATED_SEVERITY)
            .parameter(UPDATED_PARAMETER)
            .actualValue(UPDATED_ACTUAL_VALUE)
            .thresholdValue(UPDATED_THRESHOLD_VALUE)
            .message(UPDATED_MESSAGE)
            .acknowledged(UPDATED_ACKNOWLEDGED)
            .acknowledgedBy(UPDATED_ACKNOWLEDGED_BY)
            .acknowledgedAt(UPDATED_ACKNOWLEDGED_AT)
            .resolutionNote(UPDATED_RESOLUTION_NOTE);

        restEnvironmentalAlertMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedEnvironmentalAlert.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedEnvironmentalAlert))
            )
            .andExpect(status().isOk());

        // Validate the EnvironmentalAlert in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEnvironmentalAlertToMatchAllProperties(updatedEnvironmentalAlert);
    }

    @Test
    @Transactional
    void putNonExistingEnvironmentalAlert() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        environmentalAlert.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEnvironmentalAlertMockMvc
            .perform(
                put(ENTITY_API_URL_ID, environmentalAlert.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(environmentalAlert))
            )
            .andExpect(status().isBadRequest());

        // Validate the EnvironmentalAlert in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEnvironmentalAlert() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        environmentalAlert.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnvironmentalAlertMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(environmentalAlert))
            )
            .andExpect(status().isBadRequest());

        // Validate the EnvironmentalAlert in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEnvironmentalAlert() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        environmentalAlert.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnvironmentalAlertMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(environmentalAlert)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the EnvironmentalAlert in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEnvironmentalAlertWithPatch() throws Exception {
        // Initialize the database
        insertedEnvironmentalAlert = environmentalAlertRepository.saveAndFlush(environmentalAlert);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the environmentalAlert using partial update
        EnvironmentalAlert partialUpdatedEnvironmentalAlert = new EnvironmentalAlert();
        partialUpdatedEnvironmentalAlert.setId(environmentalAlert.getId());

        partialUpdatedEnvironmentalAlert
            .alertTime(UPDATED_ALERT_TIME)
            .severity(UPDATED_SEVERITY)
            .acknowledged(UPDATED_ACKNOWLEDGED)
            .acknowledgedBy(UPDATED_ACKNOWLEDGED_BY);

        restEnvironmentalAlertMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEnvironmentalAlert.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEnvironmentalAlert))
            )
            .andExpect(status().isOk());

        // Validate the EnvironmentalAlert in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEnvironmentalAlertUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedEnvironmentalAlert, environmentalAlert),
            getPersistedEnvironmentalAlert(environmentalAlert)
        );
    }

    @Test
    @Transactional
    void fullUpdateEnvironmentalAlertWithPatch() throws Exception {
        // Initialize the database
        insertedEnvironmentalAlert = environmentalAlertRepository.saveAndFlush(environmentalAlert);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the environmentalAlert using partial update
        EnvironmentalAlert partialUpdatedEnvironmentalAlert = new EnvironmentalAlert();
        partialUpdatedEnvironmentalAlert.setId(environmentalAlert.getId());

        partialUpdatedEnvironmentalAlert
            .alertTime(UPDATED_ALERT_TIME)
            .severity(UPDATED_SEVERITY)
            .parameter(UPDATED_PARAMETER)
            .actualValue(UPDATED_ACTUAL_VALUE)
            .thresholdValue(UPDATED_THRESHOLD_VALUE)
            .message(UPDATED_MESSAGE)
            .acknowledged(UPDATED_ACKNOWLEDGED)
            .acknowledgedBy(UPDATED_ACKNOWLEDGED_BY)
            .acknowledgedAt(UPDATED_ACKNOWLEDGED_AT)
            .resolutionNote(UPDATED_RESOLUTION_NOTE);

        restEnvironmentalAlertMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEnvironmentalAlert.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEnvironmentalAlert))
            )
            .andExpect(status().isOk());

        // Validate the EnvironmentalAlert in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEnvironmentalAlertUpdatableFieldsEquals(
            partialUpdatedEnvironmentalAlert,
            getPersistedEnvironmentalAlert(partialUpdatedEnvironmentalAlert)
        );
    }

    @Test
    @Transactional
    void patchNonExistingEnvironmentalAlert() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        environmentalAlert.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEnvironmentalAlertMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, environmentalAlert.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(environmentalAlert))
            )
            .andExpect(status().isBadRequest());

        // Validate the EnvironmentalAlert in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEnvironmentalAlert() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        environmentalAlert.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnvironmentalAlertMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(environmentalAlert))
            )
            .andExpect(status().isBadRequest());

        // Validate the EnvironmentalAlert in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEnvironmentalAlert() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        environmentalAlert.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnvironmentalAlertMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(environmentalAlert)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the EnvironmentalAlert in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEnvironmentalAlert() throws Exception {
        // Initialize the database
        insertedEnvironmentalAlert = environmentalAlertRepository.saveAndFlush(environmentalAlert);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the environmentalAlert
        restEnvironmentalAlertMockMvc
            .perform(delete(ENTITY_API_URL_ID, environmentalAlert.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return environmentalAlertRepository.count();
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

    protected EnvironmentalAlert getPersistedEnvironmentalAlert(EnvironmentalAlert environmentalAlert) {
        return environmentalAlertRepository.findById(environmentalAlert.getId()).orElseThrow();
    }

    protected void assertPersistedEnvironmentalAlertToMatchAllProperties(EnvironmentalAlert expectedEnvironmentalAlert) {
        assertEnvironmentalAlertAllPropertiesEquals(expectedEnvironmentalAlert, getPersistedEnvironmentalAlert(expectedEnvironmentalAlert));
    }

    protected void assertPersistedEnvironmentalAlertToMatchUpdatableProperties(EnvironmentalAlert expectedEnvironmentalAlert) {
        assertEnvironmentalAlertAllUpdatablePropertiesEquals(
            expectedEnvironmentalAlert,
            getPersistedEnvironmentalAlert(expectedEnvironmentalAlert)
        );
    }
}
