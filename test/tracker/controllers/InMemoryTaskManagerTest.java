package tracker.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.model.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private InMemoryTaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void createTaskTest() {
        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW);
        int taskId = taskManager.addNewTask(task);
        assertEquals(0, taskId);
        Task task2 = new Task("Task 1", "Description 1", TaskStatus.NEW);
        int taskId2 = taskManager.addNewTask(task);
        assertEquals(1, taskId2);
        Task retrievedTask = taskManager.getTask(taskId);
        assertEquals(task, retrievedTask);
    }

    @Test
    void createAndRetrieveEpicWithSubtasksTest() {
        Epic epic = new Epic("Epic 1", "Description 1");
        int epicId = taskManager.addNewEpic(epic);
        assertEquals(0, epicId);
        SubTask subTask1 = new SubTask("Subtask 1", "Description 1", TaskStatus.NEW, epicId);
        int subTaskId1 = taskManager.addNewSubTask(subTask1);
        assertEquals(1, subTaskId1);
        SubTask subTask2 = new SubTask("Subtask 2", "Description 2", TaskStatus.NEW, epicId);
        int subTaskId2 = taskManager.addNewSubTask(subTask2);
        assertEquals(2, subTaskId2);
        List<SubTask> subTasks = taskManager.getSubtasks();
        assertEquals(2, subTasks.size());
        List<SubTask> retrievedSubtasks = new ArrayList<>();
        for (SubTask s : subTasks) {
            if (s.getEpicId() == epicId) {
                retrievedSubtasks.add(s);
            }
        }
        assertEquals(2, retrievedSubtasks.size());
        SubTask retrievedSubtask1 = retrievedSubtasks.get(0);
        SubTask retrievedSubtask2 = retrievedSubtasks.get(1);
        assertEquals(subTask1, retrievedSubtask1);
        assertEquals(subTask2, retrievedSubtask2);
    }

    @Test
    void updateTaskStatusTest() {
        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW);
        task.setStatus(TaskStatus.DONE);
        assertEquals(TaskStatus.DONE, task.getStatus());
    }

    @Test
    void deleteTaskByIdTest() {
        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW);
        final int taskId = taskManager.addNewTask(task);
        taskManager.deleteTask(taskId);
        List<Task> tasks = taskManager.getTasks();
        assertEquals(0, tasks.size());
    }

    @Test
    void updateSubtaskAndEpicStatusTest() {
        Epic epic = new Epic("Epic 1", "Description 1");
        int epicId = taskManager.addNewEpic(epic);
        SubTask subTask1 = new SubTask("Subtask 1", "Description 1", TaskStatus.NEW, epicId);
        int subTaskId1 = taskManager.addNewSubTask(subTask1);
        taskManager.updateEpicStatus(epicId);
        assertEquals(subTask1.getStatus(), epic.getStatus());
        subTask1.setStatus(TaskStatus.DONE);
        taskManager.updateEpicStatus(epicId);
        assertEquals(subTask1.getStatus(), epic.getStatus());
    }

    @Test
    void deleteAllTasksTest() {
        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW);
        final int taskId = taskManager.addNewTask(task);
        taskManager.deleteTasks();
        List<Task> tasks = taskManager.getTasks();
        assertEquals(0, tasks.size());
    }

    @Test
    void deleteSubtaskByIdTest() {
        Epic epic = new Epic("Epic 1", "Description 1");
        int epicId = taskManager.addNewEpic(epic);
        SubTask subTask1 = new SubTask("Subtask 1", "Description 1", TaskStatus.NEW, epicId);
        int subTaskId1 = taskManager.addNewSubTask(subTask1);
        taskManager.deleteSubtask(subTaskId1);
        List<SubTask> subTasks = taskManager.getSubtasks();
        assertEquals(0, subTasks.size());
    }

    @Test
    void deleteAllEpicsTest() {
        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.deleteEpics();
        List<Epic> epics = taskManager.getEpics();
        assertEquals(0, epics.size());
    }

    @Test
    void getHistoryTest() {
        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW);
        int taskId = taskManager.addNewTask(task);
        Epic epic = new Epic("Epic 1", "Description 1");
        int epicId = taskManager.addNewEpic(epic);
        taskManager.getTask(taskId);
        taskManager.getEpic(epicId);
        taskManager.getHistory();
        List<Task> history = taskManager.getHistory();
        List<Task> historyExp = new ArrayList<>();
        historyExp.add(task);
        historyExp.add(epic);
        assertEquals(historyExp, history, "Не равны");
    }

}