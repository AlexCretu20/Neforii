package ro.neforii.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND) // sa stie ce sa returneze controllerul
public class VoteNotFoundException extends RuntimeException {
    public VoteNotFoundException(int id) {
        super("Vote with ID " + id + " not found.");
    }
}
