package ro.neforii.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Post {
    private int id;
    private String text;
    private LocalDateTime createdAt;
    private boolean isAwarded; // defualt false
    private User user;
    private List<Comment> comments = new ArrayList<>();
    private List<Vote> votes = new ArrayList<>();

    public Post(String text, LocalDateTime createdAt, boolean isAwarded, User user) {
        this.text = text;
        this.createdAt = createdAt;
        this.isAwarded = isAwarded;
        this.user = user;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("────────────────────────────\n");
        sb.append("ID: ").append(id);
        if (isAwarded) {
            sb.append("✬");
        }
        sb.append("\n");
        sb.append("Text: ").append(text).append("\n");
        sb.append("Created at: ").append(createdAt).append("\n");
        sb.append("Created by: ").append(user.getUsername()).append("\n");
        sb.append("────────────────────────────");
        return sb.toString();
    }
}
