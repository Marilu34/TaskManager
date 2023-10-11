package sprint8.manager;


import server.KV.KVServer;


import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        new KVServer().start();
    }
}
