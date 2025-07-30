package models.comment;

import java.time.LocalDateTime;
import java.util.List;

public record CommentResponseDto(
        int id,
        Integer postId,
        Integer parentId,
        String content,
        String author,
        int upVotes,
        int downVotes,
        int score,
        String userVote,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<CommentResponseDto> replies
) {}
