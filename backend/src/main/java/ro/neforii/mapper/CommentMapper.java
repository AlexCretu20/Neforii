package ro.neforii.mapper;

import org.springframework.stereotype.Component;
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

        return new CommentResponseDto(
                comment.getId(),
                comment.getText(),
                comment.getUser().getUsername(),
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                comment.getPostId(),
                comment.getParentCommentId(),
                upVotes,
                downVotes
        );
    }
}
