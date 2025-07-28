package models;

//aceasta clasa este folosita pentru returnarea raspunsului de la endpoint
//avem ca parametri succes, message, responseBody(gen responseDto daca e kk)
public class ApiResult {
    private final boolean success;
    private final String message;
    private final String responseBody;

    public ApiResult(boolean success, String message, String responseBody) {
        this.success = success;
        this.message = message;
        this.responseBody = responseBody;
    }

    public boolean getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
