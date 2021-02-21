package bg.sofia.uni.fmi.mjt.analyzer.exceptions;

public class FailedToStartException extends RuntimeException {

    public FailedToStartException(String message, Throwable cause) {
        super(message, cause);
    }
}
