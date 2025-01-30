package tracker.HTTP.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import tracker.exceptions.NotFoundException;
import tracker.interfaces.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public HistoryHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handleGet(HttpExchange exchange) throws IOException {
        try {
            String history = gson.toJson(manager.getHistory());
            sendText(exchange, history, 200);
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            sendInfernalServerError(exchange);
        }
    }

    @Override
    public void handleGetById(HttpExchange exchange) {

    }

    @Override
    public void handlePost(HttpExchange exchange) {

    }

    @Override
    public void handleDelete(HttpExchange exchange) {

    }
}
