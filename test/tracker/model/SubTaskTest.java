package tracker.model;

import org.junit.jupiter.api.Test;
import tracker.enums.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {
    @Test
    public void getName() {
        SubTask subTask = new SubTask("Subtask 1", "Description 1", TaskStatus.NEW, 1);
        assertEquals("Subtask 1", subTask.getName());
    }

    @Test
    public void setName() {
        SubTask subTask = new SubTask("Subtask 1", "Description 1", TaskStatus.NEW, 1);
        subTask.setName("New Epic Name");
        assertEquals("New Epic Name", subTask.getName());
    }

    @Test
    public void getDescription() {
        SubTask subTask = new SubTask("Subtask 1", "Description 1", TaskStatus.NEW, 1);
        assertEquals("Description 1", subTask.getDescription());
    }

    @Test
    public void setDescription() {
        SubTask subTask = new SubTask("Subtask 1", "Description 1", TaskStatus.NEW, 1);
        subTask.setDescription("New Description");
        assertEquals("New Description", subTask.getDescription());
    }

    @Test
    public void testToString() {
        SubTask subTask = new SubTask("Subtask 1", "Description 1", TaskStatus.NEW, 1);
        String expected = "0,Subtask 1,Description 1,NEW,null,null,1";
        assertEquals(expected, subTask.toString());
    }

    @Test
    public void testTimeOfSubTask() {
        SubTask subTask = new SubTask("Subtask 1", "Description 1", TaskStatus.NEW, 1);
        subTask.setDuration(Duration.ofMinutes(50));
        subTask.setStartTime(LocalDateTime.of(2025, Month.JULY, 9, 11, 40));
        LocalDateTime startTimeExp = LocalDateTime.of(2025, Month.JULY, 9, 11, 40);
        Duration durationExp = Duration.ofMinutes(50);
        assertEquals(startTimeExp, subTask.getStartTime());
        assertEquals(durationExp, subTask.getDuration());
    }


}