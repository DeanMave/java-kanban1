package tracker.model;

import org.junit.jupiter.api.Test;
import tracker.enums.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    @Test
    void getName() {
        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW);
        assertEquals("Task 1", task.getName());
    }

    @Test
    void setName() {
        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW);
        task.setName("New Task Name");
        assertEquals("New Task Name", task.getName());
    }

    @Test
    void getDescription() {
        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW);
        assertEquals("Description 1", task.getDescription());
    }

    @Test
    void setDescription() {
        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW);
        task.setDescription("New Description");
        assertEquals("New Description", task.getDescription());
    }

    @Test
    void setStatus() {
        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW);
        task.setStatus(TaskStatus.IN_PROGRESS);
        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
    }

    @Test
    void getStatus() {
        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW);
        assertEquals(TaskStatus.NEW, task.getStatus());
    }

    @Test
    void testToString() {
        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW);
        String expected = "0,Task 1,Description 1,NEW,null,null";
        assertEquals(expected, task.toString());
    }

    @Test
    void testTimeOfTask() {
        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW);
        task.setDuration(Duration.ofMinutes(5));
        task.setStartTime(LocalDateTime.of(2025, Month.JULY, 9, 1, 0));
        LocalDateTime endTime = task.getEndTime();
        LocalDateTime endTimeExp = LocalDateTime.of(2025, Month.JULY, 9, 1, 5);
        assertEquals(endTimeExp, endTime);
    }

}