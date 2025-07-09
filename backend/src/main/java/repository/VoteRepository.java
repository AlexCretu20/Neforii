package repository;

import model.EntityType;
import model.Vote;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VoteRepository implements ICrudRepository<Vote> {

    @Override
    public void save(Vote vote) {
        String sql = "INSERT INTO votes (is_upvote, created_at, entity_type, entity_id, user_id) " +
                "VALUES (?, ?, ?::entity_type, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, vote.isUpvote());
            stmt.setTimestamp(2, Timestamp.valueOf(vote.getCreatedAt()));
            stmt.setString(3, vote.getEntityType().getDbValue());
            stmt.setInt(4, vote.getEntityId());
            stmt.setInt(5, vote.getUserId());

            stmt.executeUpdate();

        } catch (SQLException e) {
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
                        EntityType.fromDbValue(rs.getString("entity_type")),
                        rs.getInt("entity_id"),
                        rs.getInt("user_id")
                );

                vote.setId(rs.getInt("id"));
                return Optional.of(vote);
            }

        } catch (SQLException e) {
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
                        EntityType.fromDbValue(rs.getString("entity_type")),
                        rs.getInt("entity_id"),
                        rs.getInt("user_id")
                );

                vote.setId(rs.getInt("id"));
                votes.add(vote);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return votes;
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM votes WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
