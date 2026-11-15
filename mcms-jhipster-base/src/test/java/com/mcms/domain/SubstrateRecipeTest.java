package com.mcms.domain;

import static com.mcms.domain.SubstrateRecipeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mcms.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SubstrateRecipeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SubstrateRecipe.class);
        SubstrateRecipe substrateRecipe1 = getSubstrateRecipeSample1();
        SubstrateRecipe substrateRecipe2 = new SubstrateRecipe();
        assertThat(substrateRecipe1).isNotEqualTo(substrateRecipe2);

        substrateRecipe2.setId(substrateRecipe1.getId());
        assertThat(substrateRecipe1).isEqualTo(substrateRecipe2);

        substrateRecipe2 = getSubstrateRecipeSample2();
        assertThat(substrateRecipe1).isNotEqualTo(substrateRecipe2);
    }
}
