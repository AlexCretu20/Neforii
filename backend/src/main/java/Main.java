import repository.UserRepository;
import service.*;
import ui.UserUI;
import validation.UserValidator;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        UserRepository userRepository = new UserRepository();
        UserService userService = new UserService(userRepository);
        PostService postService = PostService.getInstance();
        CommentService commentService = CommentService.getInstance();
        VoteService voteService = VoteService.getInstance();
        Scanner scanner = new Scanner(System.in);
        UserValidator userValidator = new UserValidator();
        UserUI userUI = new UserUI(scanner, userService, userValidator);

        MeniuService meniuService = new MeniuService(commentService, postService, userService, voteService, scanner, userUI);
        meniuService.displayLoginMeniu();
    }
}