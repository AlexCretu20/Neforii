package ro.neforii.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    private int id;
    private String text;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    private User user;
    //in the logic layer, we check by null if it belongs to post or comment
    private Integer postId;
    private Integer parentCommentId;

    public Comment(String text, User user, Integer postId, Integer parentCommentId) {
        this.text = text;
        this.user = user;
        this.postId = postId;
        this.parentCommentId = parentCommentId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("───────────────────────────────────\n");
        sb.append(" Comment ID : ").append(id).append("\n");
        sb.append(" Text : ").append(text).append("\n");
        sb.append(" Created At : ").append(createdAt).append("\n");
        sb.append(" Updated At : ").append(updatedAt).append("\n");
        sb.append(" By User : ").append(user.getUsername()).append("\n");
        if (postId != null) {
            sb.append(" Post ID : ").append(postId).append("\n");
        } else if (parentCommentId != null) {
            sb.append(" Parent Comment ID : ").append(parentCommentId).append("\n");
        }
        sb.append("───────────────────────────────────");
        return sb.toString();
    }
}
