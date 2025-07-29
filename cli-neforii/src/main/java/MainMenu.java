import client.PostClient;
import client.UserClient;
import models.ApiResult;
import models.user.UserRegisterRequestDto;

public class MainMenu {
    private static final String BASE_URL= "http://13.53.190.111:8080/users";//url backend
    private static final String POST_BASE_URL = "http://13.53.190.111:8080";

    private static final UserClient userClient = new UserClient(BASE_URL);
    private static final PostClient postClient = new PostClient(POST_BASE_URL);

    public static void main(String[] args) {
        //este doar de test treaba asta
        UserRegisterRequestDto request = new UserRegisterRequestDto(
                "adrianTare",
                "adrian@tare.com",
                "donthackme123",
                "077777779",
                "acesta este un test si trebuie tratat ca atare"
        );

        //chestiile astea o sa fie afisate frumos cu ajutorul a ce facem in views
        ApiResult result = userClient.register(request);
        System.out.println("Success: " + result.getSuccess());
        System.out.println("Message: " + result.getMessage());
        System.out.println("Body: " + result.getResponseBody());

//        PostRequestDto postRequest = new PostRequestDto(
//                "Titlu test",
//                "Continutul acestui post este doar un test.",
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

//        PostRequestDto postRequest2 = new PostRequestDto(
//                "Titlu test",
//                "Continut update 22",
//                "",
//                "user1",
//                1
//        );
//        ApiResult postResult2 = postClient.updatePost(7,postRequest2);
//        System.out.println("\nPost update:");
//        System.out.println("Success: " + postResult2.getSuccess());
//        System.out.println("Message: " + postResult2.getMessage());
//        System.out.println("Body: " + postResult2.getResponseBody());

//        ApiResult postResult3 = postClient.getPostById(7);
//        System.out.println("\nPost get post by id:");
//        System.out.println("Success: " + postResult3.getSuccess());
//        System.out.println("Message: " + postResult3.getMessage());
//        System.out.println("Body: " + postResult3.getResponseBody());

        ApiResult postResult4 = postClient.getAllPosts();
        System.out.println("\nPost get all:");
        System.out.println("Success: " + postResult4.getSuccess());
        System.out.println("Message: " + postResult4.getMessage());
        System.out.println("Body: " + postResult4.getResponseBody());

        ApiResult postResult5 = postClient.deletePost(7);
        System.out.println("\nPost delete:");
        System.out.println("Success: " + postResult5.getSuccess());
        System.out.println("Message: " + postResult5.getMessage());
        System.out.println("Body: " + postResult5.getResponseBody());
    }
}
