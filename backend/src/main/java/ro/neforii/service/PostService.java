package ro.neforii.service;

import org.springframework.stereotype.Service;
import ro.neforii.dto.post.PostRequestDto;
import ro.neforii.dto.post.PostUpdateRequestDto;
import ro.neforii.exception.PostNotFoundException;
import ro.neforii.exception.TitleAlreadyInUseException;
import ro.neforii.model.Post;
import ro.neforii.repository.PostRepository;
import ro.neforii.repository.VoteRepository;
import ro.neforii.service.crud.CrudService;

import java.util.List;
import java.util.Optional;

@Service
public class PostService implements CrudService<Post, Integer, PostRequestDto> {

    private final PostRepository postRepository;
    private final VoteRepository voteRepository;


    public PostService(PostRepository postRepository, VoteRepository voteRepository) {
        this.postRepository = postRepository;
        this.voteRepository = voteRepository;

    }


    //CREATE methods
    @Override
    public Post create(Post post) {
        return postRepository.save(post);
    }


    //READ methods
    @Override
    public Optional<Post> findById(Integer id){
        return postRepository.findById(id);
    }

    @Override
    public List<Post> findAll() {
        return postRepository.findAll();
    }

    //UPDATE methods
    @Override
    public Post update (Integer id, PostRequestDto postRequestDto) {
        Optional<Post> postOptional = findById(id);

        if (postOptional.isEmpty()) {
            throw new PostNotFoundException("The post with id " + id + " does not exist.");
        }

        Post post = postOptional.get();

        if (!(postRequestDto.title().equals(post.getTitle())) && isTitleExsiting(post.getTitle()) ){
            throw new TitleAlreadyInUseException("The title already exists.");
        }

        post.setTitle(postRequestDto.title());
        post.setContent(postRequestDto.content());
        //post.setImagePath(postRequestDto.imagePath());

        return postRepository.save(post);

    }

    public Post updatePartial(Integer id, PostUpdateRequestDto dto) {
        Post post = findById(id).orElseThrow(() ->
                new PostNotFoundException("Post with ID " + id + " not found."));

        if (dto.title() != null && !dto.title().isBlank()) {
            if (!dto.title().equals(post.getTitle()) && isTitleExsiting(dto.title())) {
                throw new TitleAlreadyInUseException("The title already exists.");
            }
            post.setTitle(dto.title());
        }

        if (dto.content() != null) {
            post.setContent(dto.content());
        }

        post.setUpdatedAt(java.time.LocalDateTime.now());
        return postRepository.save(post);
    }


    //DELETE methods
    @Override
    public void deleteById(Integer id) {
        postRepository.deleteById(id);

    }

    public Post getPostById(int id) {
        Optional<Post> postOptional = postRepository.findById(id);
        return postOptional.orElse(null);
    }

    public List<Post> getAllPosts(){
        return postRepository.findAll();
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

    public void updateAwardsForOnePost(int id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            boolean updated = updateAward(post);
            if (updated) {
                postRepository.save(post);
            }
        }
    }

    public int displayUpvotes(int id) {
        Post post = postRepository.findById(id).orElseThrow();
        return voteRepository.countByPostAndIsUpvote(post, true);
    }


    public int displayDownvotes(int id) {
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

    public List<Post> findAllPostsByUser(int userId){
        return postRepository.findAllByUserId(userId);
    }

    public boolean isTitleExsiting(String title){
        return postRepository.existsByTitle(title);
    }
}
