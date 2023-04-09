package io.github.hierarchicalcsv.core.exception.csv;

import com.opencsv.exceptions.CsvException;

public class CausedCsvException extends CsvException {

    public CausedCsvException(long lineNumber, String[] line, Throwable cause) {
        super.initCause(cause);
        super.setLineNumber(lineNumber);
        super.setLine(line);
    }

    public CausedCsvException(long lineNumber, String[] line, String message, Throwable cause) {
        super(message);
        super.setLineNumber(lineNumber);
        super.setLine(line);
        super.initCause(cause);
    }
}
