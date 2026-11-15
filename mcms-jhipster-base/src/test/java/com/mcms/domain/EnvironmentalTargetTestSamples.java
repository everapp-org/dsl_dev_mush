package com.mcms.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class EnvironmentalTargetTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static EnvironmentalTarget getEnvironmentalTargetSample1() {
        return new EnvironmentalTarget().id(1L).co2MaxPpm(1).lightLux(1).freshAirExchangesPerHour(1);
    }

    public static EnvironmentalTarget getEnvironmentalTargetSample2() {
        return new EnvironmentalTarget().id(2L).co2MaxPpm(2).lightLux(2).freshAirExchangesPerHour(2);
    }

    public static EnvironmentalTarget getEnvironmentalTargetRandomSampleGenerator() {
        return new EnvironmentalTarget()
            .id(longCount.incrementAndGet())
            .co2MaxPpm(intCount.incrementAndGet())
            .lightLux(intCount.incrementAndGet())
            .freshAirExchangesPerHour(intCount.incrementAndGet());
    }
}
