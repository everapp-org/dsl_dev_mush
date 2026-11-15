package com.mcms.web.rest;

import static com.mcms.domain.SensorReadingAsserts.*;
import static com.mcms.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mcms.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcms.IntegrationTest;
import com.mcms.domain.Sensor;
import com.mcms.domain.SensorReading;
import com.mcms.domain.enumeration.SensorUnit;
import com.mcms.repository.SensorReadingRepository;
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
 * Integration tests for the {@link SensorReadingResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SensorReadingResourceIT {

    private static final Instant DEFAULT_TIMESTAMP = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_TIMESTAMP = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final BigDecimal DEFAULT_VALUE = new BigDecimal(1);
    private static final BigDecimal UPDATED_VALUE = new BigDecimal(2);

    private static final SensorUnit DEFAULT_UNIT = SensorUnit.CELSIUS;
    private static final SensorUnit UPDATED_UNIT = SensorUnit.HUMIDITY_PERCENT;

    private static final String ENTITY_API_URL = "/api/sensor-readings";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SensorReadingRepository sensorReadingRepository;

    @Mock
    private SensorReadingRepository sensorReadingRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSensorReadingMockMvc;

    private SensorReading sensorReading;

    private SensorReading insertedSensorReading;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SensorReading createEntity(EntityManager em) {
        SensorReading sensorReading = new SensorReading().timestamp(DEFAULT_TIMESTAMP).value(DEFAULT_VALUE).unit(DEFAULT_UNIT);
        // Add required entity
        Sensor sensor;
        if (TestUtil.findAll(em, Sensor.class).isEmpty()) {
            sensor = SensorResourceIT.createEntity(em);
            em.persist(sensor);
            em.flush();
        } else {
            sensor = TestUtil.findAll(em, Sensor.class).get(0);
        }
        sensorReading.setSensor(sensor);
        return sensorReading;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SensorReading createUpdatedEntity(EntityManager em) {
        SensorReading updatedSensorReading = new SensorReading().timestamp(UPDATED_TIMESTAMP).value(UPDATED_VALUE).unit(UPDATED_UNIT);
        // Add required entity
        Sensor sensor;
        if (TestUtil.findAll(em, Sensor.class).isEmpty()) {
            sensor = SensorResourceIT.createUpdatedEntity(em);
            em.persist(sensor);
            em.flush();
        } else {
            sensor = TestUtil.findAll(em, Sensor.class).get(0);
        }
        updatedSensorReading.setSensor(sensor);
        return updatedSensorReading;
    }

    @BeforeEach
    void initTest() {
        sensorReading = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedSensorReading != null) {
            sensorReadingRepository.delete(insertedSensorReading);
            insertedSensorReading = null;
        }
    }

    @Test
    @Transactional
    void createSensorReading() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SensorReading
        var returnedSensorReading = om.readValue(
            restSensorReadingMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sensorReading)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SensorReading.class
        );

        // Validate the SensorReading in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertSensorReadingUpdatableFieldsEquals(returnedSensorReading, getPersistedSensorReading(returnedSensorReading));

        insertedSensorReading = returnedSensorReading;
    }

    @Test
    @Transactional
    void createSensorReadingWithExistingId() throws Exception {
        // Create the SensorReading with an existing ID
        sensorReading.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSensorReadingMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sensorReading)))
            .andExpect(status().isBadRequest());

        // Validate the SensorReading in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTimestampIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        sensorReading.setTimestamp(null);

        // Create the SensorReading, which fails.

        restSensorReadingMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sensorReading)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkValueIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        sensorReading.setValue(null);

        // Create the SensorReading, which fails.

        restSensorReadingMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sensorReading)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUnitIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        sensorReading.setUnit(null);

        // Create the SensorReading, which fails.

        restSensorReadingMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sensorReading)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSensorReadings() throws Exception {
        // Initialize the database
        insertedSensorReading = sensorReadingRepository.saveAndFlush(sensorReading);

        // Get all the sensorReadingList
        restSensorReadingMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sensorReading.getId().intValue())))
            .andExpect(jsonPath("$.[*].timestamp").value(hasItem(DEFAULT_TIMESTAMP.toString())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(sameNumber(DEFAULT_VALUE))))
            .andExpect(jsonPath("$.[*].unit").value(hasItem(DEFAULT_UNIT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSensorReadingsWithEagerRelationshipsIsEnabled() throws Exception {
        when(sensorReadingRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSensorReadingMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(sensorReadingRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSensorReadingsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(sensorReadingRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSensorReadingMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(sensorReadingRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getSensorReading() throws Exception {
        // Initialize the database
        insertedSensorReading = sensorReadingRepository.saveAndFlush(sensorReading);

        // Get the sensorReading
        restSensorReadingMockMvc
            .perform(get(ENTITY_API_URL_ID, sensorReading.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(sensorReading.getId().intValue()))
            .andExpect(jsonPath("$.timestamp").value(DEFAULT_TIMESTAMP.toString()))
            .andExpect(jsonPath("$.value").value(sameNumber(DEFAULT_VALUE)))
            .andExpect(jsonPath("$.unit").value(DEFAULT_UNIT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingSensorReading() throws Exception {
        // Get the sensorReading
        restSensorReadingMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSensorReading() throws Exception {
        // Initialize the database
        insertedSensorReading = sensorReadingRepository.saveAndFlush(sensorReading);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sensorReading
        SensorReading updatedSensorReading = sensorReadingRepository.findById(sensorReading.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSensorReading are not directly saved in db
        em.detach(updatedSensorReading);
        updatedSensorReading.timestamp(UPDATED_TIMESTAMP).value(UPDATED_VALUE).unit(UPDATED_UNIT);

        restSensorReadingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSensorReading.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedSensorReading))
            )
            .andExpect(status().isOk());

        // Validate the SensorReading in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSensorReadingToMatchAllProperties(updatedSensorReading);
    }

    @Test
    @Transactional
    void putNonExistingSensorReading() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sensorReading.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSensorReadingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, sensorReading.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(sensorReading))
            )
            .andExpect(status().isBadRequest());

        // Validate the SensorReading in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSensorReading() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sensorReading.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSensorReadingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(sensorReading))
            )
            .andExpect(status().isBadRequest());

        // Validate the SensorReading in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSensorReading() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sensorReading.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSensorReadingMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sensorReading)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SensorReading in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSensorReadingWithPatch() throws Exception {
        // Initialize the database
        insertedSensorReading = sensorReadingRepository.saveAndFlush(sensorReading);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sensorReading using partial update
        SensorReading partialUpdatedSensorReading = new SensorReading();
        partialUpdatedSensorReading.setId(sensorReading.getId());

        partialUpdatedSensorReading.timestamp(UPDATED_TIMESTAMP).unit(UPDATED_UNIT);

        restSensorReadingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSensorReading.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSensorReading))
            )
            .andExpect(status().isOk());

        // Validate the SensorReading in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSensorReadingUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSensorReading, sensorReading),
            getPersistedSensorReading(sensorReading)
        );
    }

    @Test
    @Transactional
    void fullUpdateSensorReadingWithPatch() throws Exception {
        // Initialize the database
        insertedSensorReading = sensorReadingRepository.saveAndFlush(sensorReading);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sensorReading using partial update
        SensorReading partialUpdatedSensorReading = new SensorReading();
        partialUpdatedSensorReading.setId(sensorReading.getId());

        partialUpdatedSensorReading.timestamp(UPDATED_TIMESTAMP).value(UPDATED_VALUE).unit(UPDATED_UNIT);

        restSensorReadingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSensorReading.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSensorReading))
            )
            .andExpect(status().isOk());

        // Validate the SensorReading in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSensorReadingUpdatableFieldsEquals(partialUpdatedSensorReading, getPersistedSensorReading(partialUpdatedSensorReading));
    }

    @Test
    @Transactional
    void patchNonExistingSensorReading() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sensorReading.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSensorReadingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, sensorReading.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(sensorReading))
            )
            .andExpect(status().isBadRequest());

        // Validate the SensorReading in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSensorReading() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sensorReading.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSensorReadingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(sensorReading))
            )
            .andExpect(status().isBadRequest());

        // Validate the SensorReading in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSensorReading() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sensorReading.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSensorReadingMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(sensorReading)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SensorReading in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSensorReading() throws Exception {
        // Initialize the database
        insertedSensorReading = sensorReadingRepository.saveAndFlush(sensorReading);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the sensorReading
        restSensorReadingMockMvc
            .perform(delete(ENTITY_API_URL_ID, sensorReading.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return sensorReadingRepository.count();
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

    protected SensorReading getPersistedSensorReading(SensorReading sensorReading) {
        return sensorReadingRepository.findById(sensorReading.getId()).orElseThrow();
    }

    protected void assertPersistedSensorReadingToMatchAllProperties(SensorReading expectedSensorReading) {
        assertSensorReadingAllPropertiesEquals(expectedSensorReading, getPersistedSensorReading(expectedSensorReading));
    }

    protected void assertPersistedSensorReadingToMatchUpdatableProperties(SensorReading expectedSensorReading) {
        assertSensorReadingAllUpdatablePropertiesEquals(expectedSensorReading, getPersistedSensorReading(expectedSensorReading));
    }
}
