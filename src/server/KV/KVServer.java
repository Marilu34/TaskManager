package server.KV;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Постман: https://www.getpostman.com/collections/a83b61d9e1c81c10575c
 */
public class KVServer {
    public static final int PORT = 8081 ;
    private final String apiToken;
    private final HttpServer server;
    private final Map<String, String> data = new HashMap<>();

    public KVServer() throws IOException {
        apiToken = generateApiToken();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/register", this::register);
        server.createContext("/save", this::save);
        server.createContext("/load", this::load);
    }

    private void load(HttpExchange server) {
        try {
            System.out.println("\n/load");
            if (!hasAuth(server)) {
                System.out.println("Запрос неавторизован, нужен параметр в query API_TOKEN со значением апи-ключа");
                server.sendResponseHeaders(403, 0);
                return;
            }
            if ("GET".equals(server.getRequestMethod())) {
                String key = server.getRequestURI().getPath().substring("/load/".length());
                if (key.isEmpty()) {
                    System.out.println("Key для сохранения пустой. key указывается в пути: /save/{key}");
                    server.sendResponseHeaders(400, 0);
                    return;
                }

                if (data.containsKey(key)) {
                    String value = data.get(key);
                    sendText(server, value);
                    System.out.println("Значение для ключа " + key + " получено!");
                    server.sendResponseHeaders(200, 0);
                } else {
                    System.out.println("Значение " + key + " нет!");
                    server.sendResponseHeaders(400, 0);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            stop();
        }
    }

    private void save(HttpExchange httpExchange) throws IOException {
        try {
            System.out.println("\n/save");
            if (!hasAuth(httpExchange)) {
                System.out.println("Запрос неавторизован, нужен параметр в query API_TOKEN со значением апи-ключа");
                httpExchange.sendResponseHeaders(403, 0);
                return;
            }
            if ("POST".equals(httpExchange.getRequestMethod())) {
                String key = httpExchange.getRequestURI().getPath().substring("/save/".length());
                if (key.isEmpty()) {
                    System.out.println("Key для сохранения пустой. key указывается в пути: /save/{key}");
                    httpExchange.sendResponseHeaders(400, 0);
                    return;
                }
                String value = readText(httpExchange);
                if (value.isEmpty()) {
                    System.out.println("Value для сохранения пустой. value указывается в теле запроса");
                    httpExchange.sendResponseHeaders(400, 0);
                    return;
                }
                data.put(key, value);
                System.out.println("Значение для ключа " + key + " успешно обновлено!");
                httpExchange.sendResponseHeaders(200, 0);
            } else {
                System.out.println("/save ждёт POST-запрос, а получил: " + httpExchange.getRequestMethod());
                httpExchange.sendResponseHeaders(405, 0);
            }
        } finally {
            httpExchange.close();
        }
    }

    private void register(HttpExchange registration) throws IOException {
        try {
            System.out.println("\n/register");
            if ("GET".equals(registration.getRequestMethod())) {
                sendText(registration, apiToken);
            } else {
                System.out.println("/register ждёт GET-запрос, а получил " + registration.getRequestMethod());
                registration.sendResponseHeaders(405, 0);
            }
        } finally {
            registration.close();
        }
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        System.out.println("API_TOKEN: " + apiToken);
        server.start();
    }

    private String generateApiToken() {
        return "" + System.currentTimeMillis();
    }

    protected boolean hasAuth(HttpExchange auth) {
        String rawQuery = auth.getRequestURI().getRawQuery();
        return rawQuery != null && (rawQuery.contains("API_TOKEN=" + apiToken) || rawQuery.contains("API_TOKEN=DEBUG"));
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


    public void stop() {
        server.stop(0);
    }
}