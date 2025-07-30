package models.post;

public record PostRequestDto(
        String title,
        String content,
        String imagePath,
        String author,
        Integer userId
) {
}
