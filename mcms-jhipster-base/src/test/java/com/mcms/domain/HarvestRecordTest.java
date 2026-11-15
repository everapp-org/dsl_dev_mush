package com.mcms.domain;

import static com.mcms.domain.FlushCycleTestSamples.*;
import static com.mcms.domain.HarvestRecordTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mcms.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class HarvestRecordTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(HarvestRecord.class);
        HarvestRecord harvestRecord1 = getHarvestRecordSample1();
        HarvestRecord harvestRecord2 = new HarvestRecord();
        assertThat(harvestRecord1).isNotEqualTo(harvestRecord2);

        harvestRecord2.setId(harvestRecord1.getId());
        assertThat(harvestRecord1).isEqualTo(harvestRecord2);

        harvestRecord2 = getHarvestRecordSample2();
        assertThat(harvestRecord1).isNotEqualTo(harvestRecord2);
    }

    @Test
    void flushCycleTest() {
        HarvestRecord harvestRecord = getHarvestRecordRandomSampleGenerator();
        FlushCycle flushCycleBack = getFlushCycleRandomSampleGenerator();

        harvestRecord.setFlushCycle(flushCycleBack);
        assertThat(harvestRecord.getFlushCycle()).isEqualTo(flushCycleBack);

        harvestRecord.flushCycle(null);
        assertThat(harvestRecord.getFlushCycle()).isNull();
    }
}
