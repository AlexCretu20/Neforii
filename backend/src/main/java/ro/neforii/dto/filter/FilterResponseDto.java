package ro.neforii.dto.filter;

import lombok.Data;

import java.util.List;

@Data
public class FilterResponseDto {
    private List<FilterDto> data;
}
