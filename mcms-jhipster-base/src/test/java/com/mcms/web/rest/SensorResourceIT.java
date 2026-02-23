package com.mcms.web.rest;

import static com.mcms.domain.SensorAsserts.*;
import static com.mcms.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcms.IntegrationTest;
import com.mcms.domain.Room;
import com.mcms.domain.Sensor;
import com.mcms.domain.enumeration.SensorStatus;
import com.mcms.domain.enumeration.SensorUnit;
import com.mcms.repository.SensorRepository;
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
 * Integration tests for the {@link SensorResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SensorResourceIT {

    private static final String DEFAULT_SENSOR_CODE = "AAAAAAAAAA";
    private static final String UPDATED_SENSOR_CODE = "BBBBBBBBBB";

    private static final SensorUnit DEFAULT_SENSOR_TYPE = SensorUnit.CELSIUS;
    private static final SensorUnit UPDATED_SENSOR_TYPE = SensorUnit.HUMIDITY_PERCENT;

    private static final SensorStatus DEFAULT_STATUS = SensorStatus.ACTIVE;
    private static final SensorStatus UPDATED_STATUS = SensorStatus.INACTIVE;

    private static final LocalDate DEFAULT_INSTALLED_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_INSTALLED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_LAST_CALIBRATION_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_LAST_CALIBRATION_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_MANUFACTURER = "AAAAAAAAAA";
    private static final String UPDATED_MANUFACTURER = "BBBBBBBBBB";

    private static final String DEFAULT_MODEL = "AAAAAAAAAA";
    private static final String UPDATED_MODEL = "BBBBBBBBBB";

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/sensors";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SensorRepository sensorRepository;

    @Mock
    private SensorRepository sensorRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSensorMockMvc;

    private Sensor sensor;

    private Sensor insertedSensor;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Sensor createEntity(EntityManager em) {
        Sensor sensor = new Sensor()
            .sensorCode(DEFAULT_SENSOR_CODE)
            .sensorType(DEFAULT_SENSOR_TYPE)
            .status(DEFAULT_STATUS)
            .installedDate(DEFAULT_INSTALLED_DATE)
            .lastCalibrationDate(DEFAULT_LAST_CALIBRATION_DATE)
            .manufacturer(DEFAULT_MANUFACTURER)
            .model(DEFAULT_MODEL)
            .note(DEFAULT_NOTE);
        // Add required entity
        Room room;
        if (TestUtil.findAll(em, Room.class).isEmpty()) {
            room = RoomResourceIT.createEntity();
            em.persist(room);
            em.flush();
        } else {
            room = TestUtil.findAll(em, Room.class).get(0);
        }
        sensor.setRoom(room);
        return sensor;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Sensor createUpdatedEntity(EntityManager em) {
        Sensor updatedSensor = new Sensor()
            .sensorCode(UPDATED_SENSOR_CODE)
            .sensorType(UPDATED_SENSOR_TYPE)
            .status(UPDATED_STATUS)
            .installedDate(UPDATED_INSTALLED_DATE)
            .lastCalibrationDate(UPDATED_LAST_CALIBRATION_DATE)
            .manufacturer(UPDATED_MANUFACTURER)
            .model(UPDATED_MODEL)
            .note(UPDATED_NOTE);
        // Add required entity
        Room room;
        if (TestUtil.findAll(em, Room.class).isEmpty()) {
            room = RoomResourceIT.createUpdatedEntity();
            em.persist(room);
            em.flush();
        } else {
            room = TestUtil.findAll(em, Room.class).get(0);
        }
        updatedSensor.setRoom(room);
        return updatedSensor;
    }

    @BeforeEach
    void initTest() {
        sensor = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedSensor != null) {
            sensorRepository.delete(insertedSensor);
            insertedSensor = null;
        }
    }

    @Test
    @Transactional
    void createSensor() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Sensor
        var returnedSensor = om.readValue(
            restSensorMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sensor)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Sensor.class
        );

        // Validate the Sensor in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertSensorUpdatableFieldsEquals(returnedSensor, getPersistedSensor(returnedSensor));

        insertedSensor = returnedSensor;
    }

    @Test
    @Transactional
    void createSensorWithExistingId() throws Exception {
        // Create the Sensor with an existing ID
        sensor.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSensorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sensor)))
            .andExpect(status().isBadRequest());

        // Validate the Sensor in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkSensorCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        sensor.setSensorCode(null);

        // Create the Sensor, which fails.

        restSensorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sensor)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSensorTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        sensor.setSensorType(null);

        // Create the Sensor, which fails.

        restSensorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sensor)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        sensor.setStatus(null);

        // Create the Sensor, which fails.

        restSensorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sensor)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSensors() throws Exception {
        // Initialize the database
        insertedSensor = sensorRepository.saveAndFlush(sensor);

        // Get all the sensorList
        restSensorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sensor.getId().intValue())))
            .andExpect(jsonPath("$.[*].sensorCode").value(hasItem(DEFAULT_SENSOR_CODE)))
            .andExpect(jsonPath("$.[*].sensorType").value(hasItem(DEFAULT_SENSOR_TYPE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].installedDate").value(hasItem(DEFAULT_INSTALLED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastCalibrationDate").value(hasItem(DEFAULT_LAST_CALIBRATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].manufacturer").value(hasItem(DEFAULT_MANUFACTURER)))
            .andExpect(jsonPath("$.[*].model").value(hasItem(DEFAULT_MODEL)))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSensorsWithEagerRelationshipsIsEnabled() throws Exception {
        when(sensorRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSensorMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(sensorRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSensorsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(sensorRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSensorMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(sensorRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getSensor() throws Exception {
        // Initialize the database
        insertedSensor = sensorRepository.saveAndFlush(sensor);

        // Get the sensor
        restSensorMockMvc
            .perform(get(ENTITY_API_URL_ID, sensor.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(sensor.getId().intValue()))
            .andExpect(jsonPath("$.sensorCode").value(DEFAULT_SENSOR_CODE))
            .andExpect(jsonPath("$.sensorType").value(DEFAULT_SENSOR_TYPE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.installedDate").value(DEFAULT_INSTALLED_DATE.toString()))
            .andExpect(jsonPath("$.lastCalibrationDate").value(DEFAULT_LAST_CALIBRATION_DATE.toString()))
            .andExpect(jsonPath("$.manufacturer").value(DEFAULT_MANUFACTURER))
            .andExpect(jsonPath("$.model").value(DEFAULT_MODEL))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE));
    }

    @Test
    @Transactional
    void getNonExistingSensor() throws Exception {
        // Get the sensor
        restSensorMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSensor() throws Exception {
        // Initialize the database
        insertedSensor = sensorRepository.saveAndFlush(sensor);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sensor
        Sensor updatedSensor = sensorRepository.findById(sensor.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSensor are not directly saved in db
        em.detach(updatedSensor);
        updatedSensor
            .sensorCode(UPDATED_SENSOR_CODE)
            .sensorType(UPDATED_SENSOR_TYPE)
            .status(UPDATED_STATUS)
            .installedDate(UPDATED_INSTALLED_DATE)
            .lastCalibrationDate(UPDATED_LAST_CALIBRATION_DATE)
            .manufacturer(UPDATED_MANUFACTURER)
            .model(UPDATED_MODEL)
            .note(UPDATED_NOTE);

        restSensorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSensor.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedSensor))
            )
            .andExpect(status().isOk());

        // Validate the Sensor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSensorToMatchAllProperties(updatedSensor);
    }

    @Test
    @Transactional
    void putNonExistingSensor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sensor.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSensorMockMvc
            .perform(put(ENTITY_API_URL_ID, sensor.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sensor)))
            .andExpect(status().isBadRequest());

        // Validate the Sensor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSensor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sensor.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSensorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(sensor))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sensor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSensor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sensor.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSensorMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sensor)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Sensor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSensorWithPatch() throws Exception {
        // Initialize the database
        insertedSensor = sensorRepository.saveAndFlush(sensor);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sensor using partial update
        Sensor partialUpdatedSensor = new Sensor();
        partialUpdatedSensor.setId(sensor.getId());

        partialUpdatedSensor
            .sensorCode(UPDATED_SENSOR_CODE)
            .sensorType(UPDATED_SENSOR_TYPE)
            .status(UPDATED_STATUS)
            .manufacturer(UPDATED_MANUFACTURER)
            .model(UPDATED_MODEL)
            .note(UPDATED_NOTE);

        restSensorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSensor.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSensor))
            )
            .andExpect(status().isOk());

        // Validate the Sensor in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSensorUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedSensor, sensor), getPersistedSensor(sensor));
    }

    @Test
    @Transactional
    void fullUpdateSensorWithPatch() throws Exception {
        // Initialize the database
        insertedSensor = sensorRepository.saveAndFlush(sensor);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sensor using partial update
        Sensor partialUpdatedSensor = new Sensor();
        partialUpdatedSensor.setId(sensor.getId());

        partialUpdatedSensor
            .sensorCode(UPDATED_SENSOR_CODE)
            .sensorType(UPDATED_SENSOR_TYPE)
            .status(UPDATED_STATUS)
            .installedDate(UPDATED_INSTALLED_DATE)
            .lastCalibrationDate(UPDATED_LAST_CALIBRATION_DATE)
            .manufacturer(UPDATED_MANUFACTURER)
            .model(UPDATED_MODEL)
            .note(UPDATED_NOTE);

        restSensorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSensor.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSensor))
            )
            .andExpect(status().isOk());

        // Validate the Sensor in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSensorUpdatableFieldsEquals(partialUpdatedSensor, getPersistedSensor(partialUpdatedSensor));
    }

    @Test
    @Transactional
    void patchNonExistingSensor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sensor.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSensorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, sensor.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(sensor))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sensor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSensor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sensor.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSensorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(sensor))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sensor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSensor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sensor.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSensorMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(sensor)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Sensor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSensor() throws Exception {
        // Initialize the database
        insertedSensor = sensorRepository.saveAndFlush(sensor);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the sensor
        restSensorMockMvc
            .perform(delete(ENTITY_API_URL_ID, sensor.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return sensorRepository.count();
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

    protected Sensor getPersistedSensor(Sensor sensor) {
        return sensorRepository.findById(sensor.getId()).orElseThrow();
    }

    protected void assertPersistedSensorToMatchAllProperties(Sensor expectedSensor) {
        assertSensorAllPropertiesEquals(expectedSensor, getPersistedSensor(expectedSensor));
    }

    protected void assertPersistedSensorToMatchUpdatableProperties(Sensor expectedSensor) {
        assertSensorAllUpdatablePropertiesEquals(expectedSensor, getPersistedSensor(expectedSensor));
    }
}
