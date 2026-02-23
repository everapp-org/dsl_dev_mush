package com.mcms.domain;

import static com.mcms.domain.BatchTestSamples.*;
import static com.mcms.domain.FlushCycleTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mcms.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FlushCycleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FlushCycle.class);
        FlushCycle flushCycle1 = getFlushCycleSample1();
        FlushCycle flushCycle2 = new FlushCycle();
        assertThat(flushCycle1).isNotEqualTo(flushCycle2);

        flushCycle2.setId(flushCycle1.getId());
        assertThat(flushCycle1).isEqualTo(flushCycle2);

        flushCycle2 = getFlushCycleSample2();
        assertThat(flushCycle1).isNotEqualTo(flushCycle2);
    }

    @Test
    void batchTest() {
        FlushCycle flushCycle = getFlushCycleRandomSampleGenerator();
        Batch batchBack = getBatchRandomSampleGenerator();

        flushCycle.setBatch(batchBack);
        assertThat(flushCycle.getBatch()).isEqualTo(batchBack);

        flushCycle.batch(null);
        assertThat(flushCycle.getBatch()).isNull();
    }
}
