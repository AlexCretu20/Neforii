package repository;

import model.Comment;
import model.EntityType;
import model.User;
import utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommentRepository implements ICrudRepository<Comment> {

    private final ICrudRepository<User> userRepository;

    public CommentRepository(ICrudRepository<User> userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void save(Comment comment) {
        String sql = "INSERT INTO comments (text, created_at, updated_at, user_id, entity_type, entity_id) " +
                "VALUES (?, ?, ?, ?, ?::entity_type, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, comment.getText());
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setTimestamp(3, null); // no update yet
            stmt.setInt(4, comment.getUser().getId());
            stmt.setString(5, comment.getEntityType().getDbValue());
            stmt.setInt(6, comment.getEntityId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Comment> findById(int id) {
        String sql = "SELECT * FROM comments WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("user_id");
                Optional<User> userOpt = userRepository.findById(userId);

                if (userOpt.isEmpty()) return Optional.empty();

                Comment comment = new Comment(
                        rs.getString("text"),
                        userOpt.get(),
                        EntityType.fromDbValue(rs.getString("entity_type")),
                        rs.getInt("entity_id")
                );

                comment.setId(rs.getInt("id"));
                comment.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

                Timestamp updatedAt = rs.getTimestamp("updated_at");
                if (updatedAt != null) {
                    comment.setUpdatedAt(updatedAt.toLocalDateTime());
                }

                return Optional.of(comment);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public List<Comment> findAll() {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT * FROM comments";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int userId = rs.getInt("user_id");
                Optional<User> userOpt = userRepository.findById(userId);

                if (userOpt.isEmpty()) continue;

                Comment comment = new Comment(
                        rs.getString("text"),
                        userOpt.get(),
                        EntityType.fromDbValue(rs.getString("entity_type")),
                        rs.getInt("entity_id")
                );

                comment.setId(rs.getInt("id"));
                comment.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

                Timestamp updatedAt = rs.getTimestamp("updated_at");
                if (updatedAt != null) {
                    comment.setUpdatedAt(updatedAt.toLocalDateTime());
                }

                comments.add(comment);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return comments;
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM comments WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
