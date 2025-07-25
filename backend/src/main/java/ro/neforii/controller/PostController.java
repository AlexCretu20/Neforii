package ro.neforii.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.neforii.dto.comment.CommentResponseDto;
import ro.neforii.dto.comment.create.CommentOnPostRequestDto;
import ro.neforii.dto.post.PostRequestDto;
import ro.neforii.dto.post.PostResponseDto;
import ro.neforii.mapper.CommentMapper;
import ro.neforii.mapper.PostMapper;
import ro.neforii.model.Comment;
import ro.neforii.model.Post;
import ro.neforii.model.User;
import ro.neforii.service.CommentService;
import ro.neforii.service.PostService;
import ro.neforii.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;
    private final PostMapper postMapper;
    private final UserService userService;
    private final CommentService commentService;
    private final CommentMapper commentMapper;

    public PostController(PostService postService, PostMapper postMapper, UserService userService, CommentService commentService,CommentMapper commentMapper) {
        this.postService = postService;
        this.postMapper = postMapper;
        this.userService = userService;
        this.commentService=commentService;
        this.commentMapper=commentMapper;
    }

    @PostMapping
    public ResponseEntity<PostResponseDto> newPost(@RequestBody PostRequestDto postRequestDto) {
        Integer userId = postRequestDto.userId();
        Optional<User> user = userService.findById(userId);
        if (user.isEmpty()){
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED) // 401
                    .build();
        }

        Post post = new Post();
        post.setTitle(postRequestDto.title());
        post.setContent(postRequestDto.content());
        post.setAuthor(postRequestDto.author());
        post.setImagePath(postRequestDto.imagePath());
        post.setCreatedAt(LocalDateTime.now());
        post.setAwarded(false);
        post.setUser(user.get());

        postService.create(post);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(postMapper.postToPostResponseDto(post));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Integer id, @RequestBody PostRequestDto postRequestDto) {
        Post post = postService.update(id, postRequestDto);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        Optional<Post> optionalPost = postService.findById(id);
        if (optionalPost.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Post post = optionalPost.get();
        postService.deleteById(id);
        return ResponseEntity
                .noContent()
                .build();
    }

    @GetMapping
    public ResponseEntity<List<PostResponseDto>> displayAll() {
        List<Post> posts = postService.getAllPosts();

        List<PostResponseDto> postResponseDtos = posts.stream()
                .map(post -> postMapper.postToPostResponseDto(post))
                .toList();
        return ResponseEntity.ok(postResponseDtos);

    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDto> displayById(@PathVariable Integer id) {
        Optional<Post> optionalPost = postService.findById(id);
        if (optionalPost.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Post post = optionalPost.get();

        if (post == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(postMapper.postToPostResponseDto(post));
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentResponseDto> createCommentOnPost(@PathVariable int id,@Valid @RequestBody CommentOnPostRequestDto request){
        User user = userService.getCurrentUser();
        if(user==null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Comment comment;
        if(request.parentId()==null){
            comment = commentService.createCommentOnPost(request.content(),user,id);
        }else{
            comment = commentService.createReplyToComment(request.content(),user, request.parentId());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(commentMapper.toCommentDto(comment));
    }

}
