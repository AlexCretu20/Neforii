package ro.neforii.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.neforii.dto.comment.*;
import ro.neforii.dto.comment.create.CommentOnPostRequestDto;
import ro.neforii.dto.comment.create.ReplyToCommentRequestDto;
import ro.neforii.dto.comment.update.CommentUpdateRequestDto;
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

    public CommentController(CommentService commentService, UserService userService, CommentMapper commentMapper) {
        this.commentService = commentService;
        this.userService = userService;
        this.commentMapper = commentMapper;
    }

    @PostMapping("/post/{postId}")
    public ResponseEntity<CommentResponseDto> createCommentOnPost(@PathVariable int postId, @RequestBody CommentOnPostRequestDto request) {
        User user = userService.findById(request.userId());
        Comment comment = commentService.createCommentOnPost(request.text(), user, postId);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentMapper.toCommentDto(comment));
    }

    @PostMapping("/comment/{commentId}")
    public ResponseEntity<CommentResponseDto> createCommentOnComment(@PathVariable int commentId, @RequestBody ReplyToCommentRequestDto request) {
        User user = userService.findById(request.userId());
        Comment comment = commentService.createReplyToComment(request.text(), user, commentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentMapper.toCommentDto(comment));
    }


    @PutMapping("/{id}")
    public ResponseEntity<CommentResponseDto> updateComment(@PathVariable int id, @RequestBody CommentUpdateRequestDto request) {
        User user = userService.findById(request.userId());
        Comment updated = commentService.updateComment(id, request.text(), user);
        CommentResponseDto response = commentMapper.toCommentDto(updated);
        return ResponseEntity.ok(response);
    }
}
