package ro.neforii.controller;

import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ro.neforii.dto.CommentListResponseDto;
import ro.neforii.dto.comment.CommentResponseDto;
import ro.neforii.dto.comment.create.CommentOnPostRequestDto;
import ro.neforii.dto.common.ExpectedResponse;
import ro.neforii.dto.post.*;
import ro.neforii.dto.vote.VoteRequestDto;
import ro.neforii.service.FakeUserAuthService;
import ro.neforii.service.FileService;
import ro.neforii.service.PostService;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;
    private final FakeUserAuthService fakeAuthService; // momentan sa simuleze userul curent
    private final FileService fileService;

    public PostController(PostService postService, FakeUserAuthService fakeAuthService, FileService fileService) {
        this.postService = postService;
        this.fakeAuthService = fakeAuthService;
        this.fileService = fileService;
    }

    @GetMapping
    public ResponseEntity<ExpectedResponse<List<PostResponseDto>>> getPosts() {
        UUID currentUserId = fakeAuthService.getCurrentUserId();
        List<PostResponseDto> posts = postService.getAllPostsAsUser(currentUserId);

        return ResponseEntity.ok(new ExpectedResponse<>(posts));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpectedResponse<PostResponseDto>> getPostById(@PathVariable UUID id) {
        UUID currentUserId = fakeAuthService.getCurrentUserId();
        PostResponseDto postResponseDto = postService.getPostByIdAsUser(id, currentUserId);

        return ResponseEntity.ok(new ExpectedResponse<>(postResponseDto));
    }

    @PostMapping
    public ResponseEntity<ExpectedResponse<PostResponseDto>> createPost(
            @Valid @ModelAttribute PostRequestDto postRequestDto
    ) {
        UUID currentUserId = fakeAuthService.getCurrentUserId();
        PostResponseDto dto = postService.createPost(postRequestDto, currentUserId);
        return ResponseEntity.ok(new ExpectedResponse<>(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpectedResponse<PostResponseDto>> updatePost(@PathVariable UUID id, @Valid @RequestBody PostUpdateRequestDto postUpdateRequestDto) {
        UUID currentUserId = fakeAuthService.getCurrentUserId();
        PostResponseDto updatedPost = postService.updatePost(id, postUpdateRequestDto, currentUserId);

        return ResponseEntity.ok(new ExpectedResponse<>(updatedPost));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ExpectedResponse<String>> deletePost(@PathVariable UUID id) {
        UUID currentUserId = fakeAuthService.getCurrentUserId();
        postService.deletePost(id, currentUserId);

        return ResponseEntity.ok(new ExpectedResponse<>("Postarea a fost ștearsă cu succes")); // mesajul asta il vrea api-ul
    }

    @PutMapping("/{id}/vote")
    public ResponseEntity<ExpectedResponse<PostVoteResponseDto>> votePost(@PathVariable UUID id, @RequestBody VoteRequestDto voteRequestDto) {
        UUID currentUserId = fakeAuthService.getCurrentUserId();
        PostVoteResponseDto postResponseDto = postService.votePost(id, currentUserId, voteRequestDto);

        return ResponseEntity.ok(new ExpectedResponse<>(postResponseDto));
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<CommentListResponseDto> getCommentsForPost(@PathVariable UUID id) {
        UUID currentUserId = fakeAuthService.getCurrentUserId();
//        List<CommentResponseDto> comments = postService.getCommentsForPost(id, currentUserId);
        CommentListResponseDto comments = postService.getCommentsForPost(id, currentUserId);

        return ResponseEntity.ok(comments);
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<ExpectedResponse<CommentResponseDto>> createCommentOnPost(@PathVariable UUID id, @Valid @RequestBody CommentOnPostRequestDto request) {
        UUID currentUserId = fakeAuthService.getCurrentUserId();
        CommentResponseDto commentResponseDto = postService.createComment(id, request, currentUserId);
        return ResponseEntity.ok(new ExpectedResponse<>(commentResponseDto));
    }
}
