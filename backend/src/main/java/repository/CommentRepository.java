package repository;

import model.Comment;
import model.EntityType;
import model.User;
import utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommentRepository implements ICrudRepository<Comment> {

    private final UserRepository userRepository = new UserRepository();

    @Override
    public void save(Comment comment) {
        String sql = "INSERT INTO comments (text, created_at, updated_at, user_id, entity_type, entity_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, comment.getText());
            preparedStatement.setTimestamp(2, java.sql.Timestamp.valueOf(comment.getCreatedAt() != null ? comment.getCreatedAt() : java.time.LocalDateTime.now()));
            preparedStatement.setTimestamp(3, java.sql.Timestamp.valueOf(comment.getUpdatedAt() != null ? comment.getUpdatedAt() : java.time.LocalDateTime.now()));
            preparedStatement.setInt(4, comment.getUser().getId());
            preparedStatement.setString(5, comment.getEntityType().name());
            preparedStatement.setInt(6, comment.getEntityId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Comment> findById(int id) {
        String sql = "SELECT * FROM comments WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int userId = resultSet.getInt("user_id");
                Optional<User> userOptional = userRepository.findById(userId);
                if (userOptional.isEmpty()) {
                    return Optional.empty();
                }

                Comment comment = new Comment(
                        resultSet.getInt("id"),
                        resultSet.getString("text"),
                        resultSet.getTimestamp("created_at").toLocalDateTime(),
                        resultSet.getTimestamp("updated_at") != null ? resultSet.getTimestamp("updated_at").toLocalDateTime() : null,
                        userOptional.get(),
                        EntityType.valueOf(resultSet.getString("entity_type")),
                        resultSet.getInt("entity_id"),
                        List.of(),
                        List.of()
                );

                return Optional.of(comment);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public List<Comment> findAll() {
        String sql = "SELECT * FROM comments";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            List<Comment> comments = new ArrayList<>();

            while (resultSet.next()) {
                int userId = resultSet.getInt("user_id");
                Optional<User> userOptional = userRepository.findById(userId);
                if (userOptional.isEmpty()) {
                    continue;
                }

                Comment comment = new Comment(
                        resultSet.getInt("id"),
                        resultSet.getString("text"),
                        resultSet.getTimestamp("created_at").toLocalDateTime(),
                        resultSet.getTimestamp("updated_at") != null ? resultSet.getTimestamp("updated_at").toLocalDateTime() : null,
                        userOptional.get(),
                        EntityType.valueOf(resultSet.getString("entity_type")),
                        resultSet.getInt("entity_id"),
                        List.of(),
                        List.of()
                );

                comments.add(comment);
            }

            return comments;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return List.of();
    }

    @Override
    public void deleteById(int id) {
        Optional<Comment> commentOptional = findById(id);
        if (commentOptional.isEmpty()) {
            System.out.println("The entered comment does not exist in order to be deleted!\n");
        } else {
            String sql = "DELETE FROM comments WHERE id = ?";
            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

                preparedStatement.setInt(1, id);
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("The comment has been deleted successfully\n");
                } else {
                    System.out.println("Could not delete comment\n");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
