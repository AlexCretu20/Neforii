package client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.ApiResult;
import models.user.UserRegisterRequestDto;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class UserClient {
    //adresa url a backendului
    private final String baseUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper; // Jackson se ocupa de mapare dto ->json

    public UserClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }
    //METODE HTTP

    //REGISTER -> baseUrl/register
    public ApiResult register(UserRegisterRequestDto registerRequestDto) {// dto ul o sa fie creat in meniu, de la datele utilizatorului
        try {
            String url = baseUrl + "/register";
            String reqBody = objectMapper.writeValueAsString(registerRequestDto); //arunca eroare JsonProcessingException

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(reqBody))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());//arunca IOException sau InterruptedException
            int statusCode = response.statusCode();

            boolean isSuccess;
            String message; //mesajul
            if (statusCode >= 200 && statusCode < 300) {
                isSuccess = true;
                message = "The user has been successfully registered";
            } else if (statusCode >= 400 && statusCode < 500) {
                isSuccess = false;
                if (response.body() != null && !response.body().isEmpty()) {
                    message = response.body(); // mesajul aruncat de exceptie, daca e cazul -> GlobalERxceptionHandler & exceptiile definite in backend
                } else { //mesaj default
                    message = "Invalid user data for register!";
                }
            } else {
                isSuccess = false;
                message = "Unexpected error has appeared! Please try again later.";
            }
            return new ApiResult(isSuccess, message, response.body());

        } catch (JsonProcessingException e) {
            return new ApiResult(false, "Couldn't map the User request to JSON.", null);
        } catch (IOException | InterruptedException e) {
            return new ApiResult(false, "Couldn't maintain the connection." + e.getMessage(), null);
        }
    }
}