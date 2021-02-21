package bg.sofia.uni.fmi.mjt.analyzer.exceptions;

public class FoodApiException extends FoodServiceException {

    public FoodApiException(String message) {
        super(message);
    }

    public FoodApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
