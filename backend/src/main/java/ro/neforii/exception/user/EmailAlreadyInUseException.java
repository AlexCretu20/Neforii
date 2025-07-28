package ro.neforii.exception.user;

public class EmailAlreadyInUseException extends RuntimeException {
    public EmailAlreadyInUseException(String email) {
        super("The specified email: " + email + " is already in use!");
    }
}
