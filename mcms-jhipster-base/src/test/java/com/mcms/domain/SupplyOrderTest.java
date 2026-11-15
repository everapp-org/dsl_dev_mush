package com.mcms.domain;

import static com.mcms.domain.SupplierTestSamples.*;
import static com.mcms.domain.SupplyOrderTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mcms.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SupplyOrderTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SupplyOrder.class);
        SupplyOrder supplyOrder1 = getSupplyOrderSample1();
        SupplyOrder supplyOrder2 = new SupplyOrder();
        assertThat(supplyOrder1).isNotEqualTo(supplyOrder2);

        supplyOrder2.setId(supplyOrder1.getId());
        assertThat(supplyOrder1).isEqualTo(supplyOrder2);

        supplyOrder2 = getSupplyOrderSample2();
        assertThat(supplyOrder1).isNotEqualTo(supplyOrder2);
    }

    @Test
    void supplierTest() {
        SupplyOrder supplyOrder = getSupplyOrderRandomSampleGenerator();
        Supplier supplierBack = getSupplierRandomSampleGenerator();

        supplyOrder.setSupplier(supplierBack);
        assertThat(supplyOrder.getSupplier()).isEqualTo(supplierBack);

        supplyOrder.supplier(null);
        assertThat(supplyOrder.getSupplier()).isNull();
    }
}
