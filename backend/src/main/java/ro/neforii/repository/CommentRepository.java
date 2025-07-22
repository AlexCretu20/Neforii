package ro.neforii.repository;

import ro.neforii.exception.RepositoryCRUDException;
import ro.neforii.model.Comment;
import ro.neforii.model.User;
import ro.neforii.utils.DatabaseConnection;

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
        String sql = "INSERT INTO comments (text, created_at, updated_at, user_id, post_id, parent_comment_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, comment.getText());
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setNull(3, Types.TIMESTAMP); // no update yet
            stmt.setInt(4, comment.getUser().getId());

            if (comment.getPostId() != null) {
                stmt.setInt(5, comment.getPostId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }

            if (comment.getParentCommentId() != null) {
                stmt.setInt(6, comment.getParentCommentId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryCRUDException("Failed to save comment.");
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

                if (userOpt.isEmpty())
                    return Optional.empty();

                String text = rs.getString("text");
                LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                Timestamp updatedAtTimestamp = rs.getTimestamp("updated_at");
                LocalDateTime updatedAt = updatedAtTimestamp != null ? updatedAtTimestamp.toLocalDateTime() : null;
                Integer postId = rs.getObject("post_id", Integer.class);
                Integer parentCommentId = rs.getObject("parent_comment_id", Integer.class);

                Comment comment = new Comment(
                        text, userOpt.get(), postId, parentCommentId
                );
                comment.setCreatedAt(createdAt);
                comment.setUpdatedAt(updatedAt);
                comment.setId(id);

                return Optional.of(comment);
            }

        } catch (SQLException e) {
            throw new RepositoryCRUDException("Failed to find comment with id=" + id);
        }

        return Optional.empty();
    }

    @Override
    public void update(Comment comment) {
        int commentId = comment.getId();
        LocalDateTime updateTime = LocalDateTime.now();
        comment.setUpdatedAt(updateTime);

        String sql = "UPDATE comments SET text = ?, updated_at = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, comment.getText());
            stmt.setTimestamp(2, Timestamp.valueOf(comment.getUpdatedAt()));
            stmt.setInt(3, commentId);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryCRUDException("Failed to update comment with id=" + commentId);
        }
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

                if (userOpt.isEmpty())
//                    user not found so skip
                    continue;

                int id = rs.getInt("id");
                String text = rs.getString("text");
                LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                Timestamp updatedAtTimestamp = rs.getTimestamp("updated_at");
                LocalDateTime updatedAt = (updatedAtTimestamp != null) ? updatedAtTimestamp.toLocalDateTime() : null;
                Integer postId = rs.getObject("post_id", Integer.class); // int to integer
                Integer parentCommentId = rs.getObject("parent_comment_id", Integer.class);

                Comment comment = new Comment(
                        id, text, createdAt, updatedAt, userOpt.get(), postId, parentCommentId
                );

                comments.add(comment);
            }

        } catch (SQLException e) {
            throw new RepositoryCRUDException("Failed to load all comments.");
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
            throw new RepositoryCRUDException("Could not delete comment with id=" + id);
        }
    }


    public List<Comment> findByPostId(int postId) {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT * FROM comments WHERE post_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, postId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int commentId = rs.getInt("id");
                int userId = rs.getInt("user_id");
                Optional<User> userOpt = userRepository.findById(userId);
                if (userOpt.isEmpty()) continue;

                String text = rs.getString("text");
                LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                Timestamp updatedAtTimestamp = rs.getTimestamp("updated_at");
                LocalDateTime updatedAt = (updatedAtTimestamp != null) ? updatedAtTimestamp.toLocalDateTime() : null;
                Integer parentCommentId = rs.getObject("parent_comment_id", Integer.class);


                if (parentCommentId == null) {
                    Comment comment = new Comment(commentId, text, createdAt, updatedAt, userOpt.get(), postId, parentCommentId);
                    comments.add(comment);
                }
            }

        } catch (SQLException e) {
            throw new RepositoryCRUDException("Failed to load comments for post with id=" + postId);
        }

        return comments;
    }

    public List<Comment> findByCommentId(int commentId) {
        List<Comment> replies = new ArrayList<>();
        String sql = "SELECT * FROM comments WHERE parent_comment_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, commentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    int userId = rs.getInt("user_id");
                    Optional<User> userOpt = userRepository.findById(userId);
                    if (userOpt.isEmpty()) continue;

                    String text = rs.getString("text");
                    LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                    Timestamp updatedAtTimestamp = rs.getTimestamp("updated_at");
                    LocalDateTime updatedAt = (updatedAtTimestamp != null) ? updatedAtTimestamp.toLocalDateTime() : null;
                    Integer postId = rs.getObject("post_id", Integer.class);

                    if (postId == null) {
                        Comment reply = new Comment(id, text, createdAt, updatedAt, userOpt.get(), postId, null);
                        replies.add(reply);
                    }
                }
            }
            return replies;
        } catch (SQLException e) {
            throw new RepositoryCRUDException("Failed to load replies for comment with id=" + commentId);
        }
    }

}
