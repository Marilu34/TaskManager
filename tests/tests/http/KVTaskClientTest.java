package tests.http;

import org.junit.jupiter.api.BeforeAll;
import server.KV.KVTaskClient;
import sprint8.manager.Managers;

public class KVTaskClientTest {
    private static final String API_TOKEN = "1234567890";
    static KVTaskClient client;

    @BeforeAll
    public static void setUp() {

        client = new KVTaskClient(Managers.URL_KV_SERVER);
    }
}


