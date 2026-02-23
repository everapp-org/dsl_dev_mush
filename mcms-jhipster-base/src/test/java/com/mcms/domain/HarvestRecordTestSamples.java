package com.mcms.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class HarvestRecordTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static HarvestRecord getHarvestRecordSample1() {
        return new HarvestRecord().id(1L).pickerName("pickerName1");
    }

    public static HarvestRecord getHarvestRecordSample2() {
        return new HarvestRecord().id(2L).pickerName("pickerName2");
    }

    public static HarvestRecord getHarvestRecordRandomSampleGenerator() {
        return new HarvestRecord().id(longCount.incrementAndGet()).pickerName(UUID.randomUUID().toString());
    }
}
