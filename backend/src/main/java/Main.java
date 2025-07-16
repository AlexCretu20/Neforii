import model.Comment;
import model.User;
import model.Vote;
import repository.*;
import service.*;
import ui.UserUI;
import utils.logger.*;
import validation.UserValidator;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ICrudRepository<User> userRepository = new UserRepository();
        ICrudRepository<Vote> voteRepository = new VoteRepository();
        ICrudRepository<Comment> commentRepository = new CommentRepository(userRepository);
        PostRepository postRepository = new PostRepository(userRepository);

        UserService userService = new UserService((UserRepository) userRepository);
        PostService postService = PostService.getInstance(postRepository, (VoteRepository) voteRepository, (CommentRepository) commentRepository);

        CommentService commentService = CommentService.getInstance();
        VoteService voteService = VoteService.getInstance();
        Scanner scanner = new Scanner(System.in);
        UserValidator userValidator = new UserValidator();
        UserUI userUI = new UserUI(scanner, userService, userValidator);

        LoggerManager.getInstance().register(new ConsoleLogger(LoggerType.WARNING));
        LoggerManager.getInstance().register(new FileLogger(LoggerType.DEBUG, ".", "info.log" ));





        Logger.log(LoggerType.FATAL, "User created");











        MeniuService meniuService = new MeniuService(commentService, postService, userService, voteService, scanner, userUI);
        meniuService.displayLoginMeniu();
    }
}