import client.CommentClient;
import client.PostClient;
import client.UserClient;

public class MainMenu {
    private static final String BASE_URL = "http://13.53.190.111:8080";//url backend
    private static final UserClient userClient = new UserClient(BASE_URL + "/users");
    private static final PostClient postClient = new PostClient(BASE_URL + "/posts");
    private static final CommentClient commentClient = new CommentClient(BASE_URL + "/comments");

    public static void main(String[] args) {
    }
}
