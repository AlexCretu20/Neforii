package ro.neforii.ui;

import ro.neforii.model.Post;
import ro.neforii.model.User;
import ro.neforii.service.PostService;

import java.util.List;
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
        List<Post> posts = postService.findAllPostsByUser(currentUser.getId());

        if (posts.isEmpty()) {
            System.out.println("You have no posts to update.");
            return;
        }

        displayPostsForUser(currentUser.getId());

        System.out.print("Enter the ID of the post you want to update: ");
        int postId = readInt();

        Optional<Post> optionalPost = posts.stream()
                .filter(p -> p.getId() == postId)
                .findFirst();

        if (optionalPost.isEmpty()) {
            System.out.println("Invalid post ID. Please choose one from your list.");
            return;
        }

        System.out.print("Enter the new text: ");
        String newText = scanner.nextLine();

        boolean success = postService.updatePost(postId, newText);
        System.out.println(success ? "Post updated successfully." : "Failed to update post.");
    }


    public void deletePostUI(User currentUser) {
        List<Post> posts = postService.findAllPostsByUser(currentUser.getId());

        if (posts.isEmpty()) {
            System.out.println("You have no posts to delete.");
            return;
        }

        displayPostsForUser(currentUser.getId());

        System.out.print("Enter the ID of the post you want to delete: ");
        int postId = readInt();

        Optional<Post> optionalPost = posts.stream()
                .filter(p -> p.getId() == postId)
                .findFirst();

        if (optionalPost.isEmpty()) {
            System.out.println("Invalid post ID. Please choose one from your list.");
            return;
        }

        boolean success = postService.deletePost(postId);
        System.out.println(success ? "Post deleted successfully." : "Failed to delete post.");
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

    public void displayPostsForUser(int userId){
        List <Post> posts = postService.findAllPostsByUser(userId);

        if(posts.isEmpty()){
            System.out.println("No posts available.");
            return;
        }

        System.out.println("\n--- My Posts ---");
        for (Post post : posts) {
            displayPostDetails(post);
        }
    }

}

