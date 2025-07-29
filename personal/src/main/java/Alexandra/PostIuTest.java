package Alexandra;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PostIuTest {

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        String bodyJson = """
            {
              "title": "Test post",
              "content": "Hello from client",
              "imagePath": "/img/test.png",
              "userId": 1
            }
        """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/posts"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(bodyJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Status code: " + response.statusCode());
        System.out.println("Response: " + response.body());
    }
}
