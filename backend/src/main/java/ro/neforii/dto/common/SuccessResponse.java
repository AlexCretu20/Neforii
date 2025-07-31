package ro.neforii.dto.common;

import lombok.*;

@Getter
@RequiredArgsConstructor
public class SuccessResponse<T> {
    private final boolean success = true;
    private final T data;
}
