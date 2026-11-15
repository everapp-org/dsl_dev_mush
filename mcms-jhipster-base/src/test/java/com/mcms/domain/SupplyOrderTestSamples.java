package com.mcms.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SupplyOrderTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static SupplyOrder getSupplyOrderSample1() {
        return new SupplyOrder().id(1L).orderCode("orderCode1").currency("currency1");
    }

    public static SupplyOrder getSupplyOrderSample2() {
        return new SupplyOrder().id(2L).orderCode("orderCode2").currency("currency2");
    }

    public static SupplyOrder getSupplyOrderRandomSampleGenerator() {
        return new SupplyOrder()
            .id(longCount.incrementAndGet())
            .orderCode(UUID.randomUUID().toString())
            .currency(UUID.randomUUID().toString());
    }
}
