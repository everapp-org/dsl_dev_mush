package com.mcms.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SalesOrderLineTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static SalesOrderLine getSalesOrderLineSample1() {
        return new SalesOrderLine().id(1L).lineNumber(1).quantityUnits(1);
    }

    public static SalesOrderLine getSalesOrderLineSample2() {
        return new SalesOrderLine().id(2L).lineNumber(2).quantityUnits(2);
    }

    public static SalesOrderLine getSalesOrderLineRandomSampleGenerator() {
        return new SalesOrderLine()
            .id(longCount.incrementAndGet())
            .lineNumber(intCount.incrementAndGet())
            .quantityUnits(intCount.incrementAndGet());
    }
}
