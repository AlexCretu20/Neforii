package ro.neforii.mapper;

import org.springframework.stereotype.Component;
import ro.neforii.dto.comment.CommentResponseDto;
import ro.neforii.model.Comment;
import ro.neforii.service.CommentService;

@Component
public class CommentMapper {
    private final CommentService commentService;

    public CommentMapper(CommentService commentService) {
        this.commentService = commentService;
    }

    public CommentResponseDto toCommentDto(Comment comment) {
        if (comment == null) {
            return null;
        }

        int upVotes = commentService.displayUpvotes(comment.getId());
        int downVotes = commentService.displayDownvotes(comment.getId());
        int score = upVotes - downVotes;

        return new CommentResponseDto(
                comment.getId(),
                comment.getPost() != null ? comment.getPost().getId() : null,
                comment.getParentComment() != null ? comment.getParentComment().getId() : null,
                comment.getContent(),
                comment.getUser().getUsername(),
                upVotes,
                downVotes,
                score,
                null,
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                comment.getReplies().stream().map(this::toCommentDto).toList()
        );
    }
}
