package model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Vote {
    private int id;
    private boolean isUpvote;
    private LocalDateTime createdAt;

    private EntityType entityType;
    private int entityId;

    private int userId;

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
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Vote vote = (Vote) o;
        return id == vote.id && isUpvote == vote.isUpvote && entityId == vote.entityId && userId == vote.userId && Objects.equals(createdAt, vote.createdAt) && entityType == vote.entityType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isUpvote, createdAt, entityType, entityId, userId);
    }

    @Override
    public String toString() {
        return "Vote{" +
                "id=" + id +
                ", isUpvote=" + isUpvote +
                ", createdAt=" + createdAt +
                ", entityType=" + entityType +
                ", entityId=" + entityId +
                ", userId=" + userId +
                '}';
    }

    public Vote(int id, boolean isUpvote, LocalDateTime createdAt, EntityType entityType, int entityId, int userId) {
        this.id = id;
        this.isUpvote = isUpvote;
        this.createdAt = createdAt;
        this.entityType = entityType;
        this.entityId = entityId;
        this.userId = userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

}
