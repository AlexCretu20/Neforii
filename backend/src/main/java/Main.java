import model.Comment;
import model.Post;
import model.User;
import model.Vote;
import repository.*;
import service.*;
import ui.CommentUI;
import ui.PostUI;
import ui.UserUI;
import ui.VoteUI;
import utils.logger.ConsoleLogger;
import utils.logger.FileLogger;
import utils.logger.LoggerManager;
import utils.logger.LoggerType;
import validation.UserValidator;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ICrudRepository<User> userRepository = new UserRepository();
        ICrudRepository<Vote> voteRepository = new VoteRepository();
        ICrudRepository<Comment> commentRepository = new CommentRepository(userRepository);
        ICrudRepository<Post> postRepository = new PostRepository(userRepository);

        UserService userService = new UserService((UserRepository) userRepository);
        PostService postService = new PostService((PostRepository) postRepository, (VoteRepository) voteRepository, (CommentRepository) commentRepository);
        CommentService commentService = new CommentService((CommentRepository) commentRepository, (UserRepository) userRepository, (PostRepository) postRepository,(VoteRepository) voteRepository);
        VoteService voteService = new VoteService((PostRepository) postRepository, (VoteRepository) voteRepository, (CommentRepository) commentRepository);
        Scanner scanner = new Scanner(System.in);
        UserValidator userValidator = new UserValidator();
        UserUI userUI = new UserUI(scanner, userService, userValidator);
        PostUI postUI = new PostUI(scanner,postService);
        CommentUI commentUI = new CommentUI(scanner,commentService);
        VoteUI voteUI = new VoteUI(scanner, voteService);

        LoggerManager.getInstance().register(new ConsoleLogger(LoggerType.WARNING));
        LoggerManager.getInstance().register(new FileLogger(LoggerType.DEBUG, ".", "info.log" ));

//        EXEMPLU CUM E FOLOSIT LOG-UL: Logger.log(LoggerType.FATAL, "User created");

        MeniuService meniuService = new MeniuService(commentService, postService, userService, voteService, scanner, userUI, postUI, commentUI, voteUI);
        meniuService.displayLoginMeniu();
    }
}