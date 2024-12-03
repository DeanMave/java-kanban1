package tracker.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.model.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private InMemoryHistoryManager historyManager;
    private Task task;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void addToHistoryTest() {
        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW);
        historyManager.addTask(task);
        List<Task> tasks = historyManager.getHistory();
        List<Task> tasksExp = new ArrayList<>();
        tasksExp.add(task);
        assertEquals(tasksExp, tasks);
    }

    @Test
    void shouldBeNullIfDublicate() {
        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW);
        historyManager.addTask(task);
        List<Task> tasks = historyManager.getHistory();
        historyManager.addTask(task);
        List<Task> tasks2 = historyManager.getHistory();
        List<Task> tasksExp = new ArrayList<>();
        tasksExp.add(task);
        assertEquals(tasksExp, tasks2);
    }

}