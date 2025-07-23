package ro.neforii.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.neforii.dto.comment.CommentRequestDto;
import ro.neforii.dto.comment.CommentResponseDto;
import ro.neforii.mapper.CommentMapper;
import ro.neforii.model.Comment;
import ro.neforii.model.User;
import ro.neforii.service.CommentService;
import ro.neforii.service.UserService;

@RestController
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;
    private final UserService userService;
    private final CommentMapper commentMapper;

    public CommentController(CommentService commentService, UserService userService,  CommentMapper commentMapper) {
        this.commentService = commentService;
        this.userService = userService;
        this.commentMapper = commentMapper;
    }

    @PostMapping("/post/{postId}")
    public ResponseEntity<CommentResponseDto> createCommentOnPost(@PathVariable int postId, @RequestBody CommentRequestDto request){
        User user = userService.findById(request.userId());
        Comment comment = commentService.createCommentOnPost(request.text(),user, postId);
        CommentResponseDto response = commentMapper.toCommentDto(comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/comment/{commentId}")
    public ResponseEntity<CommentResponseDto> createCommentOnComment(@PathVariable int commentId, @RequestBody CommentRequestDto request){
        User user  = userService.findById(request.userId());
        Comment comment = commentService.createReplyToComment(request.text(), user, commentId);
        CommentResponseDto response = commentMapper.toCommentDto(comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
