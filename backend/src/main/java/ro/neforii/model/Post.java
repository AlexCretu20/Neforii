package ro.neforii.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity // marcam clasa pentru JPA pentru a gestiona/crea tabela
@Table(name = "posts")

@Getter
@Setter
@EqualsAndHashCode
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, length = 255)
    private String content;

    @Column(nullable = false, length = 255)
    private String author;

    @Column(name = "image_path", length = 2048)
    String imagePath;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_awarded")
    private boolean isAwarded; // default false

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id") // id utlizator care a lasat postarea
    private User user;

    @Column(nullable = true, length = 255)
    private String subreddit;

    @OneToMany(
            mappedBy = "post",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(
            mappedBy = "post",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Vote> votes = new ArrayList<>();


    public Post(String title, String content, LocalDateTime createdAt, boolean isAwarded, User user) {
        this.title = title;
        this.content = content;
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
        sb.append("Text: ").append(title).append("\n");
        sb.append("Text: ").append(content).append("\n");
        sb.append("Created at: ").append(createdAt).append("\n");
        sb.append("Created by: ").append(user.getUsername()).append("\n");
        sb.append("────────────────────────────");
        return sb.toString();
    }
}
