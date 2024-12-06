package tracker.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {
    @Test
    public void getName() {
        SubTask subTask = new SubTask("Subtask 1", "Description 1", TaskStatus.NEW, 1);
        assertEquals("Subtask 1", subTask.getName());
    }

    @Test
    public void setName() {
        SubTask subTask = new SubTask("Subtask 1", "Description 1", TaskStatus.NEW, 1);
        subTask.setName("New Epic Name");
        assertEquals("New Epic Name", subTask.getName());
    }

    @Test
    public void getDescription() {
        SubTask subTask = new SubTask("Subtask 1", "Description 1", TaskStatus.NEW, 1);
        assertEquals("Description 1", subTask.getDescription());
    }

    @Test
    public void setDescription() {
        SubTask subTask = new SubTask("Subtask 1", "Description 1", TaskStatus.NEW, 1);
        subTask.setDescription("New Description");
        assertEquals("New Description", subTask.getDescription());
    }

    @Test
    public void testToString() {
        SubTask subTask = new SubTask("Subtask 1", "Description 1", TaskStatus.NEW, 1);
        String expected = "tracker.model.SubTask@7bc1a03d";
        assertEquals(expected, subTask.toString());
    }


}