package ro.neforii.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.neforii.dto.CommentListResponseDto;
import ro.neforii.dto.comment.CommentResponseDto;
import ro.neforii.dto.comment.delete.SuccesMessageDto;
import ro.neforii.dto.comment.update.CommentUpdateRequestDto;
import ro.neforii.dto.comment.vote.CommentVoteResponseDto;
import ro.neforii.dto.comment.vote.CommentVoteRequestDto;
import ro.neforii.dto.common.ExpectedResponse;
import ro.neforii.service.*;

import java.util.UUID;

@RestController
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;
    private final FakeUserAuthService fakeAuthService;
    private final UserService userService;
    private final PostService postService;


    public CommentController(CommentService commentService, FakeUserAuthService fakeAuthService, UserService userService, PostService postService) {
        this.commentService = commentService;
        this.fakeAuthService = fakeAuthService;
        this.userService = userService;
        this.postService = postService;
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpectedResponse<CommentResponseDto>> updateComment(@PathVariable UUID id, @Valid @RequestBody CommentUpdateRequestDto request) {
        UUID currentUserId = fakeAuthService.getCurrentUserId();
        CommentResponseDto updated = commentService.updateComment(id, request, currentUserId);
        return ResponseEntity.ok(new ExpectedResponse<>(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccesMessageDto> deleteComment(@PathVariable UUID id) {
        UUID currentUserId = fakeAuthService.getCurrentUserId();
        commentService.deleteComment(id, currentUserId);
        return ResponseEntity.ok(new SuccesMessageDto(true, "Comentariul a fost sters cu succes"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentListResponseDto> getCommentsForPost(@PathVariable UUID id) {
        UUID currentUserId = fakeAuthService.getCurrentUserId();
        CommentListResponseDto comments = commentService.getCommentsForPost(id, currentUserId);

        return ResponseEntity.ok(comments);
    }

    @PutMapping("/{id}/vote")
    public ResponseEntity<ExpectedResponse<CommentVoteResponseDto>> voteComment(@PathVariable UUID id, @RequestBody CommentVoteRequestDto commentVoteRequestDto) {
        UUID currentUserId = fakeAuthService.getCurrentUserId();
        CommentVoteResponseDto commentVoteResponseDto = commentService.voteComm(id, currentUserId, commentVoteRequestDto);
        return ResponseEntity.ok(new ExpectedResponse<>(commentVoteResponseDto));
    }

}
