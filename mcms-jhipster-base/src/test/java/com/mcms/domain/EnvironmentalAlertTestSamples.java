package com.mcms.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class EnvironmentalAlertTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static EnvironmentalAlert getEnvironmentalAlertSample1() {
        return new EnvironmentalAlert().id(1L).message("message1").acknowledgedBy("acknowledgedBy1");
    }

    public static EnvironmentalAlert getEnvironmentalAlertSample2() {
        return new EnvironmentalAlert().id(2L).message("message2").acknowledgedBy("acknowledgedBy2");
    }

    public static EnvironmentalAlert getEnvironmentalAlertRandomSampleGenerator() {
        return new EnvironmentalAlert()
            .id(longCount.incrementAndGet())
            .message(UUID.randomUUID().toString())
            .acknowledgedBy(UUID.randomUUID().toString());
    }
}
