package tracker.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.model.*;

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
        task.setId(1); // Устанавливаем уникальный ID
        Task task1 = new Task("Task 2", "Description 2", TaskStatus.NEW);
        task1.setId(2); // Устанавливаем уникальный ID

        historyManager.addTask(task);
        historyManager.addTask(task1);

        List<Task> tasks = historyManager.getHistory();
        List<Task> tasksExp = new ArrayList<>();
        tasksExp.add(task);
        tasksExp.add(task1);

        assertEquals(tasksExp, tasks); // Теперь тест пройдет успешно
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