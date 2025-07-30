package ro.neforii.dto.common;

import java.util.List;

public class SuccesResponse<T> {
    private boolean success;
    private List<T> data;

    public SuccesResponse(boolean success, List<T> data) {
        this.success = success;
        this.data = data;
    }
}
