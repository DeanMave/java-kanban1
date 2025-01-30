package tracker.HTTP;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.HTTP.server.HttpTaskServer;
import tracker.controllers.InMemoryTaskManager;
import tracker.enums.TaskStatus;
import tracker.exceptions.NotFoundException;
import tracker.interfaces.TaskManager;
import tracker.model.Epic;
import tracker.model.SubTask;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskManagerEpicsTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer server = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    HttpTaskManagerEpicsTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.deleteTasks();
        manager.deleteSubtasks();
        manager.deleteEpics();
        server.serverStart();
    }

    @AfterEach
    public void shutDown() {
        server.serverStop();
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        String subTaskJson = """
                {
                    "name": "Test Epic",
                    "description": "Test Description"
                }
                """;
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<Epic> epicsFromManager = manager.getEpics();
        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Test Epic", epicsFromManager.get(0).getName(), "Некорректное имя эпика");
    }

    @Test
    public void deleteEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Test Description");
        int epicId = manager.addNewEpic(epic);
        assertNotNull(manager.getEpic(epicId));
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + epicId))
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неверный статус код");
        assertEquals("Эпик удален", response.body(), "Неверное тело ответа");
        assertThrows(NotFoundException.class, () -> manager.getEpic(epicId), "Эпик должен быть удален");
    }

    @Test
    public void getSubtasksOfEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Test Description");
        int epicId = manager.addNewEpic(epic);
        SubTask subTask = new SubTask("Test SubTask", "Test Description", TaskStatus.NEW, epicId
                , Duration.ofMinutes(30), LocalDateTime.of(2023, 10, 15, 11, 00));
        manager.addNewSubTask(subTask);
        SubTask subTask2 = new SubTask("Test SubTask2", "Test Description", TaskStatus.NEW, epicId
                , Duration.ofMinutes(30), LocalDateTime.of(2023, 10, 15, 13, 00));
        manager.addNewSubTask(subTask2);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + epicId + "/subtasks"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<SubTask> subTasksOfEpic = manager.getEpicSubtasks(epicId);
        assertEquals(200, response.statusCode(), "Неверный статус код");
        assertEquals("Test SubTask", subTasksOfEpic.get(0).getName(), "Некорректное имя задачи");
    }
}
