package com.mcms.web.rest;

import static com.mcms.domain.SalesOrderLineAsserts.*;
import static com.mcms.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mcms.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcms.IntegrationTest;
import com.mcms.domain.Product;
import com.mcms.domain.SalesOrder;
import com.mcms.domain.SalesOrderLine;
import com.mcms.domain.enumeration.QualityGrade;
import com.mcms.domain.enumeration.UnitOfMeasure;
import com.mcms.repository.SalesOrderLineRepository;
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
 * Integration tests for the {@link SalesOrderLineResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SalesOrderLineResourceIT {

    private static final Integer DEFAULT_LINE_NUMBER = 1;
    private static final Integer UPDATED_LINE_NUMBER = 2;

    private static final BigDecimal DEFAULT_WEIGHT_KG = new BigDecimal(1);
    private static final BigDecimal UPDATED_WEIGHT_KG = new BigDecimal(2);

    private static final QualityGrade DEFAULT_GRADE = QualityGrade.A_PREMIUM;
    private static final QualityGrade UPDATED_GRADE = QualityGrade.B_STANDARD;

    private static final Integer DEFAULT_QUANTITY_UNITS = 1;
    private static final Integer UPDATED_QUANTITY_UNITS = 2;

    private static final UnitOfMeasure DEFAULT_UNIT = UnitOfMeasure.KG;
    private static final UnitOfMeasure UPDATED_UNIT = UnitOfMeasure.GRAM;

    private static final BigDecimal DEFAULT_PRICE_PER_KG = new BigDecimal(1);
    private static final BigDecimal UPDATED_PRICE_PER_KG = new BigDecimal(2);

    private static final BigDecimal DEFAULT_LINE_TOTAL = new BigDecimal(1);
    private static final BigDecimal UPDATED_LINE_TOTAL = new BigDecimal(2);

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/sales-order-lines";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SalesOrderLineRepository salesOrderLineRepository;

    @Mock
    private SalesOrderLineRepository salesOrderLineRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSalesOrderLineMockMvc;

    private SalesOrderLine salesOrderLine;

    private SalesOrderLine insertedSalesOrderLine;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SalesOrderLine createEntity(EntityManager em) {
        SalesOrderLine salesOrderLine = new SalesOrderLine()
            .lineNumber(DEFAULT_LINE_NUMBER)
            .weightKg(DEFAULT_WEIGHT_KG)
            .grade(DEFAULT_GRADE)
            .quantityUnits(DEFAULT_QUANTITY_UNITS)
            .unit(DEFAULT_UNIT)
            .pricePerKg(DEFAULT_PRICE_PER_KG)
            .lineTotal(DEFAULT_LINE_TOTAL)
            .note(DEFAULT_NOTE);
        // Add required entity
        SalesOrder salesOrder;
        if (TestUtil.findAll(em, SalesOrder.class).isEmpty()) {
            salesOrder = SalesOrderResourceIT.createEntity(em);
            em.persist(salesOrder);
            em.flush();
        } else {
            salesOrder = TestUtil.findAll(em, SalesOrder.class).get(0);
        }
        salesOrderLine.setSalesOrder(salesOrder);
        // Add required entity
        Product product;
        if (TestUtil.findAll(em, Product.class).isEmpty()) {
            product = ProductResourceIT.createEntity(em);
            em.persist(product);
            em.flush();
        } else {
            product = TestUtil.findAll(em, Product.class).get(0);
        }
        salesOrderLine.setProduct(product);
        return salesOrderLine;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SalesOrderLine createUpdatedEntity(EntityManager em) {
        SalesOrderLine updatedSalesOrderLine = new SalesOrderLine()
            .lineNumber(UPDATED_LINE_NUMBER)
            .weightKg(UPDATED_WEIGHT_KG)
            .grade(UPDATED_GRADE)
            .quantityUnits(UPDATED_QUANTITY_UNITS)
            .unit(UPDATED_UNIT)
            .pricePerKg(UPDATED_PRICE_PER_KG)
            .lineTotal(UPDATED_LINE_TOTAL)
            .note(UPDATED_NOTE);
        // Add required entity
        SalesOrder salesOrder;
        if (TestUtil.findAll(em, SalesOrder.class).isEmpty()) {
            salesOrder = SalesOrderResourceIT.createUpdatedEntity(em);
            em.persist(salesOrder);
            em.flush();
        } else {
            salesOrder = TestUtil.findAll(em, SalesOrder.class).get(0);
        }
        updatedSalesOrderLine.setSalesOrder(salesOrder);
        // Add required entity
        Product product;
        if (TestUtil.findAll(em, Product.class).isEmpty()) {
            product = ProductResourceIT.createUpdatedEntity(em);
            em.persist(product);
            em.flush();
        } else {
            product = TestUtil.findAll(em, Product.class).get(0);
        }
        updatedSalesOrderLine.setProduct(product);
        return updatedSalesOrderLine;
    }

    @BeforeEach
    void initTest() {
        salesOrderLine = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedSalesOrderLine != null) {
            salesOrderLineRepository.delete(insertedSalesOrderLine);
            insertedSalesOrderLine = null;
        }
    }

    @Test
    @Transactional
    void createSalesOrderLine() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SalesOrderLine
        var returnedSalesOrderLine = om.readValue(
            restSalesOrderLineMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(salesOrderLine)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SalesOrderLine.class
        );

        // Validate the SalesOrderLine in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertSalesOrderLineUpdatableFieldsEquals(returnedSalesOrderLine, getPersistedSalesOrderLine(returnedSalesOrderLine));

        insertedSalesOrderLine = returnedSalesOrderLine;
    }

    @Test
    @Transactional
    void createSalesOrderLineWithExistingId() throws Exception {
        // Create the SalesOrderLine with an existing ID
        salesOrderLine.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSalesOrderLineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(salesOrderLine)))
            .andExpect(status().isBadRequest());

        // Validate the SalesOrderLine in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkLineNumberIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        salesOrderLine.setLineNumber(null);

        // Create the SalesOrderLine, which fails.

        restSalesOrderLineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(salesOrderLine)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkWeightKgIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        salesOrderLine.setWeightKg(null);

        // Create the SalesOrderLine, which fails.

        restSalesOrderLineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(salesOrderLine)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkGradeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        salesOrderLine.setGrade(null);

        // Create the SalesOrderLine, which fails.

        restSalesOrderLineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(salesOrderLine)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPricePerKgIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        salesOrderLine.setPricePerKg(null);

        // Create the SalesOrderLine, which fails.

        restSalesOrderLineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(salesOrderLine)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLineTotalIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        salesOrderLine.setLineTotal(null);

        // Create the SalesOrderLine, which fails.

        restSalesOrderLineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(salesOrderLine)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSalesOrderLines() throws Exception {
        // Initialize the database
        insertedSalesOrderLine = salesOrderLineRepository.saveAndFlush(salesOrderLine);

        // Get all the salesOrderLineList
        restSalesOrderLineMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(salesOrderLine.getId().intValue())))
            .andExpect(jsonPath("$.[*].lineNumber").value(hasItem(DEFAULT_LINE_NUMBER)))
            .andExpect(jsonPath("$.[*].weightKg").value(hasItem(sameNumber(DEFAULT_WEIGHT_KG))))
            .andExpect(jsonPath("$.[*].grade").value(hasItem(DEFAULT_GRADE.toString())))
            .andExpect(jsonPath("$.[*].quantityUnits").value(hasItem(DEFAULT_QUANTITY_UNITS)))
            .andExpect(jsonPath("$.[*].unit").value(hasItem(DEFAULT_UNIT.toString())))
            .andExpect(jsonPath("$.[*].pricePerKg").value(hasItem(sameNumber(DEFAULT_PRICE_PER_KG))))
            .andExpect(jsonPath("$.[*].lineTotal").value(hasItem(sameNumber(DEFAULT_LINE_TOTAL))))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSalesOrderLinesWithEagerRelationshipsIsEnabled() throws Exception {
        when(salesOrderLineRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSalesOrderLineMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(salesOrderLineRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSalesOrderLinesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(salesOrderLineRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSalesOrderLineMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(salesOrderLineRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getSalesOrderLine() throws Exception {
        // Initialize the database
        insertedSalesOrderLine = salesOrderLineRepository.saveAndFlush(salesOrderLine);

        // Get the salesOrderLine
        restSalesOrderLineMockMvc
            .perform(get(ENTITY_API_URL_ID, salesOrderLine.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(salesOrderLine.getId().intValue()))
            .andExpect(jsonPath("$.lineNumber").value(DEFAULT_LINE_NUMBER))
            .andExpect(jsonPath("$.weightKg").value(sameNumber(DEFAULT_WEIGHT_KG)))
            .andExpect(jsonPath("$.grade").value(DEFAULT_GRADE.toString()))
            .andExpect(jsonPath("$.quantityUnits").value(DEFAULT_QUANTITY_UNITS))
            .andExpect(jsonPath("$.unit").value(DEFAULT_UNIT.toString()))
            .andExpect(jsonPath("$.pricePerKg").value(sameNumber(DEFAULT_PRICE_PER_KG)))
            .andExpect(jsonPath("$.lineTotal").value(sameNumber(DEFAULT_LINE_TOTAL)))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE));
    }

    @Test
    @Transactional
    void getNonExistingSalesOrderLine() throws Exception {
        // Get the salesOrderLine
        restSalesOrderLineMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSalesOrderLine() throws Exception {
        // Initialize the database
        insertedSalesOrderLine = salesOrderLineRepository.saveAndFlush(salesOrderLine);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the salesOrderLine
        SalesOrderLine updatedSalesOrderLine = salesOrderLineRepository.findById(salesOrderLine.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSalesOrderLine are not directly saved in db
        em.detach(updatedSalesOrderLine);
        updatedSalesOrderLine
            .lineNumber(UPDATED_LINE_NUMBER)
            .weightKg(UPDATED_WEIGHT_KG)
            .grade(UPDATED_GRADE)
            .quantityUnits(UPDATED_QUANTITY_UNITS)
            .unit(UPDATED_UNIT)
            .pricePerKg(UPDATED_PRICE_PER_KG)
            .lineTotal(UPDATED_LINE_TOTAL)
            .note(UPDATED_NOTE);

        restSalesOrderLineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSalesOrderLine.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedSalesOrderLine))
            )
            .andExpect(status().isOk());

        // Validate the SalesOrderLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSalesOrderLineToMatchAllProperties(updatedSalesOrderLine);
    }

    @Test
    @Transactional
    void putNonExistingSalesOrderLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        salesOrderLine.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSalesOrderLineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, salesOrderLine.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(salesOrderLine))
            )
            .andExpect(status().isBadRequest());

        // Validate the SalesOrderLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSalesOrderLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        salesOrderLine.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalesOrderLineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(salesOrderLine))
            )
            .andExpect(status().isBadRequest());

        // Validate the SalesOrderLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSalesOrderLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        salesOrderLine.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalesOrderLineMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(salesOrderLine)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SalesOrderLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSalesOrderLineWithPatch() throws Exception {
        // Initialize the database
        insertedSalesOrderLine = salesOrderLineRepository.saveAndFlush(salesOrderLine);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the salesOrderLine using partial update
        SalesOrderLine partialUpdatedSalesOrderLine = new SalesOrderLine();
        partialUpdatedSalesOrderLine.setId(salesOrderLine.getId());

        partialUpdatedSalesOrderLine.weightKg(UPDATED_WEIGHT_KG).grade(UPDATED_GRADE).pricePerKg(UPDATED_PRICE_PER_KG).note(UPDATED_NOTE);

        restSalesOrderLineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSalesOrderLine.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSalesOrderLine))
            )
            .andExpect(status().isOk());

        // Validate the SalesOrderLine in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSalesOrderLineUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSalesOrderLine, salesOrderLine),
            getPersistedSalesOrderLine(salesOrderLine)
        );
    }

    @Test
    @Transactional
    void fullUpdateSalesOrderLineWithPatch() throws Exception {
        // Initialize the database
        insertedSalesOrderLine = salesOrderLineRepository.saveAndFlush(salesOrderLine);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the salesOrderLine using partial update
        SalesOrderLine partialUpdatedSalesOrderLine = new SalesOrderLine();
        partialUpdatedSalesOrderLine.setId(salesOrderLine.getId());

        partialUpdatedSalesOrderLine
            .lineNumber(UPDATED_LINE_NUMBER)
            .weightKg(UPDATED_WEIGHT_KG)
            .grade(UPDATED_GRADE)
            .quantityUnits(UPDATED_QUANTITY_UNITS)
            .unit(UPDATED_UNIT)
            .pricePerKg(UPDATED_PRICE_PER_KG)
            .lineTotal(UPDATED_LINE_TOTAL)
            .note(UPDATED_NOTE);

        restSalesOrderLineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSalesOrderLine.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSalesOrderLine))
            )
            .andExpect(status().isOk());

        // Validate the SalesOrderLine in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSalesOrderLineUpdatableFieldsEquals(partialUpdatedSalesOrderLine, getPersistedSalesOrderLine(partialUpdatedSalesOrderLine));
    }

    @Test
    @Transactional
    void patchNonExistingSalesOrderLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        salesOrderLine.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSalesOrderLineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, salesOrderLine.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(salesOrderLine))
            )
            .andExpect(status().isBadRequest());

        // Validate the SalesOrderLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSalesOrderLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        salesOrderLine.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalesOrderLineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(salesOrderLine))
            )
            .andExpect(status().isBadRequest());

        // Validate the SalesOrderLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSalesOrderLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        salesOrderLine.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalesOrderLineMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(salesOrderLine)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SalesOrderLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSalesOrderLine() throws Exception {
        // Initialize the database
        insertedSalesOrderLine = salesOrderLineRepository.saveAndFlush(salesOrderLine);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the salesOrderLine
        restSalesOrderLineMockMvc
            .perform(delete(ENTITY_API_URL_ID, salesOrderLine.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return salesOrderLineRepository.count();
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

    protected SalesOrderLine getPersistedSalesOrderLine(SalesOrderLine salesOrderLine) {
        return salesOrderLineRepository.findById(salesOrderLine.getId()).orElseThrow();
    }

    protected void assertPersistedSalesOrderLineToMatchAllProperties(SalesOrderLine expectedSalesOrderLine) {
        assertSalesOrderLineAllPropertiesEquals(expectedSalesOrderLine, getPersistedSalesOrderLine(expectedSalesOrderLine));
    }

    protected void assertPersistedSalesOrderLineToMatchUpdatableProperties(SalesOrderLine expectedSalesOrderLine) {
        assertSalesOrderLineAllUpdatablePropertiesEquals(expectedSalesOrderLine, getPersistedSalesOrderLine(expectedSalesOrderLine));
    }
}
