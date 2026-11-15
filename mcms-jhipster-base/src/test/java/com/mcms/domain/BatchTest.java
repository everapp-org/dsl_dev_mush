package com.mcms.domain;

import static com.mcms.domain.BatchTestSamples.*;
import static com.mcms.domain.StrainTestSamples.*;
import static com.mcms.domain.SubstrateRecipeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mcms.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BatchTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Batch.class);
        Batch batch1 = getBatchSample1();
        Batch batch2 = new Batch();
        assertThat(batch1).isNotEqualTo(batch2);

        batch2.setId(batch1.getId());
        assertThat(batch1).isEqualTo(batch2);

        batch2 = getBatchSample2();
        assertThat(batch1).isNotEqualTo(batch2);
    }

    @Test
    void strainTest() {
        Batch batch = getBatchRandomSampleGenerator();
        Strain strainBack = getStrainRandomSampleGenerator();

        batch.setStrain(strainBack);
        assertThat(batch.getStrain()).isEqualTo(strainBack);

        batch.strain(null);
        assertThat(batch.getStrain()).isNull();
    }

    @Test
    void recipeTest() {
        Batch batch = getBatchRandomSampleGenerator();
        SubstrateRecipe substrateRecipeBack = getSubstrateRecipeRandomSampleGenerator();

        batch.setRecipe(substrateRecipeBack);
        assertThat(batch.getRecipe()).isEqualTo(substrateRecipeBack);

        batch.recipe(null);
        assertThat(batch.getRecipe()).isNull();
    }
}
