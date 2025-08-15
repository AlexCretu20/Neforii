package ro.neforii.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.neforii.dto.common.ExpectedResponse;
import ro.neforii.dto.filter.FilterDto;
import ro.neforii.service.FilterService;

import java.io.IOException;
import java.util.List;

@RestController
public class FilterController {
    private final FilterService filterService;
    private final ObjectMapper objectMapper;

    public FilterController(FilterService filterService, ObjectMapper objectMapper) {
        this.filterService = filterService;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    @RequestMapping("/filters")
    public ResponseEntity<ExpectedResponse<List<FilterDto>>> getFilters() throws IOException, InterruptedException {
        String jsonResponse = filterService.getFilters();

        JsonNode root = objectMapper.readTree(jsonResponse);

        // verifica json nu e gol
        JsonNode innerData = root.path("data");

        List<FilterDto> filters = objectMapper.readValue(
                innerData.toString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, FilterDto.class)
        );

        return ResponseEntity.ok(new ExpectedResponse<>(filters));

    }
}

