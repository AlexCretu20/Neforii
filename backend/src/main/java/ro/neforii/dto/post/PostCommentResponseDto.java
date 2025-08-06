package ro.neforii.dto.post;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class CommentResponse {
    private UUID id;
    private UUID postId;
    private UUID parentId;
    private String content;
    private String author;
    private int upvotes;
    private int downvotes;
    private int score;
    private String userVote;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CommentResponse> replies;
}
