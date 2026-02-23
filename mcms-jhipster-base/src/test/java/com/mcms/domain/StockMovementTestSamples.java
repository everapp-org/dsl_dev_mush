package com.mcms.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class StockMovementTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static StockMovement getStockMovementSample1() {
        return new StockMovement().id(1L).reference("reference1").reason("reason1").performedBy("performedBy1");
    }

    public static StockMovement getStockMovementSample2() {
        return new StockMovement().id(2L).reference("reference2").reason("reason2").performedBy("performedBy2");
    }

    public static StockMovement getStockMovementRandomSampleGenerator() {
        return new StockMovement()
            .id(longCount.incrementAndGet())
            .reference(UUID.randomUUID().toString())
            .reason(UUID.randomUUID().toString())
            .performedBy(UUID.randomUUID().toString());
    }
}
