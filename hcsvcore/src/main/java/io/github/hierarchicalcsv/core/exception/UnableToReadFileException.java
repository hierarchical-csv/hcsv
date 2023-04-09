package io.github.hierarchicalcsv.core.exception;

public class UnableToReadFileException extends HCSVException {

    public UnableToReadFileException(String message) {
        super(message);
    }

    public UnableToReadFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
