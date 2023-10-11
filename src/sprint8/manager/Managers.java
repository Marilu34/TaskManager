package sprint8.manager;


import server.http.HttpTaskManager;

public class Managers {

    public static final String URL_KV_SERVER = "http://localhost:8081/";

    public static TaskManager getDefaultManager() {
        return new InMemoryTaskManager();
    }

    public static TaskManager getDefault() {
        return new HttpTaskManager(URL_KV_SERVER);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}
