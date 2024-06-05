package at.along.com.exception;

public class BusinessException extends RuntimeException{

    /**
     * 返回msg
     */
    private String message;

    public BusinessException( String message) {
        super(message);

    }
}
