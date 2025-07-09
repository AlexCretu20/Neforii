package repository;

import model.Post;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PostRepository implements ICrudRepository<Post>{
    @Override
    public void save(Post post) {
        String sql = "INSERT INTO posts (text, created_at, is_awarded, user_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, post.getText());
            stmt.setTimestamp(2, Timestamp.valueOf(post.getCreatedAt()));
            stmt.setBoolean(3, post.isAwarded());
            stmt.setInt(4, post.getUser().getId());

            stmt.executeUpdate();

        } catch (Exception e) {
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
//                post.setUser(rs.getInt("user_id"));;
                return Optional.of(post);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return Optional.empty();

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
//                post.setUser(rs.getInt("user_id"));

                posts.add(post);
            }

        } catch (Exception e) {
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
            e.printStackTrace();
        }
    }
}
