import server.KV.KVServer;
import server.http.HttpTaskServer;

public class Main {

    public static void main(String[] args) {
        try {
            new KVServer().start();
            HttpTaskServer httpTaskServer = new HttpTaskServer();
            httpTaskServer.start();
        } catch (Exception ex) {
            System.err.println("Ошибка запуска");
            ex.printStackTrace();
        }
    }
}