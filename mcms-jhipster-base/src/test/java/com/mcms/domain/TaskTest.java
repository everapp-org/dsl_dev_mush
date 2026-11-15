package com.mcms.domain;

import static com.mcms.domain.BatchTestSamples.*;
import static com.mcms.domain.RoomTestSamples.*;
import static com.mcms.domain.TaskTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mcms.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TaskTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Task.class);
        Task task1 = getTaskSample1();
        Task task2 = new Task();
        assertThat(task1).isNotEqualTo(task2);

        task2.setId(task1.getId());
        assertThat(task1).isEqualTo(task2);

        task2 = getTaskSample2();
        assertThat(task1).isNotEqualTo(task2);
    }

    @Test
    void batchTest() {
        Task task = getTaskRandomSampleGenerator();
        Batch batchBack = getBatchRandomSampleGenerator();

        task.setBatch(batchBack);
        assertThat(task.getBatch()).isEqualTo(batchBack);

        task.batch(null);
        assertThat(task.getBatch()).isNull();
    }

    @Test
    void roomTest() {
        Task task = getTaskRandomSampleGenerator();
        Room roomBack = getRoomRandomSampleGenerator();

        task.setRoom(roomBack);
        assertThat(task.getRoom()).isEqualTo(roomBack);

        task.room(null);
        assertThat(task.getRoom()).isNull();
    }
}
