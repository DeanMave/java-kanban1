package tracker.HTTP.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import tracker.HTTP.handlers.*;
import tracker.HTTP.adapters.DurationAdapter;
import tracker.HTTP.adapters.LocalDataTimeAdapter;
import tracker.controllers.Manager;
import tracker.interfaces.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private final int PORT = 8080;
    private final HttpServer server;
    private final TaskManager manager;
    private final Gson gson;

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);
        this.manager = Manager.getInMemoryTaskManager();
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDataTimeAdapter())
                .setPrettyPrinting()
                .create();
        server.createContext("/tasks", new TaskHandler(manager,gson));
        server.createContext("/epics", new EpicHandler(manager,gson));
        server.createContext("/subtasks", new SubTaskHandler(manager,gson));
        server.createContext("/history", new HistoryHandler(manager,gson));
        server.createContext("/prioritized", new PriorityHandler(manager,gson));
    }

    public void serverStart(){
        server.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public void serverStop(){
        server.stop(1);
        System.out.println("HTTP-сервер остановлен");
    }

    public static Gson getGson(){
        return new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDataTimeAdapter())
                .setPrettyPrinting()
                .create();
    }

    public static void main(String[] args) throws IOException {
        TaskManager manager = Manager.getInMemoryTaskManager();
        HttpTaskServer server = new HttpTaskServer(manager);
        server.serverStart();
    }
}
