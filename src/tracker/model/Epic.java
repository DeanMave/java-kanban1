package tracker.model;

import tracker.enums.TaskStatus;

import java.time.LocalDateTime;
import java.util.*;

public class Epic extends Task {

    protected ArrayList<Integer> subtaskIds = new ArrayList<>();

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    private LocalDateTime endTime;

    public void addSubtaskId(int id) {
        subtaskIds.add(id);
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void cleanSubtaskIds() {
        subtaskIds.clear();
    }

    public void removeSubtask(int id) {
        subtaskIds.remove(Integer.valueOf(id));
    }

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return super.toString();
    }

}
