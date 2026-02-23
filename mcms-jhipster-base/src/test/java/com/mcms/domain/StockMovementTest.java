package com.mcms.domain;

import static com.mcms.domain.BatchTestSamples.*;
import static com.mcms.domain.InventoryLotTestSamples.*;
import static com.mcms.domain.StockMovementTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mcms.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StockMovementTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(StockMovement.class);
        StockMovement stockMovement1 = getStockMovementSample1();
        StockMovement stockMovement2 = new StockMovement();
        assertThat(stockMovement1).isNotEqualTo(stockMovement2);

        stockMovement2.setId(stockMovement1.getId());
        assertThat(stockMovement1).isEqualTo(stockMovement2);

        stockMovement2 = getStockMovementSample2();
        assertThat(stockMovement1).isNotEqualTo(stockMovement2);
    }

    @Test
    void inventoryLotTest() {
        StockMovement stockMovement = getStockMovementRandomSampleGenerator();
        InventoryLot inventoryLotBack = getInventoryLotRandomSampleGenerator();

        stockMovement.setInventoryLot(inventoryLotBack);
        assertThat(stockMovement.getInventoryLot()).isEqualTo(inventoryLotBack);

        stockMovement.inventoryLot(null);
        assertThat(stockMovement.getInventoryLot()).isNull();
    }

    @Test
    void batchTest() {
        StockMovement stockMovement = getStockMovementRandomSampleGenerator();
        Batch batchBack = getBatchRandomSampleGenerator();

        stockMovement.setBatch(batchBack);
        assertThat(stockMovement.getBatch()).isEqualTo(batchBack);

        stockMovement.batch(null);
        assertThat(stockMovement.getBatch()).isNull();
    }
}
