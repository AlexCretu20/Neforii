package ro.neforii.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.neforii.dto.comment.CommentResponseDto;
import ro.neforii.dto.comment.create.CommentOnPostRequestDto;
import ro.neforii.dto.common.SuccessResponse;
import ro.neforii.dto.post.PostRequestDto;
import ro.neforii.dto.post.PostResponseDto;
import ro.neforii.dto.post.PostUpdateRequestDto;
import ro.neforii.dto.post.PostVoteResponseDto;
import ro.neforii.dto.vote.VoteRequestDto;
import ro.neforii.mapper.CommentMapper;
import ro.neforii.mapper.PostMapper;
import ro.neforii.model.Comment;
import ro.neforii.model.User;
import ro.neforii.service.CommentService;
import ro.neforii.service.FakeUserAuthService;
import ro.neforii.service.PostService;
import ro.neforii.service.UserService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;
    private final PostMapper postMapper;
    private final UserService userService;
    private final CommentService commentService;
    private final CommentMapper commentMapper;
    private final FakeUserAuthService fakeAuthService; // momentan sa simuleze userul curent

    public PostController(PostService postService, PostMapper postMapper, UserService userService, CommentService commentService, CommentMapper commentMapper, FakeUserAuthService fakeAuthService) {
        this.postService = postService;
        this.postMapper = postMapper;
        this.userService = userService;
        this.commentService = commentService;
        this.commentMapper = commentMapper;
        this.fakeAuthService = fakeAuthService;
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<List<PostResponseDto>>> getPosts() {
        UUID currentUserId = fakeAuthService.getCurrentUserId();
        List<PostResponseDto> posts = postService.getAllPostsAsUser(currentUserId);

        return ResponseEntity.ok(new SuccessResponse<>(posts));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<PostResponseDto>> getPostById(@PathVariable UUID id) {
        UUID currentUserId = fakeAuthService.getCurrentUserId();
        PostResponseDto postResponseDto = postService.getPostByIdAsUser(id, currentUserId);

        return ResponseEntity.ok(new SuccessResponse<>(postResponseDto));
    }

    @PostMapping
    public ResponseEntity<SuccessResponse<PostResponseDto>> createPost(@Valid @RequestBody PostRequestDto postRequestDto) {
        UUID currentUserId = fakeAuthService.getCurrentUserId();
        PostResponseDto postResponseDto = postService.createPost(postRequestDto);

        return ResponseEntity.ok(new SuccessResponse<>(postResponseDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse<PostResponseDto>> updatePost(@PathVariable UUID id, @Valid @RequestBody PostUpdateRequestDto postUpdateRequestDto) {
        UUID currentUserId = fakeAuthService.getCurrentUserId();
        PostResponseDto updatedPost = postService.updatePost(id, postUpdateRequestDto, currentUserId);

        return ResponseEntity.ok(new SuccessResponse<>(updatedPost));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse<String>> deletePost(@PathVariable UUID id) {
        UUID currentUserId = fakeAuthService.getCurrentUserId();
        postService.deletePost(id, currentUserId);

        return ResponseEntity.ok(new SuccessResponse<>("Postarea a fost ștearsă cu succes")); // mesajul asta il vrea api-ul
    }

    @PutMapping("/{id}/vote")
    public ResponseEntity<SuccessResponse<PostVoteResponseDto>> votePost(@PathVariable UUID id, @RequestBody VoteRequestDto voteRequestDto) {
        UUID currentUserId = fakeAuthService.getCurrentUserId();
        PostVoteResponseDto postResponseDto = postService.votePost(id, currentUserId, voteRequestDto);

        return ResponseEntity.ok(new SuccessResponse<>(postResponseDto));
    }


    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentResponseDto> createCommentOnPost(@PathVariable UUID id, @Valid @RequestBody CommentOnPostRequestDto request) {
        User user = userService.findUserEntityByUsername(request.author());
        Comment comment;
        //TO DO: de mutat logica in service
        if (request.parentId() == null) {
            comment = commentService.createCommentOnPost(request.content(), user, id);
        } else {
            comment = commentService.createReplyToComment(request.content(), user, request.parentId());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(commentMapper.toCommentDto(comment, user));
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<Map<String, Object>> getCommentsForPost(@PathVariable UUID postId) {
        User user = userService.getCurrentUser();
        List<Comment> topLevelComments = commentService.getTopLevelComments(postId);

        List<CommentResponseDto> dtos = topLevelComments.stream()
                .map(c -> commentMapper.toCommentDto(c, user))
                .toList();

        int total = commentService.getComments().stream()
                .filter(c -> c.getPost() != null && c.getPost().getId() == postId)
                .toList().size();

        return ResponseEntity.ok(Map.of(
                "data", dtos,
                "total", total
        ));
    }
}
