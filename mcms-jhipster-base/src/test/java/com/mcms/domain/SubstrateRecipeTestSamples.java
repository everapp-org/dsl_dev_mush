package com.mcms.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SubstrateRecipeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static SubstrateRecipe getSubstrateRecipeSample1() {
        return new SubstrateRecipe().id(1L).name("name1").version("version1").sterilizationMethod("sterilizationMethod1");
    }

    public static SubstrateRecipe getSubstrateRecipeSample2() {
        return new SubstrateRecipe().id(2L).name("name2").version("version2").sterilizationMethod("sterilizationMethod2");
    }

    public static SubstrateRecipe getSubstrateRecipeRandomSampleGenerator() {
        return new SubstrateRecipe()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .version(UUID.randomUUID().toString())
            .sterilizationMethod(UUID.randomUUID().toString());
    }
}
