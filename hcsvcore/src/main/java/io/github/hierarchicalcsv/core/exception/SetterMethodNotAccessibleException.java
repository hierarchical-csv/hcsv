package io.github.hierarchicalcsv.core.exception;

public class SetterMethodNotAccessibleException extends MethodNotAccessibleException {

    public SetterMethodNotAccessibleException(String message) {
        super(message);
    }

    public SetterMethodNotAccessibleException(String message, Throwable cause) {
        super(message, cause);
    }
}
