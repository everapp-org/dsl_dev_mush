package com.mcms.domain;

import static com.mcms.domain.BatchTestSamples.*;
import static com.mcms.domain.ProductTestSamples.*;
import static com.mcms.domain.SalesOrderLineTestSamples.*;
import static com.mcms.domain.SalesOrderTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mcms.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SalesOrderLineTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SalesOrderLine.class);
        SalesOrderLine salesOrderLine1 = getSalesOrderLineSample1();
        SalesOrderLine salesOrderLine2 = new SalesOrderLine();
        assertThat(salesOrderLine1).isNotEqualTo(salesOrderLine2);

        salesOrderLine2.setId(salesOrderLine1.getId());
        assertThat(salesOrderLine1).isEqualTo(salesOrderLine2);

        salesOrderLine2 = getSalesOrderLineSample2();
        assertThat(salesOrderLine1).isNotEqualTo(salesOrderLine2);
    }

    @Test
    void salesOrderTest() {
        SalesOrderLine salesOrderLine = getSalesOrderLineRandomSampleGenerator();
        SalesOrder salesOrderBack = getSalesOrderRandomSampleGenerator();

        salesOrderLine.setSalesOrder(salesOrderBack);
        assertThat(salesOrderLine.getSalesOrder()).isEqualTo(salesOrderBack);

        salesOrderLine.salesOrder(null);
        assertThat(salesOrderLine.getSalesOrder()).isNull();
    }

    @Test
    void productTest() {
        SalesOrderLine salesOrderLine = getSalesOrderLineRandomSampleGenerator();
        Product productBack = getProductRandomSampleGenerator();

        salesOrderLine.setProduct(productBack);
        assertThat(salesOrderLine.getProduct()).isEqualTo(productBack);

        salesOrderLine.product(null);
        assertThat(salesOrderLine.getProduct()).isNull();
    }

    @Test
    void batchTest() {
        SalesOrderLine salesOrderLine = getSalesOrderLineRandomSampleGenerator();
        Batch batchBack = getBatchRandomSampleGenerator();

        salesOrderLine.setBatch(batchBack);
        assertThat(salesOrderLine.getBatch()).isEqualTo(batchBack);

        salesOrderLine.batch(null);
        assertThat(salesOrderLine.getBatch()).isNull();
    }
}
