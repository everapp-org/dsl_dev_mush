package com.mcms.domain;

import static com.mcms.domain.CustomerTestSamples.*;
import static com.mcms.domain.SalesOrderTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mcms.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SalesOrderTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SalesOrder.class);
        SalesOrder salesOrder1 = getSalesOrderSample1();
        SalesOrder salesOrder2 = new SalesOrder();
        assertThat(salesOrder1).isNotEqualTo(salesOrder2);

        salesOrder2.setId(salesOrder1.getId());
        assertThat(salesOrder1).isEqualTo(salesOrder2);

        salesOrder2 = getSalesOrderSample2();
        assertThat(salesOrder1).isNotEqualTo(salesOrder2);
    }

    @Test
    void customerTest() {
        SalesOrder salesOrder = getSalesOrderRandomSampleGenerator();
        Customer customerBack = getCustomerRandomSampleGenerator();

        salesOrder.setCustomer(customerBack);
        assertThat(salesOrder.getCustomer()).isEqualTo(customerBack);

        salesOrder.customer(null);
        assertThat(salesOrder.getCustomer()).isNull();
    }
}
