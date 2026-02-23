package com.mcms.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SalesOrderTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static SalesOrder getSalesOrderSample1() {
        return new SalesOrder().id(1L).orderCode("orderCode1").currency("currency1").invoiceNumber("invoiceNumber1");
    }

    public static SalesOrder getSalesOrderSample2() {
        return new SalesOrder().id(2L).orderCode("orderCode2").currency("currency2").invoiceNumber("invoiceNumber2");
    }

    public static SalesOrder getSalesOrderRandomSampleGenerator() {
        return new SalesOrder()
            .id(longCount.incrementAndGet())
            .orderCode(UUID.randomUUID().toString())
            .currency(UUID.randomUUID().toString())
            .invoiceNumber(UUID.randomUUID().toString());
    }
}
