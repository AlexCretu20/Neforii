package Alexandra;

import model.User;
import service.PostService;

public class Test {
    public static void main(String[] args) {


//        Post ps = new Post(2,"sdfghjkhgf", LocalDateTime.now(),false,null);
//        System.out.println(ps);

        User user = new User("ana1234","xcvbj","ana@gmail.com","1234567","sdfghjkjhgfdcfvgbhjn tybunnhygtfyhujg");
//        Comment comment = new Comment(1,"salut ana", LocalDateTime.now(),LocalDateTime.now(),user);
//        Vote vote = new Vote(1,true, LocalDateTime.now(), EntityType.POST,1,1);

        //test service
        PostService postService = PostService.getInstance();

        postService.CreatePost(user,"Ana are mere");
        postService.CreatePost(user,"sdfghjhgfddfghjm");
        postService.CreatePost(user,"capsuni si mere ");
        postService.DisplayPosts();
        postService.UpdatePost(2, "bauu saluuut");
        postService.DeletePost(3);
//        postService.AddComment(1,comment);
//        postService.AddVote(1,vote);
//        postService.ExpandComments(1);
//        postService.DisplayPosts();


    }


}
