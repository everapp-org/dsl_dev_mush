package com.mcms.domain;

import static com.mcms.domain.InventoryLotTestSamples.*;
import static com.mcms.domain.MaterialTestSamples.*;
import static com.mcms.domain.SupplyOrderLineTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mcms.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class InventoryLotTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(InventoryLot.class);
        InventoryLot inventoryLot1 = getInventoryLotSample1();
        InventoryLot inventoryLot2 = new InventoryLot();
        assertThat(inventoryLot1).isNotEqualTo(inventoryLot2);

        inventoryLot2.setId(inventoryLot1.getId());
        assertThat(inventoryLot1).isEqualTo(inventoryLot2);

        inventoryLot2 = getInventoryLotSample2();
        assertThat(inventoryLot1).isNotEqualTo(inventoryLot2);
    }

    @Test
    void materialTest() {
        InventoryLot inventoryLot = getInventoryLotRandomSampleGenerator();
        Material materialBack = getMaterialRandomSampleGenerator();

        inventoryLot.setMaterial(materialBack);
        assertThat(inventoryLot.getMaterial()).isEqualTo(materialBack);

        inventoryLot.material(null);
        assertThat(inventoryLot.getMaterial()).isNull();
    }

    @Test
    void supplyOrderLineTest() {
        InventoryLot inventoryLot = getInventoryLotRandomSampleGenerator();
        SupplyOrderLine supplyOrderLineBack = getSupplyOrderLineRandomSampleGenerator();

        inventoryLot.setSupplyOrderLine(supplyOrderLineBack);
        assertThat(inventoryLot.getSupplyOrderLine()).isEqualTo(supplyOrderLineBack);

        inventoryLot.supplyOrderLine(null);
        assertThat(inventoryLot.getSupplyOrderLine()).isNull();
    }
}
