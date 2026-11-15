package com.mcms.domain;

import static com.mcms.domain.BatchTestSamples.*;
import static com.mcms.domain.MaterialTestSamples.*;
import static com.mcms.domain.SupplyOrderLineTestSamples.*;
import static com.mcms.domain.SupplyOrderTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mcms.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SupplyOrderLineTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SupplyOrderLine.class);
        SupplyOrderLine supplyOrderLine1 = getSupplyOrderLineSample1();
        SupplyOrderLine supplyOrderLine2 = new SupplyOrderLine();
        assertThat(supplyOrderLine1).isNotEqualTo(supplyOrderLine2);

        supplyOrderLine2.setId(supplyOrderLine1.getId());
        assertThat(supplyOrderLine1).isEqualTo(supplyOrderLine2);

        supplyOrderLine2 = getSupplyOrderLineSample2();
        assertThat(supplyOrderLine1).isNotEqualTo(supplyOrderLine2);
    }

    @Test
    void supplyOrderTest() {
        SupplyOrderLine supplyOrderLine = getSupplyOrderLineRandomSampleGenerator();
        SupplyOrder supplyOrderBack = getSupplyOrderRandomSampleGenerator();

        supplyOrderLine.setSupplyOrder(supplyOrderBack);
        assertThat(supplyOrderLine.getSupplyOrder()).isEqualTo(supplyOrderBack);

        supplyOrderLine.supplyOrder(null);
        assertThat(supplyOrderLine.getSupplyOrder()).isNull();
    }

    @Test
    void materialTest() {
        SupplyOrderLine supplyOrderLine = getSupplyOrderLineRandomSampleGenerator();
        Material materialBack = getMaterialRandomSampleGenerator();

        supplyOrderLine.setMaterial(materialBack);
        assertThat(supplyOrderLine.getMaterial()).isEqualTo(materialBack);

        supplyOrderLine.material(null);
        assertThat(supplyOrderLine.getMaterial()).isNull();
    }

    @Test
    void batchTest() {
        SupplyOrderLine supplyOrderLine = getSupplyOrderLineRandomSampleGenerator();
        Batch batchBack = getBatchRandomSampleGenerator();

        supplyOrderLine.setBatch(batchBack);
        assertThat(supplyOrderLine.getBatch()).isEqualTo(batchBack);

        supplyOrderLine.batch(null);
        assertThat(supplyOrderLine.getBatch()).isNull();
    }
}
