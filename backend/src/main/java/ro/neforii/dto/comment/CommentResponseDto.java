package ro.neforii.dto.comment;

import java.time.LocalDateTime;

public record CommentResponseDto(int id, String text, String username, LocalDateTime createdAt, LocalDateTime updatedAt,
                                 Integer postId, Integer parentCommentId, int upVotes, int downVotes) {
}
