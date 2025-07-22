package alexandru;

import ro.neforii.model.Vote;
import ro.neforii.repository.CommentRepository;
import ro.neforii.repository.PostRepository;
import ro.neforii.repository.UserRepository;
import ro.neforii.repository.VoteRepository;
import ro.neforii.service.PostService;
import ro.neforii.service.UserService;

import java.time.LocalDateTime;

public class TestAlex {
    public static void main(String[] args) {
//        LoggerManager.getInstance().register(new ConsoleLogger(LoggerType.INFO));
//        LoggerManager.getInstance().register(new FileLogger(LoggerType.INFO, ".", "info.log" ));
//        Logger.log(LoggerType.WARNING, "Build success");
        UserRepository userRepository = new UserRepository();
        UserService userService = new UserService(userRepository);

//        User user = new User("bogdan", "bogdan@gmail.com", "parola12312", "0772204444", "sadsa" );
//        userService.registerUser(user);
//
        PostRepository postRepository = new PostRepository(userRepository);
        VoteRepository voteRepository = new VoteRepository();
        CommentRepository commentRepository = new CommentRepository(userRepository);

        PostService postService = new PostService(postRepository, voteRepository, commentRepository);
//        postService.CreatePost(userRepository.findById(1).get(), "This is a test post for Alex");

        Vote vote = new Vote(true, LocalDateTime.now(), 1, null, 1);
//        voteRepository.save(vote);
        voteRepository.deleteById(1);
        System.out.println(voteRepository.findAll());
    }
}
