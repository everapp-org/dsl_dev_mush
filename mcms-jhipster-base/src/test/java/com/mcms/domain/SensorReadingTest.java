package com.mcms.domain;

import static com.mcms.domain.SensorReadingTestSamples.*;
import static com.mcms.domain.SensorTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mcms.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SensorReadingTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SensorReading.class);
        SensorReading sensorReading1 = getSensorReadingSample1();
        SensorReading sensorReading2 = new SensorReading();
        assertThat(sensorReading1).isNotEqualTo(sensorReading2);

        sensorReading2.setId(sensorReading1.getId());
        assertThat(sensorReading1).isEqualTo(sensorReading2);

        sensorReading2 = getSensorReadingSample2();
        assertThat(sensorReading1).isNotEqualTo(sensorReading2);
    }

    @Test
    void sensorTest() {
        SensorReading sensorReading = getSensorReadingRandomSampleGenerator();
        Sensor sensorBack = getSensorRandomSampleGenerator();

        sensorReading.setSensor(sensorBack);
        assertThat(sensorReading.getSensor()).isEqualTo(sensorBack);

        sensorReading.sensor(null);
        assertThat(sensorReading.getSensor()).isNull();
    }
}
