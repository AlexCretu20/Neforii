package ro.neforii.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.neforii.dto.post.PostDto;
import ro.neforii.mapper.PostMapper;
import ro.neforii.mapper.UserMapper;
import ro.neforii.model.Post;
import ro.neforii.model.User;
import ro.neforii.service.PostService;
import ro.neforii.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/post")
public class PostController {
    private final PostService postService;
    private final PostMapper postMapper;
    private final UserService userService;
    private final UserMapper userMapper;

    public PostController(PostService postService, PostMapper postMapper, UserService userService, UserMapper userMapper) {
        this.postService = postService;
        this.postMapper = postMapper;
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping
    public ResponseEntity<PostDto> newPost(@RequestBody PostDto postDto){
        User user = postDto.user();
        Post post = postService.createPost(user, postDto.text());
        if (post == null){
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED) // 401
                    .build();
        }
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(postMapper.postToPostDto(post));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Integer id, @RequestBody PostDto postDto) {
        boolean isUpdated = postService.updatePost(id, postDto.text());
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
    public ResponseEntity<List<PostDto>> displayAll() {
        List<Post> posts = postService.getAllPosts();

        List<PostDto> postDtos = posts.stream()
                                        .map(PostMapper::postToPostDto)
                                        .toList();
        return ResponseEntity.ok(postDtos);

    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> displayById(@PathVariable Integer id){
        Post post = postService.getPostById(id);
        if (post == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(postMapper.postToPostDto(post));
    }

}
