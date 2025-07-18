package model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Comment {
    private int id;
    private String text;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private User user;

    //in the logic layer, we check by null if it belongs to post or comment
    private Integer postId;
    private Integer parentCommentId;

    public Comment(int id, String text, LocalDateTime createdAt, LocalDateTime updatedAt, User user, Integer postId, Integer parentCommentId) {
        this.id = id;
        this.text = text;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.user = user;
        this.postId = postId;
        this.parentCommentId = parentCommentId;
    }

    public Comment(String text, User user, Integer postId, Integer parentCommentId) {
        this.text = text;
        this.user = user;
        this.postId = postId;
        this.parentCommentId = parentCommentId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public Integer getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(Integer parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Comment comment)) return false;
        return id == comment.id && Objects.equals(text, comment.text) && Objects.equals(createdAt, comment.createdAt) && Objects.equals(updatedAt, comment.updatedAt) && Objects.equals(user, comment.user) && Objects.equals(postId, comment.postId) && Objects.equals(parentCommentId, comment.parentCommentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, createdAt, updatedAt, user, postId, parentCommentId);
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", user=" + user +
                ", postId=" + postId +
                ", parentCommentId=" + parentCommentId +
                '}';
    }
}
