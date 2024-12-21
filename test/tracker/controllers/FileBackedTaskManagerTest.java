package tracker.controllers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.enums.TaskStatus;
import tracker.model.*;

import java.io.*;
import java.nio.file.Files;

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
        String task2 = fileBackedTaskManager.toString(task1);
        String task22 = "0,TASK,Task1,NEW,description1,";
        assertEquals(task22, task2);
    }

    @Test
    void testFromString() {
        String valueTask = "9,TASK,Task1,NEW,description1";
        Task task = fileBackedTaskManager.fromString(valueTask);
        Task task2 = new Task("Task1", "description1", TaskStatus.NEW);
        task2.setId(9);
        assertEquals(task2, task);
    }

    @Test
    void testTextToFile() {
        Task task1 = new Task("Task1", "description1", TaskStatus.NEW);
        Integer taskId1 = fileBackedTaskManager.addNewTask(task1);
        fileBackedTaskManager.save(task1);
        String fileContent = null;
        try {
            fileContent = Files.readString(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String expectContent = "id,type,name,status,description,epicId\n" +
                "0,TASK,Task1,NEW,description1,\n";
        assertEquals(expectContent, fileContent);
    }

    @Test
    void testDuplicateTextToFile() {
        Task task1 = new Task("Task1", "description1", TaskStatus.NEW);
        Task task2 = new Task("Task1", "description1", TaskStatus.NEW);
        fileBackedTaskManager.save(task1);
        fileBackedTaskManager.save(task2);
        String fileContent = null;
        try {
            fileContent = Files.readString(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String expectContent = "id,type,name,status,description,epicId\n" +
                "0,TASK,Task1,NEW,description1,\n";
        assertEquals(expectContent, fileContent);
    }
}