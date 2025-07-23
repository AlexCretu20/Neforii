package ro.neforii.model;

import lombok.*;
import ro.neforii.exception.VoteNotOneTargetOnly;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Vote {
    private int id;
    private boolean isUpvote;
    private LocalDateTime createdAt;

    private Integer postId;
    private Integer commentId;

    private int userId;

    public Vote(boolean isUpvote, LocalDateTime createdAt, Integer postId, Integer commentId, int userId) {
        this.isUpvote = isUpvote;
        this.createdAt = createdAt;
        if ((postId == null && commentId == null) || (postId != null && commentId != null)) {
            throw new VoteNotOneTargetOnly("Votes must target either postId or commentId.");
        }
        this.postId = postId;
        this.commentId = commentId;
        this.userId = userId;
    }

    public boolean isUpvote() {
        return isUpvote;
    }
}
