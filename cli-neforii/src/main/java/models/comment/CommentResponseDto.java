package models.comment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CommentResponseDto(
        UUID id,
        UUID postId,
        UUID parentId,
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
