package ro.neforii.service;

import jakarta.validation.Valid;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.stereotype.Service;
import ro.neforii.dto.CommentListResponseDto;
import ro.neforii.dto.comment.CommentResponseDto;
import ro.neforii.dto.comment.create.CommentOnPostRequestDto;
import ro.neforii.dto.post.*;
import ro.neforii.dto.vote.VoteRequestDto;
import ro.neforii.exception.CommentNotFoundException;
import ro.neforii.exception.ForbiddenActionException;
import ro.neforii.exception.PostNotFoundException;
import ro.neforii.mapper.PostMapper;
import ro.neforii.model.Comment;
import ro.neforii.model.Post;
import ro.neforii.model.User;
import ro.neforii.model.VoteType;
import ro.neforii.repository.PostRepository;
import ro.neforii.repository.VoteRepository;
import ro.neforii.service.security.OwnershipValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final VoteService voteService;
    private final UserService userService;
    private final CommentService commentService;
    private final PostMapper postMapper;
    private final OwnershipValidator ownershipValidator;


    public PostService(PostRepository postRepository, PostMapper postMapper, UserService userService, VoteService voteService,
                       OwnershipValidator ownershipValidator, CommentService commentService) {
        this.postRepository = postRepository;
        this.postMapper = postMapper;
        this.userService = userService;
        this.voteService = voteService;
        this.ownershipValidator = ownershipValidator;
        this.commentService = commentService;
    }


    public PostResponseDto createPost(PostRequestDto postRequestDto, UUID currentUserId) {
        User user = userService.getUserByUsername(postRequestDto.author());
        Post post = postMapper.postRequestDtoToPost(postRequestDto, user);

//        ownershipValidator.assertPostOwner(currentUserId, post);

        Post savedPost = postRepository.save(post);

        return postMapper.postToPostResponseDto(savedPost, user.getId());
    }

    public List<PostResponseDto> getAllPostsAsUser(UUID currentUserId) {
        List<Post> posts = postRepository.findAll();

        return posts.stream()
                .map(post -> postMapper.postToPostResponseDto(post, currentUserId))
                .toList();
    }

    public PostResponseDto getPostByIdAsUser(UUID id, UUID currentUserId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post with ID " + id + " not found."));

        return postMapper.postToPostResponseDto(post, currentUserId);
    }

    public PostResponseDto updatePost(UUID id, PostUpdateRequestDto postUpdateRequestDto, UUID currentUserId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post with ID " + id + " not found."));

//        ownershipValidator.assertPostOwner(currentUserId, post);

        if (postUpdateRequestDto.title() != null) {
            post.setTitle(postUpdateRequestDto.title());
        }
        if (postUpdateRequestDto.content() != null) {
            post.setContent(postUpdateRequestDto.content());
        }
        post.setUpdatedAt(LocalDateTime.now());

        Post updatedPost = postRepository.save(post);
        return postMapper.postToPostResponseDto(updatedPost, post.getUser().getId());
    }

    public void deletePost(UUID id, UUID currentUserId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post with ID " + id + " not found."));

        if (!post.getUser().getId().equals(currentUserId)) {
            throw new ForbiddenActionException("The current user is not the author of the post.");
        }

        postRepository.delete(post);
    }

    public PostVoteResponseDto votePost(UUID postId, UUID currentUserId, VoteRequestDto voteRequestDto) {
        VoteType voteType = VoteType.fromString(voteRequestDto.voteType());
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post with ID " + postId + " not found."));

//        ownershipValidator.assertPostOwner(currentUserId, post);

        voteService.createOrUpdateVoteForPost(currentUserId, postId, voteType);

        Post updatedPost = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post with ID " + postId + " not found."));

        return postMapper.postToPostVoteResponseDto(updatedPost, currentUserId);
    }

    public CommentListResponseDto getCommentsForPost(UUID id, UUID currentUserId) {
        return commentService.getCommentsForPost(id, currentUserId);
    }

    public CommentResponseDto createComment(UUID postId, CommentOnPostRequestDto request, UUID currentUserId) {
        User user = userService.getUserByUsername(request.author());
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with ID: " + postId));

        Comment parent = null;
        if (request.parentId() != null) {
            parent = commentService.findById(request.parentId())
                    .orElseThrow(() -> new CommentNotFoundException("Parent comment not found with ID: " + request.parentId()));
        }

        return commentService.createCommentOnPost(request.content(), user, post, parent);
    }
}
