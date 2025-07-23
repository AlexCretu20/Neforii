package ro.neforii.repository;

import org.springframework.stereotype.Repository;
import ro.neforii.model.Vote;
import ro.neforii.utils.DatabaseConnection;
import ro.neforii.utils.logger.Logger;
import ro.neforii.utils.logger.LoggerType;

import javax.naming.OperationNotSupportedException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class VoteRepository implements ICrudRepository<Vote> {

    @Override
    public void save(Vote vote) {
        String sql = "INSERT INTO votes (is_upvote, created_at, post_id, comment_id, user_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, vote.isUpvote());
            stmt.setTimestamp(2, Timestamp.valueOf(vote.getCreatedAt()));
            if (vote.getPostId() != null) {
                stmt.setInt(3, vote.getPostId());
            } else {
                stmt.setNull(3, java.sql.Types.INTEGER);
            }

            if (vote.getCommentId() != null) {
                stmt.setInt(4, vote.getCommentId());
            } else {
                stmt.setNull(4, java.sql.Types.INTEGER);
            }
            stmt.setInt(5, vote.getUserId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            Logger.log(LoggerType.FATAL, "VoteRepository save: Vote can't be saved to database.");
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Vote> findById(int id) {
        String sql = "SELECT * FROM votes WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Vote vote = new Vote(
                        rs.getBoolean("is_upvote"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getObject("post_id", Integer.class),
                        rs.getObject("comment_id", Integer.class),
                        rs.getInt("user_id")
                );

                vote.setId(rs.getInt("id"));
                return Optional.of(vote);
            }

        } catch (SQLException e) {
            Logger.log(LoggerType.FATAL, "VoteRepository findById: Vote can't be found in database.");
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public List<Vote> findAll() {
        List<Vote> votes = new ArrayList<>();
        String sql = "SELECT * FROM votes";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Vote vote = new Vote(
                        rs.getBoolean("is_upvote"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getObject("post_id", Integer.class),
                        rs.getObject("comment_id", Integer.class),
                        rs.getInt("user_id")
                );

                vote.setId(rs.getInt("id"));
                votes.add(vote);
            }

        } catch (SQLException e) {
            Logger.log(LoggerType.FATAL, "VoteRepository findAll: Vote can't be found in database.");
            e.printStackTrace();
        }

        return votes;
    }

    @Override
    public void update(Vote entity) throws OperationNotSupportedException {
        throw new OperationNotSupportedException("TO DO");
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM votes WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            Logger.log(LoggerType.FATAL, "VoteRepository deleteById: Vote can't be found in database.");
            e.printStackTrace();
        }
    }

    public Vote findVoteByTargetId(int postId, int commentId, int userId) {
        String sql = "SELECT * FROM votes WHERE (post_id = ? OR comment_id = ?) AND user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, postId);
            stmt.setInt(2, commentId);
            stmt.setInt(3, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Vote vote = new Vote(
                        rs.getBoolean("is_upvote"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getInt("post_id"),
                        rs.getInt("comment_id"),
                        rs.getInt("user_id")
                );
                vote.setId(rs.getInt("id"));
                return vote;
            }

        } catch (SQLException e) {
            Logger.log(LoggerType.FATAL, "VoteRepository findVoteByTargetId: Vote can't be found in database.");
            e.printStackTrace();
        }

        return null;
    }

    public int countVotesByCommentId(int commentId, boolean isUpvote) {
        return countVotesByTargetId(null, commentId, isUpvote);
    }
    public int countVotesByPostId(int postId, boolean isUpvote) {
        return countVotesByTargetId(postId, null, isUpvote);
    }

    private int countVotesByTargetId(Integer postId, Integer commentId, boolean isUpvote) {
        if ((postId  == null && commentId == null) ||
                (postId  != null && commentId != null)) {
            throw new IllegalArgumentException(
                    "Exactly one of postId or commentId must be provided"
            );
        }

        final String sql;
        final int targetId;
        if (postId != null) {
            sql      = "SELECT COUNT(*) FROM votes WHERE post_id = ? AND is_upvote = ?";
            targetId = postId;
        } else {
            sql      = "SELECT COUNT(*) FROM votes WHERE comment_id = ? AND is_upvote = ?";
            targetId = commentId;
        }

        // 3) execute
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, targetId);
            stmt.setBoolean(2, isUpvote);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            Logger.log(LoggerType.FATAL,
                    "VoteRepository countVotesByTargetId: cannot retrieve count.");
            e.printStackTrace();
            return 0;
        }
    }


}
