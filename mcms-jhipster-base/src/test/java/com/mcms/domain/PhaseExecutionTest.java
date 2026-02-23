package com.mcms.domain;

import static com.mcms.domain.BatchTestSamples.*;
import static com.mcms.domain.PhaseExecutionTestSamples.*;
import static com.mcms.domain.RoomTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mcms.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PhaseExecutionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PhaseExecution.class);
        PhaseExecution phaseExecution1 = getPhaseExecutionSample1();
        PhaseExecution phaseExecution2 = new PhaseExecution();
        assertThat(phaseExecution1).isNotEqualTo(phaseExecution2);

        phaseExecution2.setId(phaseExecution1.getId());
        assertThat(phaseExecution1).isEqualTo(phaseExecution2);

        phaseExecution2 = getPhaseExecutionSample2();
        assertThat(phaseExecution1).isNotEqualTo(phaseExecution2);
    }

    @Test
    void batchTest() {
        PhaseExecution phaseExecution = getPhaseExecutionRandomSampleGenerator();
        Batch batchBack = getBatchRandomSampleGenerator();

        phaseExecution.setBatch(batchBack);
        assertThat(phaseExecution.getBatch()).isEqualTo(batchBack);

        phaseExecution.batch(null);
        assertThat(phaseExecution.getBatch()).isNull();
    }

    @Test
    void roomTest() {
        PhaseExecution phaseExecution = getPhaseExecutionRandomSampleGenerator();
        Room roomBack = getRoomRandomSampleGenerator();

        phaseExecution.setRoom(roomBack);
        assertThat(phaseExecution.getRoom()).isEqualTo(roomBack);

        phaseExecution.room(null);
        assertThat(phaseExecution.getRoom()).isNull();
    }
}
