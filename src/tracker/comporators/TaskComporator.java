package tracker.comporators;

import tracker.model.Task;

import java.util.Comparator;

public class TaskComporator implements Comparator<Task> {
    @Override
    public int compare(Task task1, Task task2) {
        if (task1.getStartTime() == null && task2.getStartTime() == null) {
            return 0;
        }
        if (task1.getStartTime() == null) {
            return 1;
        }
        if (task2.getStartTime() == null) {
            return -1;
        }
        int timeComparison = task1.getStartTime().compareTo(task2.getStartTime());
        if (timeComparison != 0) {
            return timeComparison;
        }
        return Integer.compare(task1.getId(), task2.getId());
    }
}
