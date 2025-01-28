package tracker.controllers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.enums.TaskStatus;
import tracker.model.*;

import java.io.*;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    private FileBackedTaskManager fileBackedTaskManager;
    private File file;

    @BeforeEach
    void setUp() throws IOException {
        file = File.createTempFile("text", ".csv");
        fileBackedTaskManager = new FileBackedTaskManager(file);
    }

    @AfterEach
    void tearDown() {
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void testToString() {
        Task task1 = new Task("Task1", "description1", TaskStatus.NEW);
        task1.setDuration(Duration.ofMinutes(50));
        task1.setStartTime(LocalDateTime.of(2025, Month.JULY, 9, 10, 15));
        LocalDateTime endTIme = task1.getEndTime();
        SubTask subTask = new SubTask("SubTask1", "description1", TaskStatus.NEW, 1);
        subTask.setDuration(Duration.ofMinutes(50));
        subTask.setStartTime(LocalDateTime.of(2025, Month.JULY, 9, 10, 15));
        LocalDateTime endTimeOfSubtask = subTask.getEndTime();
        String task2 = fileBackedTaskManager.toString(task1);
        String task22 = "0,TASK,Task1,NEW,description1,50,2025-07-09T10:15,2025-07-09T11:05,";
        assertEquals(task22, task2);
        String subTask1 = fileBackedTaskManager.toString(subTask);
        String subTask11 = "0,SUBTASK,SubTask1,NEW,description1,1,50,2025-07-09T10:15,2025-07-09T11:05,1";
        assertEquals(subTask11, subTask1);
    }

    @Test
    void testFromString() {
        String valueTask = "9,TASK,Task1,NEW,description1,50,2025-07-09T10:15";
        //                "id,type,name,status,description,duration,startTime,epicId\n"
        Task task = fileBackedTaskManager.fromString(valueTask);
        Task task2 = new Task("Task1", "description1", TaskStatus.NEW, Duration.ofMinutes(50)
                , LocalDateTime.of(2025, Month.JULY, 9, 10, 15));
        task2.setId(9);
        assertEquals(task2, task);
        String valueSubTask = "9,SUBTASK,SubTask11,NEW,description11,50,2025-07-09T10:15,10";
        Task subTask = fileBackedTaskManager.fromString(valueSubTask);
        SubTask subTask2 = new SubTask("SubTask11", "description11", TaskStatus.NEW, 10);
        subTask2.setId(9);
        subTask2.setDuration(Duration.ofMinutes(50));
        subTask2.setStartTime(LocalDateTime.of(2025, Month.JULY, 9, 10, 15));
        assertEquals(subTask2, subTask);
    }

    @Test
    void testTextToFile() {
        Task task1 = new Task("Task1", "description1", TaskStatus.NEW, Duration.ofMinutes(50)
                , LocalDateTime.of(2025, Month.JULY, 9, 10, 15));
        Integer taskId1 = fileBackedTaskManager.addNewTask(task1);
        fileBackedTaskManager.save(task1);
        String fileContent = null;
        try {
            fileContent = Files.readString(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String expectContent = "id,type,name,status,description,duration,startTime,endTime,epicId\n" +
                "0,TASK,Task1,NEW,description1,50,2025-07-09T10:15,2025-07-09T11:05,\n";
        assertEquals(expectContent, fileContent);
    }

    @Test
    void testDuplicateTextToFile() {
        Task task1 = new Task("Task1", "description1", TaskStatus.NEW, Duration.ofMinutes(50)
                , LocalDateTime.of(2025, Month.JULY, 9, 10, 15));
        Task task2 = new Task("Task1", "description1", TaskStatus.NEW, Duration.ofMinutes(50)
                , LocalDateTime.of(2025, Month.JULY, 9, 10, 15));
        fileBackedTaskManager.save(task1);
        fileBackedTaskManager.save(task2);
        String fileContent = null;
        try {
            fileContent = Files.readString(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String expectContent = "id,type,name,status,description,duration,startTime,endTime,epicId\n" +
                "0,TASK,Task1,NEW,description1,50,2025-07-09T10:15,2025-07-09T11:05,\n";
        assertEquals(expectContent, fileContent);
    }
}