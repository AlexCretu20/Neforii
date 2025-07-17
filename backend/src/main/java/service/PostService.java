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
    private static int counter = 0;

    public PostService(PostRepository postRepository, VoteRepository voteRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.voteRepository = voteRepository;
        this.commentRepository = commentRepository;
    }

    ;


    public Post getPostById(int id) {
        Optional<Post> post = postRepository.findById(id);
        return post.orElse(null);
    }


    public void createPost(User user, String text) {
        counter++;
        Post post = new Post(counter, text, LocalDateTime.now(), false, user);
        postRepository.save(post);
        System.out.println("The post was created.");

    }

    public void updatePost(int id, String text) {
        Optional<Post> post = postRepository.findById(id);
        if (post.isPresent()) {
            Post updatePost = post.get();
            updatePost.setText(text);
            postRepository.update(updatePost);
            System.out.println("The post was updated.");
        } else {
            System.out.println("The post doesn't exists.");
        }


    }

    public void deletePost(int id) {
        postRepository.deleteById(id);
        System.out.println("The post was deleted.");
    }


    public void displayPosts() {
        System.out.println("Loading...");
        List<Post> posts = postRepository.findAll();
        for (Post post : posts) {
            boolean updated = updateAward(post);
            if (updated) {
                postRepository.update(post);
            }
            System.out.println(post);
        }
    }


    public void displayOnePost(int id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();

            boolean updated = updateAward(post);
            if(updated){
                postRepository.update(post);
            }

            System.out.println(post);
            System.out.println("Upvotes " + displayUpvotes(id) + "\n" + "Downvotes " + displayDownvotes(id));
        } else {
            System.out.println("The post doesn't exists.");
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
