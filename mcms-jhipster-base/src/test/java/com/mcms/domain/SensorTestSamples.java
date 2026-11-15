package com.mcms.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SensorTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Sensor getSensorSample1() {
        return new Sensor().id(1L).sensorCode("sensorCode1").manufacturer("manufacturer1").model("model1");
    }

    public static Sensor getSensorSample2() {
        return new Sensor().id(2L).sensorCode("sensorCode2").manufacturer("manufacturer2").model("model2");
    }

    public static Sensor getSensorRandomSampleGenerator() {
        return new Sensor()
            .id(longCount.incrementAndGet())
            .sensorCode(UUID.randomUUID().toString())
            .manufacturer(UUID.randomUUID().toString())
            .model(UUID.randomUUID().toString());
    }
}
