package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Post {
    private int id; // 0 by default
    private String text;
    private LocalDateTime createdAt;
    private boolean isAwarded; // false bydefault
    private User user; // null by default
    private List<Comment> comments;
    private List<Vote> votes;

    public Post() {
        this.text = "";
        this.createdAt = LocalDateTime.now();
        this.comments = new ArrayList<>();
        this.votes = new ArrayList<>();
    }

    public Post(String text, LocalDateTime createdAt, boolean isAwarded, User user) {
        this.text = text;
        this.createdAt = createdAt;
        this.isAwarded = isAwarded;
        this.user = user;
    }

    public Post(int id, String text, LocalDateTime createdAt, boolean isAwarded, User user) {
        this.id = id;
        this.text = text;
        this.createdAt = createdAt;
        this.isAwarded = isAwarded;
        this.user = user;
        this.comments = new ArrayList<>();
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

    public boolean isAwarded() {
        return isAwarded;
    }

    public void setAwarded(boolean awarded) {
        isAwarded = awarded;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(Comment comment) {
        comments.add(comment);
    }

    public List<Vote> getVotes() {
        return votes;
    }

    public void setVotes(Vote vote) {
        votes.add(vote);
    }

    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Post post = (Post) object;
        return id == post.id && isAwarded == post.isAwarded && Objects.equals(text, post.text) && Objects.equals(createdAt, post.createdAt) && Objects.equals(user, post.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, createdAt, isAwarded, user);
    }

    @Override
    public String toString() {
        return id + "\n" + text + "\n Created at :" + createdAt + " " + "Created by : " + user.getUsername();
    }

}
