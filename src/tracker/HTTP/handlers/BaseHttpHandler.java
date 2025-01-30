package tracker.HTTP.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tracker.enums.EndPoint;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {
    protected void sendText(HttpExchange exchange, String text, int code) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(code, resp.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(resp);
        }
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        sendText(exchange, "Not Found", 404);
    }

    protected void sendHasInteractions(HttpExchange exchange) throws IOException {
        sendText(exchange, "Not Acceptable", 406);
    }

    protected void sendInfernalServerError(HttpExchange exchange) throws IOException {
        sendText(exchange, "Internal Server Error", 500);
    }

    protected void sendToUser(HttpExchange exchange, String text, int code) throws IOException {
        sendText(exchange, text, code);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        EndPoint endPoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
        switch (endPoint) {
            case GET_TASKS -> handleGet(exchange);
            case GET_TASKSBYID -> handleGetById(exchange);
            case POST_TASKS -> handlePost(exchange);
            case DELETE_TASK -> handleDelete(exchange);
            case GET_EPICS -> handleGet(exchange);
            case GET_EPICBYID -> handleGetById(exchange);
            case GET_EPICSUBTASKS -> handleGet(exchange);
            case POST_EPICS -> handlePost(exchange);
            case DELETE_EPIC -> handleDelete(exchange);
            case GET_SUBTASKS -> handleGet(exchange);
            case GET_SUBTASKBYID -> handleGetById(exchange);
            case POST_SUBTASKS -> handlePost(exchange);
            case DELETE_SUBTASK -> handleDelete(exchange);
            case GET_HISTORY -> handleGet(exchange);
            case GET_PRIORITIZED -> handleGet(exchange);
            default -> sendNotFound(exchange);
        }
    }

    private EndPoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");
        if (pathParts.length == 2) {
            if (pathParts[1].equals("tasks")) {
                if (requestMethod.equals("GET")) {
                    return EndPoint.GET_TASKS;
                } else if (requestMethod.equals("POST")) {
                    return EndPoint.POST_TASKS;
                }
            } else if (pathParts[1].equals("epics")) {
                if (requestMethod.equals("GET")) {
                    return EndPoint.GET_EPICS;
                } else if (requestMethod.equals("POST")) {
                    return EndPoint.POST_EPICS;
                }
            } else if (pathParts[1].equals("subtasks")) {
                if (requestMethod.equals("GET")) {
                    return EndPoint.GET_SUBTASKS;
                } else if (requestMethod.equals("POST")) {
                    return EndPoint.POST_SUBTASKS;
                }
            } else if (pathParts[1].equals("history")) {
                return EndPoint.GET_HISTORY;
            } else if (pathParts[1].equals("prioritized")) {
                return EndPoint.GET_PRIORITIZED;
            } else {
                return EndPoint.UNKNOWN;
            }
        } else if (pathParts.length == 3) {
            if (pathParts[1].equals("tasks")) {
                if (requestMethod.equals("GET")) {
                    return EndPoint.GET_TASKSBYID;
                } else if (requestMethod.equals("DELETE")) {
                    return EndPoint.DELETE_TASK;
                } else if (requestMethod.equals("POST")) {
                    return EndPoint.POST_TASKS;
                }
            } else if (pathParts[1].equals("epics")) {
                if (requestMethod.equals("GET")) {
                    return EndPoint.GET_EPICBYID;
                } else if (requestMethod.equals("DELETE")) {
                    return EndPoint.DELETE_EPIC;
                }
            } else if (pathParts[1].equals("subtasks")) {
                if (requestMethod.equals("GET")) {
                    return EndPoint.GET_SUBTASKBYID;
                } else if (requestMethod.equals("DELETE")) {
                    return EndPoint.DELETE_SUBTASK;
                } else if (requestMethod.equals("POST")) {
                    return EndPoint.POST_SUBTASKS;
                }
            } else {
                return EndPoint.UNKNOWN;
            }
        } else if (pathParts.length == 4) {
            if (pathParts[1].equals("epics") && pathParts[3].equals("subtasks") && requestMethod.equals("GET")) {
                return EndPoint.GET_EPICSUBTASKS;
            }
        }
        return EndPoint.UNKNOWN;
    }

    public abstract void handleGet(HttpExchange exchange) throws IOException;

    public abstract void handleGetById(HttpExchange exchange) throws IOException;

    public abstract void handlePost(HttpExchange exchange) throws IOException;

    public abstract void handleDelete(HttpExchange exchange) throws IOException;

}
