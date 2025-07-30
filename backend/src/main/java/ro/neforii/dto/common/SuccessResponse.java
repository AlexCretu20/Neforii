package ro.neforii.dto.common;

import lombok.*;

import java.util.List;

@Data
public class SuccessResponse<T> {
    private final boolean success = true;
    private T data;

    public SuccessResponse(T data) {
        this.data = data;
    }
}
