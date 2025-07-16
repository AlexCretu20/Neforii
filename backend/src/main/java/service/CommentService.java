package service;

import exception.CommentNotFoundException;
import model.Comment;
import model.User;
import repository.CommentRepository;
import repository.UserRepository;
import repository.VoteRepository;
import utils.logger.Logger;
import utils.logger.LoggerType;

import java.util.List;
import java.util.Optional;

public class CommentService implements IVotable {
    //private static CommentService instance;
    private CommentRepository commentRepo;
    private UserRepository userRepo;
    private VoteRepository voteRepo;

    public CommentService(CommentRepository commentRepo, UserRepository userRepo, VoteRepository voteRepo) {
        this.commentRepo = commentRepo;
        this.userRepo = userRepo;
        this.voteRepo = voteRepo;
    }

//    public synchronized static CommentService getInstance() {
//        if (instance == null) {
//            instance = new CommentService();
//        }
//        return instance;
//    }


    public List<Comment> getComments() {
        return commentRepo.findAll();
    }

    public Comment getComment(int id) {
        Optional<Comment> comment = commentRepo.findById(id);
        if (comment.isEmpty()) {
            throw new CommentNotFoundException("The comment with id " + id + "does not exist");
        }
        return comment.get();
    }

    public void createComment(String text, User user, Integer postId, Integer parentCommentId) {
        Comment comment = new Comment(text, user, postId, parentCommentId);
        commentRepo.save(comment);
        System.out.println("The comment has been successfully added!\n");
    }

    public void updateComment(int id, Comment comment) {
        Optional<Comment> commentOptional = commentRepo.findById(id);
        if (commentOptional.isEmpty()) {
            throw new CommentNotFoundException("The comment with id " + id + "does not exist");
        }
        Comment commentToUpdate = commentOptional.get();
        try {
            commentRepo.update(commentToUpdate);
            Logger.log(LoggerType.INFO, "Comment upgrated successfully!");
        } catch (Exception e) {
            Logger.log(LoggerType.FATAL, "Comment could not update!");
        }
    }

    public void deleteComment(int id) {
        Optional<Comment> commentOptional = commentRepo.findById(id);
        if (commentOptional.isEmpty()) {
            throw new CommentNotFoundException("The comment with id " + id + "does not exist");
        }
        try {
            commentRepo.deleteById(id);
            Logger.log(LoggerType.INFO, "Comment deleted successfully!");
        } catch (Exception e) {
            Logger.log(LoggerType.FATAL, "Comment could not delete!");
        }
    }

    public void showReplies(int id) {
        commentRepo.findByPostId(id);
        for (Comment comment : commentRepo.findByPostId(id)) {
            System.out.println(comment);
        }

    }


    public int displayUpvotes(int id) {
        return voteRepo.countVotesByCommentId( id, true);
    }

    public int displayDownvotes(int id) {
        return voteRepo.countVotesByCommentId( id, false);
    }
}
