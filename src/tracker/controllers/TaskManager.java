package tracker.controllers;

import tracker.model.Epic;
import tracker.model.SubTask;
import tracker.model.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    List<Task> getTasks();

    List<SubTask> getSubtasks();

    List<Epic> getEpics();

    List<SubTask> getEpicSubtasks(int epicId);

    Task getTask(int id);

    SubTask getSubtask(int id);

    Epic getEpic(int id);

    int addNewTask(Task task);

    int addNewEpic(Epic epic);

    int addNewSubTask(SubTask subTask);

    void updateEpicStatus(int id);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(SubTask subtask);

    void deleteTask(int id);

    void deleteEpic(int id);

    void deleteSubtask(int id);

    void deleteTasks();

    void deleteSubtasks();

    void deleteEpics();

    List<Task> getHistory();
}
