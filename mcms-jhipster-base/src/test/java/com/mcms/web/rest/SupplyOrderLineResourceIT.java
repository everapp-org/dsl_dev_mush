package com.mcms.web.rest;

import static com.mcms.domain.SupplyOrderLineAsserts.*;
import static com.mcms.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mcms.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcms.IntegrationTest;
import com.mcms.domain.Material;
import com.mcms.domain.SupplyOrder;
import com.mcms.domain.SupplyOrderLine;
import com.mcms.domain.enumeration.UnitOfMeasure;
import com.mcms.repository.SupplyOrderLineRepository;
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
 * Integration tests for the {@link SupplyOrderLineResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SupplyOrderLineResourceIT {

    private static final Integer DEFAULT_LINE_NUMBER = 1;
    private static final Integer UPDATED_LINE_NUMBER = 2;

    private static final String DEFAULT_ITEM_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_ITEM_DESCRIPTION = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_QUANTITY_ORDERED = new BigDecimal(1);
    private static final BigDecimal UPDATED_QUANTITY_ORDERED = new BigDecimal(2);

    private static final BigDecimal DEFAULT_QUANTITY_RECEIVED = new BigDecimal(1);
    private static final BigDecimal UPDATED_QUANTITY_RECEIVED = new BigDecimal(2);

    private static final UnitOfMeasure DEFAULT_UNIT = UnitOfMeasure.KG;
    private static final UnitOfMeasure UPDATED_UNIT = UnitOfMeasure.GRAM;

    private static final BigDecimal DEFAULT_UNIT_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_UNIT_PRICE = new BigDecimal(2);

    private static final BigDecimal DEFAULT_LINE_TOTAL = new BigDecimal(1);
    private static final BigDecimal UPDATED_LINE_TOTAL = new BigDecimal(2);

    private static final String DEFAULT_LOT_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_LOT_NUMBER = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_EXPIRY_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_EXPIRY_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_QUALITY_ON_RECEIPT = "AAAAAAAAAA";
    private static final String UPDATED_QUALITY_ON_RECEIPT = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_RECEIVED = false;
    private static final Boolean UPDATED_IS_RECEIVED = true;

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/supply-order-lines";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SupplyOrderLineRepository supplyOrderLineRepository;

    @Mock
    private SupplyOrderLineRepository supplyOrderLineRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSupplyOrderLineMockMvc;

    private SupplyOrderLine supplyOrderLine;

    private SupplyOrderLine insertedSupplyOrderLine;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SupplyOrderLine createEntity(EntityManager em) {
        SupplyOrderLine supplyOrderLine = new SupplyOrderLine()
            .lineNumber(DEFAULT_LINE_NUMBER)
            .itemDescription(DEFAULT_ITEM_DESCRIPTION)
            .quantityOrdered(DEFAULT_QUANTITY_ORDERED)
            .quantityReceived(DEFAULT_QUANTITY_RECEIVED)
            .unit(DEFAULT_UNIT)
            .unitPrice(DEFAULT_UNIT_PRICE)
            .lineTotal(DEFAULT_LINE_TOTAL)
            .lotNumber(DEFAULT_LOT_NUMBER)
            .expiryDate(DEFAULT_EXPIRY_DATE)
            .qualityOnReceipt(DEFAULT_QUALITY_ON_RECEIPT)
            .isReceived(DEFAULT_IS_RECEIVED)
            .note(DEFAULT_NOTE);
        // Add required entity
        SupplyOrder supplyOrder;
        if (TestUtil.findAll(em, SupplyOrder.class).isEmpty()) {
            supplyOrder = SupplyOrderResourceIT.createEntity(em);
            em.persist(supplyOrder);
            em.flush();
        } else {
            supplyOrder = TestUtil.findAll(em, SupplyOrder.class).get(0);
        }
        supplyOrderLine.setSupplyOrder(supplyOrder);
        // Add required entity
        Material material;
        if (TestUtil.findAll(em, Material.class).isEmpty()) {
            material = MaterialResourceIT.createEntity();
            em.persist(material);
            em.flush();
        } else {
            material = TestUtil.findAll(em, Material.class).get(0);
        }
        supplyOrderLine.setMaterial(material);
        return supplyOrderLine;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SupplyOrderLine createUpdatedEntity(EntityManager em) {
        SupplyOrderLine updatedSupplyOrderLine = new SupplyOrderLine()
            .lineNumber(UPDATED_LINE_NUMBER)
            .itemDescription(UPDATED_ITEM_DESCRIPTION)
            .quantityOrdered(UPDATED_QUANTITY_ORDERED)
            .quantityReceived(UPDATED_QUANTITY_RECEIVED)
            .unit(UPDATED_UNIT)
            .unitPrice(UPDATED_UNIT_PRICE)
            .lineTotal(UPDATED_LINE_TOTAL)
            .lotNumber(UPDATED_LOT_NUMBER)
            .expiryDate(UPDATED_EXPIRY_DATE)
            .qualityOnReceipt(UPDATED_QUALITY_ON_RECEIPT)
            .isReceived(UPDATED_IS_RECEIVED)
            .note(UPDATED_NOTE);
        // Add required entity
        SupplyOrder supplyOrder;
        if (TestUtil.findAll(em, SupplyOrder.class).isEmpty()) {
            supplyOrder = SupplyOrderResourceIT.createUpdatedEntity(em);
            em.persist(supplyOrder);
            em.flush();
        } else {
            supplyOrder = TestUtil.findAll(em, SupplyOrder.class).get(0);
        }
        updatedSupplyOrderLine.setSupplyOrder(supplyOrder);
        // Add required entity
        Material material;
        if (TestUtil.findAll(em, Material.class).isEmpty()) {
            material = MaterialResourceIT.createUpdatedEntity();
            em.persist(material);
            em.flush();
        } else {
            material = TestUtil.findAll(em, Material.class).get(0);
        }
        updatedSupplyOrderLine.setMaterial(material);
        return updatedSupplyOrderLine;
    }

    @BeforeEach
    void initTest() {
        supplyOrderLine = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedSupplyOrderLine != null) {
            supplyOrderLineRepository.delete(insertedSupplyOrderLine);
            insertedSupplyOrderLine = null;
        }
    }

    @Test
    @Transactional
    void createSupplyOrderLine() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SupplyOrderLine
        var returnedSupplyOrderLine = om.readValue(
            restSupplyOrderLineMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(supplyOrderLine)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SupplyOrderLine.class
        );

        // Validate the SupplyOrderLine in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertSupplyOrderLineUpdatableFieldsEquals(returnedSupplyOrderLine, getPersistedSupplyOrderLine(returnedSupplyOrderLine));

        insertedSupplyOrderLine = returnedSupplyOrderLine;
    }

    @Test
    @Transactional
    void createSupplyOrderLineWithExistingId() throws Exception {
        // Create the SupplyOrderLine with an existing ID
        supplyOrderLine.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSupplyOrderLineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(supplyOrderLine)))
            .andExpect(status().isBadRequest());

        // Validate the SupplyOrderLine in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkLineNumberIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        supplyOrderLine.setLineNumber(null);

        // Create the SupplyOrderLine, which fails.

        restSupplyOrderLineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(supplyOrderLine)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkItemDescriptionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        supplyOrderLine.setItemDescription(null);

        // Create the SupplyOrderLine, which fails.

        restSupplyOrderLineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(supplyOrderLine)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkQuantityOrderedIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        supplyOrderLine.setQuantityOrdered(null);

        // Create the SupplyOrderLine, which fails.

        restSupplyOrderLineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(supplyOrderLine)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUnitIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        supplyOrderLine.setUnit(null);

        // Create the SupplyOrderLine, which fails.

        restSupplyOrderLineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(supplyOrderLine)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUnitPriceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        supplyOrderLine.setUnitPrice(null);

        // Create the SupplyOrderLine, which fails.

        restSupplyOrderLineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(supplyOrderLine)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLineTotalIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        supplyOrderLine.setLineTotal(null);

        // Create the SupplyOrderLine, which fails.

        restSupplyOrderLineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(supplyOrderLine)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIsReceivedIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        supplyOrderLine.setIsReceived(null);

        // Create the SupplyOrderLine, which fails.

        restSupplyOrderLineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(supplyOrderLine)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSupplyOrderLines() throws Exception {
        // Initialize the database
        insertedSupplyOrderLine = supplyOrderLineRepository.saveAndFlush(supplyOrderLine);

        // Get all the supplyOrderLineList
        restSupplyOrderLineMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(supplyOrderLine.getId().intValue())))
            .andExpect(jsonPath("$.[*].lineNumber").value(hasItem(DEFAULT_LINE_NUMBER)))
            .andExpect(jsonPath("$.[*].itemDescription").value(hasItem(DEFAULT_ITEM_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].quantityOrdered").value(hasItem(sameNumber(DEFAULT_QUANTITY_ORDERED))))
            .andExpect(jsonPath("$.[*].quantityReceived").value(hasItem(sameNumber(DEFAULT_QUANTITY_RECEIVED))))
            .andExpect(jsonPath("$.[*].unit").value(hasItem(DEFAULT_UNIT.toString())))
            .andExpect(jsonPath("$.[*].unitPrice").value(hasItem(sameNumber(DEFAULT_UNIT_PRICE))))
            .andExpect(jsonPath("$.[*].lineTotal").value(hasItem(sameNumber(DEFAULT_LINE_TOTAL))))
            .andExpect(jsonPath("$.[*].lotNumber").value(hasItem(DEFAULT_LOT_NUMBER)))
            .andExpect(jsonPath("$.[*].expiryDate").value(hasItem(DEFAULT_EXPIRY_DATE.toString())))
            .andExpect(jsonPath("$.[*].qualityOnReceipt").value(hasItem(DEFAULT_QUALITY_ON_RECEIPT)))
            .andExpect(jsonPath("$.[*].isReceived").value(hasItem(DEFAULT_IS_RECEIVED)))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSupplyOrderLinesWithEagerRelationshipsIsEnabled() throws Exception {
        when(supplyOrderLineRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSupplyOrderLineMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(supplyOrderLineRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSupplyOrderLinesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(supplyOrderLineRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSupplyOrderLineMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(supplyOrderLineRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getSupplyOrderLine() throws Exception {
        // Initialize the database
        insertedSupplyOrderLine = supplyOrderLineRepository.saveAndFlush(supplyOrderLine);

        // Get the supplyOrderLine
        restSupplyOrderLineMockMvc
            .perform(get(ENTITY_API_URL_ID, supplyOrderLine.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(supplyOrderLine.getId().intValue()))
            .andExpect(jsonPath("$.lineNumber").value(DEFAULT_LINE_NUMBER))
            .andExpect(jsonPath("$.itemDescription").value(DEFAULT_ITEM_DESCRIPTION))
            .andExpect(jsonPath("$.quantityOrdered").value(sameNumber(DEFAULT_QUANTITY_ORDERED)))
            .andExpect(jsonPath("$.quantityReceived").value(sameNumber(DEFAULT_QUANTITY_RECEIVED)))
            .andExpect(jsonPath("$.unit").value(DEFAULT_UNIT.toString()))
            .andExpect(jsonPath("$.unitPrice").value(sameNumber(DEFAULT_UNIT_PRICE)))
            .andExpect(jsonPath("$.lineTotal").value(sameNumber(DEFAULT_LINE_TOTAL)))
            .andExpect(jsonPath("$.lotNumber").value(DEFAULT_LOT_NUMBER))
            .andExpect(jsonPath("$.expiryDate").value(DEFAULT_EXPIRY_DATE.toString()))
            .andExpect(jsonPath("$.qualityOnReceipt").value(DEFAULT_QUALITY_ON_RECEIPT))
            .andExpect(jsonPath("$.isReceived").value(DEFAULT_IS_RECEIVED))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE));
    }

    @Test
    @Transactional
    void getNonExistingSupplyOrderLine() throws Exception {
        // Get the supplyOrderLine
        restSupplyOrderLineMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSupplyOrderLine() throws Exception {
        // Initialize the database
        insertedSupplyOrderLine = supplyOrderLineRepository.saveAndFlush(supplyOrderLine);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the supplyOrderLine
        SupplyOrderLine updatedSupplyOrderLine = supplyOrderLineRepository.findById(supplyOrderLine.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSupplyOrderLine are not directly saved in db
        em.detach(updatedSupplyOrderLine);
        updatedSupplyOrderLine
            .lineNumber(UPDATED_LINE_NUMBER)
            .itemDescription(UPDATED_ITEM_DESCRIPTION)
            .quantityOrdered(UPDATED_QUANTITY_ORDERED)
            .quantityReceived(UPDATED_QUANTITY_RECEIVED)
            .unit(UPDATED_UNIT)
            .unitPrice(UPDATED_UNIT_PRICE)
            .lineTotal(UPDATED_LINE_TOTAL)
            .lotNumber(UPDATED_LOT_NUMBER)
            .expiryDate(UPDATED_EXPIRY_DATE)
            .qualityOnReceipt(UPDATED_QUALITY_ON_RECEIPT)
            .isReceived(UPDATED_IS_RECEIVED)
            .note(UPDATED_NOTE);

        restSupplyOrderLineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSupplyOrderLine.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedSupplyOrderLine))
            )
            .andExpect(status().isOk());

        // Validate the SupplyOrderLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSupplyOrderLineToMatchAllProperties(updatedSupplyOrderLine);
    }

    @Test
    @Transactional
    void putNonExistingSupplyOrderLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        supplyOrderLine.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSupplyOrderLineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, supplyOrderLine.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(supplyOrderLine))
            )
            .andExpect(status().isBadRequest());

        // Validate the SupplyOrderLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSupplyOrderLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        supplyOrderLine.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSupplyOrderLineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(supplyOrderLine))
            )
            .andExpect(status().isBadRequest());

        // Validate the SupplyOrderLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSupplyOrderLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        supplyOrderLine.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSupplyOrderLineMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(supplyOrderLine)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SupplyOrderLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSupplyOrderLineWithPatch() throws Exception {
        // Initialize the database
        insertedSupplyOrderLine = supplyOrderLineRepository.saveAndFlush(supplyOrderLine);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the supplyOrderLine using partial update
        SupplyOrderLine partialUpdatedSupplyOrderLine = new SupplyOrderLine();
        partialUpdatedSupplyOrderLine.setId(supplyOrderLine.getId());

        partialUpdatedSupplyOrderLine
            .lineNumber(UPDATED_LINE_NUMBER)
            .lineTotal(UPDATED_LINE_TOTAL)
            .lotNumber(UPDATED_LOT_NUMBER)
            .expiryDate(UPDATED_EXPIRY_DATE)
            .qualityOnReceipt(UPDATED_QUALITY_ON_RECEIPT)
            .isReceived(UPDATED_IS_RECEIVED);

        restSupplyOrderLineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSupplyOrderLine.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSupplyOrderLine))
            )
            .andExpect(status().isOk());

        // Validate the SupplyOrderLine in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSupplyOrderLineUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSupplyOrderLine, supplyOrderLine),
            getPersistedSupplyOrderLine(supplyOrderLine)
        );
    }

    @Test
    @Transactional
    void fullUpdateSupplyOrderLineWithPatch() throws Exception {
        // Initialize the database
        insertedSupplyOrderLine = supplyOrderLineRepository.saveAndFlush(supplyOrderLine);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the supplyOrderLine using partial update
        SupplyOrderLine partialUpdatedSupplyOrderLine = new SupplyOrderLine();
        partialUpdatedSupplyOrderLine.setId(supplyOrderLine.getId());

        partialUpdatedSupplyOrderLine
            .lineNumber(UPDATED_LINE_NUMBER)
            .itemDescription(UPDATED_ITEM_DESCRIPTION)
            .quantityOrdered(UPDATED_QUANTITY_ORDERED)
            .quantityReceived(UPDATED_QUANTITY_RECEIVED)
            .unit(UPDATED_UNIT)
            .unitPrice(UPDATED_UNIT_PRICE)
            .lineTotal(UPDATED_LINE_TOTAL)
            .lotNumber(UPDATED_LOT_NUMBER)
            .expiryDate(UPDATED_EXPIRY_DATE)
            .qualityOnReceipt(UPDATED_QUALITY_ON_RECEIPT)
            .isReceived(UPDATED_IS_RECEIVED)
            .note(UPDATED_NOTE);

        restSupplyOrderLineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSupplyOrderLine.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSupplyOrderLine))
            )
            .andExpect(status().isOk());

        // Validate the SupplyOrderLine in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSupplyOrderLineUpdatableFieldsEquals(
            partialUpdatedSupplyOrderLine,
            getPersistedSupplyOrderLine(partialUpdatedSupplyOrderLine)
        );
    }

    @Test
    @Transactional
    void patchNonExistingSupplyOrderLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        supplyOrderLine.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSupplyOrderLineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, supplyOrderLine.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(supplyOrderLine))
            )
            .andExpect(status().isBadRequest());

        // Validate the SupplyOrderLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSupplyOrderLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        supplyOrderLine.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSupplyOrderLineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(supplyOrderLine))
            )
            .andExpect(status().isBadRequest());

        // Validate the SupplyOrderLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSupplyOrderLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        supplyOrderLine.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSupplyOrderLineMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(supplyOrderLine)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SupplyOrderLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSupplyOrderLine() throws Exception {
        // Initialize the database
        insertedSupplyOrderLine = supplyOrderLineRepository.saveAndFlush(supplyOrderLine);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the supplyOrderLine
        restSupplyOrderLineMockMvc
            .perform(delete(ENTITY_API_URL_ID, supplyOrderLine.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return supplyOrderLineRepository.count();
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

    protected SupplyOrderLine getPersistedSupplyOrderLine(SupplyOrderLine supplyOrderLine) {
        return supplyOrderLineRepository.findById(supplyOrderLine.getId()).orElseThrow();
    }

    protected void assertPersistedSupplyOrderLineToMatchAllProperties(SupplyOrderLine expectedSupplyOrderLine) {
        assertSupplyOrderLineAllPropertiesEquals(expectedSupplyOrderLine, getPersistedSupplyOrderLine(expectedSupplyOrderLine));
    }

    protected void assertPersistedSupplyOrderLineToMatchUpdatableProperties(SupplyOrderLine expectedSupplyOrderLine) {
        assertSupplyOrderLineAllUpdatablePropertiesEquals(expectedSupplyOrderLine, getPersistedSupplyOrderLine(expectedSupplyOrderLine));
    }
}
