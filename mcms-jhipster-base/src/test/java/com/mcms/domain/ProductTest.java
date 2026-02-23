package com.mcms.domain;

import static com.mcms.domain.ProductTestSamples.*;
import static com.mcms.domain.StrainTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mcms.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Product.class);
        Product product1 = getProductSample1();
        Product product2 = new Product();
        assertThat(product1).isNotEqualTo(product2);

        product2.setId(product1.getId());
        assertThat(product1).isEqualTo(product2);

        product2 = getProductSample2();
        assertThat(product1).isNotEqualTo(product2);
    }

    @Test
    void strainTest() {
        Product product = getProductRandomSampleGenerator();
        Strain strainBack = getStrainRandomSampleGenerator();

        product.setStrain(strainBack);
        assertThat(product.getStrain()).isEqualTo(strainBack);

        product.strain(null);
        assertThat(product.getStrain()).isNull();
    }
}
