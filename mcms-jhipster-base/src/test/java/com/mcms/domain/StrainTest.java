package com.mcms.domain;

import static com.mcms.domain.StrainTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mcms.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StrainTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Strain.class);
        Strain strain1 = getStrainSample1();
        Strain strain2 = new Strain();
        assertThat(strain1).isNotEqualTo(strain2);

        strain2.setId(strain1.getId());
        assertThat(strain1).isEqualTo(strain2);

        strain2 = getStrainSample2();
        assertThat(strain1).isNotEqualTo(strain2);
    }
}
