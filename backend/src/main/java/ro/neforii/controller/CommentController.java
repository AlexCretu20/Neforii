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
import ro.neforii.mapper.CommentMapper;
import ro.neforii.model.Comment;
import ro.neforii.model.User;
import ro.neforii.service.CommentService;
import ro.neforii.service.UserService;
import ro.neforii.service.VoteService;

@RestController
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;
    private final UserService userService;
    private final CommentMapper commentMapper;
    private final VoteService voteService;

    public CommentController(CommentService commentService, UserService userService, CommentMapper commentMapper, VoteService voteService) {
        this.commentService = commentService;
        this.userService = userService;
        this.commentMapper = commentMapper;
        this.voteService = voteService;
    }


    @PostMapping("/comment/{commentId}")
    public ResponseEntity<CommentResponseDto> createCommentOnComment(@PathVariable int commentId,@Valid @RequestBody ReplyToCommentRequestDto request) {

        User user = userService.getCurrentUser();
        if(user==null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Comment comment = commentService.createReplyToComment(request.text(), user, commentId);
        if(comment==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(commentMapper.toCommentDto(comment,user));
    }


    @PutMapping("/{id}")
    public ResponseEntity<CommentResponseDto> updateComment(@PathVariable int id,@Valid @RequestBody CommentUpdateRequestDto request) {
        Comment comment = commentService.getComment(id);
        if (comment == null) {
            return ResponseEntity.notFound().build();
        }

        User user = comment.getUser();

        Comment updated = commentService.updateComment(id, request.content(), user);
        CommentResponseDto response = commentMapper.toCommentDto(updated,user);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccesMessageDto> deleteComment(@PathVariable int id){
        Comment comment = commentService.getComment(id);
        if (comment == null) {
            return ResponseEntity.notFound().build();
        }

        commentService.deleteComment(id);
        return ResponseEntity.ok(new SuccesMessageDto(true,"Comentariul a fost sters cu succes"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentResponseDto> getCommentWithReplies(@PathVariable int id){
        Comment comment = commentService.getComment(id);
        if(comment == null){
            return ResponseEntity.notFound().build();
        }

        User user = userService.getCurrentUser();

        CommentResponseDto response = commentMapper.toCommentDto(comment,user);
        return ResponseEntity.ok(response);
    }


    @PutMapping("/{id}/vote")
    public ResponseEntity<CommentVoteResponseDto> voteComment(@PathVariable int id, @Valid @RequestBody VoteRequestDto request){
        Comment comment = commentService.getComment(id);
        if (comment == null) {
            return ResponseEntity.notFound().build();
        }

//        User user = userService.getCurrentUser();
        User user = userService.findByUsername("andrei");
        if(user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if(request.voteType().equals("none")){
            voteService.deleteVoteForComment(comment,user);
        }else{
            boolean isUpVote = request.voteType().equals("up");
            voteService.createVote(user.getId(),null,comment.getId(),isUpVote);
        }
        int upVotes = commentService.displayUpvotes(comment.getId());
        int downVotes = commentService.displayDownvotes(comment.getId());
        int score = upVotes - downVotes;
        String userVote = voteService.getVoteTypeForUser(comment, user);

        CommentVoteResponseDto response = new CommentVoteResponseDto(upVotes, downVotes, score, userVote);
        return ResponseEntity.ok(response);
    }
}
