import java.util.HashMap;
import java.util.Map;

public class Task {
    private int id;
    private String name;
    private String description;
    private TaskStatus status;
    private Map<Integer, Task> tasks = new HashMap<>();

    public Task(String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

class Subtask extends Task {
    private int epicId;
    private Map<Integer, Subtask> subtasks = new HashMap<>();

    public Subtask(String name, String description, TaskStatus status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }
}

class Epic extends Task {
    private Map<Integer, Epic> epics = new HashMap<>();
    private int id;
    private TaskStatus status;

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}