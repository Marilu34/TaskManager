package server.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import server.KV.KVTaskClient;
import server.adapter.TimeTypeAdapter;
import sprint8.manager.FileBackedTaskManager;
import sprint8.tasks.Epic;
import sprint8.tasks.SubTask;
import sprint8.tasks.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HttpTaskManager extends FileBackedTaskManager {
    private final KVTaskClient client;
    private final Gson gson;

    public HttpTaskManager(String URL) {
        this.client = new KVTaskClient(URL);
        this.gson = new GsonBuilder()
                //  .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new TimeTypeAdapter())
                .create();

    }


    @Override
    public void save() {
        String taskToGson = gson.toJson(getTaskMap());
        String epicToGson = gson.toJson(getEpicMap());
        String subtaskToGson = gson.toJson(getSubTaskMap());
        String historyToGson = gson.toJson(getHistory());
        client.put("task", taskToGson);
        client.put("epic", epicToGson);
        client.put("subtask", subtaskToGson);
        client.put("history", historyToGson);
    }

    public void load(String apiToken) {
        client.setApiToken(apiToken);
        setTaskMap(gson.fromJson(client.load("task"), new TypeToken<HashMap<Integer, Task>>() {
        }.getType()));
        setEpicMap(gson.fromJson(client.load("epic"), new TypeToken<HashMap<Integer, Epic>>() {
        }.getType()));
        setSubTaskMap(gson.fromJson(client.load("subtask"), new TypeToken<HashMap<Integer, SubTask>>() {
        }.getType()));
        ArrayList<Task> loadHistoryList = new ArrayList<>(gson.fromJson(client.load("history"),
                new TypeToken<ArrayList<Task>>() {
                }.getType()));
        for (Task taskInHistory : loadHistoryList) {
            getHistoryManager().add(taskInHistory);
        }
    }

    //Перегрузка метода для добавления записи изменений в файл
    @Override
    public void addTask(Task newTask) {
        super.addTask(newTask);
        save();
    }

    //Перегрузка метода для добавления записи изменений в файл
    @Override
    public void updateTask(Task newTask) {
        super.updateTask(newTask);
        save();
    }

    //Перегрузка метода для добавления записи изменений в файл
    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    //Перегрузка метода для добавления записи изменений
    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return super.getPrioritizedTasks();
    }
}