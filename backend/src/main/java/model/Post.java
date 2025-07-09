package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Post {
    private int id;
    private String text;
    private LocalDateTime createdAt;
    private boolean isAwarded;
    private User user;
    private ArrayList<Comment> comments;
    private ArrayList<Vote> votes;

    public Post() {
        this.id = 0;
        this.text = "";
        this.createdAt = LocalDateTime.now();
        this.isAwarded = false;
        this.user = null;
        this.comments = new ArrayList<Comment>();
        this.votes = new ArrayList<Vote>();
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
        this.comments = new ArrayList<Comment>();
        this.votes = new ArrayList<Vote>();
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

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(Comment comment) {
        comments.add(comment);
    }

    public ArrayList<Vote> getVotes() {
        return votes;
    }

    public void setVotes(Vote vote) {
        votes.add(vote);
    }

    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        Post post = (Post) object;
        return id == post.id && isAwarded == post.isAwarded && java.util.Objects.equals(text, post.text) && java.util.Objects.equals(createdAt, post.createdAt) && java.util.Objects.equals(user, post.user);
    }

    public int hashCode() {
        return Objects.hash(super.hashCode(), id, text, createdAt, isAwarded, user);
    }

    @java.lang.Override
    public java.lang.String toString() {
        return id + "\n" + text + "\n Created at :" + createdAt  +  "\n";

    }
}
