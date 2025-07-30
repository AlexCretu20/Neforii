package ro.neforii.controller;

import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.neforii.dto.comment.CommentResponseDto;
import ro.neforii.dto.comment.create.CommentOnPostRequestDto;
import ro.neforii.dto.common.SuccessResponse;
import ro.neforii.dto.post.PostRequestDto;
import ro.neforii.dto.post.PostResponseDto;
import ro.neforii.dto.post.PostUpdateRequestDto;
import ro.neforii.dto.post.SuccesDeleteMessageDto;
import ro.neforii.mapper.CommentMapper;
import ro.neforii.mapper.PostMapper;
import ro.neforii.model.Comment;
import ro.neforii.model.Post;
import ro.neforii.model.User;
import ro.neforii.service.CommentService;
import ro.neforii.service.FakeUserAuthService;
import ro.neforii.service.PostService;
import ro.neforii.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;
    private final PostMapper postMapper;
    private final UserService userService;
    private final CommentService commentService;
    private final CommentMapper commentMapper;
    private final FakeUserAuthService fakeAuthService ; // momentan sa simuleze userul curent

    public PostController(PostService postService, PostMapper postMapper, UserService userService, CommentService commentService, CommentMapper commentMapper, FakeUserAuthService fakeAuthService) {
        this.postService = postService;
        this.postMapper = postMapper;
        this.userService = userService;
        this.commentService = commentService;
        this.commentMapper = commentMapper;
        this.fakeAuthService = fakeAuthService;
    }

    @PostMapping
    public ResponseEntity<PostResponseDto> newPost(@Valid @RequestBody PostRequestDto postRequestDto) {
        User user = userService.findByUsername("andrei");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Post post = new Post();
        post.setTitle(postRequestDto.title());
        post.setContent(postRequestDto.content());
        post.setAuthor(postRequestDto.author());
        post.setSubreddit(postRequestDto.subreddit());
        post.setCreatedAt(LocalDateTime.now());
        post.setAwarded(false);
        post.setUser(user);

        postService.create(post);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(postMapper.postToPostResponseDto(post, user.getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponseDto> update(@PathVariable UUID id, @RequestBody PostUpdateRequestDto dto) {
        Post updated = postService.updatePartial(id, dto);
        User user = userService.findByUsername("andrei");

        return ResponseEntity.ok(postMapper.postToPostResponseDto(updated, user.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccesDeleteMessageDto> delete(@PathVariable UUID id) {
        Optional<Post> optionalPost = postService.findById(id);
        if (optionalPost.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        postService.deleteById(id);
        return ResponseEntity.ok(new SuccesDeleteMessageDto("Postarea a fost stearsa cu succes"));
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<List<PostResponseDto>>> getPosts() {
        UUID currentUserId = fakeAuthService.getCurrentUserId();
        List<PostResponseDto> posts = postService.getAllPostsAsUser(currentUserId);

        return ResponseEntity.ok(new SuccessResponse<>(posts));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDto> displayById(@PathVariable UUID id) {
        Optional<Post> optionalPost = postService.findById(id);
        if (optionalPost.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Post post = optionalPost.get();
        User user = userService.findByUsername("andrei");
        UUID currentUserId = user != null ? user.getId() : null;

        return ResponseEntity.ok(postMapper.postToPostResponseDto(post, currentUserId));
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentResponseDto> createCommentOnPost(@PathVariable UUID id, @Valid @RequestBody CommentOnPostRequestDto request) {
        User user = userService.findByUsername(request.author());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Comment comment;
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
