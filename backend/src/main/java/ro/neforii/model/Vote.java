package ro.neforii.model;

import jakarta.persistence.*;
import lombok.*;
import ro.neforii.exception.VoteNotOneTargetOnly;

import java.time.LocalDateTime;

@Entity
@Table(name = "votes")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "is_up_vote")
    private boolean isUpvote;

    @Column(name = "created_at",nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "post_id", referencedColumnName = "id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "comment_id" , referencedColumnName = "id")
    private Comment comment;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    public Vote(boolean isUpvote, Post post, Comment comment, User user) {
        if ((post == null && comment == null) || (post != null && comment != null)) {
            throw new VoteNotOneTargetOnly("Votes must target either postId or commentId.");
        }
        this.isUpvote = isUpvote;
        this.post = post;
        this.comment = comment;
        this.user = user;
    }

    @PrePersist
    public void prePersist(){
        this.createdAt = LocalDateTime.now();
    }

    public boolean isUpvote() {
        return isUpvote;
    }
}
