package com.mcms.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class FlushCycleTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static FlushCycle getFlushCycleSample1() {
        return new FlushCycle().id(1L).flushNumber(1).yieldBagsHarvested(1).rehydrationDurationHours(1);
    }

    public static FlushCycle getFlushCycleSample2() {
        return new FlushCycle().id(2L).flushNumber(2).yieldBagsHarvested(2).rehydrationDurationHours(2);
    }

    public static FlushCycle getFlushCycleRandomSampleGenerator() {
        return new FlushCycle()
            .id(longCount.incrementAndGet())
            .flushNumber(intCount.incrementAndGet())
            .yieldBagsHarvested(intCount.incrementAndGet())
            .rehydrationDurationHours(intCount.incrementAndGet());
    }
}
