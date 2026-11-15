package com.mcms.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class BatchMaterialUsageTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static BatchMaterialUsage getBatchMaterialUsageSample1() {
        return new BatchMaterialUsage().id(1L).purpose("purpose1");
    }

    public static BatchMaterialUsage getBatchMaterialUsageSample2() {
        return new BatchMaterialUsage().id(2L).purpose("purpose2");
    }

    public static BatchMaterialUsage getBatchMaterialUsageRandomSampleGenerator() {
        return new BatchMaterialUsage().id(longCount.incrementAndGet()).purpose(UUID.randomUUID().toString());
    }
}
