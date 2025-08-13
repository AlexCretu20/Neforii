package client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.ApiResult;
import models.comment.CommentRequestDto;
import models.comment.CommentUpdateRequestDto;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

public class CommentClient {
    private final String baseUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public CommentClient(String baseUrl, HttpClient httpClient, ObjectMapper objectMapper) {
        this.baseUrl = baseUrl;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    public CommentClient(String baseUrl) {
        this(baseUrl, HttpClient.newHttpClient(), new ObjectMapper());
    }



public ApiResult updateComment(UUID id, CommentUpdateRequestDto commentUpdateRequestDto) {
    try {
        String url = baseUrl + "/comments/" + id;
        String requestBody = objectMapper.writeValueAsString(commentUpdateRequestDto);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();

        if (statusCode >= 200 && statusCode < 300) {
            return new ApiResult(true, "The comment was updated.", response.body());
        } else if (statusCode >= 400 && statusCode < 500) {
            return new ApiResult(false, response.body(), response.body());
        } else {
            return new ApiResult(false, "Unexpected error occurred. Please try again later.", response.body());
        }

    } catch (JsonProcessingException e) {
        return new ApiResult(false, "Couldn't map the Comment update request to JSON.", null);
    } catch (IOException | InterruptedException e) {
        return new ApiResult(false, "Couldn't maintain the connection: " + e.getMessage(), null);
    }
}


    public ApiResult getAllComments() {
        try {
            String url = baseUrl;

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();

            if (statusCode == 200) {
                return new ApiResult(true, "The comments were found.", response.body());
            } else if (statusCode == 404) {
                return new ApiResult(false, "No comments found.", null);
            } else if (statusCode >= 400 && statusCode < 500) {
                return new ApiResult(false, response.body(), response.body());
            } else {
                return new ApiResult(false, "Unexpected error occurred. Please try again later.", response.body());
            }

        } catch (IOException | InterruptedException e) {
            return new ApiResult(false, "Couldn't maintain the connection: " + e.getMessage(), null);
        }
    }

public ApiResult getCommentById(UUID id) {
    try {
        String url = baseUrl + "/comments/" + id;

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();

        if (statusCode >= 200 && statusCode < 300) {
            JsonNode root = objectMapper.readTree(response.body());
            JsonNode dataNode = root.get("data");

            if (dataNode == null || dataNode.isNull()) {
                return new ApiResult(false, "No comment data found.", null);
            }


            return new ApiResult(true, "Comment retrieved successfully.", dataNode.toString());
        } else if (statusCode == 404) {
            return new ApiResult(false, "Comment not found.", response.body());
        } else {
            return new ApiResult(false, "Unexpected error occurred: " + statusCode, response.body());
        }

    } catch (IOException | InterruptedException e) {
        return new ApiResult(false, "Couldn't maintain the connection: " + e.getMessage(), null);
    }
}

    public ApiResult deleteComment(UUID id) {
        try {
            String url = baseUrl + "/comments/" + id;

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .DELETE()
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();

            if (statusCode >= 200 && statusCode < 300) {
                return new ApiResult(true, "Comment deleted successfully.", response.body());
            } else if (statusCode == 404) {
                return new ApiResult(false, "Comment not found.", response.body());
            } else {
                return new ApiResult(false, "Unexpected error occurred: " + statusCode, response.body());
            }

        } catch (IOException | InterruptedException e) {
            return new ApiResult(false, "Couldn't maintain the connection: " + e.getMessage(), null);
        }
    }

    public ApiResult getCommentsByPostId(UUID postId) {
        try {
            String url = baseUrl + "/posts/" + postId + "/comments";

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

public ApiResult addComment(UUID postId, CommentRequestDto commentRequestDto) {
    try {
        String url = baseUrl + "/posts/" + postId + "/comments";
        String requestBody = objectMapper.writeValueAsString(commentRequestDto);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        int statusCode = response.statusCode();

        if (statusCode >= 200 && statusCode < 300) {
            return new ApiResult(true, "The comment was added.", response.body());
        } else if (statusCode >= 400 && statusCode < 500) {
            return new ApiResult(false, response.body(), response.body());
        } else {
            return new ApiResult(false, "Unexpected error occurred. Please try again later.", response.body());
        }

    } catch (JsonProcessingException e) {
        return new ApiResult(false, "Couldn't map the Comment request to JSON.", null);
    } catch (IOException | InterruptedException e) {
        return new ApiResult(false, "Couldn't maintain the connection: " + e.getMessage(), null);
    }
}


}
