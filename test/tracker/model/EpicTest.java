package tracker.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {
    @Test
    public void getName() {
        Epic epic = new Epic("Epic 1", "Description 1");
        assertEquals("Epic 1", epic.getName());
    }

    @Test
    public void setName() {
        Epic epic = new Epic("Epic 1", "Description 1");
        epic.setName("New Epic Name");
        assertEquals("New Epic Name", epic.getName());
    }

    @Test
    public void getDescription() {
        Epic epic = new Epic("Epic 1", "Description 1");
        assertEquals("Description 1", epic.getDescription());
    }

    @Test
    public void setDescription() {
        Epic epic = new Epic("Epic 1", "Description 1");
        epic.setDescription("New Description");
        assertEquals("New Description", epic.getDescription());
    }

    @Test
    public void testToString() {
        Epic epic = new Epic("Epic 1", "Description 1");
        String expected = "0,Epic 1,Description 1,NEW";
        assertEquals(expected, epic.toString());
    }
}