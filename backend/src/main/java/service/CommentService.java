package service;

import model.Comment;
import model.EntityType;
import model.User;
import model.Vote;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommentService {
    private static CommentService instance;
    //count for first available id of comment
    private static int cnt = 0;
    private List<Comment> comments = new ArrayList<>();

    private CommentService() {}

    public static CommentService getInstance() {
        if (instance == null) {
            instance = new CommentService();
        }
        return instance;
    }

    public static int getCnt(){
        return cnt;
    }

    public void increaseCnt(){
        cnt ++;
    }
    public void initService(){
        comments = new ArrayList<>();
    }

    public List<Comment> getComments() {
        return comments;
    }
    public boolean isEmpty(Optional<Comment> comment){
        return comment.isEmpty();
    }
    //used for linking comment to comment or comment to post, by using entityType
    public void createComment(String text, User user, EntityType entityType, int entityId){
        LocalDateTime currentTime = LocalDateTime.now();
        List<Comment> replies = new ArrayList<>();
        List<Vote> votes = new ArrayList<>();
        Comment comment = new Comment(cnt, text, currentTime, currentTime, user, entityType, entityId, replies, votes);

        if(entityType == EntityType.POST){
            //add the new comment to the list of comments
            comments.add(comment);
            System.out.println("Comment added to post.\n");
        }
        else if(entityType == EntityType.COMMENT){
            Optional<Comment> commentOptional = getComment(entityId);
            if(isEmpty(commentOptional)){
                System.out.println("The entered comment id does not exist.\n");
            }
            else{
                Comment parentComment = commentOptional.get();
                parentComment.getReplies().add(comment);
                System.out.println("Reply added to comment.\n");
            }
        }
        //next id
        increaseCnt();
        System.out.println("The comment has succesfully been added!\n");
    }

    public Optional<Comment> getComment(int id){
        for(Comment c : comments){
            if(c.getId() == id){
                return Optional.of(c);
            }
        }
        //if the comment with this id doesnt exist, return Optional.empty
        return Optional.empty();
    }

    public void updateComment(int id, String updateText){
        Optional<Comment> commentOptional = getComment(id);
        if(isEmpty(commentOptional)){
            System.out.println("The entered comment id does not exist.\n");
        }
        else {
            Comment comment = commentOptional.get();

            LocalDateTime currentTime = LocalDateTime.now();
            comment.setUpdatedAt(currentTime);
            comment.setText(updateText);

            System.out.println("The comment has been edited succesfully.\n");
        }
    }

    public void deleteComment(int id){
        Optional<Comment> commentOptional = getComment(id);
        if(isEmpty(commentOptional)){
            System.out.println("The entered comment id does not exist.\n");
        }
        else {
            Comment comment = commentOptional.get();

            comments.remove(comment);
            System.out.println("The comment has been deleted succesfully");
        }
    }

    public void showComments(){
        for(Comment c : comments){
            System.out.println(c);
        }
    }

    public void showReplies(int id){
        Optional<Comment> commentOptional = getComment(id);
        if(isEmpty(commentOptional)){
            System.out.println("The entered comment id does not exist.\n");
        }
        else{
            Comment comment = commentOptional.get();
            List<Comment> replies = comment.getReplies();
            System.out.println("The list of replies:\n");
            for(Comment r : replies){
                System.out.println(r);
            }
        }
    }
}
