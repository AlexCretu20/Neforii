package ro.neforii.dto.comment;

public record ApiResponse<T>(
        boolean succes,
        T data
) {
}
