package tracker.controllers;

import tracker.enums.*;
import tracker.model.*;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private File file;
    private List<Task> changedTasks = new ArrayList<>();

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public int addNewTask(Task task) {
        int id = super.addNewTask(task);
        save(task);
        return id;
    }

    @Override
    public int addNewEpic(Epic epic) {
        int id = super.addNewEpic(epic);
        save(epic);
        return id;
    }

    @Override
    public int addNewSubTask(SubTask subTask) {
        int id = super.addNewTask(subTask);
        save(subTask);
        return id;
    }

    public void save(Task task) {
        try {
            if (file.length() == 0) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write("id,type,name,status,description,epicId\n");
                }
            }
            String taskAsString = toString(task);
            boolean isDuplicate = false;
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                reader.readLine();
                while ((line = reader.readLine()) != null) {
                    if (line.equals(taskAsString)) {
                        isDuplicate = true;
                        break;
                    }
                }
            }
            if (!isDuplicate) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                    writer.write(taskAsString + "\n");
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении данных в файл", e);
        }
    }


    public String toString(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append(task.getId()).append(",");

        if (task instanceof SubTask) {
            sb.append(TasksTypes.SUBTASK).append(",");
        } else if (task instanceof Epic) {
            sb.append(TasksTypes.EPIC).append(",");
        } else {
            sb.append(TasksTypes.TASK).append(",");
        }

        sb.append(task.getName()).append(",").append(task.getStatus()).append(",").append(task.getDescription())
                        .append(",");


        if (task instanceof SubTask) {
            sb.append(((SubTask) task).getEpicId());
        }
        return sb.toString();
    }

    public static Task fromString(String value) {
        String[] parts = value.split(",");
        Task task;
        Task epic;
        Task subTask;
        String idStr = parts[0];
        String type = parts[1];
        String name = parts[2];
        String statusStr = parts[3];
        String description = parts[4];
        Integer epicId = null;
        if (parts.length > 5 && parts[5] != null) {
            try {
                epicId = Integer.parseInt(parts[5]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Некорректный формат epicId");
            }
        }
        TaskStatus status = TaskStatus.valueOf(statusStr);
        TasksTypes types = TasksTypes.valueOf(type);
        switch (types) {
            case TasksTypes.TASK:
                task = new Task(name, description, status);
                task.setId(Integer.parseInt(idStr));
                return task;
            case TasksTypes.EPIC:
                epic = new Epic(name, description);
                epic.setId(Integer.parseInt(idStr));
                epic.setStatus(status);
                return epic;
            case TasksTypes.SUBTASK:
                subTask = new SubTask(name, description, status, epicId);
                subTask.setId(Integer.parseInt(idStr));
                return subTask;
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи");
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try {
            String text = Files.readString(file.toPath());
            String[] lines = text.split("\n");
            for (int i = 1; i < lines.length; i++) {
                Task task = fromString(lines[i]);
                if (task instanceof Epic) {
                    manager.addNewEpic((Epic) task);
                } else if (task instanceof SubTask) {
                    manager.addNewSubTask((SubTask) task);
                } else {
                    manager.addNewTask(task);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке данных из файла", e);
        }
        return manager;
    }

    public static class ManagerSaveException extends RuntimeException {
        public ManagerSaveException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
