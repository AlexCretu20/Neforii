import client.UserClient;
import models.ApiResult;
import models.user.UserRegisterRequestDto;

public class MainMenu {
    private static final String BASE_URL = "http://13.53.190.111:8080/users";//url backend
    private static final UserClient userClient = new UserClient(BASE_URL);

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
    }
}
