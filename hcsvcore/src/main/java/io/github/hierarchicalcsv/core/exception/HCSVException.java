package io.github.hierarchicalcsv.core.exception;

public class HCSVException extends RuntimeException {

    public HCSVException() {
    }

    public HCSVException(String message) {
        super(message);
    }

    public HCSVException(String message, Throwable cause) {
        super(message, cause);
    }

    public HCSVException(Throwable cause) {
        super(cause);
    }

    public HCSVException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
