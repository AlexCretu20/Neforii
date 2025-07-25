package ro.neforii.exception;

public class TitleAlreadyInUseException extends RuntimeException {
    public TitleAlreadyInUseException(String message) {
        super(message);
    }
}

