package ro.neforii.service;

import org.springframework.stereotype.Service;
import ro.neforii.dto.post.PostRequestDto;
import ro.neforii.dto.post.PostResponseDto;
import ro.neforii.dto.post.PostUpdateRequestDto;
import ro.neforii.exception.PostNotFoundException;
import ro.neforii.exception.TitleAlreadyInUseException;
import ro.neforii.mapper.PostMapper;
import ro.neforii.model.Post;
import ro.neforii.model.User;
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
    private final UserService userService;
    private final PostMapper postMapper;


    public PostService(PostRepository postRepository, VoteRepository voteRepository, PostMapper postMapper, UserService userService) {
        this.postRepository = postRepository;
        this.voteRepository = voteRepository;
        this.postMapper = postMapper;
        this.userService = userService;
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

        if(postUpdateRequestDto.title() != null) {
            post.setTitle(postUpdateRequestDto.title());
        }
        if(postUpdateRequestDto.content()!=null) {
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

//    public Post update(UUID id, PostRequestDto postRequestDto) {
//        Optional<Post> postOptional = findById(id);
//
//        if (postOptional.isEmpty()) {
//            throw new PostNotFoundException("The post with id " + id + " does not exist.");
//        }
//
//        Post post = postOptional.get();
//
//        if (!(postRequestDto.title().equals(post.getTitle())) && isTitleExsiting(post.getTitle())) {
//            throw new TitleAlreadyInUseException("The title already exists.");
//        }
//
//        post.setTitle(postRequestDto.title());
//        post.setContent(postRequestDto.content());
//        //post.setImagePath(postRequestDto.imagePath());
//
//        return postRepository.save(post);
//
//    }

//    public Post updatePartial(UUID id, PostUpdateRequestDto dto) {
//        Post post = findById(id).orElseThrow(() ->
//                new PostNotFoundException("Post with ID " + id + " not found."));
//
//        if (dto.title() != null && !dto.title().isBlank()) {
//            if (!dto.title().equals(post.getTitle()) && isTitleExsiting(dto.title())) {
//                throw new TitleAlreadyInUseException("The title already exists.");
//            }
//            post.setTitle(dto.title());
//        }
//
//        if (dto.content() != null) {
//            post.setContent(dto.content());
//        }
//
//        post.setUpdatedAt(java.time.LocalDateTime.now());
//        return postRepository.save(post);
//    }


    public void deleteById(UUID id) {
        postRepository.deleteById(id);

    }

    public Post getPostById(UUID id) {
        Optional<Post> postOptional = postRepository.findById(id);
        return postOptional.orElse(null);
    }

    public void updateAwardsForAllPosts() {
        List<Post> posts = postRepository.findAll();
        for (int i = 0; i < posts.size(); i++) {
            Post post = posts.get(i);
            boolean updated = updateAward(post);
            if (updated) {
                postRepository.save(post);
            }
        }
    }

    public void updateAwardsForOnePost(UUID id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            boolean updated = updateAward(post);
            if (updated) {
                postRepository.save(post);
            }
        }
    }

    public int displayUpvotes(UUID id) {
        Post post = postRepository.findById(id).orElseThrow();
        return voteRepository.countByPostAndIsUpvote(post, true);
    }


    public int displayDownvotes(UUID id) {
        Post post = postRepository.findById(id).orElseThrow();
        return voteRepository.countByPostAndIsUpvote(post, false);
    }


    public boolean updateAward(Post post) {
        int upvotes = displayUpvotes(post.getId());
        int downvotes = displayDownvotes(post.getId());
        int diff = upvotes - downvotes;

        if (diff > 2 && !post.isAwarded()) {
            post.setAwarded(true);
            return true;
        } else if (diff <= 2 && post.isAwarded()) {
            post.setAwarded(false);
            return true;
        }
        return false;
    }

    public List<Post> findAllPostsByUser(UUID userId) {
        return postRepository.findAllByUserId(userId);
    }

    public boolean isTitleExsiting(String title) {
        return postRepository.existsByTitle(title);
    }


}
