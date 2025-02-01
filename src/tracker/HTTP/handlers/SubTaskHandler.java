package tracker.HTTP.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import tracker.enums.TaskStatus;
import tracker.exceptions.NotFoundException;
import tracker.interfaces.TaskManager;
import tracker.model.SubTask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SubTaskHandler extends BaseHttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public SubTaskHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handleGet(HttpExchange exchange) throws IOException {
        try {
            String[] path = exchange.getRequestURI().getPath().split("/");
            if (path.length == 2) {
                String subTasksJson = gson.toJson(manager.getSubtasks());
                sendText(exchange, subTasksJson, 200);
            } else if (path.length == 3) {
                String subTaskByIdJson = gson.toJson(manager.getSubtask(Integer.parseInt(path[2])));
                sendText(exchange, subTaskByIdJson, 200);
            } else {
                sendNotFound(exchange);
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            sendInfernalServerError(exchange);
        }
    }

    @Override
    public void handlePost(HttpExchange exchange) throws IOException {
        try {
            String[] path = exchange.getRequestURI().getPath().split("/");
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            JsonObject jsonObject;
            SubTask subTask;
            try {
                jsonObject = JsonParser.parseString(body).getAsJsonObject();
                String name = jsonObject.get("name").getAsString();
                String description = jsonObject.get("description").getAsString();
                TaskStatus status = TaskStatus.valueOf(jsonObject.get("status").getAsString());
                int epicId = jsonObject.get("epicId").getAsInt();
                Duration duration = Duration.ofMinutes(jsonObject.get("duration").getAsLong());
                LocalDateTime time = LocalDateTime.parse(jsonObject.get("time").getAsString(),
                        DateTimeFormatter.ofPattern("HH:mm,dd.MM.yyyy"));
                subTask = new SubTask(name, description, status, epicId, duration, time);
            } catch (Exception e) {
                sendNotFound(exchange);
                return;
            }
            try {
                if (path.length == 2) {
                    manager.addNewSubTask(subTask);
                    sendText(exchange, "Подзадача успешно добавлена", 200);
                } else if (path.length == 3) {
                    subTask.setId(Integer.parseInt(path[2]));
                    manager.updateSubtask(subTask);
                    sendText(exchange, "Подзадача успешно обновлена", 201);
                } else {
                    sendNotFound(exchange);
                }
            } catch (IllegalArgumentException e) {
                sendHasInteractions(exchange);
            } catch (Exception e) {
                sendInfernalServerError(exchange);
            }
        } catch (Exception e) {
            sendInfernalServerError(exchange);
        }
    }

    @Override
    public void handleDelete(HttpExchange exchange) throws IOException {
        try {
            String[] path = exchange.getRequestURI().getPath().split("/");
            manager.deleteSubtask(Integer.parseInt(path[2]));
            sendText(exchange, "Подзадача удалена", 200);
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            sendInfernalServerError(exchange);
        }
    }
}
