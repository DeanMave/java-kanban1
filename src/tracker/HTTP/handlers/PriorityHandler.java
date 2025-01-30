package tracker.HTTP.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import tracker.exceptions.NotFoundException;
import tracker.interfaces.TaskManager;

import java.io.IOException;

public class PriorityHandler extends BaseHttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public PriorityHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handleGet(HttpExchange exchange) throws IOException {
        try {
            String priority = gson.toJson(manager.getPrioritizedTasks());
            sendText(exchange, priority, 200);
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            sendInfernalServerError(exchange);
        }
    }
}
