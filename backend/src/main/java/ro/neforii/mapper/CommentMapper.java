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

//    public CommentResponseDto toDto(Comment comment) {
//        return new CommentResponseDto(
//                comment.getId(),
//                comment.getPost() != null ? comment.getPost().getId() : null,
//                comment.getParentComment() != null ? comment.getParentComment().getId() : null,
//                comment.getContent(),
//                comment.getUser().getUsername(),
//                0,
//                0,
//                0,
//
//        );
//    }



}
