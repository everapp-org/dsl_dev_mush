package com.mcms.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SupplyOrderLineTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static SupplyOrderLine getSupplyOrderLineSample1() {
        return new SupplyOrderLine()
            .id(1L)
            .lineNumber(1)
            .itemDescription("itemDescription1")
            .lotNumber("lotNumber1")
            .qualityOnReceipt("qualityOnReceipt1");
    }

    public static SupplyOrderLine getSupplyOrderLineSample2() {
        return new SupplyOrderLine()
            .id(2L)
            .lineNumber(2)
            .itemDescription("itemDescription2")
            .lotNumber("lotNumber2")
            .qualityOnReceipt("qualityOnReceipt2");
    }

    public static SupplyOrderLine getSupplyOrderLineRandomSampleGenerator() {
        return new SupplyOrderLine()
            .id(longCount.incrementAndGet())
            .lineNumber(intCount.incrementAndGet())
            .itemDescription(UUID.randomUUID().toString())
            .lotNumber(UUID.randomUUID().toString())
            .qualityOnReceipt(UUID.randomUUID().toString());
    }
}
