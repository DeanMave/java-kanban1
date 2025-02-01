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
import tracker.model.*;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerSubTasksTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer server = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    HttpTaskManagerSubTasksTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.deleteTasks();
        manager.deleteSubtasks();
        manager.deleteEpics();
        server.serverStart();
        Epic epic = new Epic("Эпик для теста", "Эпик описание");
        manager.addNewEpic(epic);
    }

    @AfterEach
    public void shutDown() {
        server.serverStop();
    }

    @Test
    public void testAddSubTask() throws IOException, InterruptedException {
        String subTaskJson = """
                {
                    "name": "Test SubTask",
                    "description": "Test Description",
                    "status": "NEW",
                    "epicId": 1,
                    "duration": 30,
                    "time": "14:00,15.10.2023"
                }
                """;
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<SubTask> subTasksFromManager = manager.getSubtasks();
        assertNotNull(subTasksFromManager, "Задачи не возвращаются");
        assertEquals(1, subTasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test SubTask", subTasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void deleteSubTask() throws IOException, InterruptedException {
        SubTask subTask = new SubTask("Test SubTask", "Test Description", TaskStatus.NEW, 1
                , Duration.ofMinutes(30), LocalDateTime.now());
        manager.addNewSubTask(subTask);
        int subTaskId = subTask.getId();
        assertNotNull(manager.getSubtask(subTaskId));
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + subTaskId))
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неверный статус код");
        assertEquals("Подзадача удалена", response.body(), "Неверное тело ответа");
        assertThrows(NotFoundException.class, () -> manager.getTask(subTaskId), "Подзадача должна быть удалена");
    }

    @Test
    public void updateSubTask() throws IOException, InterruptedException {
        SubTask subTask = new SubTask("Test SubTask", "Test Description", TaskStatus.NEW, 1
                , Duration.ofMinutes(30), LocalDateTime.of(2023, 10, 15, 14, 00));
        manager.addNewSubTask(subTask);
        int subTaskId = subTask.getId();
        String updateSubTask = """
                {
                    "name": "Update",
                    "description": "Test Description",
                    "status": "NEW",
                    "epicId": 1,
                    "duration": 30,
                    "time": "14:00,15.10.2023"
                }
                """;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + subTaskId))
                .POST(HttpRequest.BodyPublishers.ofString(updateSubTask))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<SubTask> subTasks = manager.getSubtasks();
        assertEquals(201, response.statusCode(), "Неверный статус код");
        assertEquals("Update", subTasks.get(0).getName(), "Подзадача не обновлена");
    }
}
