package com.mcms.domain;

import static com.mcms.domain.EnvironmentalTargetTestSamples.*;
import static com.mcms.domain.RoomTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mcms.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EnvironmentalTargetTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(EnvironmentalTarget.class);
        EnvironmentalTarget environmentalTarget1 = getEnvironmentalTargetSample1();
        EnvironmentalTarget environmentalTarget2 = new EnvironmentalTarget();
        assertThat(environmentalTarget1).isNotEqualTo(environmentalTarget2);

        environmentalTarget2.setId(environmentalTarget1.getId());
        assertThat(environmentalTarget1).isEqualTo(environmentalTarget2);

        environmentalTarget2 = getEnvironmentalTargetSample2();
        assertThat(environmentalTarget1).isNotEqualTo(environmentalTarget2);
    }

    @Test
    void roomTest() {
        EnvironmentalTarget environmentalTarget = getEnvironmentalTargetRandomSampleGenerator();
        Room roomBack = getRoomRandomSampleGenerator();

        environmentalTarget.setRoom(roomBack);
        assertThat(environmentalTarget.getRoom()).isEqualTo(roomBack);

        environmentalTarget.room(null);
        assertThat(environmentalTarget.getRoom()).isNull();
    }
}
