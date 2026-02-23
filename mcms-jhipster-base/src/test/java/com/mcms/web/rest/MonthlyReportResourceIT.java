package com.mcms.web.rest;

import static com.mcms.domain.MonthlyReportAsserts.*;
import static com.mcms.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mcms.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcms.IntegrationTest;
import com.mcms.domain.MonthlyReport;
import com.mcms.repository.MonthlyReportRepository;
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
 * Integration tests for the {@link MonthlyReportResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class MonthlyReportResourceIT {

    private static final Integer DEFAULT_YEAR = 1;
    private static final Integer UPDATED_YEAR = 2;

    private static final Integer DEFAULT_MONTH = 1;
    private static final Integer UPDATED_MONTH = 2;

    private static final Instant DEFAULT_GENERATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_GENERATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final BigDecimal DEFAULT_TOTAL_YIELD_KG = new BigDecimal(1);
    private static final BigDecimal UPDATED_TOTAL_YIELD_KG = new BigDecimal(2);

    private static final BigDecimal DEFAULT_TOTAL_COST = new BigDecimal(1);
    private static final BigDecimal UPDATED_TOTAL_COST = new BigDecimal(2);

    private static final BigDecimal DEFAULT_TOTAL_REVENUE = new BigDecimal(1);
    private static final BigDecimal UPDATED_TOTAL_REVENUE = new BigDecimal(2);

    private static final BigDecimal DEFAULT_PROFIT_MARGIN_PERCENT = new BigDecimal(1);
    private static final BigDecimal UPDATED_PROFIT_MARGIN_PERCENT = new BigDecimal(2);

    private static final Integer DEFAULT_TOTAL_CONTAMINATION_EVENTS = 1;
    private static final Integer UPDATED_TOTAL_CONTAMINATION_EVENTS = 2;

    private static final Integer DEFAULT_TOTAL_MISSING_FIELDS = 1;
    private static final Integer UPDATED_TOTAL_MISSING_FIELDS = 2;

    private static final String DEFAULT_SUMMARY = "AAAAAAAAAA";
    private static final String UPDATED_SUMMARY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/monthly-reports";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MonthlyReportRepository monthlyReportRepository;

    @Mock
    private MonthlyReportRepository monthlyReportRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMonthlyReportMockMvc;

    private MonthlyReport monthlyReport;

    private MonthlyReport insertedMonthlyReport;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MonthlyReport createEntity() {
        return new MonthlyReport()
            .year(DEFAULT_YEAR)
            .month(DEFAULT_MONTH)
            .generatedAt(DEFAULT_GENERATED_AT)
            .totalYieldKg(DEFAULT_TOTAL_YIELD_KG)
            .totalCost(DEFAULT_TOTAL_COST)
            .totalRevenue(DEFAULT_TOTAL_REVENUE)
            .profitMarginPercent(DEFAULT_PROFIT_MARGIN_PERCENT)
            .totalContaminationEvents(DEFAULT_TOTAL_CONTAMINATION_EVENTS)
            .totalMissingFields(DEFAULT_TOTAL_MISSING_FIELDS)
            .summary(DEFAULT_SUMMARY);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MonthlyReport createUpdatedEntity() {
        return new MonthlyReport()
            .year(UPDATED_YEAR)
            .month(UPDATED_MONTH)
            .generatedAt(UPDATED_GENERATED_AT)
            .totalYieldKg(UPDATED_TOTAL_YIELD_KG)
            .totalCost(UPDATED_TOTAL_COST)
            .totalRevenue(UPDATED_TOTAL_REVENUE)
            .profitMarginPercent(UPDATED_PROFIT_MARGIN_PERCENT)
            .totalContaminationEvents(UPDATED_TOTAL_CONTAMINATION_EVENTS)
            .totalMissingFields(UPDATED_TOTAL_MISSING_FIELDS)
            .summary(UPDATED_SUMMARY);
    }

    @BeforeEach
    void initTest() {
        monthlyReport = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedMonthlyReport != null) {
            monthlyReportRepository.delete(insertedMonthlyReport);
            insertedMonthlyReport = null;
        }
    }

    @Test
    @Transactional
    void createMonthlyReport() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the MonthlyReport
        var returnedMonthlyReport = om.readValue(
            restMonthlyReportMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(monthlyReport)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            MonthlyReport.class
        );

        // Validate the MonthlyReport in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertMonthlyReportUpdatableFieldsEquals(returnedMonthlyReport, getPersistedMonthlyReport(returnedMonthlyReport));

        insertedMonthlyReport = returnedMonthlyReport;
    }

    @Test
    @Transactional
    void createMonthlyReportWithExistingId() throws Exception {
        // Create the MonthlyReport with an existing ID
        monthlyReport.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMonthlyReportMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(monthlyReport)))
            .andExpect(status().isBadRequest());

        // Validate the MonthlyReport in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkYearIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        monthlyReport.setYear(null);

        // Create the MonthlyReport, which fails.

        restMonthlyReportMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(monthlyReport)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMonthIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        monthlyReport.setMonth(null);

        // Create the MonthlyReport, which fails.

        restMonthlyReportMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(monthlyReport)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkGeneratedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        monthlyReport.setGeneratedAt(null);

        // Create the MonthlyReport, which fails.

        restMonthlyReportMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(monthlyReport)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllMonthlyReports() throws Exception {
        // Initialize the database
        insertedMonthlyReport = monthlyReportRepository.saveAndFlush(monthlyReport);

        // Get all the monthlyReportList
        restMonthlyReportMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(monthlyReport.getId().intValue())))
            .andExpect(jsonPath("$.[*].year").value(hasItem(DEFAULT_YEAR)))
            .andExpect(jsonPath("$.[*].month").value(hasItem(DEFAULT_MONTH)))
            .andExpect(jsonPath("$.[*].generatedAt").value(hasItem(DEFAULT_GENERATED_AT.toString())))
            .andExpect(jsonPath("$.[*].totalYieldKg").value(hasItem(sameNumber(DEFAULT_TOTAL_YIELD_KG))))
            .andExpect(jsonPath("$.[*].totalCost").value(hasItem(sameNumber(DEFAULT_TOTAL_COST))))
            .andExpect(jsonPath("$.[*].totalRevenue").value(hasItem(sameNumber(DEFAULT_TOTAL_REVENUE))))
            .andExpect(jsonPath("$.[*].profitMarginPercent").value(hasItem(sameNumber(DEFAULT_PROFIT_MARGIN_PERCENT))))
            .andExpect(jsonPath("$.[*].totalContaminationEvents").value(hasItem(DEFAULT_TOTAL_CONTAMINATION_EVENTS)))
            .andExpect(jsonPath("$.[*].totalMissingFields").value(hasItem(DEFAULT_TOTAL_MISSING_FIELDS)))
            .andExpect(jsonPath("$.[*].summary").value(hasItem(DEFAULT_SUMMARY)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMonthlyReportsWithEagerRelationshipsIsEnabled() throws Exception {
        when(monthlyReportRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restMonthlyReportMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(monthlyReportRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMonthlyReportsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(monthlyReportRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restMonthlyReportMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(monthlyReportRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getMonthlyReport() throws Exception {
        // Initialize the database
        insertedMonthlyReport = monthlyReportRepository.saveAndFlush(monthlyReport);

        // Get the monthlyReport
        restMonthlyReportMockMvc
            .perform(get(ENTITY_API_URL_ID, monthlyReport.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(monthlyReport.getId().intValue()))
            .andExpect(jsonPath("$.year").value(DEFAULT_YEAR))
            .andExpect(jsonPath("$.month").value(DEFAULT_MONTH))
            .andExpect(jsonPath("$.generatedAt").value(DEFAULT_GENERATED_AT.toString()))
            .andExpect(jsonPath("$.totalYieldKg").value(sameNumber(DEFAULT_TOTAL_YIELD_KG)))
            .andExpect(jsonPath("$.totalCost").value(sameNumber(DEFAULT_TOTAL_COST)))
            .andExpect(jsonPath("$.totalRevenue").value(sameNumber(DEFAULT_TOTAL_REVENUE)))
            .andExpect(jsonPath("$.profitMarginPercent").value(sameNumber(DEFAULT_PROFIT_MARGIN_PERCENT)))
            .andExpect(jsonPath("$.totalContaminationEvents").value(DEFAULT_TOTAL_CONTAMINATION_EVENTS))
            .andExpect(jsonPath("$.totalMissingFields").value(DEFAULT_TOTAL_MISSING_FIELDS))
            .andExpect(jsonPath("$.summary").value(DEFAULT_SUMMARY));
    }

    @Test
    @Transactional
    void getNonExistingMonthlyReport() throws Exception {
        // Get the monthlyReport
        restMonthlyReportMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMonthlyReport() throws Exception {
        // Initialize the database
        insertedMonthlyReport = monthlyReportRepository.saveAndFlush(monthlyReport);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the monthlyReport
        MonthlyReport updatedMonthlyReport = monthlyReportRepository.findById(monthlyReport.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedMonthlyReport are not directly saved in db
        em.detach(updatedMonthlyReport);
        updatedMonthlyReport
            .year(UPDATED_YEAR)
            .month(UPDATED_MONTH)
            .generatedAt(UPDATED_GENERATED_AT)
            .totalYieldKg(UPDATED_TOTAL_YIELD_KG)
            .totalCost(UPDATED_TOTAL_COST)
            .totalRevenue(UPDATED_TOTAL_REVENUE)
            .profitMarginPercent(UPDATED_PROFIT_MARGIN_PERCENT)
            .totalContaminationEvents(UPDATED_TOTAL_CONTAMINATION_EVENTS)
            .totalMissingFields(UPDATED_TOTAL_MISSING_FIELDS)
            .summary(UPDATED_SUMMARY);

        restMonthlyReportMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedMonthlyReport.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedMonthlyReport))
            )
            .andExpect(status().isOk());

        // Validate the MonthlyReport in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMonthlyReportToMatchAllProperties(updatedMonthlyReport);
    }

    @Test
    @Transactional
    void putNonExistingMonthlyReport() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        monthlyReport.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMonthlyReportMockMvc
            .perform(
                put(ENTITY_API_URL_ID, monthlyReport.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(monthlyReport))
            )
            .andExpect(status().isBadRequest());

        // Validate the MonthlyReport in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMonthlyReport() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        monthlyReport.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMonthlyReportMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(monthlyReport))
            )
            .andExpect(status().isBadRequest());

        // Validate the MonthlyReport in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMonthlyReport() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        monthlyReport.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMonthlyReportMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(monthlyReport)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MonthlyReport in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMonthlyReportWithPatch() throws Exception {
        // Initialize the database
        insertedMonthlyReport = monthlyReportRepository.saveAndFlush(monthlyReport);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the monthlyReport using partial update
        MonthlyReport partialUpdatedMonthlyReport = new MonthlyReport();
        partialUpdatedMonthlyReport.setId(monthlyReport.getId());

        partialUpdatedMonthlyReport
            .month(UPDATED_MONTH)
            .generatedAt(UPDATED_GENERATED_AT)
            .profitMarginPercent(UPDATED_PROFIT_MARGIN_PERCENT)
            .totalContaminationEvents(UPDATED_TOTAL_CONTAMINATION_EVENTS)
            .summary(UPDATED_SUMMARY);

        restMonthlyReportMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMonthlyReport.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMonthlyReport))
            )
            .andExpect(status().isOk());

        // Validate the MonthlyReport in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMonthlyReportUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMonthlyReport, monthlyReport),
            getPersistedMonthlyReport(monthlyReport)
        );
    }

    @Test
    @Transactional
    void fullUpdateMonthlyReportWithPatch() throws Exception {
        // Initialize the database
        insertedMonthlyReport = monthlyReportRepository.saveAndFlush(monthlyReport);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the monthlyReport using partial update
        MonthlyReport partialUpdatedMonthlyReport = new MonthlyReport();
        partialUpdatedMonthlyReport.setId(monthlyReport.getId());

        partialUpdatedMonthlyReport
            .year(UPDATED_YEAR)
            .month(UPDATED_MONTH)
            .generatedAt(UPDATED_GENERATED_AT)
            .totalYieldKg(UPDATED_TOTAL_YIELD_KG)
            .totalCost(UPDATED_TOTAL_COST)
            .totalRevenue(UPDATED_TOTAL_REVENUE)
            .profitMarginPercent(UPDATED_PROFIT_MARGIN_PERCENT)
            .totalContaminationEvents(UPDATED_TOTAL_CONTAMINATION_EVENTS)
            .totalMissingFields(UPDATED_TOTAL_MISSING_FIELDS)
            .summary(UPDATED_SUMMARY);

        restMonthlyReportMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMonthlyReport.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMonthlyReport))
            )
            .andExpect(status().isOk());

        // Validate the MonthlyReport in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMonthlyReportUpdatableFieldsEquals(partialUpdatedMonthlyReport, getPersistedMonthlyReport(partialUpdatedMonthlyReport));
    }

    @Test
    @Transactional
    void patchNonExistingMonthlyReport() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        monthlyReport.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMonthlyReportMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, monthlyReport.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(monthlyReport))
            )
            .andExpect(status().isBadRequest());

        // Validate the MonthlyReport in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMonthlyReport() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        monthlyReport.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMonthlyReportMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(monthlyReport))
            )
            .andExpect(status().isBadRequest());

        // Validate the MonthlyReport in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMonthlyReport() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        monthlyReport.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMonthlyReportMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(monthlyReport)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MonthlyReport in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMonthlyReport() throws Exception {
        // Initialize the database
        insertedMonthlyReport = monthlyReportRepository.saveAndFlush(monthlyReport);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the monthlyReport
        restMonthlyReportMockMvc
            .perform(delete(ENTITY_API_URL_ID, monthlyReport.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return monthlyReportRepository.count();
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

    protected MonthlyReport getPersistedMonthlyReport(MonthlyReport monthlyReport) {
        return monthlyReportRepository.findById(monthlyReport.getId()).orElseThrow();
    }

    protected void assertPersistedMonthlyReportToMatchAllProperties(MonthlyReport expectedMonthlyReport) {
        assertMonthlyReportAllPropertiesEquals(expectedMonthlyReport, getPersistedMonthlyReport(expectedMonthlyReport));
    }

    protected void assertPersistedMonthlyReportToMatchUpdatableProperties(MonthlyReport expectedMonthlyReport) {
        assertMonthlyReportAllUpdatablePropertiesEquals(expectedMonthlyReport, getPersistedMonthlyReport(expectedMonthlyReport));
    }
}
