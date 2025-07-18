package ui;

import model.Post;
import model.User;
import service.PostService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class PostUI {
    private final Scanner scanner;
    private final PostService postService;

    public PostUI(Scanner scanner, PostService postService) {
        this.scanner = scanner;
        this.postService = postService;
    }

    public void createPostUI(User currentUser) {
        System.out.print("Enter the text of your post: ");
        String text = scanner.nextLine();

        Post post = postService.createPost(currentUser, text);
        System.out.println("Post created successfully with ID: " + post.getId());
    }

    public void updatePostUI(User currentUser) {
        System.out.print("Enter the ID of the post to update: ");
        int postId = readInt();

        Post post = postService.getPostById(postId);
        if (post == null) {
            System.out.println("Post not found.");
            return;
        }

        if (!post.getUser().equals(currentUser)) {
            System.out.println("You can only update your own posts.");
            return;
        }

        System.out.print("Enter the new text: ");
        String newText = scanner.nextLine();

        boolean success = postService.updatePost(postId, newText);
        if (success) {
            System.out.println("Post updated successfully.");
        } else {
            System.out.println("Failed to update post.");
        }
    }

    public void deletePostUI(User currentUser) {
        System.out.print("Enter the ID of the post to delete: ");
        int postId = readInt();

        Post post = postService.getPostById(postId);
        if (post == null) {
            System.out.println("Post not found.");
            return;
        }

        if (!post.getUser().equals(currentUser)) {
            System.out.println("You can only delete your own posts.");
            return;
        }

        boolean success = postService.deletePost(postId);
        if (success) {
            System.out.println("Post deleted successfully.");
        } else {
            System.out.println("Failed to delete post.");
        }
    }

    public void displayAllPostsUI() {
        postService.updateAwardsForAllPosts();

        List<Post> posts = postService.getAllPosts();
        if (posts.isEmpty()) {
            System.out.println("No posts available.");
            return;
        }

        for (Post post : posts) {
            displayPostDetails(post);
        }
    }

    public int displayOnePostUI() {
        System.out.print("Enter the ID of the post to view: ");
        int postId = readInt();

        postService.updateAwardsForOnePost(postId);

        Post post = postService.getPostById(postId);
        if (post == null) {
            System.out.println("Post not found.");
            return -1;
        }

        displayPostDetails(post);
        return postId;
    }

    private void displayPostDetails(Post post) {
        System.out.println(post);
        int upvotes = postService.displayUpvotes(post.getId());
        int downvotes = postService.displayDownvotes(post.getId());
        System.out.println("Upvotes: " + upvotes);
        System.out.println("Downvotes: " + downvotes);
        System.out.println();
    }

    private int readInt() {
        while (true) {
            try {
                String input = scanner.nextLine();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a valid number: ");
            }
        }
    }

    public void invalidOption(){
        System.out.println("Invalid option.");
    }
}

