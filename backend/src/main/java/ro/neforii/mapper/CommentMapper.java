package ro.neforii.mapper;

import org.springframework.stereotype.Component;
import ro.neforii.dto.comment.CommentResponseDto;
import ro.neforii.dto.comment.vote.CommentVoteResponseDto;
import ro.neforii.model.Comment;
import ro.neforii.model.User;
import ro.neforii.model.Vote;
import ro.neforii.service.CommentService;
import ro.neforii.service.VoteService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class CommentMapper {

    public CommentResponseDto commentToDto(Comment comment, UUID currentUserId) {
        List<Vote> votes = comment.getVotes() != null ? comment.getVotes() : List.of();

        int upvotes = (int) votes.stream().filter(Vote::isUpvote).count();
        int downvotes = votes.size() - upvotes;
        int score = upvotes - downvotes;

        String userVote = votes.stream()
                .filter(v -> v.getUser().getId().equals(currentUserId))
                .findFirst()
                .map(v -> v.isUpvote() ? "up" : "down")
                .orElse(null);

        return new CommentResponseDto(
                comment.getId(),
                comment.getPost() != null ? comment.getPost().getId() : null,
                comment.getParentComment() != null ? comment.getParentComment().getId() : null,
                comment.getContent(),
                comment.getUser().getUsername(),
                upvotes,
                downvotes,
                score,
                userVote,
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                new ArrayList<>()
        );
    }

    public CommentVoteResponseDto commentVoteResponseDto (Comment comment, UUID currentUserId) {
        List<Vote> votes = comment.getVotes() != null ? comment.getVotes() : List.of();

        int upvotes = (int) votes.stream().filter(Vote::isUpvote).count();
        int downvotes = votes.size() - upvotes;
        int score = upvotes - downvotes;

        String userVote = votes.stream()
                .filter(v -> v.getUser().getId().equals(currentUserId))
                .findFirst()
                .map(v -> v.isUpvote() ? "up" : "down")
                .orElse(null);

        return new  CommentVoteResponseDto (
                upvotes,
                downvotes,
                score,
                userVote
        );
    }

}



