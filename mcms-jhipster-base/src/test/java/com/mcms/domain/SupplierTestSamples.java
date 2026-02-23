package com.mcms.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SupplierTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Supplier getSupplierSample1() {
        return new Supplier().id(1L).name("name1").contactPerson("contactPerson1").email("email1").phone("phone1").rating(1);
    }

    public static Supplier getSupplierSample2() {
        return new Supplier().id(2L).name("name2").contactPerson("contactPerson2").email("email2").phone("phone2").rating(2);
    }

    public static Supplier getSupplierRandomSampleGenerator() {
        return new Supplier()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .contactPerson(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .phone(UUID.randomUUID().toString())
            .rating(intCount.incrementAndGet());
    }
}
