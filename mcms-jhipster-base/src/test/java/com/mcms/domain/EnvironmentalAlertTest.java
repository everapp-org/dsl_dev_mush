package com.mcms.domain;

import static com.mcms.domain.BatchTestSamples.*;
import static com.mcms.domain.EnvironmentalAlertTestSamples.*;
import static com.mcms.domain.RoomTestSamples.*;
import static com.mcms.domain.SensorTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mcms.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EnvironmentalAlertTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(EnvironmentalAlert.class);
        EnvironmentalAlert environmentalAlert1 = getEnvironmentalAlertSample1();
        EnvironmentalAlert environmentalAlert2 = new EnvironmentalAlert();
        assertThat(environmentalAlert1).isNotEqualTo(environmentalAlert2);

        environmentalAlert2.setId(environmentalAlert1.getId());
        assertThat(environmentalAlert1).isEqualTo(environmentalAlert2);

        environmentalAlert2 = getEnvironmentalAlertSample2();
        assertThat(environmentalAlert1).isNotEqualTo(environmentalAlert2);
    }

    @Test
    void roomTest() {
        EnvironmentalAlert environmentalAlert = getEnvironmentalAlertRandomSampleGenerator();
        Room roomBack = getRoomRandomSampleGenerator();

        environmentalAlert.setRoom(roomBack);
        assertThat(environmentalAlert.getRoom()).isEqualTo(roomBack);

        environmentalAlert.room(null);
        assertThat(environmentalAlert.getRoom()).isNull();
    }

    @Test
    void sensorTest() {
        EnvironmentalAlert environmentalAlert = getEnvironmentalAlertRandomSampleGenerator();
        Sensor sensorBack = getSensorRandomSampleGenerator();

        environmentalAlert.setSensor(sensorBack);
        assertThat(environmentalAlert.getSensor()).isEqualTo(sensorBack);

        environmentalAlert.sensor(null);
        assertThat(environmentalAlert.getSensor()).isNull();
    }

    @Test
    void batchTest() {
        EnvironmentalAlert environmentalAlert = getEnvironmentalAlertRandomSampleGenerator();
        Batch batchBack = getBatchRandomSampleGenerator();

        environmentalAlert.setBatch(batchBack);
        assertThat(environmentalAlert.getBatch()).isEqualTo(batchBack);

        environmentalAlert.batch(null);
        assertThat(environmentalAlert.getBatch()).isNull();
    }
}
