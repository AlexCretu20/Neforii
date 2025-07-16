package repository;

import model.Post;
import model.User;
import utils.DatabaseConnection;
import utils.logger.Logger;
import utils.logger.LoggerType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PostRepository implements ICrudRepository<Post>{
    private final ICrudRepository userRepository;

    public PostRepository(ICrudRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public void save(Post post) {
        String sql = "INSERT INTO posts (text, created_at, is_awarded, user_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, post.getText());
            stmt.setTimestamp(2, Timestamp.valueOf(post.getCreatedAt()));
            stmt.setBoolean(3, post.isAwarded());
            System.out.println(post.getUser().getId());
            System.out.println(post.getUser().getUsername());
            stmt.setInt(4, post.getUser().getId());

            stmt.executeUpdate();

        } catch (Exception e) {
            Logger.log(LoggerType.FATAL, "PostRepository save: Post can't be saved to database.");
            e.printStackTrace();
        }

    }

    @Override
    public Optional<Post> findById(int id) {
        String sql = "SELECT * FROM posts WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Post post = new Post();
                post.setId(rs.getInt("id"));
                post.setText(rs.getString("text"));
                post.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                post.setAwarded(rs.getBoolean("is_awarded"));
                int userId = rs.getInt("user_id");
                Optional<User> user = userRepository.findById(userId);
                post.setUser(user.get());
                return Optional.of(post);
            }

        } catch (Exception e) {
            Logger.log(LoggerType.FATAL, "PostRepository findById: Post can't be found in database.");
            e.printStackTrace();
        }

        return Optional.empty();

    }

    @Override
    public void update(Post post) {
        String sql = "UPDATE posts SET text = ?, created_at = ?, is_awarded = ?, user_id = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, post.getText());
            stmt.setTimestamp(2, Timestamp.valueOf(post.getCreatedAt()));
            stmt.setBoolean(3, post.isAwarded());
            stmt.setInt(4, post.getUser().getId());
            stmt.setInt(5, post.getId());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                System.out.println("No post found with id " + post.getId() + " to update.");
            } else {
                System.out.println("Post updated successfully.");
            }

        } catch (SQLException e) {
            Logger.log(LoggerType.FATAL, "PostRepository update: Post can't be found in database.");
            e.printStackTrace();
        }
    }


    @Override
    public List<Post> findAll() {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT * FROM posts";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Post post = new Post();
                post.setId(rs.getInt("id"));
                post.setText(rs.getString("text"));
                post.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                post.setAwarded(rs.getBoolean("is_awarded"));
                int userId = rs.getInt("user_id");
                Optional<User> user = userRepository.findById(userId);
                post.setUser(user.get());
                posts.add(post);
            }

        } catch (Exception e) {
            Logger.log(LoggerType.FATAL, "PostRepository findAll: Post can't be found in database.");
            e.printStackTrace();
        }

        return posts;
    }


    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM posts WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (Exception e) {
            Logger.log(LoggerType.FATAL, "PostRepository deleteById: Post can't be found in database.");
            e.printStackTrace();
        }
    }
}
