package tracker.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.enums.TaskStatus;
import tracker.model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private InMemoryTaskManager taskManager;
    private InMemoryHistoryManager historyManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void createTaskTest() {
        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW, Duration.ofMinutes(50)
                , LocalDateTime.of(2025, Month.JULY, 9, 10, 15));
        int taskId = taskManager.addNewTask(task);
        assertEquals(0, taskId);
        Task task2 = new Task("Task 1", "Description 1", TaskStatus.NEW, Duration.ofMinutes(50)
                , LocalDateTime.of(2025, Month.JULY, 9, 12
                , 50));
        int taskId2 = taskManager.addNewTask(task2);
        assertEquals(1, taskId2);
        Task retrievedTask = taskManager.getTask(taskId);
        assertEquals(task, retrievedTask);
    }

    @Test
    void createAndRetrieveEpicWithSubtasksTest() {
        Epic epic = new Epic("Epic 1", "Description 1");
        int epicId = taskManager.addNewEpic(epic);
        assertEquals(0, epicId);
        SubTask subTask1 = new SubTask("Subtask 1", "Description 1", TaskStatus.NEW, epicId, Duration.ofMinutes(50)
                , LocalDateTime.of(2025, Month.JULY, 9, 10, 15));
        int subTaskId1 = taskManager.addNewSubTask(subTask1);
        assertEquals(1, subTaskId1);
        SubTask subTask2 = new SubTask("Subtask 2", "Description 2", TaskStatus.NEW, epicId, Duration.ofMinutes(50)
                , LocalDateTime.of(2025, Month.JULY, 9, 11, 50));
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
        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW, Duration.ofMinutes(50)
                , LocalDateTime.of(2025, Month.JULY, 9, 10, 15));
        final int taskId = taskManager.addNewTask(task);
        taskManager.deleteTask(taskId);
        List<Task> tasks = taskManager.getTasks();
        assertEquals(0, tasks.size());
    }

    @Test
    void updateSubtaskAndEpicStatusTest() {
        Epic epic = new Epic("Epic 1", "Description 1");
        int epicId = taskManager.addNewEpic(epic);
        SubTask subTask1 = new SubTask("Subtask 1", "Description 1", TaskStatus.NEW, epicId, Duration.ofMinutes(50)
                , LocalDateTime.of(2025, Month.JULY, 9, 11, 15));
        int subTaskId1 = taskManager.addNewSubTask(subTask1);
        taskManager.updateEpicStatus(epicId);
        assertEquals(subTask1.getStatus(), epic.getStatus());
        subTask1.setStatus(TaskStatus.DONE);
        taskManager.updateEpicStatus(epicId);
        assertEquals(subTask1.getStatus(), epic.getStatus());
    }

    @Test
    void deleteAllTasksTest() {
        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW, Duration.ofMinutes(50)
                , LocalDateTime.of(2025, Month.JULY, 9, 10, 15));
        final int taskId = taskManager.addNewTask(task);
        taskManager.deleteTasks();
        List<Task> tasks = taskManager.getTasks();
        assertEquals(0, tasks.size());
    }

    @Test
    void deleteSubtaskByIdTest() {
        Epic epic = new Epic("Epic 1", "Description 1");
        int epicId = taskManager.addNewEpic(epic);
        SubTask subTask1 = new SubTask("Subtask 1", "Description 1", TaskStatus.NEW, epicId, Duration.ofMinutes(50)
                , LocalDateTime.of(2025, Month.JULY, 9, 11, 15));
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
        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW, Duration.ofMinutes(50)
                , LocalDateTime.of(2025, Month.JULY, 9, 10, 15));
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

    @Test
    void getPrioritizedTasks() {
        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW, Duration.ofMinutes(50)
                , LocalDateTime.of(2025, Month.JULY, 9, 10, 15));
        int taskId = taskManager.addNewTask(task);
        Epic epic = new Epic("Epic 1", "Description 1");
        epic.setDuration(Duration.ofMinutes(50));
        epic.setStartTime(LocalDateTime.of(2025, Month.JULY, 9, 1, 15));
        int epicId = taskManager.addNewEpic(epic);
        SubTask subTask1 = new SubTask("Subtask 1", "Description 1", TaskStatus.NEW, epicId, Duration.ofMinutes(50)
                , LocalDateTime.of(2025, Month.JULY, 9, 11, 15));
        int subTaskId1 = taskManager.addNewSubTask(subTask1);
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        List<Task> prioritizedTasksExp = new ArrayList<>();
        prioritizedTasksExp.add(task);
        prioritizedTasksExp.add(subTask1);
        assertEquals(prioritizedTasksExp, prioritizedTasks);
    }

    @Test
    void getEpicSubtasks() {
        Epic epic = new Epic("Epic 1", "Description 1");
        int epicId = taskManager.addNewEpic(epic);
        SubTask subTask1 = new SubTask("Subtask 1", "Description 1", TaskStatus.NEW, epicId, Duration.ofMinutes(50)
                , LocalDateTime.of(2025, Month.JULY, 9, 10, 15));
        int subTaskId1 = taskManager.addNewSubTask(subTask1);
        Epic epic1 = new Epic("Epic 1", "Description 1");
        int epicId1 = taskManager.addNewEpic(epic1);
        SubTask subTask11 = new SubTask("Subtask 1", "Description 1", TaskStatus.NEW, epicId1, Duration.ofMinutes(50)
                , LocalDateTime.of(2025, Month.JULY, 9, 11, 40));
        int subTaskId11 = taskManager.addNewSubTask(subTask11);
        List<SubTask> subTasksOfEpic = taskManager.getEpicSubtasks(epicId);
        List<SubTask> subTasksOfEpicExp = new ArrayList<>();
        subTasksOfEpicExp.add(subTask1);
        assertEquals(subTasksOfEpicExp, subTasksOfEpic);
    }

    @Test
    void getTimeOfEpic() {
        Epic epic = new Epic("Epic 1", "Description 1");
        int epicId = taskManager.addNewEpic(epic);
        SubTask subTask1 = new SubTask("Subtask 1", "Description 1", TaskStatus.NEW, epicId, Duration.ofMinutes(50)
                , LocalDateTime.of(2025, Month.JULY, 9, 10, 15));
        int subTaskId1 = taskManager.addNewSubTask(subTask1);
        SubTask subTask2 = new SubTask("Subtask 1", "Description 1", TaskStatus.NEW, epicId, Duration.ofMinutes(50)
                , LocalDateTime.of(2025, Month.JULY, 9, 11, 40));
        int subTaskId2 = taskManager.addNewSubTask(subTask2);
        SubTask subTask3 = new SubTask("Subtask 1", "Description 1", TaskStatus.NEW, epicId, Duration.ofMinutes(50)
                , LocalDateTime.of(2025, Month.JULY, 9, 13, 45));
        int subTaskId3 = taskManager.addNewSubTask(subTask3);
        LocalDateTime startTimeExp = LocalDateTime.of(2025, Month.JULY, 9, 10, 15);
        LocalDateTime endTimeExp = LocalDateTime.of(2025, Month.JULY, 9, 14, 35);
        Duration durationExp = Duration.ofMinutes(150);
        assertEquals(startTimeExp, epic.getStartTime());
        assertEquals(endTimeExp, epic.getEndTime());
        assertEquals(durationExp, epic.getDuration());
    }

    @Test
    void deleteAllTasksInLists() {
        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW, Duration.ofMinutes(50)
                , LocalDateTime.of(2025, Month.JULY, 9, 10, 15));
        int taskId = taskManager.addNewTask(task);
        Epic epic = new Epic("Epic 1", "Description 1");
        int epicId = taskManager.addNewEpic(epic);
        SubTask subTask1 = new SubTask("Subtask 1", "Description 1", TaskStatus.NEW, epicId, Duration.ofMinutes(50)
                , LocalDateTime.of(2025, Month.JULY, 9, 12, 15));
        int subTaskId1 = taskManager.addNewSubTask(subTask1);
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        taskManager.deleteTasks();
        List<Task> prioritizedTasks1 = taskManager.getPrioritizedTasks();
        List<Task> prioritizedTasksExp = new ArrayList<>();
        prioritizedTasksExp.add(subTask1);
        assertEquals(prioritizedTasksExp, prioritizedTasks1);
        taskManager.getTask(taskId);
        taskManager.getSubtask(subTaskId1);
        List<Task> history = taskManager.getHistory();
        taskManager.deleteTasks();
        List<Task> history1 = taskManager.getHistory();
        List<Task> historyExp = new ArrayList<>();
        historyExp.add(subTask1);
        assertEquals(historyExp, history1);
    }

    @Test
    void deleteAllSubTasksInLists() {
        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW, Duration.ofMinutes(50)
                , LocalDateTime.of(2025, Month.JULY, 9, 10, 15));
        int taskId = taskManager.addNewTask(task);
        Epic epic = new Epic("Epic 1", "Description 1");
        int epicId = taskManager.addNewEpic(epic);
        SubTask subTask1 = new SubTask("Subtask 1", "Description 1", TaskStatus.NEW, epicId, Duration.ofMinutes(50)
                , LocalDateTime.of(2025, Month.JULY, 9, 12, 15));
        int subTaskId1 = taskManager.addNewSubTask(subTask1);
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        taskManager.deleteSubtasks();
        List<Task> prioritizedTasks1 = taskManager.getPrioritizedTasks();
        List<Task> prioritizedTasksExp = new ArrayList<>();
        prioritizedTasksExp.add(task);
        assertEquals(prioritizedTasksExp, prioritizedTasks1);
        taskManager.getTask(taskId);
        taskManager.getSubtask(subTaskId1);
        List<Task> history = taskManager.getHistory();
        taskManager.deleteSubtasks();
        List<Task> history1 = taskManager.getHistory();
        List<Task> historyExp = new ArrayList<>();
        historyExp.add(task);
        assertEquals(historyExp, history1);
    }
}