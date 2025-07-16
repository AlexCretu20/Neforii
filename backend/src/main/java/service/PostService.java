package service;

import model.Comment;
import model.Post;
import model.User;
import model.Vote;
import repository.CommentRepository;
import repository.PostRepository;
import repository.VoteRepository;

import java.time.LocalDateTime;
import java.util.*;

public class PostService implements IVotable {

    private static PostService instance;
    private final PostRepository postRepository;
    private final VoteRepository voteRepository;
    private final CommentRepository commentRepository;
    private static int counter = 0;

    private PostService(PostRepository postRepository, VoteRepository voteRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.voteRepository = voteRepository;
        this.commentRepository = commentRepository;

    }

    ;

    private int countVotes(int id, boolean isUpvote) {
        int counter = 0;
        List<Vote> votes;
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            votes = post.getVotes();
            for (Vote vote : votes) {
                if (vote.isUpvote() == isUpvote) {
                    counter++;
                }
            }
            return counter;
        }

        return 0;

    }

    public static PostService getInstance(PostRepository postRepository, VoteRepository voteRepository, CommentRepository commentRepository) {
        if (instance == null) {
            instance = new PostService(postRepository, voteRepository, commentRepository);
        }
        return instance;
    }

    public Post getPostById(int id) {
        Optional<Post> post = postRepository.findById(id);
        return post.orElse(null);
    }


    public void CreatePost(User user, String text) {
        counter++;
        Post post = new Post(counter, text, LocalDateTime.now(), false, user);
//        posts.put(counter, post);
        postRepository.save(post);
        System.out.println("The post was created.");

    }

    public void UpdatePost(int id, String text) {
        Optional<Post> post = postRepository.findById(id);
        if (post.isPresent()) {
            Post updatePost = post.get();
            updatePost.setText(text);
            postRepository.save(updatePost);
            System.out.println("The post was updated.");
        } else {
            System.out.println("The post doesn't exists.");
        }


    }

    public void DeletePost(int id) {
        postRepository.deleteById(id);
        System.out.println("The post was deleted.");
    }

    public void addComment(int id, Comment comment) {
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            post.setComments(comment);
            commentRepository.save(comment);


        }
        System.out.println("The commnet was added. ");

    }


    public void AddVote(int postid, Vote vote) {
        Optional<Post> optionalPost = postRepository.findById(postid);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            post.setVotes(vote);
            voteRepository.save(vote);

        }
        System.out.println("The vote was added.");
    }

    public void ExpandComments(int id) {
        boolean isNumber = false;
        Optional<Post> optionalPost = postRepository.findById(id);
        List<Comment> comments = new ArrayList<Comment>();
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            comments = post.getComments();
            System.out.println(post);
            System.out.println("Upvotes " + displayUpvotes(id) + "\n" + "Downvotes " + displayDownvotes(id));
            isNumber = true;


        }

        if (isNumber) {
            System.out.println("Comments");
            for (Comment cm : comments) {
                System.out.println(cm.getId() + " " + cm.getText());
            }
            if (comments.size() == 0) {
                System.out.println("The post doesn't have comments.");
            }

        } else {
            System.out.println("The post doesn't exist.");
        }
    }

    public void DisplayPosts() {
        System.out.println("Loading...");
        List<Post> posts = postRepository.findAll();
        for (Post post : posts) {
            updateAward(post.getId());
            System.out.println(post);
        }

    }

    public void displayOnePost(int id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            updateAward(post.getId());
            // update
            postRepository.save(post);
            System.out.println(post);
            System.out.println("Upvotes " + displayUpvotes(id) + "\n" + "Downvotes " + displayDownvotes(id));
        } else {
            System.out.println("The post doesn't exists.");
        }
    }


    public int displayUpvotes(int id) {
        return countVotes(id, true);
    }

    public int displayDownvotes(int id) {
        return countVotes(id, false);
    }

    public void updateAward(int id) {
        if (displayUpvotes(id) > 1 && displayUpvotes(id) - displayDownvotes(id) > 0) {
            getPostById(id).setAwarded(true);
        }
    }

}
