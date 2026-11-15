package com.mcms.domain;

import static com.mcms.domain.BatchMaterialUsageTestSamples.*;
import static com.mcms.domain.BatchTestSamples.*;
import static com.mcms.domain.InventoryLotTestSamples.*;
import static com.mcms.domain.MaterialTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mcms.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BatchMaterialUsageTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BatchMaterialUsage.class);
        BatchMaterialUsage batchMaterialUsage1 = getBatchMaterialUsageSample1();
        BatchMaterialUsage batchMaterialUsage2 = new BatchMaterialUsage();
        assertThat(batchMaterialUsage1).isNotEqualTo(batchMaterialUsage2);

        batchMaterialUsage2.setId(batchMaterialUsage1.getId());
        assertThat(batchMaterialUsage1).isEqualTo(batchMaterialUsage2);

        batchMaterialUsage2 = getBatchMaterialUsageSample2();
        assertThat(batchMaterialUsage1).isNotEqualTo(batchMaterialUsage2);
    }

    @Test
    void batchTest() {
        BatchMaterialUsage batchMaterialUsage = getBatchMaterialUsageRandomSampleGenerator();
        Batch batchBack = getBatchRandomSampleGenerator();

        batchMaterialUsage.setBatch(batchBack);
        assertThat(batchMaterialUsage.getBatch()).isEqualTo(batchBack);

        batchMaterialUsage.batch(null);
        assertThat(batchMaterialUsage.getBatch()).isNull();
    }

    @Test
    void inventoryLotTest() {
        BatchMaterialUsage batchMaterialUsage = getBatchMaterialUsageRandomSampleGenerator();
        InventoryLot inventoryLotBack = getInventoryLotRandomSampleGenerator();

        batchMaterialUsage.setInventoryLot(inventoryLotBack);
        assertThat(batchMaterialUsage.getInventoryLot()).isEqualTo(inventoryLotBack);

        batchMaterialUsage.inventoryLot(null);
        assertThat(batchMaterialUsage.getInventoryLot()).isNull();
    }

    @Test
    void materialTest() {
        BatchMaterialUsage batchMaterialUsage = getBatchMaterialUsageRandomSampleGenerator();
        Material materialBack = getMaterialRandomSampleGenerator();

        batchMaterialUsage.setMaterial(materialBack);
        assertThat(batchMaterialUsage.getMaterial()).isEqualTo(materialBack);

        batchMaterialUsage.material(null);
        assertThat(batchMaterialUsage.getMaterial()).isNull();
    }
}
