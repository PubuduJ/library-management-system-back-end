package lk.ijse.dep9.dao.exception;

public class ConstraintViolationException extends RuntimeException {

    public ConstraintViolationException(String message, Throwable cause) {
        super(message, cause);
    }
}
