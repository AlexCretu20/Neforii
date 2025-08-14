package client;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.ApiResult;
import models.post.PostRequestDto;
import models.post.PostUpdateRequestDto;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

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
            String url = baseUrl;
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


public ApiResult updatePost(UUID id, PostUpdateRequestDto postUpdateDto) {
    try {
        String url = baseUrl + "/" + id;
        String requestBody = objectMapper.writeValueAsString(postUpdateDto);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();

        boolean isSuccess;
        String message;
        if (statusCode >= 200 && statusCode < 300) {
            isSuccess = true;
            message = "The post was updated.";
        } else if (statusCode >= 400 && statusCode < 500) {
            isSuccess = false;
            message = (response.body() != null && !response.body().isEmpty())
                    ? response.body()
                    : "Client-side error occurred while updating the post.";
        } else {
            isSuccess = false;
            message = "Unexpected error has appeared! Please try again later.";
        }

        return new ApiResult(isSuccess, message, response.body());

    } catch (JsonProcessingException e) {
        return new ApiResult(false, "Couldn't map the update request to JSON.", null);
    } catch (IOException | InterruptedException e) {
        return new ApiResult(false, "Couldn't maintain the connection: " + e.getMessage(), null);
    }
}

    public ApiResult deletePost(UUID id) {
        try {
            String url = baseUrl + '/' + id;

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
            }else if (statusCode == 200 && response.body().contains("\"success\":true")) {
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
            String url = baseUrl;

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

    public ApiResult getPostById(UUID id) {
        try {
            String url = baseUrl + '/' + id;

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
    public ApiResult getCommentsByPostId(UUID postId) {
        try {
            String url = baseUrl + "/" + postId + "/comments";


            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());


            int statusCode = response.statusCode();

            if (statusCode == 200) {
                return new ApiResult(true, "The comments for this post were found.", response.body());
            } else if (statusCode == 404) {
                return new ApiResult(false, "No comments found for this post.", null);
            } else if (statusCode >= 400 && statusCode < 500) {
                return new ApiResult(false, response.body(), response.body());
            } else {
                return new ApiResult(false, "Unexpected error occurred. Please try again later.", response.body());
            }

        } catch (IOException | InterruptedException e) {
            return new ApiResult(false, "Couldn't maintain the connection: " + e.getMessage(), null);
        }
    }


}

