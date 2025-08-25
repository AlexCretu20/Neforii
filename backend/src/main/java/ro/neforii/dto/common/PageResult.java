package ro.neforii.dto.common;

import java.util.List;

public record PageResult<T>(
        List<T> items,
        boolean hasMore,
        int page,
        int limit
) {
}
