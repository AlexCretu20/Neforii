import model.Comment;
import model.EntityType;
import model.User;
import model.Vote;
import repository.CommentRepository;
import repository.PostRepository;
import repository.UserRepository;
import repository.VoteRepository;
import service.*;
import ui.UserUI;
import validation.UserValidator;

import java.time.LocalDateTime;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        UserService userService = UserService.getInstance();
        PostService postService = PostService.getInstance();
        CommentService commentService = CommentService.getInstance();
        VoteService voteService = VoteService.getInstance();
        Scanner scanner = new Scanner(System.in);
        UserValidator userValidator = new UserValidator();
        UserUI userUI = new UserUI(scanner,userService,userValidator);
//
//        User user = new User("test", "alex@yahoo.com", "parola"," 0772208997", "Test user");
//        Post post = new Post("test post", LocalDateTime.now(), false, user);



//        String insertSQL = "INSERT INTO users (username, email, password, phone_number, description) VALUES (?, ?, ?, ?, ?)";
//
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(insertSQL)) {
//
//            stmt.setString(1, "alex");
//            stmt.setString(2, "alex@example.com");
//            stmt.setString(3, "secureHashedPassword123");
//            stmt.setString(4, "+40712345678");
//            stmt.setString(5, "Just a test user");
//
//            int rows = stmt.executeUpdate();
//            System.out.println("Inserted rows: " + rows);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        UserRepository userRepository = new UserRepository();
        PostRepository postRepository = new PostRepository(userRepository);
        CommentRepository commentRepository = new CommentRepository(userRepository);
        User user = new User("aaaa", "aaaa@yahoo.com", "alsdJ23", "079438232", "aadnasdsadsadsad");
        //Post post = new Post("test post", LocalDateTime.now(), false, userRepository.findById(1).get());
//        userRepository.save(user);
        Comment comment = new Comment(
                "nasdasjdjsa",

                userRepository.findById(2).get(),
                EntityType.POST,
                8
        );

        //commentRepository.save(comment);
//        System.out.println(commentRepository.findAll());
        commentRepository.deleteById(1);
//        postRepository.save(post);
//        postRepository.deleteById(6);

        Vote vote = new Vote(true, LocalDateTime.now(), EntityType.POST, 8, 2);
        VoteRepository voteRepository = new VoteRepository();
//        voteRepository.save(vote);
        voteRepository.deleteById(1);

        MeniuService meniuService = new MeniuService(commentService, postService, userService, voteService, scanner, userUI);
        meniuService.displayLoginMeniu();


    }
}
