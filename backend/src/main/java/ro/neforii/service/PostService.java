package ro.neforii.service;

import org.springframework.stereotype.Service;
import ro.neforii.dto.post.PostRequestDto;
import ro.neforii.dto.post.PostResponseDto;
import ro.neforii.dto.post.PostUpdateRequestDto;
import ro.neforii.dto.post.PostVoteResponseDto;
import ro.neforii.dto.vote.VoteRequestDto;
import ro.neforii.exception.PostNotFoundException;
import ro.neforii.exception.TitleAlreadyInUseException;
import ro.neforii.mapper.PostMapper;
import ro.neforii.model.Post;
import ro.neforii.model.User;
import ro.neforii.model.VoteType;
import ro.neforii.repository.PostRepository;
import ro.neforii.repository.VoteRepository;
import ro.neforii.service.crud.CrudService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final VoteRepository voteRepository;
    private final VoteService voteService;
    private final UserService userService;
    private final PostMapper postMapper;


    public PostService(PostRepository postRepository, VoteRepository voteRepository, PostMapper postMapper, UserService userService, VoteService voteService) {
        this.postRepository = postRepository;
        this.voteRepository = voteRepository;
        this.postMapper = postMapper;
        this.userService = userService;
        this.voteService = voteService;
    }


    public PostResponseDto createPost(PostRequestDto postRequestDto) {
        User user = userService.getUserByUsername(postRequestDto.author());
        Post post = postMapper.postRequestDtoToPost(postRequestDto, user);

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

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public PostResponseDto updatePost(UUID id, PostUpdateRequestDto postUpdateRequestDto, UUID currentUserId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post with ID " + id + " not found."));

        // TODO cand avem autentificare putem verifica daca userul curent este cel care a creat postarea

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

        // TODO cand avem autentificare putem verifica daca userul curent este cel care a creat postarea
//        if (!post.getUser().getId().equals(currentUserId)) {
//            throw new PostNotFoundException("You are not authorized to delete this post.");
//        }

        postRepository.delete(post);
    }

    public PostVoteResponseDto votePost(UUID postId, UUID currentUserId, VoteRequestDto voteRequestDto) {
//      TODO userId la autentificare si award la un anumit numar de voturi
        VoteType voteType = VoteType.fromString(voteRequestDto.voteType());

        voteService.deleteByUserIdAndPostId(currentUserId, postId);

        if (voteType != VoteType.NONE) {
            voteService.createVote(currentUserId, postId, null, voteType == VoteType.UP);
        }

        Post updatedPost = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post with ID " + postId + " not found."));

        return postMapper.postToPostVoteResponseDto(updatedPost, currentUserId);
    }

}
