package com.mcms.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ContaminationEventTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static ContaminationEvent getContaminationEventSample1() {
        return new ContaminationEvent().id(1L).affectedBags(1).detectedBy("detectedBy1").photosReference("photosReference1");
    }

    public static ContaminationEvent getContaminationEventSample2() {
        return new ContaminationEvent().id(2L).affectedBags(2).detectedBy("detectedBy2").photosReference("photosReference2");
    }

    public static ContaminationEvent getContaminationEventRandomSampleGenerator() {
        return new ContaminationEvent()
            .id(longCount.incrementAndGet())
            .affectedBags(intCount.incrementAndGet())
            .detectedBy(UUID.randomUUID().toString())
            .photosReference(UUID.randomUUID().toString());
    }
}
