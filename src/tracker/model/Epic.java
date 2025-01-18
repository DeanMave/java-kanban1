package tracker.model;

import tracker.enums.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    public void getEndTimeOfEpics(List<SubTask> subTasks) {
        List<Integer> subtaskIds = getSubtaskIds();
        Duration durationOfAllSubtasks = Duration.ZERO;
        LocalDateTime startTimeEarlySubTask = null;
        LocalDateTime endTimeLastetSubtask = null;
        for (Integer subtaskId : subtaskIds) {
            for (SubTask subTask : subTasks) {
                if (subTask.getId() == subtaskId) {
                    if (startTimeEarlySubTask == null || subTask.getStartTime().isBefore(startTimeEarlySubTask)) {
                        startTimeEarlySubTask = subTask.getStartTime();
                    }
                    if (endTimeLastetSubtask == null || subTask.getEndTime().isAfter(endTimeLastetSubtask)) {
                        endTimeLastetSubtask = subTask.getEndTime();
                    }
                    durationOfAllSubtasks = durationOfAllSubtasks.plus(subTask.getDuration());
                }
            }
        }
        setDuration(durationOfAllSubtasks);
        setStartTime(startTimeEarlySubTask);
        setEndTime(endTimeLastetSubtask);
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return super.toString();
    }

}
