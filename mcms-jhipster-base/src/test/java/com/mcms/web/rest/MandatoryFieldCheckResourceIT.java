package com.mcms.web.rest;

import static com.mcms.domain.MandatoryFieldCheckAsserts.*;
import static com.mcms.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcms.IntegrationTest;
import com.mcms.domain.MandatoryFieldCheck;
import com.mcms.repository.MandatoryFieldCheckRepository;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link MandatoryFieldCheckResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class MandatoryFieldCheckResourceIT {

    private static final String DEFAULT_FIELD_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIELD_NAME = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_FILLED = false;
    private static final Boolean UPDATED_IS_FILLED = true;

    private static final LocalDate DEFAULT_CHECK_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CHECK_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/mandatory-field-checks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MandatoryFieldCheckRepository mandatoryFieldCheckRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMandatoryFieldCheckMockMvc;

    private MandatoryFieldCheck mandatoryFieldCheck;

    private MandatoryFieldCheck insertedMandatoryFieldCheck;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MandatoryFieldCheck createEntity() {
        return new MandatoryFieldCheck().fieldName(DEFAULT_FIELD_NAME).isFilled(DEFAULT_IS_FILLED).checkDate(DEFAULT_CHECK_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MandatoryFieldCheck createUpdatedEntity() {
        return new MandatoryFieldCheck().fieldName(UPDATED_FIELD_NAME).isFilled(UPDATED_IS_FILLED).checkDate(UPDATED_CHECK_DATE);
    }

    @BeforeEach
    void initTest() {
        mandatoryFieldCheck = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedMandatoryFieldCheck != null) {
            mandatoryFieldCheckRepository.delete(insertedMandatoryFieldCheck);
            insertedMandatoryFieldCheck = null;
        }
    }

    @Test
    @Transactional
    void createMandatoryFieldCheck() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the MandatoryFieldCheck
        var returnedMandatoryFieldCheck = om.readValue(
            restMandatoryFieldCheckMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(mandatoryFieldCheck)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            MandatoryFieldCheck.class
        );

        // Validate the MandatoryFieldCheck in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertMandatoryFieldCheckUpdatableFieldsEquals(
            returnedMandatoryFieldCheck,
            getPersistedMandatoryFieldCheck(returnedMandatoryFieldCheck)
        );

        insertedMandatoryFieldCheck = returnedMandatoryFieldCheck;
    }

    @Test
    @Transactional
    void createMandatoryFieldCheckWithExistingId() throws Exception {
        // Create the MandatoryFieldCheck with an existing ID
        mandatoryFieldCheck.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMandatoryFieldCheckMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(mandatoryFieldCheck)))
            .andExpect(status().isBadRequest());

        // Validate the MandatoryFieldCheck in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkFieldNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        mandatoryFieldCheck.setFieldName(null);

        // Create the MandatoryFieldCheck, which fails.

        restMandatoryFieldCheckMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(mandatoryFieldCheck)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsFilledIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        mandatoryFieldCheck.setIsFilled(null);

        // Create the MandatoryFieldCheck, which fails.

        restMandatoryFieldCheckMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(mandatoryFieldCheck)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCheckDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        mandatoryFieldCheck.setCheckDate(null);

        // Create the MandatoryFieldCheck, which fails.

        restMandatoryFieldCheckMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(mandatoryFieldCheck)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllMandatoryFieldChecks() throws Exception {
        // Initialize the database
        insertedMandatoryFieldCheck = mandatoryFieldCheckRepository.saveAndFlush(mandatoryFieldCheck);

        // Get all the mandatoryFieldCheckList
        restMandatoryFieldCheckMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(mandatoryFieldCheck.getId().intValue())))
            .andExpect(jsonPath("$.[*].fieldName").value(hasItem(DEFAULT_FIELD_NAME)))
            .andExpect(jsonPath("$.[*].isFilled").value(hasItem(DEFAULT_IS_FILLED)))
            .andExpect(jsonPath("$.[*].checkDate").value(hasItem(DEFAULT_CHECK_DATE.toString())));
    }

    @Test
    @Transactional
    void getMandatoryFieldCheck() throws Exception {
        // Initialize the database
        insertedMandatoryFieldCheck = mandatoryFieldCheckRepository.saveAndFlush(mandatoryFieldCheck);

        // Get the mandatoryFieldCheck
        restMandatoryFieldCheckMockMvc
            .perform(get(ENTITY_API_URL_ID, mandatoryFieldCheck.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(mandatoryFieldCheck.getId().intValue()))
            .andExpect(jsonPath("$.fieldName").value(DEFAULT_FIELD_NAME))
            .andExpect(jsonPath("$.isFilled").value(DEFAULT_IS_FILLED))
            .andExpect(jsonPath("$.checkDate").value(DEFAULT_CHECK_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingMandatoryFieldCheck() throws Exception {
        // Get the mandatoryFieldCheck
        restMandatoryFieldCheckMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMandatoryFieldCheck() throws Exception {
        // Initialize the database
        insertedMandatoryFieldCheck = mandatoryFieldCheckRepository.saveAndFlush(mandatoryFieldCheck);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mandatoryFieldCheck
        MandatoryFieldCheck updatedMandatoryFieldCheck = mandatoryFieldCheckRepository.findById(mandatoryFieldCheck.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedMandatoryFieldCheck are not directly saved in db
        em.detach(updatedMandatoryFieldCheck);
        updatedMandatoryFieldCheck.fieldName(UPDATED_FIELD_NAME).isFilled(UPDATED_IS_FILLED).checkDate(UPDATED_CHECK_DATE);

        restMandatoryFieldCheckMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedMandatoryFieldCheck.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedMandatoryFieldCheck))
            )
            .andExpect(status().isOk());

        // Validate the MandatoryFieldCheck in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMandatoryFieldCheckToMatchAllProperties(updatedMandatoryFieldCheck);
    }

    @Test
    @Transactional
    void putNonExistingMandatoryFieldCheck() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mandatoryFieldCheck.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMandatoryFieldCheckMockMvc
            .perform(
                put(ENTITY_API_URL_ID, mandatoryFieldCheck.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(mandatoryFieldCheck))
            )
            .andExpect(status().isBadRequest());

        // Validate the MandatoryFieldCheck in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMandatoryFieldCheck() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mandatoryFieldCheck.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMandatoryFieldCheckMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(mandatoryFieldCheck))
            )
            .andExpect(status().isBadRequest());

        // Validate the MandatoryFieldCheck in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMandatoryFieldCheck() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mandatoryFieldCheck.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMandatoryFieldCheckMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(mandatoryFieldCheck)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MandatoryFieldCheck in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMandatoryFieldCheckWithPatch() throws Exception {
        // Initialize the database
        insertedMandatoryFieldCheck = mandatoryFieldCheckRepository.saveAndFlush(mandatoryFieldCheck);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mandatoryFieldCheck using partial update
        MandatoryFieldCheck partialUpdatedMandatoryFieldCheck = new MandatoryFieldCheck();
        partialUpdatedMandatoryFieldCheck.setId(mandatoryFieldCheck.getId());

        partialUpdatedMandatoryFieldCheck.fieldName(UPDATED_FIELD_NAME).isFilled(UPDATED_IS_FILLED);

        restMandatoryFieldCheckMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMandatoryFieldCheck.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMandatoryFieldCheck))
            )
            .andExpect(status().isOk());

        // Validate the MandatoryFieldCheck in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMandatoryFieldCheckUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMandatoryFieldCheck, mandatoryFieldCheck),
            getPersistedMandatoryFieldCheck(mandatoryFieldCheck)
        );
    }

    @Test
    @Transactional
    void fullUpdateMandatoryFieldCheckWithPatch() throws Exception {
        // Initialize the database
        insertedMandatoryFieldCheck = mandatoryFieldCheckRepository.saveAndFlush(mandatoryFieldCheck);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mandatoryFieldCheck using partial update
        MandatoryFieldCheck partialUpdatedMandatoryFieldCheck = new MandatoryFieldCheck();
        partialUpdatedMandatoryFieldCheck.setId(mandatoryFieldCheck.getId());

        partialUpdatedMandatoryFieldCheck.fieldName(UPDATED_FIELD_NAME).isFilled(UPDATED_IS_FILLED).checkDate(UPDATED_CHECK_DATE);

        restMandatoryFieldCheckMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMandatoryFieldCheck.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMandatoryFieldCheck))
            )
            .andExpect(status().isOk());

        // Validate the MandatoryFieldCheck in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMandatoryFieldCheckUpdatableFieldsEquals(
            partialUpdatedMandatoryFieldCheck,
            getPersistedMandatoryFieldCheck(partialUpdatedMandatoryFieldCheck)
        );
    }

    @Test
    @Transactional
    void patchNonExistingMandatoryFieldCheck() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mandatoryFieldCheck.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMandatoryFieldCheckMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, mandatoryFieldCheck.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(mandatoryFieldCheck))
            )
            .andExpect(status().isBadRequest());

        // Validate the MandatoryFieldCheck in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMandatoryFieldCheck() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mandatoryFieldCheck.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMandatoryFieldCheckMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(mandatoryFieldCheck))
            )
            .andExpect(status().isBadRequest());

        // Validate the MandatoryFieldCheck in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMandatoryFieldCheck() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mandatoryFieldCheck.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMandatoryFieldCheckMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(mandatoryFieldCheck)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MandatoryFieldCheck in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMandatoryFieldCheck() throws Exception {
        // Initialize the database
        insertedMandatoryFieldCheck = mandatoryFieldCheckRepository.saveAndFlush(mandatoryFieldCheck);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the mandatoryFieldCheck
        restMandatoryFieldCheckMockMvc
            .perform(delete(ENTITY_API_URL_ID, mandatoryFieldCheck.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return mandatoryFieldCheckRepository.count();
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

    protected MandatoryFieldCheck getPersistedMandatoryFieldCheck(MandatoryFieldCheck mandatoryFieldCheck) {
        return mandatoryFieldCheckRepository.findById(mandatoryFieldCheck.getId()).orElseThrow();
    }

    protected void assertPersistedMandatoryFieldCheckToMatchAllProperties(MandatoryFieldCheck expectedMandatoryFieldCheck) {
        assertMandatoryFieldCheckAllPropertiesEquals(
            expectedMandatoryFieldCheck,
            getPersistedMandatoryFieldCheck(expectedMandatoryFieldCheck)
        );
    }

    protected void assertPersistedMandatoryFieldCheckToMatchUpdatableProperties(MandatoryFieldCheck expectedMandatoryFieldCheck) {
        assertMandatoryFieldCheckAllUpdatablePropertiesEquals(
            expectedMandatoryFieldCheck,
            getPersistedMandatoryFieldCheck(expectedMandatoryFieldCheck)
        );
    }
}
