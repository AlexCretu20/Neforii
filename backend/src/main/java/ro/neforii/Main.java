package ro.neforii;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ro.neforii.utils.logger.ConsoleLogger;
import ro.neforii.utils.logger.FileLogger;
import ro.neforii.utils.logger.LoggerManager;
import ro.neforii.utils.logger.LoggerType;

@SpringBootApplication //Component Scan
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);

        LoggerManager.getInstance().register(new ConsoleLogger(LoggerType.WARNING));
        LoggerManager.getInstance().register(new FileLogger(LoggerType.DEBUG, ".", "info.log"));

////        EXEMPLU CUM E FOLOSIT LOG-UL: Logger.log(LoggerType.FATAL, "User created");

//        UserService userService = new UserService();
//        PostService postService = new PostService((PostRepository) postRepository, (VoteRepository) voteRepository, (CommentRepository) commentRepository);
//        CommentService commentService = new CommentService((CommentRepository) commentRepository, (UserRepository) userRepository, (PostRepository) postRepository, (VoteRepository) voteRepository);
//        VoteService voteService = new VoteService(postRepository, (VoteRepository) voteRepository, (CommentRepository) commentRepository);

//        Scanner scanner = new Scanner(System.in);
//        UserValidator userValidator = new UserValidator();
//        UserUI userUI = new UserUI(scanner, (IUserService) userService, userValidator);
//        CommentUI commentUI = new CommentUI(scanner, commentService);
//        PostUI postUI = new PostUI(scanner, postService,commentUI);
//        VoteUI voteUI = new VoteUI(scanner, voteService);
//        MenuUI menuUI = new MenuUI(scanner);
//
//
//        MeniuService meniuService = new MeniuService(commentService, postService, userService, voteService, scanner, userUI, postUI, commentUI, voteUI, menuUI);
//        meniuService.displayLoginMeniu();
    }
}