package com.mcms.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class BatchTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Batch getBatchSample1() {
        return new Batch().id(1L).batchCode("batchCode1").numberOfBags(1);
    }

    public static Batch getBatchSample2() {
        return new Batch().id(2L).batchCode("batchCode2").numberOfBags(2);
    }

    public static Batch getBatchRandomSampleGenerator() {
        return new Batch().id(longCount.incrementAndGet()).batchCode(UUID.randomUUID().toString()).numberOfBags(intCount.incrementAndGet());
    }
}
