package exception;

public class VoteNotOneTargetOnly extends RuntimeException {
    public VoteNotOneTargetOnly(String message) {
        super(message);
    }
}
