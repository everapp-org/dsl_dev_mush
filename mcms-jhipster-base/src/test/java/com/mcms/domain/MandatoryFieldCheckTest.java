package com.mcms.domain;

import static com.mcms.domain.MandatoryFieldCheckTestSamples.*;
import static com.mcms.domain.PhaseExecutionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mcms.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MandatoryFieldCheckTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MandatoryFieldCheck.class);
        MandatoryFieldCheck mandatoryFieldCheck1 = getMandatoryFieldCheckSample1();
        MandatoryFieldCheck mandatoryFieldCheck2 = new MandatoryFieldCheck();
        assertThat(mandatoryFieldCheck1).isNotEqualTo(mandatoryFieldCheck2);

        mandatoryFieldCheck2.setId(mandatoryFieldCheck1.getId());
        assertThat(mandatoryFieldCheck1).isEqualTo(mandatoryFieldCheck2);

        mandatoryFieldCheck2 = getMandatoryFieldCheckSample2();
        assertThat(mandatoryFieldCheck1).isNotEqualTo(mandatoryFieldCheck2);
    }

    @Test
    void phaseExecutionTest() {
        MandatoryFieldCheck mandatoryFieldCheck = getMandatoryFieldCheckRandomSampleGenerator();
        PhaseExecution phaseExecutionBack = getPhaseExecutionRandomSampleGenerator();

        mandatoryFieldCheck.setPhaseExecution(phaseExecutionBack);
        assertThat(mandatoryFieldCheck.getPhaseExecution()).isEqualTo(phaseExecutionBack);

        mandatoryFieldCheck.phaseExecution(null);
        assertThat(mandatoryFieldCheck.getPhaseExecution()).isNull();
    }
}
