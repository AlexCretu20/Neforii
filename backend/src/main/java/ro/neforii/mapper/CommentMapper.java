package ro.neforii.mapper;

import org.springframework.stereotype.Component;
import ro.neforii.dto.comment.CommentResponseDto;
import ro.neforii.model.Comment;
import ro.neforii.model.User;
import ro.neforii.service.CommentService;
import ro.neforii.service.VoteService;

import java.util.List;
import java.util.Optional;

@Component
public class CommentMapper {
    private final CommentService commentService;
    private final VoteService voteService;

    public CommentMapper(CommentService commentService, VoteService voteService) {
        this.commentService = commentService;
        this.voteService = voteService;
    }

    public CommentResponseDto toCommentDto(Comment comment, User currentUser) {
        if (comment == null) {
            return null;
        }

        int upVotes = commentService.displayUpvotes(comment.getId());
        int downVotes = commentService.displayDownvotes(comment.getId());
        int score = upVotes - downVotes;
        String userVote = voteService.getVoteTypeForUser(comment,currentUser);

        List<CommentResponseDto> repliesDto = Optional.ofNullable(comment.getReplies())
                .orElse(List.of()) // dacă e null -> listă goală
                .stream()
                .map(reply -> toCommentDto(reply, currentUser))
                .toList();


        return new CommentResponseDto(
                comment.getId(),
                comment.getPost() != null ? comment.getPost().getId() : null,
                comment.getParentComment() != null ? comment.getParentComment().getId() : null,
                comment.getContent(),
                comment.getUser().getUsername(),
                upVotes,
                downVotes,
                score,
                userVote,
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                repliesDto
        );
    }
}
