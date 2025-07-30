package ro.neforii.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity // marcam clasa pentru JPA pentru a gestiona/crea tabela
@Table(name = "comments")

@Getter
@Setter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String content;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    //un comment poate apartine unui singur user, dar un user poate avea mai multe comentarii
    //optional = false-> un comment trebuie sa aiba un user asociat
    //FetchType.LAZY insemana Lazy Loading -> datele nu sunt incarcate in memorie pana cand nu sunt accesate explicit gen post.getUser()
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id") // denumirea coloanei in tabela
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;

    @OneToMany(
            mappedBy = "parentComment",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Comment> replies = new ArrayList<>();

    @OneToMany(
            mappedBy = "comment",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Vote> votes = new ArrayList<>();

//    public Comment(String text, User user, Integer postId, Integer parentCommentId) {
//        this.text = text;
//        this.user = user;
//        this.postId = postId;
//        this.parentCommentId = parentCommentId;
//    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("───────────────────────────────────\n")
                .append("Comment ID : ").append(id).append("\n")
                .append("Text : ").append(content).append("\n")
                .append("Created At : ").append(createdAt).append("\n")
                .append("Updated At : ").append(updatedAt).append("\n")
                .append("By User : ").append(user.getUsername()).append("\n");
        if (post != null) {
            sb.append("On Post ID       : ").append(post.getId()).append("\n");
        } else if (parentComment != null) {
            sb.append("Reply to Comment : ").append(parentComment.getId()).append("\n");
        }
        if (!replies.isEmpty()) {
            sb.append("Replies IDs : ");
            for (Comment r : replies) {
                sb.append(r.getId()).append(" ");
            }
            sb.append("\n");
        }
        sb.append("───────────────────────────────────");
        return sb.toString();
    }
}
