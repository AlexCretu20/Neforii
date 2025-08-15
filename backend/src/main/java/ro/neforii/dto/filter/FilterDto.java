package ro.neforii.dto.filter;

import lombok.Data;

public record FilterDto(
        int id,
        String name,
        String label

) {
}
