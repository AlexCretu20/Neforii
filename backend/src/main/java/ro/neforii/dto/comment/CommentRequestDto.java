package ro.neforii.dto.comment;

public record CommentRequestDto(String text, int userId, int postId, int parentCommentId) {
}
