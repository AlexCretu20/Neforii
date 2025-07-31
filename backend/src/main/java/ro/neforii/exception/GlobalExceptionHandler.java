package ro.neforii.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ro.neforii.exception.user.EmailAlreadyInUseException;
import ro.neforii.exception.user.InvalidUserLoginException;
import ro.neforii.exception.user.UserNotFoundException;
import ro.neforii.exception.user.UsernameAlreadyInUseException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidation(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.badRequest().body(errors);
    }

    // USER
    //returnam 404 daca user nu e gasit
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity
                .status(404)
                .body(ex.getMessage());
    }
    //returnam 409(conflict pe variabile unice) daca username-ul sau email-ul e deja folosit
    @ExceptionHandler({UsernameAlreadyInUseException.class, EmailAlreadyInUseException.class})
    public ResponseEntity<String> handleConflict(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }

    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<String> handlePostNotFound(PostNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(InvalidUserLoginException.class)
    public ResponseEntity<String> handleUserLogin(InvalidUserLoginException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ex.getMessage());
    }
}
