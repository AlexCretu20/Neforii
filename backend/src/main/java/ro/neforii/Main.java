package ro.neforii;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ro.neforii.model.Comment;
import ro.neforii.model.Post;
import ro.neforii.model.User;
import ro.neforii.model.Vote;
import ro.neforii.repository.*;
import ro.neforii.service.*;
import ro.neforii.ui.*;
import ro.neforii.utils.logger.ConsoleLogger;
import ro.neforii.utils.logger.FileLogger;
import ro.neforii.utils.logger.LoggerManager;
import ro.neforii.utils.logger.LoggerType;
import ro.neforii.validation.UserValidator;

import java.util.Scanner;

@SpringBootApplication //Component Scan
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);

         IuserRepository = new UserRepository();
        ICrudRepository<Vote> voteRepository = new VoteRepository();
        ICrudRepository<Comment> commentRepository = new CommentRepository(userRepository);
        ICrudRepository<Post> postRepository = new PostRepository(userRepository);

        UserService userService = new UserService((UserRepository) userRepository);
        PostService postService = new PostService((PostRepository) postRepository, (VoteRepository) voteRepository, (CommentRepository) commentRepository);
        CommentService commentService = new CommentService((CommentRepository) commentRepository, (UserRepository) userRepository, (PostRepository) postRepository, (VoteRepository) voteRepository);
        VoteService voteService = new VoteService((PostRepository) postRepository, (VoteRepository) voteRepository, (CommentRepository) commentRepository);
        Scanner scanner = new Scanner(System.in);
        UserValidator userValidator = new UserValidator();
        UserUI userUI = new UserUI(scanner, userService, userValidator);
        CommentUI commentUI = new CommentUI(scanner, commentService);
        PostUI postUI = new PostUI(scanner, postService,commentUI);
        VoteUI voteUI = new VoteUI(scanner, voteService);
        MenuUI menuUI = new MenuUI(scanner);

        LoggerManager.getInstance().register(new ConsoleLogger(LoggerType.WARNING));
        LoggerManager.getInstance().register(new FileLogger(LoggerType.DEBUG, ".", "info.log"));

//        EXEMPLU CUM E FOLOSIT LOG-UL: Logger.log(LoggerType.FATAL, "User created");

        MeniuService meniuService = new MeniuService(commentService, postService, userService, voteService, scanner, userUI, postUI, commentUI, voteUI, menuUI);
        meniuService.displayLoginMeniu();
    }
}