package io.github.hierarchicalcsv.core.exception;

public class GetterMethodNotAccessibleException extends MethodNotAccessibleException {

    public GetterMethodNotAccessibleException(String message) {
        super(message);
    }

    public GetterMethodNotAccessibleException(String message, Throwable cause) {
        super(message, cause);
    }
}
