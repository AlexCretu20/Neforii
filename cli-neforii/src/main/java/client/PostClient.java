package client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.ApiResult;
import models.post.PostRequestDto;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PostClient {
    private final String baseUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public PostClient(String baseUrl, HttpClient httpClient, ObjectMapper objectMapper) {
        this.baseUrl = baseUrl;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    public PostClient(String baseUrl) {
        this(baseUrl, HttpClient.newHttpClient(), new ObjectMapper());
    }


    public ApiResult newPost(PostRequestDto postRequestDto) {
        try {
            String url = baseUrl + "/posts";
            String requestBody = objectMapper.writeValueAsString(postRequestDto);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();

            boolean isSuccess;
            String message = "";
            if (statusCode >= 200 && statusCode < 300) {
                isSuccess = true;
                message = "The post was made. ";
            } else if (statusCode >= 400 && statusCode < 500) {
                isSuccess = false;
                if (response.body() != null && !response.body().isEmpty()) {
                    message = response.body();
                }
            } else {
                isSuccess = false;
                message = "Unexpected error has appeared! Please try again later.";
            }

            return new ApiResult(isSuccess, message, response.body());

        } catch (JsonProcessingException e) {
            return new ApiResult(false, "Couldn't map the Post request to JSON.", null);
        } catch (IOException | InterruptedException e) {
            return new ApiResult(false, "Couldn't maintain the connection." + e.getMessage(), null);
        }
    }

    public ApiResult updatePost(Integer id, PostRequestDto postRequestDto) {
        try {
            String url = baseUrl + "/posts/" + id;
            String requestBody = objectMapper.writeValueAsString(postRequestDto);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();

            boolean isSuccess;
            String message = "";
            if (statusCode >= 200 && statusCode < 300) {
                isSuccess = true;
                message = "The post was updated.";
            } else if (statusCode >= 400 && statusCode < 500) {
                isSuccess = false;
                if (response.body() != null && !response.body().isEmpty()) {
                    message = response.body();
                }
            } else {
                isSuccess = false;
                message = "Unexpected error has appeared! Please try again later.";
            }

            return new ApiResult(isSuccess, message, response.body());

        } catch (JsonProcessingException e) {
            return new ApiResult(false, "Couldn't map the Post request to JSON.", null);
        } catch (IOException | InterruptedException e) {
            return new ApiResult(false, "Couldn't maintain the connection." + e.getMessage(), null);
        }
    }

    public ApiResult deletePost(Integer id) {
        try {
            String url = baseUrl + "/posts/" + id;

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .DELETE().build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();

            boolean isSuccess;
            String message = "";
            if (statusCode == 204) {
                isSuccess = true;
                message = "The post was deleted.";
            } else if (statusCode >= 400 && statusCode < 500) {
                isSuccess = false;
                message = (response.body() != null && !response.body().isEmpty())
                        ? response.body()
                        : "Client-side error occurred while deleting the post.";
            } else {
                isSuccess = false;
                message = "Unexpected error has appeared! Please try again later.";
            }

            return new ApiResult(isSuccess, message, response.body());

        } catch (IOException | InterruptedException e) {
            return new ApiResult(false, "Couldn't maintain the connection." + e.getMessage(), null);
        }
    }

    public ApiResult getAllPosts() {
        try {
            String url = baseUrl + "/posts";

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();

            boolean isSuccess;
            String message = "";

            if (statusCode == 200) {
                isSuccess = true;
                message = "The posts were found. ";
            } else if (statusCode == 404) {
                isSuccess = false;
                message = "No post found.";

            } else if (statusCode >= 400 && statusCode < 500) {
                isSuccess = false;
                message = (response.body() != null && !response.body().isEmpty())
                        ? response.body()
                        : "Client-side error occurred.";
            } else {
                isSuccess = false;
                message = "Unexpected error has appeared! Please try again later.";
            }

            return new ApiResult(isSuccess, message, response.body());

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public ApiResult getPostById(Integer id) {
        try {
            String url = baseUrl + "/posts/" + id;

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET().build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();

            boolean isSuccess;
            String message = "";
            if (statusCode == 200) {
                isSuccess = true;
                message = "The post was found.";
            } else if (statusCode == 404) {
                isSuccess = false;
                message = "Post not found.";

            } else if (statusCode >= 400 && statusCode < 500) {
                isSuccess = false;
                message = (response.body() != null && !response.body().isEmpty())
                        ? response.body()
                        : "Client-side error occurred.";
            } else {
                isSuccess = false;
                message = "Unexpected error has appeared! Please try again later.";
            }

            return new ApiResult(isSuccess, message, response.body());

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}

