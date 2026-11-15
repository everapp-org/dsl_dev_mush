package com.mcms.domain;

import static com.mcms.domain.BatchTestSamples.*;
import static com.mcms.domain.ContaminationEventTestSamples.*;
import static com.mcms.domain.PhaseExecutionTestSamples.*;
import static com.mcms.domain.RoomTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mcms.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ContaminationEventTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ContaminationEvent.class);
        ContaminationEvent contaminationEvent1 = getContaminationEventSample1();
        ContaminationEvent contaminationEvent2 = new ContaminationEvent();
        assertThat(contaminationEvent1).isNotEqualTo(contaminationEvent2);

        contaminationEvent2.setId(contaminationEvent1.getId());
        assertThat(contaminationEvent1).isEqualTo(contaminationEvent2);

        contaminationEvent2 = getContaminationEventSample2();
        assertThat(contaminationEvent1).isNotEqualTo(contaminationEvent2);
    }

    @Test
    void batchTest() {
        ContaminationEvent contaminationEvent = getContaminationEventRandomSampleGenerator();
        Batch batchBack = getBatchRandomSampleGenerator();

        contaminationEvent.setBatch(batchBack);
        assertThat(contaminationEvent.getBatch()).isEqualTo(batchBack);

        contaminationEvent.batch(null);
        assertThat(contaminationEvent.getBatch()).isNull();
    }

    @Test
    void phaseExecutionTest() {
        ContaminationEvent contaminationEvent = getContaminationEventRandomSampleGenerator();
        PhaseExecution phaseExecutionBack = getPhaseExecutionRandomSampleGenerator();

        contaminationEvent.setPhaseExecution(phaseExecutionBack);
        assertThat(contaminationEvent.getPhaseExecution()).isEqualTo(phaseExecutionBack);

        contaminationEvent.phaseExecution(null);
        assertThat(contaminationEvent.getPhaseExecution()).isNull();
    }

    @Test
    void roomTest() {
        ContaminationEvent contaminationEvent = getContaminationEventRandomSampleGenerator();
        Room roomBack = getRoomRandomSampleGenerator();

        contaminationEvent.setRoom(roomBack);
        assertThat(contaminationEvent.getRoom()).isEqualTo(roomBack);

        contaminationEvent.room(null);
        assertThat(contaminationEvent.getRoom()).isNull();
    }
}
