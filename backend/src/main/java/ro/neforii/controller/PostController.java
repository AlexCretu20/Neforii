package ro.neforii.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.neforii.dto.post.PostResponseDto;
import ro.neforii.mapper.PostMapper;
import ro.neforii.model.Post;
import ro.neforii.service.PostService;
import ro.neforii.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/post")
public class PostController {
    private final PostService postService;
    private final PostMapper postMapper;
    private final UserService userService;

    public PostController(PostService postService, PostMapper postMapper, UserService userService) {
        this.postService = postService;
        this.postMapper = postMapper;
        this.userService = userService;

    }

    @PostMapping
    public ResponseEntity<PostResponseDto> newPost(@RequestBody PostResponseDto postResponseDto){
        Integer userId = postResponseDto.userId();
        Post post = postService.createPost(userService.findById(userId), postResponseDto.text());
        if (post == null){
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED) // 401
                    .build();
        }
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(postMapper.postToPostResponseDto(post));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Integer id, @RequestBody PostResponseDto postResponseDto) {
        boolean isUpdated = postService.updatePost(id, postResponseDto.text());
        if(!isUpdated){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id){
        boolean isDeleted = postService.deletePost(id);
        if (!isDeleted){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<PostResponseDto>> displayAll() {
        List<Post> posts = postService.getAllPosts();

        List<PostResponseDto> postResponseDtos = posts.stream()
                                        .map(PostMapper::postToPostResponseDto)
                                        .toList();
        return ResponseEntity.ok(postResponseDtos);

    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDto> displayById(@PathVariable Integer id){
        Post post = postService.getPostById(id);
        if (post == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(postMapper.postToPostResponseDto(post));
    }

}
