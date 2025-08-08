package ro.neforii.dto.common;

import lombok.*;

@Getter
@RequiredArgsConstructor
public class ExpectedResponse<T> {
    private final boolean success = true;
    private final T data;
}
