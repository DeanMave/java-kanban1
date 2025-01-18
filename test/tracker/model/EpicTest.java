package tracker.model;

import org.junit.jupiter.api.Test;
import tracker.controllers.InMemoryTaskManager;
import tracker.enums.TaskStatus;
import tracker.interfaces.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {
    @Test
    public void getName() {
        Epic epic = new Epic("Epic 1", "Description 1");
        assertEquals("Epic 1", epic.getName());
    }

    @Test
    public void setName() {
        Epic epic = new Epic("Epic 1", "Description 1");
        epic.setName("New Epic Name");
        assertEquals("New Epic Name", epic.getName());
    }

    @Test
    public void getDescription() {
        Epic epic = new Epic("Epic 1", "Description 1");
        assertEquals("Description 1", epic.getDescription());
    }

    @Test
    public void setDescription() {
        Epic epic = new Epic("Epic 1", "Description 1");
        epic.setDescription("New Description");
        assertEquals("New Description", epic.getDescription());
    }

    @Test
    public void testToString() {
        Epic epic = new Epic("Epic 1", "Description 1");
        String expected = "0,Epic 1,Description 1,NEW,null,null";
        assertEquals(expected, epic.toString());
    }

    @Test
    public void testTimeOfEpic() {
        TaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic("Epic 1", "Description 1");
        int epicId = manager.addNewEpic(epic);
        SubTask subTask1 = new SubTask("Subtask 1", "Description 1", TaskStatus.NEW, epicId);
        subTask1.setStartTime(LocalDateTime.of(2025, Month.JULY, 9, 10, 15));
        subTask1.setDuration(Duration.ofMinutes(20));
        int subTaskId1 = manager.addNewSubTask(subTask1);
        SubTask subTask2 = new SubTask("Subtask 2", "Description 2", TaskStatus.NEW, epicId);
        subTask2.setStartTime(LocalDateTime.of(2025, Month.JULY, 9, 11, 40));
        subTask2.setDuration(Duration.ofMinutes(30));
        int subTaskId2 = manager.addNewSubTask(subTask2);
        SubTask subTask3 = new SubTask("Subtask 3", "Description 3", TaskStatus.NEW, epicId);
        subTask3.setStartTime(LocalDateTime.of(2025, Month.JULY, 9, 13, 0));
        subTask3.setDuration(Duration.ofMinutes(50));
        int subTaskId3 = manager.addNewSubTask(subTask3);
        List<SubTask> subTasks = manager.getEpicSubtasks(epicId);
        epic.getEndTimeOfEpics(subTasks);
        LocalDateTime startTimeExp = LocalDateTime.of(2025, Month.JULY, 9, 10, 15);
        LocalDateTime endTimeExp = LocalDateTime.of(2025, Month.JULY, 9, 13, 50);
        Duration durationExp = Duration.ofMinutes(100);
        assertEquals(startTimeExp, epic.getStartTime());
        assertEquals(endTimeExp, epic.getEndTime());
        assertEquals(durationExp, epic.getDuration());
    }
}