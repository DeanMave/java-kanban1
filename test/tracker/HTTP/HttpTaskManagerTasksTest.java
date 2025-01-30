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

class HttpTaskManagerTasksTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer server = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    HttpTaskManagerTasksTest() throws IOException {
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
    public void testAddTask() throws IOException, InterruptedException {
        String taskJson = """
                {
                    "name": "Test Task",
                    "description": "Test Description",
                    "status": "NEW",
                    "duration": 30,
                    "time": "14:00,15.10.2023"
                }
                """;
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<Task> tasksFromManager = manager.getTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test Task", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void deleteTask() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Test Description", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        manager.addNewTask(task);
        int taskId = task.getId();
        assertNotNull(manager.getTask(taskId));
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + taskId))
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неверный статус код");
        assertEquals("Задача удалена", response.body(), "Неверное тело ответа");
        assertThrows(NotFoundException.class, () -> manager.getTask(taskId), "Задача должна быть удалена");
    }

    @Test
    public void updateTask() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Test Description", TaskStatus.NEW, Duration.ofMinutes(30)
                , LocalDateTime.of(2023, 10, 15, 14, 00));
        manager.addNewTask(task);
        int taskId = task.getId();
        String updateTask = """
                {
                    "name": "Update",
                    "description": "Test Description",
                    "status": "NEW",
                    "duration": 30,
                    "time": "14:00,15.10.2023"
                }
                """;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + taskId))
                .POST(HttpRequest.BodyPublishers.ofString(updateTask))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> tasks = manager.getTasks();
        assertEquals(201, response.statusCode(), "Неверный статус код");
        assertEquals("Update", tasks.get(0).getName(), "Задача не обновлена");
    }
}
