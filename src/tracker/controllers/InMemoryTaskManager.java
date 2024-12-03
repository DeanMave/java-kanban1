package tracker.controllers;

import tracker.model.*;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        List<SubTask> subtasks = new ArrayList<>();
        for (SubTask subtask : subtasks) {
            if (subtask.getEpicId() == epicId) {
                subtasks.add(subtask);
            }
        }
        return subtasks;
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if(task == null) {
            return tasks.get(id);
        } else {
            historyManager.addTask(task);
        }
        return tasks.get(id);
    }

    @Override
    public SubTask getSubtask(int id) {
        SubTask subTask = subTasks.get(id);
        if(subTask == null) {
            return subTasks.get(id);
        } else {
            historyManager.addTask(subTask);
        }
        return subTasks.get(id);
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if(epic == null) {
            return epics.get(id);
        } else {
            historyManager.addTask(epic);
        }
        return epics.get(id);
    }

    @Override
    public int addNewTask(Task task) {
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
        subTask.setId(taskCounter++);
        subTasks.put(subTask.getId(), subTask);
        Epic epic = epics.get(subTask.getEpicId());
        epic.getSubtaskIds().add(subTask.getId());
        updateEpicStatus(subTask.getEpicId());
        return subTask.getId();
    }

    @Override
    public void updateEpicStatus(int id) {
        int statusNew = 0;
        int statusDone = 0;
        Epic epic = epics.get(id);
        List<Integer> subTaskIds = epic.getSubtaskIds();
        for (Integer subTaskId : subTaskIds) {
            SubTask subTask = subTasks.get(subTaskId);
            TaskStatus status = subTask.getStatus();
            if (status == TaskStatus.NEW) {
                statusNew++;
            } else if (status == TaskStatus.DONE) {
                statusDone++;
            }
        }
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
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(SubTask subtask) {
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
        for(Task h: history) {
            System.out.println(h);
        }
        return history;
    }

}
