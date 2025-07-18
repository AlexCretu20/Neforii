package service;

import model.Post;
import model.User;
import repository.CommentRepository;
import repository.PostRepository;
import repository.VoteRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class PostService implements IVotable {

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

    public Post createPost(User user, String text) {
        Post post = new Post(text, LocalDateTime.now(), false, user);
        postRepository.save(post);
        return post;
    }

    public boolean updatePost(int id, String text) {
        Optional<Post> postOptional = postRepository.findById(id);
        if (postOptional.isPresent()) {
            Post post = postOptional.get();
            post.setText(text);
            postRepository.update(post);
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
                postRepository.update(post);
            }
        }
    }

    public void updateAwardsForOnePost(int id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            boolean updated = updateAward(post);
            if (updated) {
                postRepository.update(post);
            }
        }
    }

    public int displayUpvotes(int id) {
        return voteRepository.countVotesByPostId(id, true);
    }


    public int displayDownvotes(int id) {
        return voteRepository.countVotesByPostId(id, false);
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
}
