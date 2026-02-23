package com.mcms.web.rest;

import static com.mcms.domain.EnvironmentalTargetAsserts.*;
import static com.mcms.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mcms.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcms.IntegrationTest;
import com.mcms.domain.EnvironmentalTarget;
import com.mcms.domain.Room;
import com.mcms.domain.enumeration.PhaseName;
import com.mcms.repository.EnvironmentalTargetRepository;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link EnvironmentalTargetResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class EnvironmentalTargetResourceIT {

    private static final PhaseName DEFAULT_PHASE = PhaseName.INOCULATION;
    private static final PhaseName UPDATED_PHASE = PhaseName.EARLY_COLONIZATION;

    private static final BigDecimal DEFAULT_TEMP_MIN_C = new BigDecimal(1);
    private static final BigDecimal UPDATED_TEMP_MIN_C = new BigDecimal(2);

    private static final BigDecimal DEFAULT_TEMP_MAX_C = new BigDecimal(1);
    private static final BigDecimal UPDATED_TEMP_MAX_C = new BigDecimal(2);

    private static final BigDecimal DEFAULT_HUMIDITY_MIN_PERCENT = new BigDecimal(1);
    private static final BigDecimal UPDATED_HUMIDITY_MIN_PERCENT = new BigDecimal(2);

    private static final BigDecimal DEFAULT_HUMIDITY_MAX_PERCENT = new BigDecimal(1);
    private static final BigDecimal UPDATED_HUMIDITY_MAX_PERCENT = new BigDecimal(2);

    private static final Integer DEFAULT_CO_2_MAX_PPM = 1;
    private static final Integer UPDATED_CO_2_MAX_PPM = 2;

    private static final Integer DEFAULT_LIGHT_LUX = 1;
    private static final Integer UPDATED_LIGHT_LUX = 2;

    private static final Integer DEFAULT_FRESH_AIR_EXCHANGES_PER_HOUR = 1;
    private static final Integer UPDATED_FRESH_AIR_EXCHANGES_PER_HOUR = 2;

    private static final String ENTITY_API_URL = "/api/environmental-targets";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EnvironmentalTargetRepository environmentalTargetRepository;

    @Mock
    private EnvironmentalTargetRepository environmentalTargetRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEnvironmentalTargetMockMvc;

    private EnvironmentalTarget environmentalTarget;

    private EnvironmentalTarget insertedEnvironmentalTarget;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EnvironmentalTarget createEntity(EntityManager em) {
        EnvironmentalTarget environmentalTarget = new EnvironmentalTarget()
            .phase(DEFAULT_PHASE)
            .tempMinC(DEFAULT_TEMP_MIN_C)
            .tempMaxC(DEFAULT_TEMP_MAX_C)
            .humidityMinPercent(DEFAULT_HUMIDITY_MIN_PERCENT)
            .humidityMaxPercent(DEFAULT_HUMIDITY_MAX_PERCENT)
            .co2MaxPpm(DEFAULT_CO_2_MAX_PPM)
            .lightLux(DEFAULT_LIGHT_LUX)
            .freshAirExchangesPerHour(DEFAULT_FRESH_AIR_EXCHANGES_PER_HOUR);
        // Add required entity
        Room room;
        if (TestUtil.findAll(em, Room.class).isEmpty()) {
            room = RoomResourceIT.createEntity();
            em.persist(room);
            em.flush();
        } else {
            room = TestUtil.findAll(em, Room.class).get(0);
        }
        environmentalTarget.setRoom(room);
        return environmentalTarget;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EnvironmentalTarget createUpdatedEntity(EntityManager em) {
        EnvironmentalTarget updatedEnvironmentalTarget = new EnvironmentalTarget()
            .phase(UPDATED_PHASE)
            .tempMinC(UPDATED_TEMP_MIN_C)
            .tempMaxC(UPDATED_TEMP_MAX_C)
            .humidityMinPercent(UPDATED_HUMIDITY_MIN_PERCENT)
            .humidityMaxPercent(UPDATED_HUMIDITY_MAX_PERCENT)
            .co2MaxPpm(UPDATED_CO_2_MAX_PPM)
            .lightLux(UPDATED_LIGHT_LUX)
            .freshAirExchangesPerHour(UPDATED_FRESH_AIR_EXCHANGES_PER_HOUR);
        // Add required entity
        Room room;
        if (TestUtil.findAll(em, Room.class).isEmpty()) {
            room = RoomResourceIT.createUpdatedEntity();
            em.persist(room);
            em.flush();
        } else {
            room = TestUtil.findAll(em, Room.class).get(0);
        }
        updatedEnvironmentalTarget.setRoom(room);
        return updatedEnvironmentalTarget;
    }

    @BeforeEach
    void initTest() {
        environmentalTarget = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedEnvironmentalTarget != null) {
            environmentalTargetRepository.delete(insertedEnvironmentalTarget);
            insertedEnvironmentalTarget = null;
        }
    }

    @Test
    @Transactional
    void createEnvironmentalTarget() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the EnvironmentalTarget
        var returnedEnvironmentalTarget = om.readValue(
            restEnvironmentalTargetMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(environmentalTarget)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            EnvironmentalTarget.class
        );

        // Validate the EnvironmentalTarget in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertEnvironmentalTargetUpdatableFieldsEquals(
            returnedEnvironmentalTarget,
            getPersistedEnvironmentalTarget(returnedEnvironmentalTarget)
        );

        insertedEnvironmentalTarget = returnedEnvironmentalTarget;
    }

    @Test
    @Transactional
    void createEnvironmentalTargetWithExistingId() throws Exception {
        // Create the EnvironmentalTarget with an existing ID
        environmentalTarget.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEnvironmentalTargetMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(environmentalTarget)))
            .andExpect(status().isBadRequest());

        // Validate the EnvironmentalTarget in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkPhaseIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        environmentalTarget.setPhase(null);

        // Create the EnvironmentalTarget, which fails.

        restEnvironmentalTargetMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(environmentalTarget)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTempMinCIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        environmentalTarget.setTempMinC(null);

        // Create the EnvironmentalTarget, which fails.

        restEnvironmentalTargetMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(environmentalTarget)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTempMaxCIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        environmentalTarget.setTempMaxC(null);

        // Create the EnvironmentalTarget, which fails.

        restEnvironmentalTargetMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(environmentalTarget)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkHumidityMinPercentIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        environmentalTarget.setHumidityMinPercent(null);

        // Create the EnvironmentalTarget, which fails.

        restEnvironmentalTargetMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(environmentalTarget)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkHumidityMaxPercentIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        environmentalTarget.setHumidityMaxPercent(null);

        // Create the EnvironmentalTarget, which fails.

        restEnvironmentalTargetMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(environmentalTarget)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllEnvironmentalTargets() throws Exception {
        // Initialize the database
        insertedEnvironmentalTarget = environmentalTargetRepository.saveAndFlush(environmentalTarget);

        // Get all the environmentalTargetList
        restEnvironmentalTargetMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(environmentalTarget.getId().intValue())))
            .andExpect(jsonPath("$.[*].phase").value(hasItem(DEFAULT_PHASE.toString())))
            .andExpect(jsonPath("$.[*].tempMinC").value(hasItem(sameNumber(DEFAULT_TEMP_MIN_C))))
            .andExpect(jsonPath("$.[*].tempMaxC").value(hasItem(sameNumber(DEFAULT_TEMP_MAX_C))))
            .andExpect(jsonPath("$.[*].humidityMinPercent").value(hasItem(sameNumber(DEFAULT_HUMIDITY_MIN_PERCENT))))
            .andExpect(jsonPath("$.[*].humidityMaxPercent").value(hasItem(sameNumber(DEFAULT_HUMIDITY_MAX_PERCENT))))
            .andExpect(jsonPath("$.[*].co2MaxPpm").value(hasItem(DEFAULT_CO_2_MAX_PPM)))
            .andExpect(jsonPath("$.[*].lightLux").value(hasItem(DEFAULT_LIGHT_LUX)))
            .andExpect(jsonPath("$.[*].freshAirExchangesPerHour").value(hasItem(DEFAULT_FRESH_AIR_EXCHANGES_PER_HOUR)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEnvironmentalTargetsWithEagerRelationshipsIsEnabled() throws Exception {
        when(environmentalTargetRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEnvironmentalTargetMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(environmentalTargetRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEnvironmentalTargetsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(environmentalTargetRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEnvironmentalTargetMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(environmentalTargetRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getEnvironmentalTarget() throws Exception {
        // Initialize the database
        insertedEnvironmentalTarget = environmentalTargetRepository.saveAndFlush(environmentalTarget);

        // Get the environmentalTarget
        restEnvironmentalTargetMockMvc
            .perform(get(ENTITY_API_URL_ID, environmentalTarget.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(environmentalTarget.getId().intValue()))
            .andExpect(jsonPath("$.phase").value(DEFAULT_PHASE.toString()))
            .andExpect(jsonPath("$.tempMinC").value(sameNumber(DEFAULT_TEMP_MIN_C)))
            .andExpect(jsonPath("$.tempMaxC").value(sameNumber(DEFAULT_TEMP_MAX_C)))
            .andExpect(jsonPath("$.humidityMinPercent").value(sameNumber(DEFAULT_HUMIDITY_MIN_PERCENT)))
            .andExpect(jsonPath("$.humidityMaxPercent").value(sameNumber(DEFAULT_HUMIDITY_MAX_PERCENT)))
            .andExpect(jsonPath("$.co2MaxPpm").value(DEFAULT_CO_2_MAX_PPM))
            .andExpect(jsonPath("$.lightLux").value(DEFAULT_LIGHT_LUX))
            .andExpect(jsonPath("$.freshAirExchangesPerHour").value(DEFAULT_FRESH_AIR_EXCHANGES_PER_HOUR));
    }

    @Test
    @Transactional
    void getNonExistingEnvironmentalTarget() throws Exception {
        // Get the environmentalTarget
        restEnvironmentalTargetMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingEnvironmentalTarget() throws Exception {
        // Initialize the database
        insertedEnvironmentalTarget = environmentalTargetRepository.saveAndFlush(environmentalTarget);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the environmentalTarget
        EnvironmentalTarget updatedEnvironmentalTarget = environmentalTargetRepository.findById(environmentalTarget.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedEnvironmentalTarget are not directly saved in db
        em.detach(updatedEnvironmentalTarget);
        updatedEnvironmentalTarget
            .phase(UPDATED_PHASE)
            .tempMinC(UPDATED_TEMP_MIN_C)
            .tempMaxC(UPDATED_TEMP_MAX_C)
            .humidityMinPercent(UPDATED_HUMIDITY_MIN_PERCENT)
            .humidityMaxPercent(UPDATED_HUMIDITY_MAX_PERCENT)
            .co2MaxPpm(UPDATED_CO_2_MAX_PPM)
            .lightLux(UPDATED_LIGHT_LUX)
            .freshAirExchangesPerHour(UPDATED_FRESH_AIR_EXCHANGES_PER_HOUR);

        restEnvironmentalTargetMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedEnvironmentalTarget.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedEnvironmentalTarget))
            )
            .andExpect(status().isOk());

        // Validate the EnvironmentalTarget in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEnvironmentalTargetToMatchAllProperties(updatedEnvironmentalTarget);
    }

    @Test
    @Transactional
    void putNonExistingEnvironmentalTarget() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        environmentalTarget.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEnvironmentalTargetMockMvc
            .perform(
                put(ENTITY_API_URL_ID, environmentalTarget.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(environmentalTarget))
            )
            .andExpect(status().isBadRequest());

        // Validate the EnvironmentalTarget in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEnvironmentalTarget() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        environmentalTarget.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnvironmentalTargetMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(environmentalTarget))
            )
            .andExpect(status().isBadRequest());

        // Validate the EnvironmentalTarget in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEnvironmentalTarget() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        environmentalTarget.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnvironmentalTargetMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(environmentalTarget)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the EnvironmentalTarget in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEnvironmentalTargetWithPatch() throws Exception {
        // Initialize the database
        insertedEnvironmentalTarget = environmentalTargetRepository.saveAndFlush(environmentalTarget);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the environmentalTarget using partial update
        EnvironmentalTarget partialUpdatedEnvironmentalTarget = new EnvironmentalTarget();
        partialUpdatedEnvironmentalTarget.setId(environmentalTarget.getId());

        partialUpdatedEnvironmentalTarget
            .tempMaxC(UPDATED_TEMP_MAX_C)
            .humidityMinPercent(UPDATED_HUMIDITY_MIN_PERCENT)
            .humidityMaxPercent(UPDATED_HUMIDITY_MAX_PERCENT)
            .freshAirExchangesPerHour(UPDATED_FRESH_AIR_EXCHANGES_PER_HOUR);

        restEnvironmentalTargetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEnvironmentalTarget.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEnvironmentalTarget))
            )
            .andExpect(status().isOk());

        // Validate the EnvironmentalTarget in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEnvironmentalTargetUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedEnvironmentalTarget, environmentalTarget),
            getPersistedEnvironmentalTarget(environmentalTarget)
        );
    }

    @Test
    @Transactional
    void fullUpdateEnvironmentalTargetWithPatch() throws Exception {
        // Initialize the database
        insertedEnvironmentalTarget = environmentalTargetRepository.saveAndFlush(environmentalTarget);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the environmentalTarget using partial update
        EnvironmentalTarget partialUpdatedEnvironmentalTarget = new EnvironmentalTarget();
        partialUpdatedEnvironmentalTarget.setId(environmentalTarget.getId());

        partialUpdatedEnvironmentalTarget
            .phase(UPDATED_PHASE)
            .tempMinC(UPDATED_TEMP_MIN_C)
            .tempMaxC(UPDATED_TEMP_MAX_C)
            .humidityMinPercent(UPDATED_HUMIDITY_MIN_PERCENT)
            .humidityMaxPercent(UPDATED_HUMIDITY_MAX_PERCENT)
            .co2MaxPpm(UPDATED_CO_2_MAX_PPM)
            .lightLux(UPDATED_LIGHT_LUX)
            .freshAirExchangesPerHour(UPDATED_FRESH_AIR_EXCHANGES_PER_HOUR);

        restEnvironmentalTargetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEnvironmentalTarget.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEnvironmentalTarget))
            )
            .andExpect(status().isOk());

        // Validate the EnvironmentalTarget in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEnvironmentalTargetUpdatableFieldsEquals(
            partialUpdatedEnvironmentalTarget,
            getPersistedEnvironmentalTarget(partialUpdatedEnvironmentalTarget)
        );
    }

    @Test
    @Transactional
    void patchNonExistingEnvironmentalTarget() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        environmentalTarget.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEnvironmentalTargetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, environmentalTarget.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(environmentalTarget))
            )
            .andExpect(status().isBadRequest());

        // Validate the EnvironmentalTarget in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEnvironmentalTarget() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        environmentalTarget.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnvironmentalTargetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(environmentalTarget))
            )
            .andExpect(status().isBadRequest());

        // Validate the EnvironmentalTarget in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEnvironmentalTarget() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        environmentalTarget.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnvironmentalTargetMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(environmentalTarget)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the EnvironmentalTarget in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEnvironmentalTarget() throws Exception {
        // Initialize the database
        insertedEnvironmentalTarget = environmentalTargetRepository.saveAndFlush(environmentalTarget);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the environmentalTarget
        restEnvironmentalTargetMockMvc
            .perform(delete(ENTITY_API_URL_ID, environmentalTarget.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return environmentalTargetRepository.count();
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

    protected EnvironmentalTarget getPersistedEnvironmentalTarget(EnvironmentalTarget environmentalTarget) {
        return environmentalTargetRepository.findById(environmentalTarget.getId()).orElseThrow();
    }

    protected void assertPersistedEnvironmentalTargetToMatchAllProperties(EnvironmentalTarget expectedEnvironmentalTarget) {
        assertEnvironmentalTargetAllPropertiesEquals(
            expectedEnvironmentalTarget,
            getPersistedEnvironmentalTarget(expectedEnvironmentalTarget)
        );
    }

    protected void assertPersistedEnvironmentalTargetToMatchUpdatableProperties(EnvironmentalTarget expectedEnvironmentalTarget) {
        assertEnvironmentalTargetAllUpdatablePropertiesEquals(
            expectedEnvironmentalTarget,
            getPersistedEnvironmentalTarget(expectedEnvironmentalTarget)
        );
    }
}
