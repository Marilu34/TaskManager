package server.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import server.adapter.TimeTypeAdapter;
import sprint8.tasks.Status;
import sprint8.tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

import static sprint8.tasks.TaskTypes.TASK;

public class Client {

    public static void main(String[] args) throws IOException, InterruptedException {
        Task newTask = new Task("Task1", "Описание Таска", Status.DONE, TASK, LocalDateTime.now(), 11);
        URI url = URI.create("http://localhost:8080/tasks/task/");
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new TimeTypeAdapter()).create();
        String json = gson.toJson(newTask);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);

    }

}

