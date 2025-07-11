package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Comment {
    private int id;
    private String text;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private User user;

    //EntityType is either POST or COMMENT to know if its reply or not
    private EntityType entityType;
    private int entityId;

    private List<Comment> replies;
    private List<Vote> votes;

    public Comment(int id, String text, LocalDateTime createdAt, LocalDateTime updatedAt, User user, EntityType entityType, int entityId, List<Comment> replies, List<Vote> votes) {
        this.id = id;
        this.text = text;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.user = user;
        this.entityType = entityType;
        this.entityId = entityId;
        this.replies = replies;
        this.votes = votes;
    }

    public Comment(String text, User user, EntityType entityType, int entityId) {
        this.text = text;
        this.user = user;
        this.entityType = entityType;
        this.entityId = entityId;
        this.replies = new ArrayList<>();
        this.votes = new ArrayList<>();
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

    public EntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public List<Comment> getReplies() {
        return replies;
    }

    public void setReplies(Comment replie) {
        replies.add(replie);
    }

    public List<Vote> getVotes() {
        return votes;
    }

    public void setVotes(Vote vote) {
        votes.add(vote);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Comment comment)) return false;
        return id == comment.id && entityId == comment.entityId && Objects.equals(text, comment.text) && Objects.equals(createdAt, comment.createdAt) && Objects.equals(updatedAt, comment.updatedAt) && Objects.equals(user, comment.user) && entityType == comment.entityType && Objects.equals(replies, comment.replies) && Objects.equals(votes, comment.votes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, createdAt, updatedAt, user, entityType, entityId, replies, votes);
    }

    @Override
    public String toString() {
        return id + "\n" + text + "\n Created at :" + createdAt  +  " " + "Created by : " + user.getUsername();
    }

}
