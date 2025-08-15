package ro.neforii.dto.common;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@Getter
@RequiredArgsConstructor
@JsonPropertyOrder({"success", "data"})
public class ExpectedResponse<T> {
    private final boolean success = true;
    private final T data;
}
