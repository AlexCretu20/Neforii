package ro.neforii.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import ro.neforii.dto.filter.FilterDto;
import ro.neforii.dto.filter.FilterResponseDto;
import ro.neforii.exception.FilterException;

import javax.annotation.processing.FilerException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
public class FilterService {

    private static final String url = "http://3.121.100.69/api/filters";
    private final ObjectMapper objectMapper;

    public FilterService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<FilterDto> getFilters() {

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new FilerException("Api status code: " + response.statusCode());
            }

            FilterResponseDto filterResponseDto = objectMapper.readValue(response.body(), FilterResponseDto.class);

            return filterResponseDto.getData();

        } catch (Exception e) {
            throw new FilterException("Error uploading the filters:" + e.getMessage());
        }
    }
}
