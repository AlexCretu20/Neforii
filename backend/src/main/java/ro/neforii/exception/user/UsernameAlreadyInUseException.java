package ro.neforii.exception.user;

public class UsernameAlreadyInUseException extends RuntimeException {
    public UsernameAlreadyInUseException(String username) {
        super("The specified username: " + username + " is already in use!");
    }
}
