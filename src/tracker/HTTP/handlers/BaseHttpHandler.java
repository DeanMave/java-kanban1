package tracker.HTTP.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler implements HttpHandler {
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
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET" -> handleGet(exchange);
            case "POST" -> handlePost(exchange);
            case "DELETE" -> handleDelete(exchange);
            default -> sendNotFound(exchange);
        }
    }

    public void handleGet(HttpExchange exchange) throws IOException {
        sendToUser(exchange, "Такой метод не предусмотрен для данного класса", 400);
    }

    public void handlePost(HttpExchange exchange) throws IOException {
        sendToUser(exchange, "Такой метод не предусмотрен для данного класса", 400);
    }

    public void handleDelete(HttpExchange exchange) throws IOException {
        sendToUser(exchange, "Такой метод не предусмотрен для данного класса", 400);
    }

}
