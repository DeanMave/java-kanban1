package tracker;

import tracker.controllers.*;
import tracker.model.*;


public class Main {
    public static void main(String[] args) {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        // Создание
        Task task1 = new Task("tracker.model.Task #1", "Task1 description", TaskStatus.NEW);
        Task task2 = new Task("tracker.model.Task #2", "Task2 description", TaskStatus.IN_PROGRESS);
        final int taskId1 = manager.addNewTask(task1);
        final int taskId2 = manager.addNewTask(task2);
        Epic epic1 = new Epic("tracker.model.Epic #1", "Epic1 description");
        Epic epic2 = new Epic("tracker.model.Epic #2", "Epic2 description");
        final int epicId1 = manager.addNewEpic(epic1);
        final int epicId2 = manager.addNewEpic(epic2);
        SubTask subtask1 = new SubTask("Subtask #1-1", "Subtask1 description", TaskStatus.NEW, epicId1);
        SubTask subtask2 = new SubTask("Subtask #2-1", "Subtask1 description", TaskStatus.NEW, epicId1);
        SubTask subtask3 = new SubTask("Subtask #3-2", "Subtask1 description", TaskStatus.DONE, epicId2);
        final Integer subtaskId1 = manager.addNewSubTask(subtask1);
        final Integer subtaskId2 = manager.addNewSubTask(subtask2);
        final Integer subtaskId3 = manager.addNewSubTask(subtask3);

        printAllTasks(manager);
        // Обновление
        final Task task = manager.getTask(taskId2);
        task.setStatus(TaskStatus.DONE);
        manager.updateTask(task);
        System.out.println("CHANGE STATUS: Task2 IN_PROGRESS->DONE");
        System.out.println("Задачи:");
        for (Task t : manager.getTasks()) {
            System.out.println(t);
        }

        SubTask subtask = manager.getSubtask(subtaskId2);
        subtask.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask);
        System.out.println("CHANGE STATUS: Subtask2 NEW->DONE");
        subtask = manager.getSubtask(subtaskId3);
        subtask.setStatus(TaskStatus.NEW);
        manager.updateSubtask(subtask);
        System.out.println("CHANGE STATUS: Subtask3 DONE->NEW");
        System.out.println("Подзадачи:");
        for (Task t : manager.getSubtasks()) {
            System.out.println(t);
        }

        System.out.println("Эпики:");
        for (Task e : manager.getEpics()) {
            System.out.println(e);
            for (Task t : manager.getEpicSubtasks(e.getId())) {
                System.out.println("--> " + t);
            }
        }

        final Epic epic = manager.getEpic(epicId1);
        epic.setStatus(TaskStatus.NEW);
        manager.updateEpic(epic);
        System.out.println("CHANGE STATUS: Epic1 IN_PROGRESS->NEW");
        printAllTasks(manager);

        System.out.println("Эпики:");
        for (Task e : manager.getEpics()) {
            System.out.println(e);
            for (Task t : manager.getEpicSubtasks(e.getId())) {
                System.out.println("--> " + t);
            }
        }

        manager.getTask(taskId2);
        System.out.println("История поиска");
        manager.getHistory();

        System.out.println("DELETE: Task1");
        manager.deleteTask(taskId1);
        System.out.println("DELETE: Epic1");
        manager.deleteEpic(epicId1);
        printAllTasks(manager);
    }

    public static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task t : manager.getTasks()) {
            System.out.println(t);
        }
    }
}