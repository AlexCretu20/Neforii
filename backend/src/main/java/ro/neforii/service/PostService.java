package ro.neforii.service;

import org.springframework.stereotype.Service;
import ro.neforii.model.Post;
import ro.neforii.model.User;
import ro.neforii.repository.CommentRepository;
import ro.neforii.repository.PostRepository;
import ro.neforii.repository.VoteRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PostService implements IVotable, IPostService {

    private final PostRepository postRepository;
    private final VoteRepository voteRepository;
    private final CommentRepository commentRepository;

    public PostService(PostRepository postRepository, VoteRepository voteRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.voteRepository = voteRepository;
        this.commentRepository = commentRepository;
    }

    public Post getPostById(int id) {
        Optional<Post> postOptional = postRepository.findById(id);
        return postOptional.orElse(null);
    }

    public List<Post> getAllPosts(){
        return postRepository.findAll();
    }

    public Post createPost(User user, String title, String content) {
        if (user == null) {
            return null; // sau throw UnauthorizedException
        }

        Post post = Post.builder()
                .title(title)
                .content(content)
                .createdAt(LocalDateTime.now())
                .isAwarded(false)
                .user(user)
                .build();

        return postRepository.save(post);
    }


    public boolean updatePost(int id, String text) {
        Optional<Post> postOptional = postRepository.findById(id);
        if (postOptional.isPresent()) {
            Post post = postOptional.get();
            post.setContent(text);
            postRepository.save(post);
            return true;
        }
        return false;
    }

    public boolean deletePost(int id) {
        Optional<Post> postOptional = postRepository.findById(id);
        if (postOptional.isPresent()) {
            postRepository.deleteById(id);
            return true;
        }
        return false;
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
}
