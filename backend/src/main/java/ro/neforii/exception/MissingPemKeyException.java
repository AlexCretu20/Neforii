package ro.neforii.exception;

public class MissingPemKeyException extends RuntimeException {
    public MissingPemKeyException(String message) {
        super(message);
    }
}
