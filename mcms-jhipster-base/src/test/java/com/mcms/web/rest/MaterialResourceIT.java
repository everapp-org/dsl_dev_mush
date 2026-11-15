package com.mcms.web.rest;

import static com.mcms.domain.MaterialAsserts.*;
import static com.mcms.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mcms.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcms.IntegrationTest;
import com.mcms.domain.Material;
import com.mcms.domain.enumeration.MaterialCategory;
import com.mcms.domain.enumeration.UnitOfMeasure;
import com.mcms.repository.MaterialRepository;
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
 * Integration tests for the {@link MaterialResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class MaterialResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final MaterialCategory DEFAULT_CATEGORY = MaterialCategory.SPAWN;
    private static final MaterialCategory UPDATED_CATEGORY = MaterialCategory.SUBSTRATE;

    private static final UnitOfMeasure DEFAULT_DEFAULT_UNIT = UnitOfMeasure.KG;
    private static final UnitOfMeasure UPDATED_DEFAULT_UNIT = UnitOfMeasure.GRAM;

    private static final BigDecimal DEFAULT_MINIMUM_STOCK_LEVEL = new BigDecimal(1);
    private static final BigDecimal UPDATED_MINIMUM_STOCK_LEVEL = new BigDecimal(2);

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/materials";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMaterialMockMvc;

    private Material material;

    private Material insertedMaterial;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Material createEntity() {
        return new Material()
            .code(DEFAULT_CODE)
            .name(DEFAULT_NAME)
            .category(DEFAULT_CATEGORY)
            .defaultUnit(DEFAULT_DEFAULT_UNIT)
            .minimumStockLevel(DEFAULT_MINIMUM_STOCK_LEVEL)
            .description(DEFAULT_DESCRIPTION)
            .active(DEFAULT_ACTIVE)
            .note(DEFAULT_NOTE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Material createUpdatedEntity() {
        return new Material()
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .category(UPDATED_CATEGORY)
            .defaultUnit(UPDATED_DEFAULT_UNIT)
            .minimumStockLevel(UPDATED_MINIMUM_STOCK_LEVEL)
            .description(UPDATED_DESCRIPTION)
            .active(UPDATED_ACTIVE)
            .note(UPDATED_NOTE);
    }

    @BeforeEach
    void initTest() {
        material = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedMaterial != null) {
            materialRepository.delete(insertedMaterial);
            insertedMaterial = null;
        }
    }

    @Test
    @Transactional
    void createMaterial() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Material
        var returnedMaterial = om.readValue(
            restMaterialMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(material)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Material.class
        );

        // Validate the Material in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertMaterialUpdatableFieldsEquals(returnedMaterial, getPersistedMaterial(returnedMaterial));

        insertedMaterial = returnedMaterial;
    }

    @Test
    @Transactional
    void createMaterialWithExistingId() throws Exception {
        // Create the Material with an existing ID
        material.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMaterialMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(material)))
            .andExpect(status().isBadRequest());

        // Validate the Material in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        material.setCode(null);

        // Create the Material, which fails.

        restMaterialMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(material)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        material.setName(null);

        // Create the Material, which fails.

        restMaterialMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(material)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCategoryIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        material.setCategory(null);

        // Create the Material, which fails.

        restMaterialMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(material)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDefaultUnitIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        material.setDefaultUnit(null);

        // Create the Material, which fails.

        restMaterialMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(material)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        material.setActive(null);

        // Create the Material, which fails.

        restMaterialMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(material)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllMaterials() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get all the materialList
        restMaterialMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(material.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY.toString())))
            .andExpect(jsonPath("$.[*].defaultUnit").value(hasItem(DEFAULT_DEFAULT_UNIT.toString())))
            .andExpect(jsonPath("$.[*].minimumStockLevel").value(hasItem(sameNumber(DEFAULT_MINIMUM_STOCK_LEVEL))))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)));
    }

    @Test
    @Transactional
    void getMaterial() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        // Get the material
        restMaterialMockMvc
            .perform(get(ENTITY_API_URL_ID, material.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(material.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.category").value(DEFAULT_CATEGORY.toString()))
            .andExpect(jsonPath("$.defaultUnit").value(DEFAULT_DEFAULT_UNIT.toString()))
            .andExpect(jsonPath("$.minimumStockLevel").value(sameNumber(DEFAULT_MINIMUM_STOCK_LEVEL)))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE));
    }

    @Test
    @Transactional
    void getNonExistingMaterial() throws Exception {
        // Get the material
        restMaterialMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMaterial() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the material
        Material updatedMaterial = materialRepository.findById(material.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedMaterial are not directly saved in db
        em.detach(updatedMaterial);
        updatedMaterial
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .category(UPDATED_CATEGORY)
            .defaultUnit(UPDATED_DEFAULT_UNIT)
            .minimumStockLevel(UPDATED_MINIMUM_STOCK_LEVEL)
            .description(UPDATED_DESCRIPTION)
            .active(UPDATED_ACTIVE)
            .note(UPDATED_NOTE);

        restMaterialMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedMaterial.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedMaterial))
            )
            .andExpect(status().isOk());

        // Validate the Material in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMaterialToMatchAllProperties(updatedMaterial);
    }

    @Test
    @Transactional
    void putNonExistingMaterial() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        material.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMaterialMockMvc
            .perform(
                put(ENTITY_API_URL_ID, material.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(material))
            )
            .andExpect(status().isBadRequest());

        // Validate the Material in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMaterial() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        material.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMaterialMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(material))
            )
            .andExpect(status().isBadRequest());

        // Validate the Material in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMaterial() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        material.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMaterialMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(material)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Material in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMaterialWithPatch() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the material using partial update
        Material partialUpdatedMaterial = new Material();
        partialUpdatedMaterial.setId(material.getId());

        partialUpdatedMaterial
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .category(UPDATED_CATEGORY)
            .minimumStockLevel(UPDATED_MINIMUM_STOCK_LEVEL)
            .description(UPDATED_DESCRIPTION)
            .note(UPDATED_NOTE);

        restMaterialMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMaterial.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMaterial))
            )
            .andExpect(status().isOk());

        // Validate the Material in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMaterialUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedMaterial, material), getPersistedMaterial(material));
    }

    @Test
    @Transactional
    void fullUpdateMaterialWithPatch() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the material using partial update
        Material partialUpdatedMaterial = new Material();
        partialUpdatedMaterial.setId(material.getId());

        partialUpdatedMaterial
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .category(UPDATED_CATEGORY)
            .defaultUnit(UPDATED_DEFAULT_UNIT)
            .minimumStockLevel(UPDATED_MINIMUM_STOCK_LEVEL)
            .description(UPDATED_DESCRIPTION)
            .active(UPDATED_ACTIVE)
            .note(UPDATED_NOTE);

        restMaterialMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMaterial.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMaterial))
            )
            .andExpect(status().isOk());

        // Validate the Material in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMaterialUpdatableFieldsEquals(partialUpdatedMaterial, getPersistedMaterial(partialUpdatedMaterial));
    }

    @Test
    @Transactional
    void patchNonExistingMaterial() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        material.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMaterialMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, material.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(material))
            )
            .andExpect(status().isBadRequest());

        // Validate the Material in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMaterial() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        material.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMaterialMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(material))
            )
            .andExpect(status().isBadRequest());

        // Validate the Material in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMaterial() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        material.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMaterialMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(material)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Material in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMaterial() throws Exception {
        // Initialize the database
        insertedMaterial = materialRepository.saveAndFlush(material);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the material
        restMaterialMockMvc
            .perform(delete(ENTITY_API_URL_ID, material.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return materialRepository.count();
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

    protected Material getPersistedMaterial(Material material) {
        return materialRepository.findById(material.getId()).orElseThrow();
    }

    protected void assertPersistedMaterialToMatchAllProperties(Material expectedMaterial) {
        assertMaterialAllPropertiesEquals(expectedMaterial, getPersistedMaterial(expectedMaterial));
    }

    protected void assertPersistedMaterialToMatchUpdatableProperties(Material expectedMaterial) {
        assertMaterialAllUpdatablePropertiesEquals(expectedMaterial, getPersistedMaterial(expectedMaterial));
    }
}
