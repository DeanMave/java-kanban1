package tracker.model;

import tracker.enums.TaskStatus;

import java.util.ArrayList;

public class Epic extends Task {

    protected ArrayList<Integer> subtaskIds = new ArrayList<>();

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


    @Override
    public String toString() {
        return super.toString();
    }
}
