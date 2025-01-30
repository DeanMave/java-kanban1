package tracker.HTTP.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import tracker.enums.TaskStatus;
import tracker.exceptions.NotFoundException;
import tracker.interfaces.TaskManager;
import tracker.model.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TaskHandler extends BaseHttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public TaskHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }


    @Override
    public void handleGet(HttpExchange exchange) throws IOException {
        try {
            String tasksJson = gson.toJson(manager.getTasks());
            sendText(exchange, tasksJson, 200);
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            sendInfernalServerError(exchange);
        }
    }

    @Override
    public void handleGetById(HttpExchange exchange) throws IOException {
        try {
            String[] path = exchange.getRequestURI().getPath().split("/");
            String taskByIdJson = gson.toJson(manager.getTask(Integer.parseInt(path[2])));
            sendText(exchange, taskByIdJson, 200);
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
            Task task;
            try {
                jsonObject = JsonParser.parseString(body).getAsJsonObject();
                String name = jsonObject.get("name").getAsString();
                String description = jsonObject.get("description").getAsString();
                TaskStatus status = TaskStatus.valueOf(jsonObject.get("status").getAsString());
                Duration duration = Duration.ofMinutes(jsonObject.get("duration").getAsLong());
                LocalDateTime time = LocalDateTime.parse(jsonObject.get("time").getAsString(),
                        DateTimeFormatter.ofPattern("HH:mm,dd.MM.yyyy"));
                task = new Task(name, description, status, duration, time);
            } catch (Exception e) {
                sendNotFound(exchange);
                return;
            }
            try {
                if (path.length == 2) {
                    manager.addNewTask(task);
                    sendText(exchange, "Задача успешно добавлена", 200);
                } else if (path.length == 3) {
                    task.setId(Integer.parseInt(path[2]));
                    manager.updateTask(task);
                    sendText(exchange, "Задача успешно обновлена", 201);
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
            manager.deleteTask(Integer.parseInt(path[2]));
            sendText(exchange, "Задача удалена", 200);
        } catch (Exception e) {
            sendInfernalServerError(exchange);
        }
    }
}
