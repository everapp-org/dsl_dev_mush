package com.mcms.domain;

import static com.mcms.domain.RoomTestSamples.*;
import static com.mcms.domain.SensorTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mcms.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SensorTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Sensor.class);
        Sensor sensor1 = getSensorSample1();
        Sensor sensor2 = new Sensor();
        assertThat(sensor1).isNotEqualTo(sensor2);

        sensor2.setId(sensor1.getId());
        assertThat(sensor1).isEqualTo(sensor2);

        sensor2 = getSensorSample2();
        assertThat(sensor1).isNotEqualTo(sensor2);
    }

    @Test
    void roomTest() {
        Sensor sensor = getSensorRandomSampleGenerator();
        Room roomBack = getRoomRandomSampleGenerator();

        sensor.setRoom(roomBack);
        assertThat(sensor.getRoom()).isEqualTo(roomBack);

        sensor.room(null);
        assertThat(sensor.getRoom()).isNull();
    }
}
