package com.mcms.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class MandatoryFieldCheckTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static MandatoryFieldCheck getMandatoryFieldCheckSample1() {
        return new MandatoryFieldCheck().id(1L).fieldName("fieldName1");
    }

    public static MandatoryFieldCheck getMandatoryFieldCheckSample2() {
        return new MandatoryFieldCheck().id(2L).fieldName("fieldName2");
    }

    public static MandatoryFieldCheck getMandatoryFieldCheckRandomSampleGenerator() {
        return new MandatoryFieldCheck().id(longCount.incrementAndGet()).fieldName(UUID.randomUUID().toString());
    }
}
