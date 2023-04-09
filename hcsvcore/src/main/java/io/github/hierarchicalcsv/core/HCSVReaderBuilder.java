package io.github.hierarchicalcsv.core;

import com.opencsv.ICSVParser;
import com.opencsv.bean.exceptionhandler.CsvExceptionHandler;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import com.opencsv.processor.RowProcessor;
import com.opencsv.validators.LineValidator;
import com.opencsv.validators.RowValidator;
import io.github.hierarchicalcsv.core.exception.UnableToReadFileException;
import io.github.hierarchicalcsv.core.model.CsvBeanType;
import io.github.hierarchicalcsv.core.model.CsvLineProcessListener;
import io.github.hierarchicalcsv.core.util.HCSVErrorMessageUtils;
import io.github.hierarchicalcsv.core.util.HCSVUtils;
import org.apache.commons.lang3.ObjectUtils;

import java.io.IOException;
import java.io.Reader;
import java.text.MessageFormat;
import java.util.*;

public class HCSVReaderBuilder {

    /**
     * @see CsvExceptionHandler Exception Handler functional interface in the OpenCSV dependecy
     */
    protected CsvExceptionHandler exceptionHandler;

    protected CsvLineProcessListener lineProcessListener;

    protected final Set<CsvBeanType<?>> beanTypes;

    protected boolean ignoreUnknownBeanType;

    protected Reader reader;

    protected int skipLines;

    /**
     * @see ICSVParser Behavior Defenition interface in the OpenCSV dependecy
     */
    protected ICSVParser icsvParser;

    protected boolean keepCR;

    protected boolean verifyReader;

    protected CSVReaderNullFieldIndicator nullFieldIndicator;

    protected Locale errorLocale;

    /**
     * @see ICSVParser LineValidator in the OpenCSV dependecy
     */
    protected final List<LineValidator> lineValidators;

    /**
     * @see RowValidator RowValidator in the OpenCSV dependecy
     */
    protected final List<RowValidator> rowValidators;

    /**
     * @see RowProcessor RowProcessor in the OpenCSV dependecy
     */
    protected RowProcessor rowProcessor;

    public HCSVReaderBuilder(Reader reader) {
        this.lineValidators = new ArrayList<>();
        this.rowValidators = new ArrayList<>();
        this.reader = reader;
        this.beanTypes = new HashSet<>();
        this.errorLocale = Locale.getDefault();
    }

    public HCSVReaderBuilder withSkipLines(
            final int skipLines) {
        this.skipLines = Math.max(skipLines, 0);
        return this;
    }

    public HCSVReaderBuilder withCSVParser(
            final ICSVParser icsvParser) {
        this.icsvParser = icsvParser;
        return this;
    }

    public HCSVReaderBuilder withKeepCarriageReturn(boolean keepCR) {
        this.keepCR = keepCR;
        return this;
    }

    public HCSVReaderBuilder withVerifyReader(boolean verifyReader) {
        this.verifyReader = verifyReader;
        return this;
    }

    public HCSVReaderBuilder withFieldAsNull(CSVReaderNullFieldIndicator indicator) {
        this.nullFieldIndicator = indicator;
        return this;
    }


    public HCSVReaderBuilder withErrorLocale(Locale errorLocale) {
        this.errorLocale = ObjectUtils.defaultIfNull(errorLocale, Locale.getDefault());
        return this;
    }

    public HCSVReaderBuilder withLineValidator(LineValidator lineValidator) {
        lineValidators.add(lineValidator);
        return this;
    }

    public HCSVReaderBuilder withRowValidator(RowValidator rowValidator) {
        rowValidators.add(rowValidator);
        return this;
    }

    public HCSVReaderBuilder withRowProcessor(RowProcessor rowProcessor) {
        this.rowProcessor = rowProcessor;
        return this;
    }

    public HCSVReaderBuilder withExceptionHandler(CsvExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        return this;
    }

    public HCSVReaderBuilder withListener(CsvLineProcessListener csvLineProcessListener) {
        this.lineProcessListener = csvLineProcessListener;
        return this;
    }

    public HCSVReaderBuilder withBeanClass(Class<?> beanClass) {
        withBeanType(new CsvBeanType<>(beanClass));
        return this;
    }

    public HCSVReaderBuilder withBeanClasses(List<Class<?>> beanClasses) {
        beanClasses.forEach(beanClass -> withBeanType(new CsvBeanType<>(beanClass)));
        return this;
    }

    public HCSVReaderBuilder withBeanType(CsvBeanType<?> beanType) {
        this.beanTypes.add(beanType);
        return this;
    }

    public HCSVReaderBuilder withBeanTypes(List<CsvBeanType<?>> beanTypes) {
        this.beanTypes.addAll(beanTypes);
        return this;
    }

    public HCSVReaderBuilder withIgnoreUnknownBeanType(boolean ignoreUnknownBeanType) {
        this.ignoreUnknownBeanType = ignoreUnknownBeanType;
        return this;
    }

    public HCSVReader build() {
        try {
            return new HCSVReader(reader).initialize(this);
        } catch (IOException ex) {
            throw new UnableToReadFileException(
                    MessageFormat.format(
                            ResourceBundle.getBundle(HCSVUtils.ERROR_RESOURCE_BUNDLE_NAME, errorLocale)
                                    .getString(HCSVErrorMessageUtils.UNABLE_TO_READ_FILE_WITH_EXCEPTION)
                            , ex.getLocalizedMessage())
                    , ex);
        }
    }

}
