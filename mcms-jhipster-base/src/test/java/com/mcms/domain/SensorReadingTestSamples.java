package com.mcms.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class SensorReadingTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static SensorReading getSensorReadingSample1() {
        return new SensorReading().id(1L);
    }

    public static SensorReading getSensorReadingSample2() {
        return new SensorReading().id(2L);
    }

    public static SensorReading getSensorReadingRandomSampleGenerator() {
        return new SensorReading().id(longCount.incrementAndGet());
    }
}
