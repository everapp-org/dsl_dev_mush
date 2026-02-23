package com.mcms.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CostRecordTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static CostRecord getCostRecordSample1() {
        return new CostRecord().id(1L).description("description1").currency("currency1");
    }

    public static CostRecord getCostRecordSample2() {
        return new CostRecord().id(2L).description("description2").currency("currency2");
    }

    public static CostRecord getCostRecordRandomSampleGenerator() {
        return new CostRecord()
            .id(longCount.incrementAndGet())
            .description(UUID.randomUUID().toString())
            .currency(UUID.randomUUID().toString());
    }
}
