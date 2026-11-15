package com.mcms.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class StrainTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Strain getStrainSample1() {
        return new Strain()
            .id(1L)
            .name("name1")
            .species("species1")
            .variety("variety1")
            .optimalCO2MaxPpm(1)
            .colonizationDaysMin(1)
            .colonizationDaysMax(1)
            .shelfLifeDays(1);
    }

    public static Strain getStrainSample2() {
        return new Strain()
            .id(2L)
            .name("name2")
            .species("species2")
            .variety("variety2")
            .optimalCO2MaxPpm(2)
            .colonizationDaysMin(2)
            .colonizationDaysMax(2)
            .shelfLifeDays(2);
    }

    public static Strain getStrainRandomSampleGenerator() {
        return new Strain()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .species(UUID.randomUUID().toString())
            .variety(UUID.randomUUID().toString())
            .optimalCO2MaxPpm(intCount.incrementAndGet())
            .colonizationDaysMin(intCount.incrementAndGet())
            .colonizationDaysMax(intCount.incrementAndGet())
            .shelfLifeDays(intCount.incrementAndGet());
    }
}
