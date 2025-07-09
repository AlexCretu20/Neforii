import service.*;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        UserService userService = UserService.getInstance();
        PostService postService = PostService.getInstance();
        CommentService commentService = CommentService.getInstance();
        VoteService voteService = VoteService.getInstance();
        Scanner scanner = new Scanner(System.in);
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

        MeniuService meniuService = new MeniuService(commentService, postService, userService, voteService, scanner);
        meniuService.displayLoginMeniu();


    }
}
