package ro.neforii.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.neforii.dto.comment.CommentResponseDto;
import ro.neforii.dto.comment.create.ReplyToCommentRequestDto;
import ro.neforii.dto.comment.delete.SuccesMessageDto;
import ro.neforii.dto.comment.update.CommentUpdateRequestDto;
import ro.neforii.dto.comment.vote.CommentVoteResponseDto;
import ro.neforii.dto.comment.vote.VoteRequestDto;
import ro.neforii.dto.common.SuccessResponse;
import ro.neforii.mapper.CommentMapper;
import ro.neforii.model.Comment;
import ro.neforii.model.User;
import ro.neforii.service.CommentService;
import ro.neforii.service.FakeUserAuthService;
import ro.neforii.service.UserService;
import ro.neforii.service.VoteService;

import java.util.UUID;

@RestController
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;
    private final FakeUserAuthService fakeAuthService;
    private final UserService userService;


    public CommentController(CommentService commentService, FakeUserAuthService fakeAuthService, UserService userService) {
        this.commentService = commentService;
        this.fakeAuthService = fakeAuthService;
        this.userService = userService;
    }

    @PostMapping("/comment/{commentId}")
    public ResponseEntity<CommentResponseDto> createCommentOnComment(@PathVariable UUID commentId, @Valid @RequestBody ReplyToCommentRequestDto request) {

        User user = userService.getCurrentUser();
        if(user==null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        CommentResponseDto commentDto = commentService.createReplyToComment(request.text(), user, commentId);
        if(commentDto==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(commentDto);
    }


    @PutMapping("/{id}")
    public ResponseEntity<CommentResponseDto> updateComment(@PathVariable UUID id,@Valid @RequestBody CommentUpdateRequestDto request) {
        Comment comment = commentService.getComment(id);
        if (comment == null) {
            return ResponseEntity.notFound().build();
        }

        User user = comment.getUser();

        CommentResponseDto updated = commentService.updateComment(id, request, user.getId());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccesMessageDto> deleteComment(@PathVariable UUID id){
        Comment comment = commentService.getComment(id);
        User user = userService.getCurrentUser();
        UUID userId = user.getId();

        if (comment == null) {
            return ResponseEntity.notFound().build();
        }

        commentService.deleteComment(id, userId);
        return ResponseEntity.ok(new SuccesMessageDto(true,"Comentariul a fost sters cu succes"));
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<CommentResponseDto> getCommentWithReplies(@PathVariable UUID id){
//        Comment comment = commentService.getComment(id);
//        if(comment == null){
//            return ResponseEntity.notFound().build();
//        }
//
//        User user = userService.getCurrentUser();
//
//        CommentResponseDto response = commentMapper.toCommentDto(comment,user);
//        return ResponseEntity.ok(response);
//    }
//
//
//    @PutMapping("/{id}/vote")
//    public ResponseEntity<CommentVoteResponseDto> voteComment(@PathVariable UUID id, @Valid @RequestBody VoteRequestDto request){
//        Comment comment = commentService.getComment(id);
//        if (comment == null) {
//            return ResponseEntity.notFound().build();
//        }
//
////        User user = userService.getCurrentUser();
//        User user = userService.findUserEntityByUsername("andrei");
//        if(request.voteType().equals("none")){
//            voteService.deleteVoteForComment(comment,user);
//        }else{
//            boolean isUpVote = request.voteType().equals("up");
//            voteService.createVote(user.getId(),null,comment.getId(),isUpVote);
//        }
//        int upVotes = commentService.displayUpvotes(comment.getId());
//        int downVotes = commentService.displayDownvotes(comment.getId());
//        int score = upVotes - downVotes;
//        String userVote = voteService.getVoteTypeForUser(comment, user);
//
//        CommentVoteResponseDto response = new CommentVoteResponseDto(upVotes, downVotes, score, userVote);
//        return ResponseEntity.ok(response);
//    }
}
