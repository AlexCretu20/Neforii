package service;

import model.EntityType;
import model.Vote;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VoteService {
    private static VoteService instance;
    private final List<Vote> votes = new ArrayList<>();
    private int counter = 0;


    private VoteService() {
    }

    public static VoteService getInstance() {
        if (instance == null) {
            instance = new VoteService();
        }
        return instance;
    }

    public String createVote(int userId, EntityType entityType, int entityId, boolean isUpvote) {
        votes.removeIf(vote -> vote.getUserId() == userId && vote.getEntityId() == entityId && vote.getEntityType() == entityType);

        Vote vote = new Vote(
                counter++,
                isUpvote,
                LocalDateTime.now(),
                entityType,
                entityId,
                userId
        );
        votes.add(vote);
//        commentService.getCommentById(entityId).getVotes().add(vote);

        if (entityType == EntityType.POST) {
//            postService.getPostById(entityId).setAwarded(true)
        }
        return "You have successfully voted!";
    }



    public String deleteVote(int userId, EntityType entityType, int entityId) {
        boolean removed = votes.removeIf(vote -> vote.getUserId() == userId && vote.getEntityType() == entityType && vote.getEntityId() == entityId);
        if (removed) {
            return "Vote deleted successfully!";
        } else {
            return "No vote found to delete.";
        }
    }

    public boolean hasUserVoted(int userId, EntityType entityType, int entityId) {
        return votes.stream()
                .anyMatch(vote -> vote.getUserId() == userId && vote.getEntityType() == entityType && vote.getEntityId() == entityId);
    }


}
