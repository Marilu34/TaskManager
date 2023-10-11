package server.http;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import server.adapter.TimeTypeAdapter;
import sprint8.manager.Managers;
import sprint8.tasks.Epic;
import sprint8.tasks.SubTask;
import sprint8.tasks.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.time.LocalDateTime;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {

    public static final int PORT = 8080;
    public static final String HOST = "localhost";
    private final HttpServer server;

    private final HttpTaskManager manager;

    public HttpTaskServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(HOST, PORT), 0);
        manager = (HttpTaskManager) Managers.getDefault();
        createContext(server);
    }


    private void createContext(HttpServer serverContext) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new TimeTypeAdapter())
                .create();
        //Получение списка задач с сортировкой по приоритету для выполнения
        serverContext.createContext("/tasks", (h) -> {// для создания контекстов сервера.
            JsonObject resp = new JsonObject();
            try {
                System.out.println("\n/tasks");
// Создание контекста сервера для получения списка задач с сортировкой по приоритету для выполнения.
                if ("GET".equals(h.getRequestMethod())) {
                    JsonArray priority = new JsonArray();
                    resp.add("taskPriority", priority);
                    for (Task task : manager.getPrioritizedTasks()) {
                        priority.add(task.getId());
                    }
                    sendText(h, resp.toString());
                } else {
                    System.out.println("/tasks ждёт GET - запрос, а получил " + h.getRequestMethod());
                    h.sendResponseHeaders(405, 0);
                }
            } finally {
                // Закрытие соединения после выполнения запроса.
                h.close();
            }
        });

        //Получение истории обращения к задачам
        serverContext.createContext("/tasks/history", (h) -> {
            JsonObject resp = new JsonObject();
            try {
                System.out.println("\n/tasks/history");
                if ("GET".equals(h.getRequestMethod())) {
                    JsonArray hist = new JsonArray();//создаем массив для хранения историй
                    resp.add("history", hist);
                    for (Task task : manager.getHistory()) {
                        hist.add(task.getId());
                    }
                    sendText(h, resp.toString());
                } else {
                    System.out.println("/tasks/history ждёт GET - запрос, а получил " + h.getRequestMethod());
                    h.sendResponseHeaders(405, 0);
                }
            } finally {
                h.close();
            }
        });

        //Работа с задачами: получение(клиентом), создание, удаление
        serverContext.createContext("/tasks/task", (h) -> {
            JsonObject resp = new JsonObject();
            Integer id = readFromQueryString(h.getRequestURI(), "id");
            try {
                System.out.println("\n/tasks/task");
                //Внутри каждого контекста происходит обработка запросов в зависимости от метода запроса (GET, POST, DELETE).
                switch (h.getRequestMethod()) {
                    //При GET-запросе возвращается список задач или история обращения к задачам
                    case "GET":     //Отправить задачу(задачи) по запросу
                        if (id == null) {   //Если идентификатор не указан - выдать все
                            resp.add("tasks", gson.toJsonTree(manager.getAllTasks()));
                            sendText(h, resp.toString());
                        } else {
                            sendText(h, gson.toJson(manager.getTaskById(id)));
                        }
                        break;
// при POST-запросе создается новая задача на основе полученного JSON
                    case "POST":    //Создать задачу на основе полученного Json
                        manager.addTask(gson.fromJson(JsonParser.parseString(readText(h)), Task.class));
                        h.sendResponseHeaders(200, 0);
                        break;
                    //при DELETE-запросе удаляется задача или подзадачи по указанному id
                    case "DELETE":  //Удаление задачи (либо - задач, если идентификатор пустой)
                        if (id == null) {
                            manager.deleteTasks();
                        } else {
                            manager.deleteTaskById(id);
                        }
                        h.sendResponseHeaders(200, 0);
                        break;
                    //Если метод запроса не соответствует GET, POST или DELETE, то возвращается ошибка 405.
                    default:
                        System.out.println("/tasks/task ждёт GET, POST, DELETE - запрос, а получил " + h.getRequestMethod());
                        h.sendResponseHeaders(405, 0);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                h.close();
            }
        });

        //Работа с подзадачами: получение(клиентом), создание, удаление
        serverContext.createContext("/tasks/subtask", (h) -> {
            Integer id = readFromQueryString(h.getRequestURI(), "id");
            try {
                System.out.println("\n/tasks/subtask");

                switch (h.getRequestMethod()) {
                    case "GET":     //Отправить подзадачу по запросу
                        if (id != null) {
                            sendText(h, gson.toJson((SubTask) manager.getTaskById(id)));
                        } else {
                            h.sendResponseHeaders(404, 0);
                        }
                        break;
                    case "POST":    //Создать задачу на основе полученного Json
                        SubTask subTask = gson.fromJson(JsonParser.parseString(readText(h)), SubTask.class);
                        manager.addTask(subTask);
                        //manager.addTask(gson.fromJson(JsonParser.parseString(readText(h)), SubTask.class));
                        h.sendResponseHeaders(200, 0);
                        break;
                    case "DELETE":  //Удаление подзадачи
                        if (id != null) {
                            manager.deleteTaskById(id);
                        }
                        h.sendResponseHeaders(200, 0);
                        break;
                    default:
                        System.out.println("/tasks/task ждёт GET, POST, DELETE - запрос, а получил " + h.getRequestMethod());
                        h.sendResponseHeaders(405, 0);
                }
            } finally {
                h.close();
            }
        });

        //Получение эпика подзадачи
        serverContext.createContext("/tasks/subtask/epic", (h) -> {
            Integer id = readFromQueryString(h.getRequestURI(), "id");
            try {
                System.out.println("\n/tasks/subtask/epic");
                if ("GET".equals(h.getRequestMethod())) {
                    if (id != null) {
                        sendText(h, gson.toJson(((SubTask) manager.getAllEpics())));
                    } else {
                        h.sendResponseHeaders(404, 0);
                    }
                } else {
                    System.out.println("/tasks/subtask/epic ждёт GET - запрос, а получил " + h.getRequestMethod());
                    h.sendResponseHeaders(405, 0);
                }
            } finally {
                h.close();
            }
        });

        //Работа с эпиками: получение(клиентом), создание, удаление
        serverContext.createContext("/tasks/epic", (h) -> {
            Integer id = readFromQueryString(h.getRequestURI(), "id");
            try {
                System.out.println("\n/tasks/epic");
                switch (h.getRequestMethod()) {
                    case "GET":     //Отправить эпик по запросу
                        if (id != null) {
                            sendText(h, gson.toJson(manager.getEpicById(id)));
                        } else {
                            h.sendResponseHeaders(404, 0);
                        }
                        break;
                    case "POST":    //Создать эпика на основе полученного Json
                        String body = readText(h);
                        if (body != null) {
                            Epic epic = gson.fromJson(JsonParser.parseString(body), Epic.class);
                            manager.addEpic(epic);
                            //Коррекция подзадач (если они пришли в составе эпика) после десериализации
                            for (SubTask subTask : manager.getSubtasksByEpicId(epic.getId())) {
                                subTask.getEpicId();  //Восстановление обратной связи с эпиком
                                manager.getAllSubtasks().add(subTask.getId(), subTask);    //Прописывание подзадачи в общем списке менеджера
                            }
                        } else {
                            System.out.println("Пустые данные для создания эпика");
                        }
                        h.sendResponseHeaders(200, 0);
                        break;
                    case "DELETE":  //Удаление эпика (с подзадачами)
                        if (id != null) {
                            manager.deleteTaskById(id);
                        }
                        h.sendResponseHeaders(200, 0);
                        break;
                    default:
                        System.out.println("/tasks/task ждёт GET, POST, DELETE - запрос, а получил " + h.getRequestMethod());
                        h.sendResponseHeaders(405, 0);
                }
            } finally {
                h.close();
            }
        });
    }

    //Процедура получения заданного числового параметра из строки запроса
    private Integer readFromQueryString(URI uri, String name) {
        if (uri != null && name != null) {
            String query = uri.getQuery();
            if (query != null) {
                String[] pairs = query.split("&");
                for (String pair : pairs) {
                    String[] param = pair.split("=");
                    if (param.length >= 1 && name.equals(param[0]))
                        return Integer.parseInt(param[1]);
                }
            }
        }
        return null;
    }

    protected String readText(HttpExchange text) throws IOException {
        return new String(text.getRequestBody().readAllBytes(), UTF_8);
    }

    protected void sendText(HttpExchange exchangeText, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        exchangeText.getResponseHeaders().add("Content-Type", "application/json");
        exchangeText.sendResponseHeaders(200, resp.length);
        exchangeText.getResponseBody().write(resp);
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://" + HOST + ":" + PORT + "/");
        server.start();
    }

    public void stop() {
        server.stop(0);
    }
}