package ro.neforii.service;

import ro.neforii.model.Post;
import ro.neforii.model.User;

import java.util.List;

public interface IPostService {
    public List<Post> getAllPosts();

    public Post createPost(User user, String text);

    public boolean updatePost(int id, String text);

    public boolean deletePost(int id);

    public void updateAwardsForAllPosts();

    public void updateAwardsForOnePost(int id);

    public int displayUpvotes(int id);

    public int displayDownvotes(int id);

    public boolean updateAward(Post post);

    public List<Post> findAllPostsByUser(int userId);
}
