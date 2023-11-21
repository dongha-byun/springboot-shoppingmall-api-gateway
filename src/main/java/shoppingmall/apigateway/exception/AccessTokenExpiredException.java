package shoppingmall.apigateway.exception;

public class AccessTokenExpiredException extends RuntimeException{
    private static final String DEFAULT_MESSAGE = "인증 정보가 만료됐습니다.";

    public AccessTokenExpiredException() {
        this(DEFAULT_MESSAGE);
    }

    public AccessTokenExpiredException(Throwable cause) {
        this(DEFAULT_MESSAGE, cause);
    }

    public AccessTokenExpiredException(String message) {
        super(message);
    }

    public AccessTokenExpiredException(String message, Throwable cause) {
        super(message, cause);
    }
}
