package tracker.HTTP.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import tracker.exceptions.NotFoundException;
import tracker.interfaces.TaskManager;
import tracker.model.Epic;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class EpicHandler extends BaseHttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public EpicHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handleGet(HttpExchange exchange) throws IOException {
        try {
            String[] path = exchange.getRequestURI().getPath().split("/");
            if (path.length == 2) {
                try {
                    String epicsJson = gson.toJson(manager.getEpics());
                    sendText(exchange, epicsJson, 200);
                } catch (NotFoundException e) {
                    sendNotFound(exchange);
                }
            } else if (path.length > 2 && path[3].equals("subtasks")) {
                try {
                    String epicsSubtasksJson = gson.toJson(manager.getEpicSubtasks(Integer.parseInt(path[2])));
                    sendText(exchange, epicsSubtasksJson, 200);
                } catch (NotFoundException e) {
                    sendNotFound(exchange);
                }
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
    public void handleGetById(HttpExchange exchange) throws IOException {
        try {
            String[] path = exchange.getRequestURI().getPath().split("/");
            String epicByIdJson = gson.toJson(manager.getEpic(Integer.parseInt(path[2])));
            sendText(exchange, epicByIdJson, 200);
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
            Epic epic;
            try {
                jsonObject = JsonParser.parseString(body).getAsJsonObject();
                String name = jsonObject.get("name").getAsString();
                String description = jsonObject.get("description").getAsString();
                epic = new Epic(name, description);
            } catch (Exception e) {
                sendToUser(exchange, "Некорректные данные эпика", 400);
                return;
            }
            try {
                if (path.length == 2) {
                    manager.addNewEpic(epic);
                    sendText(exchange, "Эпик добавлен", 200);
                } else {
                    sendNotFound(exchange);
                }
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
            manager.deleteEpic(Integer.parseInt(path[2]));
            sendText(exchange, "Эпик удален", 200);
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            sendInfernalServerError(exchange);
        }
    }
}
