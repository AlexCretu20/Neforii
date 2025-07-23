package ro.neforii.dto.post;

public record PostResponseDto(
        Integer id,
        String text,
        Integer userId
) {
}

