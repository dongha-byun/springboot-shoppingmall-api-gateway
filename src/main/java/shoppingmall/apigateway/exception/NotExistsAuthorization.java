package shoppingmall.apigateway.exception;

public class NotExistsAuthorization extends RuntimeException{
    private static final String MESSAGE = "인증 정보가 존재하지 않습니다.";

    public NotExistsAuthorization() {
        this(MESSAGE);
    }

    public NotExistsAuthorization(Throwable cause) {
        this(MESSAGE, cause);
    }

    public NotExistsAuthorization(String message) {
        super(message);
    }

    public NotExistsAuthorization(String message, Throwable cause) {
        super(message, cause);
    }
}
