package ro.neforii.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.neforii.dto.common.ExpectedResponse;
import ro.neforii.dto.filter.FilterDto;
import ro.neforii.service.FilterService;

import java.util.List;

@RestController
public class FilterController {
    private final FilterService filterService;

    public FilterController(FilterService filterService) {
        this.filterService = filterService;
    }

    @GetMapping
    @RequestMapping("/filters")
    public ResponseEntity<ExpectedResponse<List<FilterDto>>> getFilters() {

        List<FilterDto> filters = filterService.getFilters();
        return ResponseEntity.ok(new ExpectedResponse<>(filters));

    }
}
