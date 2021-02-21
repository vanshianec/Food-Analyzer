package bg.sofia.uni.fmi.mjt.analyzer.exceptions;

public class FailedToStopException extends RuntimeException {

    public FailedToStopException(String message, Throwable cause) {
        super(message, cause);
    }
}
