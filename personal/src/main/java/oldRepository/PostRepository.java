//package ro.neforii.repository;
//
//import org.springframework.stereotype.Repository;
//import ro.neforii.exception.RepositoryCRUDException;
//import ro.neforii.model.Post;
//import ro.neforii.model.User;
//import ro.neforii.utils.DatabaseConnection;
//import ro.neforii.utils.logger.Logger;
//import ro.neforii.utils.logger.LoggerType;
//
//import java.sql.*;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public class PostRepository implements ICrudRepository<Post>{
//    private final ICrudRepository userRepository;
//
//    public PostRepository(ICrudRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//    @Override
//    public void save(Post post) {
//        String sql = "INSERT INTO posts (text, created_at, is_awarded, user_id) VALUES (?, ?, ?, ?)";
//
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
//
//            stmt.setString(1, post.getText());
//            stmt.setTimestamp(2, Timestamp.valueOf(post.getCreatedAt()));
//            stmt.setBoolean(3, post.isAwarded());
//            System.out.println(post.getUser().getId());
//            System.out.println(post.getUser().getUsername());
//            stmt.setInt(4, post.getUser().getId());
//
//            stmt.executeUpdate();
//
//            try(ResultSet keys = stmt.getGeneratedKeys()){
//                if(keys.next()) {
//                    post.setId(keys.getInt(1));
//                }
//                else {
//                    throw new RepositoryCRUDException("Failed to save post because no id could be generated.");
//                }
//            }catch (SQLException e) {
//                throw new RepositoryCRUDException("Failed to save post because no id could be generated.");
//            }
//
//        } catch (Exception e) {
//            Logger.log(LoggerType.FATAL, "PostRepository save: Post can't be saved to database.");
//            e.printStackTrace();
//        }
//
//    }
//
//    @Override
//    public Optional<Post> findById(int id) {
//        String sql = "SELECT * FROM posts WHERE id = ?";
//
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//
//            stmt.setInt(1, id);
//            ResultSet rs = stmt.executeQuery();
//
//            if (rs.next()) {
//                Post post = new Post();
//                post.setId(rs.getInt("id"));
//                post.setText(rs.getString("text"));
//                post.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
//                post.setAwarded(rs.getBoolean("is_awarded"));
//                int userId = rs.getInt("user_id");
//                Optional<User> user = userRepository.findById(userId);
//                post.setUser(user.get());
//                return Optional.of(post);
//            }
//
//        } catch (Exception e) {
//            Logger.log(LoggerType.FATAL, "PostRepository findById: Post can't be found in database.");
//            e.printStackTrace();
//        }
//
//        return Optional.empty();
//
//    }
//
//    @Override
//    public void update(Post post) {
//        String sql = "UPDATE posts SET text = ?, created_at = ?, is_awarded = ?, user_id = ? WHERE id = ?";
//
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//
//            stmt.setString(1, post.getText());
//            stmt.setTimestamp(2, Timestamp.valueOf(post.getCreatedAt()));
//            stmt.setBoolean(3, post.isAwarded());
//            stmt.setInt(4, post.getUser().getId());
//            stmt.setInt(5, post.getId());
//
//            int affectedRows = stmt.executeUpdate();
//
//            if (affectedRows == 0) {
//                System.out.println("No post found with id " + post.getId() + " to update.");
//            }
//
//        } catch (SQLException e) {
//            Logger.log(LoggerType.FATAL, "PostRepository update: Post can't be found in database.");
//            e.printStackTrace();
//        }
//    }
//
//
//    @Override
//    public List<Post> findAll() {
//        List<Post> posts = new ArrayList<>();
//        String sql = "SELECT * FROM posts";
//
//        try (Connection conn = DatabaseConnection.getConnection();
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(sql)) {
//
//            while (rs.next()) {
//                Post post = new Post();
//                post.setId(rs.getInt("id"));
//                post.setText(rs.getString("text"));
//                post.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
//                post.setAwarded(rs.getBoolean("is_awarded"));
//                int userId = rs.getInt("user_id");
//                Optional<User> user = userRepository.findById(userId);
//                post.setUser(user.get());
//                posts.add(post);
//            }
//
//        } catch (Exception e) {
//            Logger.log(LoggerType.FATAL, "PostRepository findAll: Post can't be found in database.");
//            e.printStackTrace();
//        }
//
//        return posts;
//    }
//
//
//    @Override
//    public void deleteById(int id) {
//        String sql = "DELETE FROM posts WHERE id = ?";
//
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//
//            stmt.setInt(1, id);
//            stmt.executeUpdate();
//
//        } catch (Exception e) {
//            Logger.log(LoggerType.FATAL, "PostRepository deleteById: Post can't be found in database.");
//            e.printStackTrace();
//        }
//    }
//
//    public List<Post> findAllByUserId(int id) {
//        String sql = "SELECT * FROM posts WHERE user_id = ?";
//        List<Post> posts = new ArrayList<>();
//
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//
//            stmt.setInt(1, id);
//            ResultSet rs = stmt.executeQuery();
//
//            while (rs.next()) {
//                Post post = new Post();
//                post.setId(rs.getInt("id"));
//                post.setText(rs.getString("text"));
//                post.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
//                post.setAwarded(rs.getBoolean("is_awarded"));
//
//                int userId = rs.getInt("user_id");
//                Optional<User> user = userRepository.findById(userId);
//                if (user.isPresent()) {
//                    post.setUser(user.get());
//                    posts.add(post);
//                } else {
//                    Logger.log(LoggerType.ERROR, "User not found for post with ID " + post.getId());
//                }
//            }
//
//        } catch (Exception e) {
//            Logger.log(LoggerType.FATAL, "PostRepository findAllByUserId: Error loading posts.");
//            e.printStackTrace();
//        }
//
//        return posts;
//    }
//
//}
