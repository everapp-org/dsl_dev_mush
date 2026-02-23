package com.mcms.web.rest;

import static com.mcms.domain.StrainAsserts.*;
import static com.mcms.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mcms.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcms.IntegrationTest;
import com.mcms.domain.Strain;
import com.mcms.repository.StrainRepository;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link StrainResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class StrainResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SPECIES = "AAAAAAAAAA";
    private static final String UPDATED_SPECIES = "BBBBBBBBBB";

    private static final String DEFAULT_VARIETY = "AAAAAAAAAA";
    private static final String UPDATED_VARIETY = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_OPTIMAL_TEMP_MIN_C = new BigDecimal(1);
    private static final BigDecimal UPDATED_OPTIMAL_TEMP_MIN_C = new BigDecimal(2);

    private static final BigDecimal DEFAULT_OPTIMAL_TEMP_MAX_C = new BigDecimal(1);
    private static final BigDecimal UPDATED_OPTIMAL_TEMP_MAX_C = new BigDecimal(2);

    private static final BigDecimal DEFAULT_OPTIMAL_HUMIDITY_MIN = new BigDecimal(1);
    private static final BigDecimal UPDATED_OPTIMAL_HUMIDITY_MIN = new BigDecimal(2);

    private static final BigDecimal DEFAULT_OPTIMAL_HUMIDITY_MAX = new BigDecimal(1);
    private static final BigDecimal UPDATED_OPTIMAL_HUMIDITY_MAX = new BigDecimal(2);

    private static final Integer DEFAULT_OPTIMAL_CO_2_MAX_PPM = 1;
    private static final Integer UPDATED_OPTIMAL_CO_2_MAX_PPM = 2;

    private static final Integer DEFAULT_COLONIZATION_DAYS_MIN = 1;
    private static final Integer UPDATED_COLONIZATION_DAYS_MIN = 2;

    private static final Integer DEFAULT_COLONIZATION_DAYS_MAX = 1;
    private static final Integer UPDATED_COLONIZATION_DAYS_MAX = 2;

    private static final BigDecimal DEFAULT_EXPECTED_YIELD_PERCENT = new BigDecimal(1);
    private static final BigDecimal UPDATED_EXPECTED_YIELD_PERCENT = new BigDecimal(2);

    private static final Integer DEFAULT_SHELF_LIFE_DAYS = 1;
    private static final Integer UPDATED_SHELF_LIFE_DAYS = 2;

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/strains";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private StrainRepository strainRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStrainMockMvc;

    private Strain strain;

    private Strain insertedStrain;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Strain createEntity() {
        return new Strain()
            .name(DEFAULT_NAME)
            .species(DEFAULT_SPECIES)
            .variety(DEFAULT_VARIETY)
            .optimalTempMinC(DEFAULT_OPTIMAL_TEMP_MIN_C)
            .optimalTempMaxC(DEFAULT_OPTIMAL_TEMP_MAX_C)
            .optimalHumidityMin(DEFAULT_OPTIMAL_HUMIDITY_MIN)
            .optimalHumidityMax(DEFAULT_OPTIMAL_HUMIDITY_MAX)
            .optimalCO2MaxPpm(DEFAULT_OPTIMAL_CO_2_MAX_PPM)
            .colonizationDaysMin(DEFAULT_COLONIZATION_DAYS_MIN)
            .colonizationDaysMax(DEFAULT_COLONIZATION_DAYS_MAX)
            .expectedYieldPercent(DEFAULT_EXPECTED_YIELD_PERCENT)
            .shelfLifeDays(DEFAULT_SHELF_LIFE_DAYS)
            .note(DEFAULT_NOTE)
            .active(DEFAULT_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Strain createUpdatedEntity() {
        return new Strain()
            .name(UPDATED_NAME)
            .species(UPDATED_SPECIES)
            .variety(UPDATED_VARIETY)
            .optimalTempMinC(UPDATED_OPTIMAL_TEMP_MIN_C)
            .optimalTempMaxC(UPDATED_OPTIMAL_TEMP_MAX_C)
            .optimalHumidityMin(UPDATED_OPTIMAL_HUMIDITY_MIN)
            .optimalHumidityMax(UPDATED_OPTIMAL_HUMIDITY_MAX)
            .optimalCO2MaxPpm(UPDATED_OPTIMAL_CO_2_MAX_PPM)
            .colonizationDaysMin(UPDATED_COLONIZATION_DAYS_MIN)
            .colonizationDaysMax(UPDATED_COLONIZATION_DAYS_MAX)
            .expectedYieldPercent(UPDATED_EXPECTED_YIELD_PERCENT)
            .shelfLifeDays(UPDATED_SHELF_LIFE_DAYS)
            .note(UPDATED_NOTE)
            .active(UPDATED_ACTIVE);
    }

    @BeforeEach
    void initTest() {
        strain = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedStrain != null) {
            strainRepository.delete(insertedStrain);
            insertedStrain = null;
        }
    }

    @Test
    @Transactional
    void createStrain() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Strain
        var returnedStrain = om.readValue(
            restStrainMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(strain)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Strain.class
        );

        // Validate the Strain in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertStrainUpdatableFieldsEquals(returnedStrain, getPersistedStrain(returnedStrain));

        insertedStrain = returnedStrain;
    }

    @Test
    @Transactional
    void createStrainWithExistingId() throws Exception {
        // Create the Strain with an existing ID
        strain.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStrainMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(strain)))
            .andExpect(status().isBadRequest());

        // Validate the Strain in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        strain.setName(null);

        // Create the Strain, which fails.

        restStrainMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(strain)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSpeciesIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        strain.setSpecies(null);

        // Create the Strain, which fails.

        restStrainMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(strain)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        strain.setActive(null);

        // Create the Strain, which fails.

        restStrainMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(strain)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllStrains() throws Exception {
        // Initialize the database
        insertedStrain = strainRepository.saveAndFlush(strain);

        // Get all the strainList
        restStrainMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(strain.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].species").value(hasItem(DEFAULT_SPECIES)))
            .andExpect(jsonPath("$.[*].variety").value(hasItem(DEFAULT_VARIETY)))
            .andExpect(jsonPath("$.[*].optimalTempMinC").value(hasItem(sameNumber(DEFAULT_OPTIMAL_TEMP_MIN_C))))
            .andExpect(jsonPath("$.[*].optimalTempMaxC").value(hasItem(sameNumber(DEFAULT_OPTIMAL_TEMP_MAX_C))))
            .andExpect(jsonPath("$.[*].optimalHumidityMin").value(hasItem(sameNumber(DEFAULT_OPTIMAL_HUMIDITY_MIN))))
            .andExpect(jsonPath("$.[*].optimalHumidityMax").value(hasItem(sameNumber(DEFAULT_OPTIMAL_HUMIDITY_MAX))))
            .andExpect(jsonPath("$.[*].optimalCO2MaxPpm").value(hasItem(DEFAULT_OPTIMAL_CO_2_MAX_PPM)))
            .andExpect(jsonPath("$.[*].colonizationDaysMin").value(hasItem(DEFAULT_COLONIZATION_DAYS_MIN)))
            .andExpect(jsonPath("$.[*].colonizationDaysMax").value(hasItem(DEFAULT_COLONIZATION_DAYS_MAX)))
            .andExpect(jsonPath("$.[*].expectedYieldPercent").value(hasItem(sameNumber(DEFAULT_EXPECTED_YIELD_PERCENT))))
            .andExpect(jsonPath("$.[*].shelfLifeDays").value(hasItem(DEFAULT_SHELF_LIFE_DAYS)))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));
    }

    @Test
    @Transactional
    void getStrain() throws Exception {
        // Initialize the database
        insertedStrain = strainRepository.saveAndFlush(strain);

        // Get the strain
        restStrainMockMvc
            .perform(get(ENTITY_API_URL_ID, strain.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(strain.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.species").value(DEFAULT_SPECIES))
            .andExpect(jsonPath("$.variety").value(DEFAULT_VARIETY))
            .andExpect(jsonPath("$.optimalTempMinC").value(sameNumber(DEFAULT_OPTIMAL_TEMP_MIN_C)))
            .andExpect(jsonPath("$.optimalTempMaxC").value(sameNumber(DEFAULT_OPTIMAL_TEMP_MAX_C)))
            .andExpect(jsonPath("$.optimalHumidityMin").value(sameNumber(DEFAULT_OPTIMAL_HUMIDITY_MIN)))
            .andExpect(jsonPath("$.optimalHumidityMax").value(sameNumber(DEFAULT_OPTIMAL_HUMIDITY_MAX)))
            .andExpect(jsonPath("$.optimalCO2MaxPpm").value(DEFAULT_OPTIMAL_CO_2_MAX_PPM))
            .andExpect(jsonPath("$.colonizationDaysMin").value(DEFAULT_COLONIZATION_DAYS_MIN))
            .andExpect(jsonPath("$.colonizationDaysMax").value(DEFAULT_COLONIZATION_DAYS_MAX))
            .andExpect(jsonPath("$.expectedYieldPercent").value(sameNumber(DEFAULT_EXPECTED_YIELD_PERCENT)))
            .andExpect(jsonPath("$.shelfLifeDays").value(DEFAULT_SHELF_LIFE_DAYS))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE));
    }

    @Test
    @Transactional
    void getNonExistingStrain() throws Exception {
        // Get the strain
        restStrainMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStrain() throws Exception {
        // Initialize the database
        insertedStrain = strainRepository.saveAndFlush(strain);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the strain
        Strain updatedStrain = strainRepository.findById(strain.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedStrain are not directly saved in db
        em.detach(updatedStrain);
        updatedStrain
            .name(UPDATED_NAME)
            .species(UPDATED_SPECIES)
            .variety(UPDATED_VARIETY)
            .optimalTempMinC(UPDATED_OPTIMAL_TEMP_MIN_C)
            .optimalTempMaxC(UPDATED_OPTIMAL_TEMP_MAX_C)
            .optimalHumidityMin(UPDATED_OPTIMAL_HUMIDITY_MIN)
            .optimalHumidityMax(UPDATED_OPTIMAL_HUMIDITY_MAX)
            .optimalCO2MaxPpm(UPDATED_OPTIMAL_CO_2_MAX_PPM)
            .colonizationDaysMin(UPDATED_COLONIZATION_DAYS_MIN)
            .colonizationDaysMax(UPDATED_COLONIZATION_DAYS_MAX)
            .expectedYieldPercent(UPDATED_EXPECTED_YIELD_PERCENT)
            .shelfLifeDays(UPDATED_SHELF_LIFE_DAYS)
            .note(UPDATED_NOTE)
            .active(UPDATED_ACTIVE);

        restStrainMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedStrain.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedStrain))
            )
            .andExpect(status().isOk());

        // Validate the Strain in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedStrainToMatchAllProperties(updatedStrain);
    }

    @Test
    @Transactional
    void putNonExistingStrain() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        strain.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStrainMockMvc
            .perform(put(ENTITY_API_URL_ID, strain.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(strain)))
            .andExpect(status().isBadRequest());

        // Validate the Strain in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStrain() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        strain.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStrainMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(strain))
            )
            .andExpect(status().isBadRequest());

        // Validate the Strain in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStrain() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        strain.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStrainMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(strain)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Strain in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStrainWithPatch() throws Exception {
        // Initialize the database
        insertedStrain = strainRepository.saveAndFlush(strain);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the strain using partial update
        Strain partialUpdatedStrain = new Strain();
        partialUpdatedStrain.setId(strain.getId());

        partialUpdatedStrain
            .optimalTempMaxC(UPDATED_OPTIMAL_TEMP_MAX_C)
            .optimalHumidityMax(UPDATED_OPTIMAL_HUMIDITY_MAX)
            .optimalCO2MaxPpm(UPDATED_OPTIMAL_CO_2_MAX_PPM)
            .colonizationDaysMin(UPDATED_COLONIZATION_DAYS_MIN)
            .note(UPDATED_NOTE)
            .active(UPDATED_ACTIVE);

        restStrainMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStrain.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStrain))
            )
            .andExpect(status().isOk());

        // Validate the Strain in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStrainUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedStrain, strain), getPersistedStrain(strain));
    }

    @Test
    @Transactional
    void fullUpdateStrainWithPatch() throws Exception {
        // Initialize the database
        insertedStrain = strainRepository.saveAndFlush(strain);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the strain using partial update
        Strain partialUpdatedStrain = new Strain();
        partialUpdatedStrain.setId(strain.getId());

        partialUpdatedStrain
            .name(UPDATED_NAME)
            .species(UPDATED_SPECIES)
            .variety(UPDATED_VARIETY)
            .optimalTempMinC(UPDATED_OPTIMAL_TEMP_MIN_C)
            .optimalTempMaxC(UPDATED_OPTIMAL_TEMP_MAX_C)
            .optimalHumidityMin(UPDATED_OPTIMAL_HUMIDITY_MIN)
            .optimalHumidityMax(UPDATED_OPTIMAL_HUMIDITY_MAX)
            .optimalCO2MaxPpm(UPDATED_OPTIMAL_CO_2_MAX_PPM)
            .colonizationDaysMin(UPDATED_COLONIZATION_DAYS_MIN)
            .colonizationDaysMax(UPDATED_COLONIZATION_DAYS_MAX)
            .expectedYieldPercent(UPDATED_EXPECTED_YIELD_PERCENT)
            .shelfLifeDays(UPDATED_SHELF_LIFE_DAYS)
            .note(UPDATED_NOTE)
            .active(UPDATED_ACTIVE);

        restStrainMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStrain.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStrain))
            )
            .andExpect(status().isOk());

        // Validate the Strain in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStrainUpdatableFieldsEquals(partialUpdatedStrain, getPersistedStrain(partialUpdatedStrain));
    }

    @Test
    @Transactional
    void patchNonExistingStrain() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        strain.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStrainMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, strain.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(strain))
            )
            .andExpect(status().isBadRequest());

        // Validate the Strain in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStrain() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        strain.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStrainMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(strain))
            )
            .andExpect(status().isBadRequest());

        // Validate the Strain in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStrain() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        strain.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStrainMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(strain)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Strain in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStrain() throws Exception {
        // Initialize the database
        insertedStrain = strainRepository.saveAndFlush(strain);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the strain
        restStrainMockMvc
            .perform(delete(ENTITY_API_URL_ID, strain.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return strainRepository.count();
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

    protected Strain getPersistedStrain(Strain strain) {
        return strainRepository.findById(strain.getId()).orElseThrow();
    }

    protected void assertPersistedStrainToMatchAllProperties(Strain expectedStrain) {
        assertStrainAllPropertiesEquals(expectedStrain, getPersistedStrain(expectedStrain));
    }

    protected void assertPersistedStrainToMatchUpdatableProperties(Strain expectedStrain) {
        assertStrainAllUpdatablePropertiesEquals(expectedStrain, getPersistedStrain(expectedStrain));
    }
}
