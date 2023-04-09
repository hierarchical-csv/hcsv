package io.github.hierarchicalcsv.core.exception.csv;

import com.opencsv.exceptions.CsvException;

public class ParentBeanNotFoundException extends CsvException {

    public ParentBeanNotFoundException(long lineNumber, String[] line, String message) {
        super(message);
        super.setLine(line);
        super.setLineNumber(lineNumber);
    }

}
