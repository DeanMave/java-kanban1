package tracker.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    @Test
    void getName() {
        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW);
        assertEquals("Task 1", task.getName());
    }

    @Test
    void setName() {
        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW);
        task.setName("New Task Name");
        assertEquals("New Task Name", task.getName());
    }

    @Test
    void getDescription() {
        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW);
        assertEquals("Description 1", task.getDescription());
    }

    @Test
    void setDescription() {
        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW);
        task.setDescription("New Description");
        assertEquals("New Description", task.getDescription());
    }

    @Test
    void setStatus() {
        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW);
        task.setStatus(TaskStatus.IN_PROGRESS);
        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
    }

    @Test
    void getStatus() {
        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW);
        assertEquals(TaskStatus.NEW, task.getStatus());
    }

    @Test
    void testToString() {
        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW);
        String expected = "Task{id=0, name='Task 1', description='Description 1', status=NEW}";
        assertEquals(expected, task.toString());
    }

}