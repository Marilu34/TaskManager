package server.KV;

import sprint8.exceptions.ManagerSaveException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {//клиент для взаимодействия с сервером по протоколу HTTP
    private final HttpClient client;
    private final String URL;
    private String apiToken;

    public KVTaskClient(String URL) {
        this.client = HttpClient.newHttpClient();
        this.URL = URL;
        this.apiToken = register();// выдаётся токен (API_TOKEN), который нужен при работе с сервером.
    }

    private String register() {// который возвращает API-токен, полученный от сервера при регистрации клиента.
        URI url = URI.create(URL + "register/");// для регистрации.
        HttpRequest request = HttpRequest.newBuilder()///Создание объекта URI на основе переданного адреса сервера и пути для регистрации.
                .GET()// Выполнение GET-запроса на сервер и возврат тела ответа в виде строки.
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        return request(request).body();
    }

    private HttpResponse<String> request(HttpRequest request) {//выполняет HTTP-запрос на сервер и возвращает тело ответа в виде строки.
        HttpResponse<String> response;
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        try {
            response = client.send(request, handler);
            if (response.statusCode() != 200) {
                throw new ManagerSaveException("Ошибка");
            }
            return response;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void put(String value, String gson) {//сохраняет данные на сервере.
        URI url = URI.create(URL + "save/" + value + "?API_TOKEN=" + apiToken);// для сохранения данных и API-токена клиента.
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson);//для выполнения POST-запроса на сервер с указанием адреса, тела запроса в формате JSON и версии протокола.
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)//Выполнение POST-запроса на сервер без получения ответа.
                .uri(url)
                .header("Accept", "application/json")
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        request(request);
    }

    public String load(String value) {//возвращает состояние менеджера задач через запрос
        URI url = URI.create(URL + "load/" + value + "?API_TOKEN=" + apiToken);//для загрузки данных и API-токена клиента
        HttpRequest request = HttpRequest.newBuilder()//для выполнения GET-запроса на сервер с указанием адреса и версии протокола.
                .GET()//Выполнение GET-запроса на сервер и возврат тела ответа в виде строки.
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        return request(request).body();
    }

    public void setApiToken(String apiToken) {// устанавливает API-токен клиента.
        this.apiToken = apiToken;
    }
}