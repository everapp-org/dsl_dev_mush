package com.mcms.domain;

import static com.mcms.domain.BatchTestSamples.*;
import static com.mcms.domain.MonthlyReportTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mcms.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MonthlyReportTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MonthlyReport.class);
        MonthlyReport monthlyReport1 = getMonthlyReportSample1();
        MonthlyReport monthlyReport2 = new MonthlyReport();
        assertThat(monthlyReport1).isNotEqualTo(monthlyReport2);

        monthlyReport2.setId(monthlyReport1.getId());
        assertThat(monthlyReport1).isEqualTo(monthlyReport2);

        monthlyReport2 = getMonthlyReportSample2();
        assertThat(monthlyReport1).isNotEqualTo(monthlyReport2);
    }

    @Test
    void batchTest() {
        MonthlyReport monthlyReport = getMonthlyReportRandomSampleGenerator();
        Batch batchBack = getBatchRandomSampleGenerator();

        monthlyReport.setBatch(batchBack);
        assertThat(monthlyReport.getBatch()).isEqualTo(batchBack);

        monthlyReport.batch(null);
        assertThat(monthlyReport.getBatch()).isNull();
    }
}
