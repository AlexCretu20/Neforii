import client.CommentClient;
import client.PostClient;
import client.UserClient;

public class MainMenu {
    private static final String BASE_URL = "http://13.53.190.111:8080/users";//url backend
    private static final String POST_BASE_URL = "http://13.53.190.111:8080";
    private static final String COMMENT_BASE_URL = "http://13.53.190.111:8080/posts/1";
    private static final UserClient userClient = new UserClient(BASE_URL);
    private static final PostClient postClient = new PostClient(POST_BASE_URL);
    private static final CommentClient commentClient = new CommentClient(COMMENT_BASE_URL);


    public static void main(String[] args) {

//        UserRegisterRequestDto request = new UserRegisterRequestDto(
//                "adrianTare",
//                "adrian@tare.com",
//                "donthackme123",
//                "077777779",
//                "acesta este un test si trebuie tratat ca atare"
//        );
//        ApiResult result = userClient.register(request);
//        UserLoginRequestDto requestLogin = new UserLoginRequestDto(
//                "adrian@tare.com",
//                "donthackme123"
//        );
//        ApiResult res = userClient.login(requestLogin);
//        UserView.displayUserResult(res);
//        //este doar de test treaba asta
////        UserRegisterRequestDto request = new UserRegisterRequestDto(
////                "adrianTare",
////                "adrian@tare.com",
////                "donthackme123",
////                "077777779",
////                "acesta este un test si trebuie tratat ca atare"
////        );
////
////        //chestiile astea o sa fie afisate frumos cu ajutorul a ce facem in views
////        ApiResult result = userClient.register(request);
////        System.out.println("Success: " + result.getSuccess());
////        System.out.println("Message: " + result.getMessage());
////        System.out.println("Body: " + result.getResponseBody());
//
//        String username = "cristianoRONALDOOO";
//        ApiResult resultFind = userClient.getUserByUsername(username);
//        UserView.displayUserResult(resultFind);
//
//        UserLoginRequestDto requestFail = new UserLoginRequestDto(
//                "nu.ar.trebui@sa.mearga.com",
//                "donthackme123"
//        );
//        ApiResult resultLoginFail = userClient.login(requestFail);
//        UserView.displayUserResult(resultLoginFail);
//
//        UserLoginRequestDto requestGood = new UserLoginRequestDto(
//                "a@gmail.com",
//                "123456789"
//        );
//        ApiResult resultLoginSuccess = userClient.login(requestGood);
//        UserView.displayUserResult(resultLoginSuccess);
//        //chestiile astea o sa fie afisate frumos cu ajutorul a ce facem in views
//
//        PostRequestDto postRequest = new PostRequestDto(
//                "Titlu test kufoue fgyuiuhgbh vghu8iuhhjiokj vgyu7uhghuiijnh ghu8iuhhjioijnb ",
//                "Continutul acestui post este doar un test.gggggggggggg gggggggggggggggggggggg ggggggggggggggg ggggggggggggggggggggggggggggg ggggggggggggggggggg gggggggggggggggggg gggggggggggggggggggggg",
//                "",
//                "user1",
//                1
//        );
//
//        ApiResult postResult = postClient.newPost(postRequest);
//        System.out.println("\nPost creation:");
//        System.out.println("Success: " + postResult.getSuccess());
//        System.out.println("Message: " + postResult.getMessage());
//        System.out.println("Body: " + postResult.getResponseBody());
//
//        PostView.displayPostResult(postResult);
//
////        PostRequestDto postRequest2 = new PostRequestDto(
////                "Titlu test",
////                "Continut update 22",
////                "",
////                "user1",
////                1
////        );
////        ApiResult postResult2 = postClient.updatePost(7,postRequest2);
////        System.out.println("\nPost update:");
////        System.out.println("Success: " + postResult2.getSuccess());
////        System.out.println("Message: " + postResult2.getMessage());
////        System.out.println("Body: " + postResult2.getResponseBody());
////        PostView.displayPostResult(postResult2);
//
////        ApiResult postResult3 = postClient.getPostById(7);
////        System.out.println("\nPost get post by id:");
////        System.out.println("Success: " + postResult3.getSuccess());
////        System.out.println("Message: " + postResult3.getMessage());
////        System.out.println("Body: " + postResult3.getResponseBody());
//        //Comments test
////        int testCommentId = 1;
////        ApiResult commentResult = commentClient.getCommentById(testCommentId);
////        System.out.println("\nComment get by id:");
////        CommentView.displayCommentResult(commentResult);
////
////        int testPostId = 1; // schimbă cu id-ul unui post real
////        ApiResult allCommentsResult = commentClient.getCommentsByPostId(testPostId);
////        System.out.println("\nComments for post id " + testPostId + ":");
////        CommentView.displayCommentResult(allCommentsResult);
////        int postId = 1;
////        String author = "andrei";
////
////        CommentRequestDto commentRequest = new CommentRequestDto(
////                "Salut! Acesta este un comentariu de test.",
////                author,
////                null
////        );
////
////        ApiResult addCommentResult = commentClient.addComment(commentRequest);
////        System.out.println("\nAdd Comment:");
////        CommentView.displayCommentResult(addCommentResult);
////
////        ApiResult commentsResult = commentClient.getCommentsByPostId(postId);
////        System.out.println("\nComments for Post ID: " + postId);
////        CommentView.displayCommentsResult(commentsResult);
////
////// end comments test
//
//
//        ApiResult postResult4 = postClient.getAllPosts();
//        System.out.println("\nPost get all:");
//        System.out.println("Success: " + postResult4.getSuccess());
//        System.out.println("Message: " + postResult4.getMessage());
//        System.out.println("Body: " + postResult4.getResponseBody());
//
//        PostView.displayPostListResult(postResult4);
//
////
////        ApiResult postResult5 = postClient.deletePost(7);
////        System.out.println("\nPost delete:");
////        System.out.println("Success: " + postResult5.getSuccess());
////        System.out.println("Message: " + postResult5.getMessage());
////        System.out.println("Body: " + postResult5.getResponseBody());
    }


}
