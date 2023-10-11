package tests.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.KV.KVServer;
import server.adapter.TimeTypeAdapter;
import server.http.HttpTaskManager;
import server.http.HttpTaskServer;
import sprint8.manager.Managers;
import sprint8.tasks.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class HttpTaskManagerTest {
    HttpTaskServer taskServer;
    private final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new TimeTypeAdapter()).create();
    private HttpTaskManager manager;
    KVServer kvServer;
    private final HttpClient client = HttpClient.newHttpClient();
    private static final String URL_HTTP_TASK_MANAGER = "http://localhost:8080/";

    @BeforeEach
    public void startServer() {
        try {
            kvServer = new KVServer();
            kvServer.start();
            taskServer = new HttpTaskServer();
            taskServer.start();
            manager = (HttpTaskManager) Managers.getDefault();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterEach  //Остановка сервера HTTPTaskServer перед проведением следующего теста
    public void stopServer() {
        taskServer.stop();
        kvServer.stop();
    }
//для getPrioritizedTasks()
    @Test
    public void testGetTasksPriority() throws IOException, InterruptedException {
        Task newTask = new Task("Task1", "Описание Task", Status.NEW, TaskTypes.TASK, LocalDateTime.now(), 11);
        newTask.setId(1);
        URI uriAddNewTask = URI.create(URL_HTTP_TASK_MANAGER + "tasks/task?id=1");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(newTask));
        HttpRequest requestAddNewTask = HttpRequest
                .newBuilder()
                .uri(uriAddNewTask)
                .POST(body)
                .build();
        HttpResponse<String> responseAddNewTask = client.send(requestAddNewTask, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseAddNewTask.statusCode());


        Task newTask2 = new Task("Task2", "Описание Task", Status.NEW, TaskTypes.TASK, LocalDateTime.now().minusDays(1), 11);
        newTask2.setId(2);
        URI uriAddNewTask2 = URI.create(URL_HTTP_TASK_MANAGER + "tasks/task?id=2");
        HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(gson.toJson(newTask2));
        HttpRequest requestAddNewTask2 = HttpRequest
                .newBuilder()
                .uri(uriAddNewTask2)
                .POST(body2)
                .build();
        HttpResponse<String> responseAddNewTask2 = client.send(requestAddNewTask2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseAddNewTask2.statusCode());

        URI uriGetTask = URI.create(URL_HTTP_TASK_MANAGER + "tasks");
        HttpRequest requestGetTask = HttpRequest
                .newBuilder()
                .uri(uriGetTask)
                .GET()
                .build();

        HttpResponse<String> responseGetTask = client.send(requestGetTask, HttpResponse.BodyHandlers.ofString());
        assertEquals("{\"taskPriority\":[2,1]}", responseGetTask.body());
    }

    @Test
    public void testGetEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "Описание Epic", Status.NEW, TaskTypes.EPIC, LocalDateTime.of(2023, 4, 2, 10, 0), 11);
        epic.setId(1);
        URI uriAddNewTask = URI.create(URL_HTTP_TASK_MANAGER + "tasks/epic?id=1");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(epic));
        HttpRequest requestAddNewTask = HttpRequest
                .newBuilder()
                .uri(uriAddNewTask)
                .POST(body)
                .build();
        HttpResponse<String> responseAddNewTask = client.send(requestAddNewTask, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseAddNewTask.statusCode());


        Epic epic2 = new Epic("Epic2", "Описание Epic", Status.NEW, TaskTypes.EPIC, LocalDateTime.of(2023, 4, 2, 10, 0), 11);
        epic2.setId(2);
        URI uriAddNewTask2 = URI.create(URL_HTTP_TASK_MANAGER + "tasks/epic?id=2");
        HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(gson.toJson(epic2));
        HttpRequest requestAddNewTask2 = HttpRequest
                .newBuilder()
                .uri(uriAddNewTask2)
                .POST(body2)
                .build();
        HttpResponse<String> responseAddNewTask2 = client.send(requestAddNewTask2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseAddNewTask2.statusCode());

        URI uriGetTask = URI.create(URL_HTTP_TASK_MANAGER + "tasks/epic?id=2");
        HttpRequest requestGetTask = HttpRequest
                .newBuilder()
                .uri(uriGetTask)
                .GET()
                .build();

        HttpResponse<String> responseGetTask = client.send(requestGetTask, HttpResponse.BodyHandlers.ofString());
        String excepted = "{\"subtaskIds\":[],\"description\":\"Epic2\",\"id\":2,\"name\":\"Описание Epic\",\"status\":\"NEW\",\"taskTypes\":\"EPIC\",\"duration\":0}";
        assertEquals(excepted, responseGetTask.body());
    }

    @Test
    public void testGetSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "Описание Epic", Status.NEW, TaskTypes.EPIC, LocalDateTime.of(2023, 4, 2, 10, 0), 11);
        epic.setId(1);
        SubTask sub = new SubTask("Sub1", "Описание Sub", Status.NEW, TaskTypes.SUBTASK, LocalDateTime.of(2023, 4, 2, 10, 0), 1, 1);
        sub.setId(1);
        URI uriAddNewTask = URI.create(URL_HTTP_TASK_MANAGER + "tasks/subtask?id=1");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(sub));
        HttpRequest requestAddNewTask = HttpRequest
                .newBuilder()
                .uri(uriAddNewTask)
                .POST(body)
                .build();
        HttpResponse<String> responseAddNewTask = client.send(requestAddNewTask, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseAddNewTask.statusCode());


        SubTask sub1 = new SubTask("Sub1", "Описание Sub", Status.NEW, TaskTypes.SUBTASK, LocalDateTime.of(2023, 4, 2, 10, 0), 1, 1);
        sub1.setId(1);
        URI uriAddNewTask2 = URI.create(URL_HTTP_TASK_MANAGER + "tasks/subtask?id=2");
        HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(gson.toJson(sub1));
        HttpRequest requestAddNewTask2 = HttpRequest
                .newBuilder()
                .uri(uriAddNewTask2)
                .POST(body2)
                .build();
        HttpResponse<String> responseAddNewTask2 = client.send(requestAddNewTask2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseAddNewTask2.statusCode());

        URI uriGetTask = URI.create(URL_HTTP_TASK_MANAGER + "tasks/subtask?id=2");
        HttpRequest requestGetTask = HttpRequest
                .newBuilder()
                .uri(uriGetTask)
                .GET()
                .build();

        HttpResponse<String> responseGetTask = client.send(requestGetTask, HttpResponse.BodyHandlers.ofString());
        String excepted = "{\"epicId\":1,\"description\":\"Sub1\",\"id\":2,\"name\":\"Описание Sub\",\"status\":\"NEW\",\"taskTypes\":\"SUBTASK\",\"startTime\":\"2023-04-02T10:00\",\"duration\":1}";
        assertEquals(excepted, responseGetTask.body());
    }

    @Test
    public void testSaveAndLoadHistory() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "Описание1 epic", Status.NEW, TaskTypes.EPIC, LocalDateTime.now(), 11);
        epic.setId(1);
        SubTask subTask = new SubTask("SubTask1", "Описание1 sub", Status.NEW, TaskTypes.SUBTASK, LocalDateTime.now(), 12, 1);
        subTask.setId(2);
        Task task = new Task("Task1", "Описание1 task", Status.NEW, TaskTypes.TASK, LocalDateTime.now(), 13);
        task.setId(3);
        manager.addEpic(epic);
        manager.addSubtask(subTask);
        manager.addTask(task);
        manager.getTaskById(3);
        manager.getEpicById(1);
        manager.getSubtaskById(2);
        manager.getHistory();
        URI uriAddNewTask = URI.create(URL_HTTP_TASK_MANAGER + "tasks/history");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(manager.getHistory()));
        HttpRequest requestAddNewTask = HttpRequest
                .newBuilder()
                .uri(uriAddNewTask)
                .GET()
                .build();
        HttpResponse<String> responseAddNewTask = client.send(requestAddNewTask, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseAddNewTask.statusCode());

    }

    @Test
    public void testDeleteTaskById() throws IOException, InterruptedException {
        URI uri = URI.create(URL_HTTP_TASK_MANAGER + "tasks/task/?id=1");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        Task serverTask = gson.fromJson(response.body(), Task.class);
        assertNull(serverTask);
    }

    @Test
    public void testDeleteEpicById() throws IOException, InterruptedException {
        URI uri = URI.create(URL_HTTP_TASK_MANAGER + "tasks/epic/?id=1");
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        Epic serverTask = gson.fromJson(response.body(), Epic.class);
        assertNull(serverTask);
    }

    @Test
    public void testDeleteSubTaskById() throws IOException, InterruptedException {
        URI uri = URI.create(URL_HTTP_TASK_MANAGER + "tasks/subtask/?id=1");
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        SubTask serverTask = gson.fromJson(response.body(), SubTask.class);
        assertNull(serverTask);
    }

    @Test
    public void testAddNewTask() throws IOException, InterruptedException {
        Task newTask = new Task("Task1", "Описание Task", Status.NEW, TaskTypes.TASK, LocalDateTime.now(), 11);
        newTask.setId(1);
        URI uriAddNewTask = URI.create(URL_HTTP_TASK_MANAGER + "tasks/task?id=1");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(newTask));
        HttpRequest requestAddNewTask = HttpRequest
                .newBuilder()
                .uri(uriAddNewTask)
                .POST(body)
                .build();
        HttpResponse<String> responseAddNewTask = client.send(requestAddNewTask, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseAddNewTask.statusCode());
    }

    @Test
    public void testAddNewEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "Описание Epic", Status.NEW, TaskTypes.EPIC, LocalDateTime.now(), 11);
        epic.setId(1);
        URI uriAddNewTask = URI.create(URL_HTTP_TASK_MANAGER + "tasks/epic?id=1");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(epic));
        HttpRequest requestAddNewTask = HttpRequest
                .newBuilder()
                .uri(uriAddNewTask)
                .POST(body)
                .build();
        HttpResponse<String> responseAddNewTask = client.send(requestAddNewTask, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseAddNewTask.statusCode());
    }

    @Test
    public void testAddNewSubTask() throws IOException, InterruptedException {
        SubTask sub = new SubTask("Sub1", "Описание Sub", Status.NEW, TaskTypes.SUBTASK, LocalDateTime.now(), 1, 1);
        sub.setId(1);
        URI uriAddNewTask = URI.create(URL_HTTP_TASK_MANAGER + "tasks/subtask?id=1");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(sub));
        HttpRequest requestAddNewTask = HttpRequest
                .newBuilder()
                .uri(uriAddNewTask)
                .POST(body)
                .build();
        HttpResponse<String> responseAddNewTask = client.send(requestAddNewTask, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseAddNewTask.statusCode());
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        Task newTask = new Task("Task1", "Описание Task", Status.NEW, TaskTypes.TASK, LocalDateTime.now(), 11);
        newTask.setId(1);
        String expectedJson = gson.toJson(newTask);
        URI uriAddNewTask = URI.create(URL_HTTP_TASK_MANAGER + "tasks/task?id=1");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(newTask));
        HttpRequest requestAddNewTask = HttpRequest
                .newBuilder()
                .uri(uriAddNewTask)
                .POST(body)
                .build();
        HttpResponse<String> responseAddNewTask = client.send(requestAddNewTask, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseAddNewTask.statusCode());

        URI uriGetTask = URI.create(URL_HTTP_TASK_MANAGER + "tasks/task?id=1");
        HttpRequest requestGetTask = HttpRequest
                .newBuilder()
                .uri(uriGetTask)
                .GET()
                .build();

        HttpResponse<String> responseGetTask = client.send(requestGetTask, HttpResponse.BodyHandlers.ofString());
        assertEquals(expectedJson, responseGetTask.body());
    }

    @Test
    public void testGetEpicById() throws IOException, InterruptedException {
        Epic newTask = new Epic("Epic1", "Описание Epic", Status.NEW, TaskTypes.EPIC, LocalDateTime.now(), 0);
        newTask.setId(1);
        String expectedJson = "{\"subtaskIds\":[],\"description\":\"Epic1\",\"id\":1,\"name\":\"Описание Epic\",\"status\":\"NEW\",\"taskTypes\":\"EPIC\",\"duration\":0}";
        URI uriAddNewTask = URI.create(URL_HTTP_TASK_MANAGER + "tasks/epic?id=1");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(newTask));
        HttpRequest requestAddNewTask = HttpRequest
                .newBuilder()
                .uri(uriAddNewTask)
                .POST(body)
                .build();
        HttpResponse<String> responseAddNewTask = client.send(requestAddNewTask, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseAddNewTask.statusCode());

        URI uriGetTask = URI.create(URL_HTTP_TASK_MANAGER + "tasks/epic?id=1");
        HttpRequest requestGetTask = HttpRequest
                .newBuilder()
                .uri(uriGetTask)
                .GET()
                .build();

        HttpResponse<String> responseGetTask = client.send(requestGetTask, HttpResponse.BodyHandlers.ofString());
        assertEquals(expectedJson, responseGetTask.body());

    }

    @Test
    public void testGetSubTaskById() throws IOException, InterruptedException {
        SubTask newTask = new SubTask("SubTask1", "Описание SubTask", Status.NEW, TaskTypes.SUBTASK, LocalDateTime.of(2023, 4, 2, 10, 0), 0, 1);
        newTask.setId(1);
        String expectedJson = gson.toJson(newTask);
        URI uriAddNewTask = URI.create(URL_HTTP_TASK_MANAGER + "tasks/subtask?id=1");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(newTask));
        HttpRequest requestAddNewTask = HttpRequest
                .newBuilder()
                .uri(uriAddNewTask)
                .POST(body)
                .build();
        HttpResponse<String> responseAddNewTask = client.send(requestAddNewTask, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseAddNewTask.statusCode());

        URI uriGetTask = URI.create(URL_HTTP_TASK_MANAGER + "tasks/subtask?id=1");
        HttpRequest requestGetTask = HttpRequest
                .newBuilder()
                .uri(uriGetTask)
                .GET()
                .build();

        HttpResponse<String> responseGetTask = client.send(requestGetTask, HttpResponse.BodyHandlers.ofString());
        assertEquals(expectedJson, responseGetTask.body());

    }

}