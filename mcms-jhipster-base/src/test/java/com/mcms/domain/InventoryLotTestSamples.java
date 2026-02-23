package com.mcms.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class InventoryLotTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static InventoryLot getInventoryLotSample1() {
        return new InventoryLot().id(1L).lotCode("lotCode1").storageLocation("storageLocation1").supplierLotNumber("supplierLotNumber1");
    }

    public static InventoryLot getInventoryLotSample2() {
        return new InventoryLot().id(2L).lotCode("lotCode2").storageLocation("storageLocation2").supplierLotNumber("supplierLotNumber2");
    }

    public static InventoryLot getInventoryLotRandomSampleGenerator() {
        return new InventoryLot()
            .id(longCount.incrementAndGet())
            .lotCode(UUID.randomUUID().toString())
            .storageLocation(UUID.randomUUID().toString())
            .supplierLotNumber(UUID.randomUUID().toString());
    }
}
