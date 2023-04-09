package io.github.hierarchicalcsv.core.bean;

import com.opencsv.bean.exceptionhandler.CsvExceptionHandler;
import com.opencsv.bean.util.OrderedObject;
import com.opencsv.exceptions.CsvException;
import io.github.hierarchicalcsv.core.exception.csv.UnknownBeanTypeException;
import io.github.hierarchicalcsv.core.model.CsvBeanType;
import io.github.hierarchicalcsv.core.model.ProcessCsvLineDecorator;
import io.github.hierarchicalcsv.core.model.ProcessCsvLineResult;
import io.github.hierarchicalcsv.core.model.UnknownBean;
import io.github.hierarchicalcsv.core.util.HCSVErrorMessageUtils;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * This class is responsible processing a Csv line and converts it to
 * a bean
 */
public class CsvLineToBeanProcessor {

    /**
     * A reference to the global properties factory inherited from the reader
     */
    private final CsvBeanPropertiesFactory beanPropertiesFactory;

    /**
     * A reference to the global Exception Handler inherited from the reader
     */
    private final CsvExceptionHandler exceptionHandler;

    /**
     * Whether to ignore unknown bean type. Causing {@code exceptionHandler.handleException()}
     * to be called if this attribute is {@literal true} and the bean's type is unknown
     */
    private final boolean ignoreUnknownBeanType;

    private final ResourceBundle resourceBundle;

    public CsvLineToBeanProcessor(CsvBeanPropertiesFactory beanPropertiesFactory, CsvExceptionHandler exceptionHandler, boolean ignoreUnknownBeanType, ResourceBundle resourceBundle) {
        this.beanPropertiesFactory = beanPropertiesFactory;
        this.exceptionHandler = exceptionHandler;
        this.ignoreUnknownBeanType = ignoreUnknownBeanType;
        this.resourceBundle = resourceBundle;
    }

    /**
     * Processes a line and returns an encapsulated result.
     *
     * @param lineNumber line's number
     * @param line array representing the line
     * @return The result of the processing
     * @throws CsvException If the {@link ProcessCsvLineDecorator} throws an exception
     *      if a CSV exception in original OpenCSV {@link com.opencsv.bean.concurrent.ProcessCsvLine}
     *      is thrown by the {@code exceptionHandler}. Or {@link UnknownBeanTypeException} if
     *      {@code ignoreUnknownBeanType} is {@literal false} and {@code exceptionHandler} rethrows
     *      the exception
     */
    public ProcessCsvLineResult<?> processLine(long lineNumber, String[] line) throws CsvException {
        CsvBeanType<?> beanType = getBeanType(line);
        if(beanType == null) {
            return handleUnknownBean(lineNumber, line);
        } else {
            return handleBean(lineNumber, beanType, line);
        }
    }

    /**
     * Takes an array representing a Csv Line as input and returns the
     * corresponding bean type. Or null if the bean type is unknown.
     *
     * @param line line's array
     * @return Bean type, or null if not known
     */
    private CsvBeanType<?> getBeanType(String[] line) {
        for(var entry: beanPropertiesFactory.getPositionToCodeMap().entrySet()) {
            if(entry.getKey() >= line.length) {
                break;
            }
            for(var propList: entry.getValue()) {
                if(propList.getCodeValue().equals(line[propList.getCodePosition()])) {
                    return propList.getBeanType();
                }
            }
        }
        return null;
    }

    /**
     * Handles the Unknown bean type. Returns the encapsulated {@link UnknownBean}
     * and possibly throws {@link UnknownBeanTypeException}.
     *
     * @param lineNumber The line number
     * @param line The array representing CSV line
     * @return Encapsulated {@link UnknownBean}
     * @throws CsvException Potentially a {@link UnknownBeanTypeException}
     */
    private ProcessCsvLineResult<UnknownBean> handleUnknownBean(long lineNumber, String[] line) throws CsvException {
        var processCsvLineResult = new ProcessCsvLineResult<>(UnknownBean.getNewInstance());
        if(!ignoreUnknownBeanType) {
            CsvException csvException = new UnknownBeanTypeException(lineNumber, line, MessageFormat.format(
                    resourceBundle.getString(HCSVErrorMessageUtils.UNKNOWN_BEAN_TYPE),
                    lineNumber
            ));
            processCsvLineResult.getThrownExceptionsQueue().add(new OrderedObject<>(0, csvException));
            exceptionHandler.handleException(csvException);
        }
        return processCsvLineResult;
    }

    /**
     * Convert the array line to the corresponding bean and returns the encapsulated result.
     *
     * @param lineNumber The line number
     * @param beanType The target bean type
     * @param line The array representing CSV line
     * @return The encapsulated result
     * @param <T> Any bean with {@link io.github.hierarchicalcsv.core.annotation.HCSVBean} annotation
     */
    private <T> ProcessCsvLineResult<T> handleBean(long lineNumber, CsvBeanType<T> beanType, String[] line) {
        var builder = new ProcessCsvLineDecorator.Builder<T>(beanType.getType())
                .setLineNumber(lineNumber)
                .setFilter(beanType.getFilter())
                .setVerifiers(beanType.getVerifiers())
                .setExceptionHandler(beanType.getExceptionHandler() != null?
                        beanType.getExceptionHandler(): exceptionHandler);
        return builder.buildAndRun(line);
    }

}
