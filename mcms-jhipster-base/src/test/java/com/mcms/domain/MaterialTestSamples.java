package com.mcms.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class MaterialTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Material getMaterialSample1() {
        return new Material().id(1L).code("code1").name("name1");
    }

    public static Material getMaterialSample2() {
        return new Material().id(2L).code("code2").name("name2");
    }

    public static Material getMaterialRandomSampleGenerator() {
        return new Material().id(longCount.incrementAndGet()).code(UUID.randomUUID().toString()).name(UUID.randomUUID().toString());
    }
}
