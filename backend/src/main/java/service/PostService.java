package service;

import model.Comment;
import model.Post;
import model.User;
import model.Vote;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PostService {

   private static PostService instance;
   private static  int counter;
   private Map<Integer, Post> posts = new HashMap<>();
   private PostService() {
       counter = 0;
   };

    public static PostService getInstance() {
        if (instance == null){
            instance = new PostService();
        }
        return instance;
    }

    public Integer GetPostById(Post post) {
        for (Map.Entry<Integer, Post> entry : posts.entrySet()) {
            if (entry.getValue().equals(post)) {
                return entry.getKey();
            }
        }
        System.out.println("The post doesn't exist. ");
        return null;
    }

    public void CreatePost(User user, String text){
        counter ++;
        Post post = new Post(counter, text, LocalDateTime.now(),false, user);
        posts.put(counter, post);
        System.out.println("The post was created.");
    }

    public void UpdatePost(int id, String text){
        for (Integer key: posts.keySet()){
            if (key == id){
                posts.get(key).setText(text);
            }

        }
        System.out.println("The post was updated");

    }

    public void DeletePost(int id){
       posts.remove(id);
       System.out.println("The post was deleted.");
    }

    public void AddComment(int id, Comment comment){
        for (Integer key: posts.keySet()){
            if (key == id){
                posts.get(key).setComments(comment);
            }

        }
        System.out.println("The comment was added.");
    }

    public void AddVote(int id, Vote vote){
        for (Integer key: posts.keySet()){
            if (key == id){
                posts.get(key).setVotes(vote);
            }

        }
        System.out.println("The vote was added.");
    }

    public void ExpandComments(int id){
        ArrayList<Comment> comments = new ArrayList<>();
        for (Integer key: posts.keySet()){
            if (key == id){
                comments = posts.get(key).getComments();
            }

        }

        System.out.println("Comments");
        for (Comment cm: comments){
            System.out.println(cm.getText());
        }

    }

    public void DisplayPosts(){
        for (Map.Entry<Integer, Post> entry : posts.entrySet()) {
            System.out.println(entry.getValue());
        }

    }
}
