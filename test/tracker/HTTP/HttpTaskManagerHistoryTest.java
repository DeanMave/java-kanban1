package tracker.HTTP;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.HTTP.server.HttpTaskServer;
import tracker.controllers.InMemoryTaskManager;
import tracker.enums.TaskStatus;
import tracker.interfaces.TaskManager;
import tracker.model.Epic;
import tracker.model.SubTask;
import tracker.model.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerHistoryTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer server = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    HttpTaskManagerHistoryTest() throws IOException {
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
    public void getHistory() throws IOException, InterruptedException {
        Epic epic = new Epic("Task Epic", "Test Description");
        int epicId = manager.addNewEpic(epic);
        SubTask subTask = new SubTask("Test SubTask", "Test Description", TaskStatus.NEW, epicId
                , Duration.ofMinutes(30), LocalDateTime.of(2023, 10, 15, 14, 00));
        int subTaskId = manager.addNewSubTask(subTask);
        Task task = new Task("Test Task", "Test Description", TaskStatus.NEW, Duration.ofMinutes(30)
                , LocalDateTime.of(2023, 10, 15, 14, 00));
        int taskId = manager.addNewTask(task);
        manager.getTask(taskId);
        manager.getSubtask(subTaskId);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<Task> history = manager.getHistory();
        assertNotNull(history, "История не возвращается");
        assertEquals(2, history.size(), "Некорректное количество задач в истории");
        assertEquals("Test Task", history.get(0).getName(), "Некорректное имя эпика");
    }
}
