package tracker.controllers;

import tracker.interfaces.HistoryManager;
import tracker.interfaces.TaskManager;

public class Manager {
    public static TaskManager getInMemoryTaskManager() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
