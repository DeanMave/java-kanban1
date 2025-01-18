package tracker.controllers;

import tracker.comporators.TaskComporator;
import tracker.enums.TaskStatus;
import tracker.interfaces.HistoryManager;
import tracker.interfaces.TaskManager;
import tracker.model.*;


import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private HistoryManager historyManager = new InMemoryHistoryManager();
    private int taskCounter = 0;
    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private Map<Integer, SubTask> subTasks = new HashMap<>();


    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<SubTask> getSubtasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<SubTask> getEpicSubtasks(int epicId) {
        return getSubtasks().stream().filter(subTask -> subTask.getEpicId() == epicId).toList();
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task == null) {
            return tasks.get(id);
        } else {
            historyManager.addTask(task);
        }
        return tasks.get(id);
    }

    @Override
    public SubTask getSubtask(int id) {
        SubTask subTask = subTasks.get(id);
        if (subTask == null) {
            return subTasks.get(id);
        } else {
            historyManager.addTask(subTask);
        }
        return subTasks.get(id);
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            return epics.get(id);
        } else {
            historyManager.addTask(epic);
        }
        return epics.get(id);
    }

    @Override
    public int addNewTask(Task task) {
        boolean intersectionOfTasks = tasks.values().stream()
                .anyMatch(presentTask -> intersectionOfTasks(task, presentTask));
        if (intersectionOfTasks) {
            throw new IllegalArgumentException("Время выполнения задачи пересекается уже с существующей задачей");
        }
        task.setId(taskCounter++);
        tasks.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public int addNewEpic(Epic epic) {
        epic.setId(taskCounter++);
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public int addNewSubTask(SubTask subTask) {
        boolean intersectionOfSubTasks = subTasks.values().stream()
                .anyMatch(presentSubtask -> intersectionOfTasks(subTask, presentSubtask));
        if (intersectionOfSubTasks) {
            throw new IllegalArgumentException("Время выполнения подзадачи пересекается уже с существующей задачей");
        }
        subTask.setId(taskCounter++);
        subTasks.put(subTask.getId(), subTask);
        Epic epic = epics.get(subTask.getEpicId());
        epic.getSubtaskIds().add(subTask.getId());
        updateEpicStatus(subTask.getEpicId());
        return subTask.getId();
    }

    @Override
    public void updateEpicStatus(int id) {
        Epic epic = epics.get(id);
        List<Integer> subTaskIds = epic.getSubtaskIds();
        long statusNew = subTaskIds.stream().map(subTasks::get)
                .filter(subTask -> subTask.getStatus() == TaskStatus.NEW).count();
        long statusDone = subTaskIds.stream().map(subTasks::get)
                .filter(subTask -> subTask.getStatus() == TaskStatus.DONE).count();
        if (subTaskIds.isEmpty() || statusNew == subTaskIds.size()) {
            epic.setStatus(TaskStatus.NEW);
        } else if (statusDone == subTaskIds.size()) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }


    @Override
    public void updateTask(Task task) {
        boolean isOverlap = tasks.values().stream()
                .filter(existingTask -> existingTask.getId() != task.getId())
                .anyMatch(existingTask -> intersectionOfTasks(task, existingTask));
        if (isOverlap) {
            throw new IllegalArgumentException("Время выполнения задачи пересекается с другой задачей");
        }
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(SubTask subtask) {
        boolean intersectionOfSubTasks = subTasks.values().stream()
                .filter(presentSubtask -> presentSubtask.getId() != subtask.getId())
                .filter(presentSubtask -> presentSubtask.getEpicId() != subtask.getEpicId())
                .anyMatch(presentSubtask -> intersectionOfTasks(subtask, presentSubtask));
        if (intersectionOfSubTasks) {
            throw new IllegalArgumentException("Время выполнения подзадачи пересекается с другой подзадачей");
        }
        subTasks.put(subtask.getId(), subtask);
        updateEpicStatus(subtask.getEpicId());
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        final Epic epic = epics.remove(id);
        for (Integer subtaskId : epic.getSubtaskIds()) {
            subTasks.remove(subtaskId);
        }
    }

    @Override
    public void deleteSubtask(int id) {
        for (Epic epic : epics.values()) {
            SubTask subTask = subTasks.get(id);
            epic.removeSubtask(id);
            updateEpicStatus(subTask.getEpicId());
        }
        subTasks.remove(id);
    }

    @Override
    public void deleteTasks() {
        tasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        for (Epic epic : epics.values()) {
            epic.cleanSubtaskIds();
            updateEpicStatus(epic.getId());
        }
        subTasks.clear();
    }

    @Override
    public void deleteEpics() {
        epics.clear();
        subTasks.clear();
    }

    @Override
    public List<Task> getHistory() {
        List<Task> history = historyManager.getHistory();
        history.stream().forEach(System.out::println);
        return history;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        List<Task> allTasks = new ArrayList<>();
        allTasks.addAll(getTasks());
        allTasks.addAll(getEpics());
        allTasks.addAll(getSubtasks());
        TreeSet<Task> prioritizedTasks = new TreeSet<>(new TaskComporator());
        prioritizedTasks.addAll(allTasks);
        return new ArrayList<>(prioritizedTasks);
    }

    public boolean intersectionOfTasks(Task t1, Task t2) {
        return t1.getStartTime().isBefore(t2.getEndTime()) && t1.getEndTime().isAfter(t2.getStartTime());
    }
}
