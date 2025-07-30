package ro.neforii.ui;

import ro.neforii.dto.post.PostRequestDto;
import ro.neforii.model.Post;
import ro.neforii.model.User;
import ro.neforii.service.PostService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class PostUI {
    private final Scanner scanner;
    private final PostService postService;
    private final CommentUI commentUI;

    public PostUI(Scanner scanner, PostService postService, CommentUI commentUI) {
        this.scanner = scanner;
        this.postService = postService;
        this.commentUI = commentUI;
    }

    public void createPostUI(User currentUser) {
        System.out.print("Enter the title of your post: ");
        String title = scanner.nextLine();
        System.out.print("Enter the content of your post: ");
        String content = scanner.nextLine();
        System.out.print("Enter the path of the for your post: ");
        String imagePath = scanner.nextLine();
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setAuthor(currentUser.getUsername());
        post.setImagePath(imagePath);
        post.setCreatedAt(LocalDateTime.now());
        post.setUser(currentUser);

        postService.create(post);
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

        System.out.print("Enter the new title: ");
        String newTitle = scanner.nextLine();

        System.out.print("Enter the new content: ");
        String newContent = scanner.nextLine();

        Post post = optionalPost.get();

        post.setTitle(newTitle);
        post.setContent(newContent);

        PostRequestDto postDto = new PostRequestDto(
                newTitle,
                newContent,
                post.getAuthor(),
                post.getSubreddit()
        );
        postService.update(postId,postDto);
        System.out.println("Post updated successfully.");
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

        postService.deleteById(postId);
        System.out.println("Post deleted successfully.");
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
            commentUI.showCommentsForPost(post.getId());
        }
    }

}

