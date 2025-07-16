package model;

import exception.VoteNotOneTargetOnly;

import java.time.LocalDateTime;
import java.util.Objects;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isUpvote() {
        return isUpvote;
    }

    public void setUpvote(boolean upvote) {
        isUpvote = upvote;
    }

    @Override
    public String toString() {
        return "Vote{" +
                "id=" + id +
                ", isUpvote=" + isUpvote +
                ", createdAt=" + createdAt +
                ", postId=" + postId +
                ", commentId=" + commentId +
                ", userId=" + userId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Vote vote = (Vote) o;
        return id == vote.id && isUpvote == vote.isUpvote && userId == vote.userId && Objects.equals(createdAt, vote.createdAt) && Objects.equals(postId, vote.postId) && Objects.equals(commentId, vote.commentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isUpvote, createdAt, postId, commentId, userId);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
