package client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import config.HttpStatusCategory;
import models.ApiResult;
import models.user.UserLoginRequestDto;
import models.user.UserRegisterRequestDto;
import models.user.UserUpdateRequestDto;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

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
            String reqBody = objectMapper.writeValueAsString(registerRequestDto);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(reqBody))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            //pentru switch case al status code-urilor, sa nu mai fie magic numbers
            HttpStatusCategory codeCategory = HttpStatusCategory.from(statusCode);

            boolean isSuccess;
            String message;

            switch (codeCategory) {
                case SUCCESS -> {
                    isSuccess = true;
                    message = "The user has been successfully registered!";
                }
                case CLIENT_ERROR -> {
                    isSuccess = false;
                    if (response.body() != null && !response.body().isEmpty())
                        message = response.body();
                    else
                        message = "Invalid user data for register!";
                }
                case SERVER_ERROR -> {
                    isSuccess = false;
                    message = "Server error occurred. Please try again later.";
                }
                default -> {
                    isSuccess = false;
                    message = "Unexpected error has appeared! Please try again later.";
                }
            }
            return new ApiResult(isSuccess, message, response.body());

        } catch (JsonProcessingException e) {
            return new ApiResult(false, "Couldn't map the User request to JSON.", null);
        } catch (IOException | InterruptedException e) {
            return new ApiResult(false, "Couldn't maintain the connection." + e.getMessage(), null);
        }
    }

    //LOGIN -> baseUrl/login
    public ApiResult login(UserLoginRequestDto userLoginRequestDto) {
        try {
            String url = baseUrl + "/login";
            String reqBody = objectMapper.writeValueAsString(userLoginRequestDto);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(reqBody))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            HttpStatusCategory codeCategory = HttpStatusCategory.from(statusCode);

            boolean isSuccess;
            String message;

            switch (codeCategory) {
                case SUCCESS -> {
                    isSuccess = true;
                    message = "You have successfully logged in successfully!";
                }
                case CLIENT_ERROR -> {
                    isSuccess = false;
                    if (response.body() != null && !response.body().isEmpty())
                        message = response.body();
                    else
                        message = "Invalid user data for login!";
                }
                case SERVER_ERROR -> {
                    isSuccess = false;
                    message = "Server error occurred. Please try again later.";
                }
                default -> {
                    isSuccess = false;
                    message = "Unexpected error has appeared! Please try again later.";
                }
            }
            return new ApiResult(isSuccess, message, response.body());

        } catch (JsonProcessingException e) {
            return new ApiResult(false, "Couldn't map the User request to JSON.", null);
        } catch (IOException | InterruptedException e) {
            return new ApiResult(false, "Couldn't maintain the connection." + e.getMessage(), null);
        }
    }

    //get user by username -> baseUrl/{username}
    public ApiResult getUserByUsername(String username) {
        try {
            String url = baseUrl + "/" + username;

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            HttpStatusCategory codeCategory = HttpStatusCategory.from(statusCode);

            boolean isSuccess;
            String message;

            switch (codeCategory) {
                case SUCCESS -> {
                    isSuccess = true;
                    message = "The user has been found successfully!";
                }
                case CLIENT_ERROR -> {
                    isSuccess = false;
                    if (response.body() != null && !response.body().isEmpty())
                        message = response.body();
                    else
                        message = "The user couldn't be found!";
                }
                case SERVER_ERROR -> {
                    isSuccess = false;
                    message = "Server error occurred. Please try again later.";
                }
                default -> {
                    isSuccess = false;
                    message = "Unexpected error has appeared! Please try again later.";
                }
            }
            return new ApiResult(isSuccess, message, response.body());

        } catch (IOException | InterruptedException e) {
            return new ApiResult(false, "Couldn't maintain the connection." + e.getMessage(), null);
        }
    }

    //delete user by id -> baseUrl/{id}
    public ApiResult deleteUserById(UUID id) {
        try {
            String url = baseUrl + "/" + id;

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .DELETE()
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            HttpStatusCategory codeCategory = HttpStatusCategory.from(statusCode);

            boolean isSuccess;
            String message;

            switch (codeCategory) {
                case SUCCESS -> {
                    isSuccess = true;
                    message = "You have successfully deleted your account!";
                }
                case CLIENT_ERROR -> {
                    isSuccess = false;
                    if (response.body() != null && !response.body().isEmpty())
                        message = response.body();
                    else
                        message = "Couldn't perform delete because the user doesn't exist.";
                }
                case SERVER_ERROR -> {
                    isSuccess = false;
                    message = "Server error occurred. Please try again later.";
                }
                default -> {
                    isSuccess = false;
                    message = "Unexpected error has appeared! Please try again later.";
                }
            }
            return new ApiResult(isSuccess, message, response.body());

        } catch (IOException | InterruptedException e) {
            return new ApiResult(false, "Couldn't maintain the connection." + e.getMessage(), null);
        }
    }

    //update user by id -> baseUrl/{id}
    public ApiResult updateUser(UUID id, UserUpdateRequestDto userUpdateRequestDto) {
        try {
            String url = baseUrl + "/" + id;
            String reqBody = objectMapper.writeValueAsString(userUpdateRequestDto);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(reqBody))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            HttpStatusCategory codeCategory = HttpStatusCategory.from(statusCode);

            boolean isSuccess;
            String message;

            switch (codeCategory) {
                case SUCCESS -> {
                    isSuccess = true;
                    message = "You have successfully updated your account!";
                }
                case CLIENT_ERROR -> {
                    isSuccess = false;
                    if (response.body() != null && !response.body().isEmpty())
                        message = response.body();
                    else
                        message = "Invalid user data for update!";
                }
                case SERVER_ERROR -> {
                    isSuccess = false;
                    message = "Server error occurred. Please try again later.";
                }
                default -> {
                    isSuccess = false;
                    message = "Unexpected error has appeared! Please try again later.";
                }
            }
            return new ApiResult(isSuccess, message, response.body());

        } catch (IOException | InterruptedException e) {
            return new ApiResult(false, "Couldn't maintain the connection." + e.getMessage(), null);
        }
    }

    public UUID extractUserId(ApiResult apiResult) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules(); // pentru UUID, date etc.

            JsonNode root = mapper.readTree(apiResult.getResponseBody());
            if (root.has("id")) {
                return UUID.fromString(root.get("id").asText());
            } else {
                System.out.println("[ERROR]: Response does not contain user ID.");
            }
        } catch (Exception e) {
            System.out.println("[ERROR]: Failed to parse user ID: " + e.getMessage());
        }
        return null;
    }
}