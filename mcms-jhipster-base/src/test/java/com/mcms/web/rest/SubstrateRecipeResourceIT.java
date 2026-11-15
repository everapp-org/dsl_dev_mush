package com.mcms.web.rest;

import static com.mcms.domain.SubstrateRecipeAsserts.*;
import static com.mcms.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mcms.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcms.IntegrationTest;
import com.mcms.domain.SubstrateRecipe;
import com.mcms.domain.enumeration.SubstrateBase;
import com.mcms.repository.SubstrateRecipeRepository;
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
 * Integration tests for the {@link SubstrateRecipeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SubstrateRecipeResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_VERSION = "AAAAAAAAAA";
    private static final String UPDATED_VERSION = "BBBBBBBBBB";

    private static final SubstrateBase DEFAULT_BASE_TYPE = SubstrateBase.WHEAT_STRAW;
    private static final SubstrateBase UPDATED_BASE_TYPE = SubstrateBase.BARLEY_STRAW;

    private static final String DEFAULT_COMPOSITION_DETAIL = "AAAAAAAAAA";
    private static final String UPDATED_COMPOSITION_DETAIL = "BBBBBBBBBB";

    private static final String DEFAULT_STERILIZATION_METHOD = "AAAAAAAAAA";
    private static final String UPDATED_STERILIZATION_METHOD = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_MOISTURE_TARGET_PERCENT = new BigDecimal(1);
    private static final BigDecimal UPDATED_MOISTURE_TARGET_PERCENT = new BigDecimal(2);

    private static final BigDecimal DEFAULT_PH_TARGET = new BigDecimal(1);
    private static final BigDecimal UPDATED_PH_TARGET = new BigDecimal(2);

    private static final String DEFAULT_SUPPLEMENT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_SUPPLEMENT_NOTES = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/substrate-recipes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SubstrateRecipeRepository substrateRecipeRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSubstrateRecipeMockMvc;

    private SubstrateRecipe substrateRecipe;

    private SubstrateRecipe insertedSubstrateRecipe;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SubstrateRecipe createEntity() {
        return new SubstrateRecipe()
            .name(DEFAULT_NAME)
            .version(DEFAULT_VERSION)
            .baseType(DEFAULT_BASE_TYPE)
            .compositionDetail(DEFAULT_COMPOSITION_DETAIL)
            .sterilizationMethod(DEFAULT_STERILIZATION_METHOD)
            .moistureTargetPercent(DEFAULT_MOISTURE_TARGET_PERCENT)
            .phTarget(DEFAULT_PH_TARGET)
            .supplementNotes(DEFAULT_SUPPLEMENT_NOTES)
            .active(DEFAULT_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SubstrateRecipe createUpdatedEntity() {
        return new SubstrateRecipe()
            .name(UPDATED_NAME)
            .version(UPDATED_VERSION)
            .baseType(UPDATED_BASE_TYPE)
            .compositionDetail(UPDATED_COMPOSITION_DETAIL)
            .sterilizationMethod(UPDATED_STERILIZATION_METHOD)
            .moistureTargetPercent(UPDATED_MOISTURE_TARGET_PERCENT)
            .phTarget(UPDATED_PH_TARGET)
            .supplementNotes(UPDATED_SUPPLEMENT_NOTES)
            .active(UPDATED_ACTIVE);
    }

    @BeforeEach
    void initTest() {
        substrateRecipe = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedSubstrateRecipe != null) {
            substrateRecipeRepository.delete(insertedSubstrateRecipe);
            insertedSubstrateRecipe = null;
        }
    }

    @Test
    @Transactional
    void createSubstrateRecipe() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SubstrateRecipe
        var returnedSubstrateRecipe = om.readValue(
            restSubstrateRecipeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(substrateRecipe)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SubstrateRecipe.class
        );

        // Validate the SubstrateRecipe in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertSubstrateRecipeUpdatableFieldsEquals(returnedSubstrateRecipe, getPersistedSubstrateRecipe(returnedSubstrateRecipe));

        insertedSubstrateRecipe = returnedSubstrateRecipe;
    }

    @Test
    @Transactional
    void createSubstrateRecipeWithExistingId() throws Exception {
        // Create the SubstrateRecipe with an existing ID
        substrateRecipe.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSubstrateRecipeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(substrateRecipe)))
            .andExpect(status().isBadRequest());

        // Validate the SubstrateRecipe in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        substrateRecipe.setName(null);

        // Create the SubstrateRecipe, which fails.

        restSubstrateRecipeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(substrateRecipe)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkVersionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        substrateRecipe.setVersion(null);

        // Create the SubstrateRecipe, which fails.

        restSubstrateRecipeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(substrateRecipe)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkBaseTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        substrateRecipe.setBaseType(null);

        // Create the SubstrateRecipe, which fails.

        restSubstrateRecipeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(substrateRecipe)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        substrateRecipe.setActive(null);

        // Create the SubstrateRecipe, which fails.

        restSubstrateRecipeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(substrateRecipe)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSubstrateRecipes() throws Exception {
        // Initialize the database
        insertedSubstrateRecipe = substrateRecipeRepository.saveAndFlush(substrateRecipe);

        // Get all the substrateRecipeList
        restSubstrateRecipeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(substrateRecipe.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION)))
            .andExpect(jsonPath("$.[*].baseType").value(hasItem(DEFAULT_BASE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].compositionDetail").value(hasItem(DEFAULT_COMPOSITION_DETAIL)))
            .andExpect(jsonPath("$.[*].sterilizationMethod").value(hasItem(DEFAULT_STERILIZATION_METHOD)))
            .andExpect(jsonPath("$.[*].moistureTargetPercent").value(hasItem(sameNumber(DEFAULT_MOISTURE_TARGET_PERCENT))))
            .andExpect(jsonPath("$.[*].phTarget").value(hasItem(sameNumber(DEFAULT_PH_TARGET))))
            .andExpect(jsonPath("$.[*].supplementNotes").value(hasItem(DEFAULT_SUPPLEMENT_NOTES)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));
    }

    @Test
    @Transactional
    void getSubstrateRecipe() throws Exception {
        // Initialize the database
        insertedSubstrateRecipe = substrateRecipeRepository.saveAndFlush(substrateRecipe);

        // Get the substrateRecipe
        restSubstrateRecipeMockMvc
            .perform(get(ENTITY_API_URL_ID, substrateRecipe.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(substrateRecipe.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.version").value(DEFAULT_VERSION))
            .andExpect(jsonPath("$.baseType").value(DEFAULT_BASE_TYPE.toString()))
            .andExpect(jsonPath("$.compositionDetail").value(DEFAULT_COMPOSITION_DETAIL))
            .andExpect(jsonPath("$.sterilizationMethod").value(DEFAULT_STERILIZATION_METHOD))
            .andExpect(jsonPath("$.moistureTargetPercent").value(sameNumber(DEFAULT_MOISTURE_TARGET_PERCENT)))
            .andExpect(jsonPath("$.phTarget").value(sameNumber(DEFAULT_PH_TARGET)))
            .andExpect(jsonPath("$.supplementNotes").value(DEFAULT_SUPPLEMENT_NOTES))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE));
    }

    @Test
    @Transactional
    void getNonExistingSubstrateRecipe() throws Exception {
        // Get the substrateRecipe
        restSubstrateRecipeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSubstrateRecipe() throws Exception {
        // Initialize the database
        insertedSubstrateRecipe = substrateRecipeRepository.saveAndFlush(substrateRecipe);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the substrateRecipe
        SubstrateRecipe updatedSubstrateRecipe = substrateRecipeRepository.findById(substrateRecipe.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSubstrateRecipe are not directly saved in db
        em.detach(updatedSubstrateRecipe);
        updatedSubstrateRecipe
            .name(UPDATED_NAME)
            .version(UPDATED_VERSION)
            .baseType(UPDATED_BASE_TYPE)
            .compositionDetail(UPDATED_COMPOSITION_DETAIL)
            .sterilizationMethod(UPDATED_STERILIZATION_METHOD)
            .moistureTargetPercent(UPDATED_MOISTURE_TARGET_PERCENT)
            .phTarget(UPDATED_PH_TARGET)
            .supplementNotes(UPDATED_SUPPLEMENT_NOTES)
            .active(UPDATED_ACTIVE);

        restSubstrateRecipeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSubstrateRecipe.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedSubstrateRecipe))
            )
            .andExpect(status().isOk());

        // Validate the SubstrateRecipe in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSubstrateRecipeToMatchAllProperties(updatedSubstrateRecipe);
    }

    @Test
    @Transactional
    void putNonExistingSubstrateRecipe() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        substrateRecipe.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSubstrateRecipeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, substrateRecipe.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(substrateRecipe))
            )
            .andExpect(status().isBadRequest());

        // Validate the SubstrateRecipe in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSubstrateRecipe() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        substrateRecipe.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubstrateRecipeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(substrateRecipe))
            )
            .andExpect(status().isBadRequest());

        // Validate the SubstrateRecipe in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSubstrateRecipe() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        substrateRecipe.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubstrateRecipeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(substrateRecipe)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SubstrateRecipe in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSubstrateRecipeWithPatch() throws Exception {
        // Initialize the database
        insertedSubstrateRecipe = substrateRecipeRepository.saveAndFlush(substrateRecipe);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the substrateRecipe using partial update
        SubstrateRecipe partialUpdatedSubstrateRecipe = new SubstrateRecipe();
        partialUpdatedSubstrateRecipe.setId(substrateRecipe.getId());

        partialUpdatedSubstrateRecipe
            .version(UPDATED_VERSION)
            .baseType(UPDATED_BASE_TYPE)
            .compositionDetail(UPDATED_COMPOSITION_DETAIL)
            .sterilizationMethod(UPDATED_STERILIZATION_METHOD)
            .phTarget(UPDATED_PH_TARGET)
            .supplementNotes(UPDATED_SUPPLEMENT_NOTES);

        restSubstrateRecipeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSubstrateRecipe.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSubstrateRecipe))
            )
            .andExpect(status().isOk());

        // Validate the SubstrateRecipe in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSubstrateRecipeUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSubstrateRecipe, substrateRecipe),
            getPersistedSubstrateRecipe(substrateRecipe)
        );
    }

    @Test
    @Transactional
    void fullUpdateSubstrateRecipeWithPatch() throws Exception {
        // Initialize the database
        insertedSubstrateRecipe = substrateRecipeRepository.saveAndFlush(substrateRecipe);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the substrateRecipe using partial update
        SubstrateRecipe partialUpdatedSubstrateRecipe = new SubstrateRecipe();
        partialUpdatedSubstrateRecipe.setId(substrateRecipe.getId());

        partialUpdatedSubstrateRecipe
            .name(UPDATED_NAME)
            .version(UPDATED_VERSION)
            .baseType(UPDATED_BASE_TYPE)
            .compositionDetail(UPDATED_COMPOSITION_DETAIL)
            .sterilizationMethod(UPDATED_STERILIZATION_METHOD)
            .moistureTargetPercent(UPDATED_MOISTURE_TARGET_PERCENT)
            .phTarget(UPDATED_PH_TARGET)
            .supplementNotes(UPDATED_SUPPLEMENT_NOTES)
            .active(UPDATED_ACTIVE);

        restSubstrateRecipeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSubstrateRecipe.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSubstrateRecipe))
            )
            .andExpect(status().isOk());

        // Validate the SubstrateRecipe in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSubstrateRecipeUpdatableFieldsEquals(
            partialUpdatedSubstrateRecipe,
            getPersistedSubstrateRecipe(partialUpdatedSubstrateRecipe)
        );
    }

    @Test
    @Transactional
    void patchNonExistingSubstrateRecipe() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        substrateRecipe.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSubstrateRecipeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, substrateRecipe.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(substrateRecipe))
            )
            .andExpect(status().isBadRequest());

        // Validate the SubstrateRecipe in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSubstrateRecipe() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        substrateRecipe.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubstrateRecipeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(substrateRecipe))
            )
            .andExpect(status().isBadRequest());

        // Validate the SubstrateRecipe in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSubstrateRecipe() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        substrateRecipe.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubstrateRecipeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(substrateRecipe)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SubstrateRecipe in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSubstrateRecipe() throws Exception {
        // Initialize the database
        insertedSubstrateRecipe = substrateRecipeRepository.saveAndFlush(substrateRecipe);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the substrateRecipe
        restSubstrateRecipeMockMvc
            .perform(delete(ENTITY_API_URL_ID, substrateRecipe.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return substrateRecipeRepository.count();
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

    protected SubstrateRecipe getPersistedSubstrateRecipe(SubstrateRecipe substrateRecipe) {
        return substrateRecipeRepository.findById(substrateRecipe.getId()).orElseThrow();
    }

    protected void assertPersistedSubstrateRecipeToMatchAllProperties(SubstrateRecipe expectedSubstrateRecipe) {
        assertSubstrateRecipeAllPropertiesEquals(expectedSubstrateRecipe, getPersistedSubstrateRecipe(expectedSubstrateRecipe));
    }

    protected void assertPersistedSubstrateRecipeToMatchUpdatableProperties(SubstrateRecipe expectedSubstrateRecipe) {
        assertSubstrateRecipeAllUpdatablePropertiesEquals(expectedSubstrateRecipe, getPersistedSubstrateRecipe(expectedSubstrateRecipe));
    }
}
