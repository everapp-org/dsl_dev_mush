package com.mcms.domain;

import static com.mcms.domain.BatchTestSamples.*;
import static com.mcms.domain.CostRecordTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mcms.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CostRecordTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CostRecord.class);
        CostRecord costRecord1 = getCostRecordSample1();
        CostRecord costRecord2 = new CostRecord();
        assertThat(costRecord1).isNotEqualTo(costRecord2);

        costRecord2.setId(costRecord1.getId());
        assertThat(costRecord1).isEqualTo(costRecord2);

        costRecord2 = getCostRecordSample2();
        assertThat(costRecord1).isNotEqualTo(costRecord2);
    }

    @Test
    void batchTest() {
        CostRecord costRecord = getCostRecordRandomSampleGenerator();
        Batch batchBack = getBatchRandomSampleGenerator();

        costRecord.setBatch(batchBack);
        assertThat(costRecord.getBatch()).isEqualTo(batchBack);

        costRecord.batch(null);
        assertThat(costRecord.getBatch()).isNull();
    }
}
