package bg.sofia.uni.fmi.mjt.analyzer.exceptions;

public class FoodServiceException extends Exception {

    public FoodServiceException(String message) {
        super(message);
    }

    public FoodServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
