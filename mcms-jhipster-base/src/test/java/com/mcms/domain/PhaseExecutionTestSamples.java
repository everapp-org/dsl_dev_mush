package com.mcms.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class PhaseExecutionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static PhaseExecution getPhaseExecutionSample1() {
        return new PhaseExecution()
            .id(1L)
            .sequenceOrder(1)
            .plannedDurationDays(1)
            .actualDurationDays(1)
            .responsiblePerson("responsiblePerson1");
    }

    public static PhaseExecution getPhaseExecutionSample2() {
        return new PhaseExecution()
            .id(2L)
            .sequenceOrder(2)
            .plannedDurationDays(2)
            .actualDurationDays(2)
            .responsiblePerson("responsiblePerson2");
    }

    public static PhaseExecution getPhaseExecutionRandomSampleGenerator() {
        return new PhaseExecution()
            .id(longCount.incrementAndGet())
            .sequenceOrder(intCount.incrementAndGet())
            .plannedDurationDays(intCount.incrementAndGet())
            .actualDurationDays(intCount.incrementAndGet())
            .responsiblePerson(UUID.randomUUID().toString());
    }
}
