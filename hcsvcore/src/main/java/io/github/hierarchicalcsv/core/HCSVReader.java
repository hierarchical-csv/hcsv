package io.github.hierarchicalcsv.core;

/*
 Copyright 2005 Bytecode Pty Ltd.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.exceptionhandler.CsvExceptionHandler;
import com.opencsv.bean.exceptionhandler.ExceptionHandlerThrow;
import com.opencsv.bean.util.OrderedObject;
import com.opencsv.exceptions.CsvException;
import io.github.hierarchicalcsv.core.bean.CsvLineToBeanProcessor;
import io.github.hierarchicalcsv.core.bean.CsvTreeConstructor;
import io.github.hierarchicalcsv.core.exception.UnableToReadFileException;
import io.github.hierarchicalcsv.core.model.CsvBeanType;
import io.github.hierarchicalcsv.core.bean.CsvBeanPropertiesFactory;
import io.github.hierarchicalcsv.core.model.CsvLineProcessListener;
import io.github.hierarchicalcsv.core.model.EmptyBean;
import io.github.hierarchicalcsv.core.model.UnknownBean;
import io.github.hierarchicalcsv.core.util.HCSVErrorMessageUtils;
import io.github.hierarchicalcsv.core.util.HCSVUtils;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The Hierarchical CSV Reader
 */
public class HCSVReader implements AutoCloseable {

    /**
     * Whether the CSV reader has a next line
     */
    private boolean hasNext;

    /**
     * List of HCSV beans to be parsed by the reader
     */
    private CsvBeanType<?>[] beanTypes;

    /**
     * Factory used to create each bean's properties
     */
    private CsvBeanPropertiesFactory beanTypeFactory;

    /**
     * CSV line processor that decorates OpenCSV line processor
     */
    private CsvLineToBeanProcessor lineToBeanProcessor;

    /**
     * Strategy used to construct relationships between beans
     */
    private CsvTreeConstructor csvTreeConstructor;

    /**
     * OpenCSV builder
     */
    private CSVReaderBuilder csvReaderBuilder;

    /**
     * OpenCSV reader
     */
    private CSVReader csvReader;

    /**
     * Default exception handler (if not override for specific bean)
     */
    private CsvExceptionHandler exceptionHandler;

    /**
     * Listener before and after parsing
     */
    private CsvLineProcessListener lineProcessListener;

    /**
     * Whether to ignore lines of unknown bean types
     */
    private boolean ignoreUnknownBeanType;

    /**
     * Resource bundle used for language resources
     */
    private ResourceBundle resourceBundle;

    /**
     * Constructs an HCSVReader with empty parameters. Should be only used
     * from the Builder part
     *
     * @param reader An OpenCSV Reader
     */
    protected HCSVReader(Reader reader) {
        csvReaderBuilder = new CSVReaderBuilder(reader);
    }

    /**
     * Initializes the HCSVReader with supplied Builder's parameters
     *
     * @param builder The configured builder
     * @return The same Reader with configuration
     * @throws IOException In case of problem while reading the file
     */
    protected HCSVReader initialize(HCSVReaderBuilder builder) throws IOException {
        csvReaderBuilder.withSkipLines(builder.skipLines)
                .withCSVParser(builder.icsvParser)
                .withKeepCarriageReturn(builder.keepCR)
                .withVerifyReader(builder.verifyReader)
                .withFieldAsNull(builder.nullFieldIndicator)
                .withErrorLocale(builder.errorLocale)
                .withRowProcessor(builder.rowProcessor);
        resourceBundle = ResourceBundle.getBundle(HCSVUtils.ERROR_RESOURCE_BUNDLE_NAME, builder.errorLocale);
        builder.lineValidators.forEach(csvReaderBuilder::withLineValidator);
        builder.rowValidators.forEach(csvReaderBuilder::withRowValidator);
        csvReader = csvReaderBuilder.build();
        exceptionHandler = builder.exceptionHandler;
        if(exceptionHandler == null) {
            exceptionHandler = new ExceptionHandlerThrow();
        }
        lineProcessListener = builder.lineProcessListener;
        beanTypes = builder.beanTypes.toArray(new CsvBeanType[0]);
        ignoreUnknownBeanType = builder.ignoreUnknownBeanType;
        beanTypeFactory = new CsvBeanPropertiesFactory(beanTypes, resourceBundle);
        lineToBeanProcessor = new CsvLineToBeanProcessor(beanTypeFactory, exceptionHandler, ignoreUnknownBeanType, resourceBundle);
        csvTreeConstructor = new CsvTreeConstructor(beanTypeFactory, exceptionHandler, resourceBundle);
        csvReaderBuilder = null; // Destroy reader
        hasNext = csvReader.peek() != null;
        return this;
    }

    /**
     * @return Whether the Reader can read one more line
     */
    protected boolean hasNext() {
        return hasNext;
    }

    /**
     * Verifies if the Reader can read one more line. Throws {@link UnableToReadFileException}
     * if it's not the case
     */
    private void assertHasNext() {
        if(!hasNext()) {
            throw new UnableToReadFileException(resourceBundle.getString(HCSVErrorMessageUtils.UNABLE_TO_READ_FILE));
        }
    }

    /**
     * Updates the hasNext attribute
     * @throws IOException In case of problem while reading the file
     */
    private void updateHasNext() throws IOException {
        hasNext = (csvReader.peek() != null);
    }

    /**
     * Reads a CSV file and returns a list of Objects from potential bean types (passed
     * to the {@code beanTypes} attribute using {@link HCSVReaderBuilder}{@code .withBeanType}
     * method or similar methods). For each line read of the CSV file, it is transformed
     * into a bean with {@link io.github.hierarchicalcsv.core.annotation.HCSVBean} annotation
     * and uses {@link CsvTreeConstructor} to detect parent/child relations.
     *
     * @return List of {@link io.github.hierarchicalcsv.core.annotation.HCSVBean} annotated objects
     * @throws IOException In case of problem while reading the file
     * @throws CsvException In case of problem while Bean transformation
     */
    public List<Object> readFile() throws IOException, CsvException {
        while (this.hasNext()) {
            this.readNext();
        }
        return csvTreeConstructor.getBeans();
    }

    private void readNext() throws CsvException, IOException {
        assertHasNext();
        long lineNumber = csvReader.getLinesRead();
        String[] line = csvReader.readNext();
        handleBeforeLineProcess(lineNumber, line);
        Object bean = null;
        BlockingQueue<OrderedObject<CsvException>> thrownExceptionsQueue = null;
        if(line.length > 0) {
            var result = lineToBeanProcessor.processLine(lineNumber, line);
            bean = result.getBean();
            thrownExceptionsQueue = result.getThrownExceptionsQueue();
        } else {
            thrownExceptionsQueue = new LinkedBlockingQueue<>();
            bean = EmptyBean.getNewInstance();
        }
        boolean filteredBean = bean == null && thrownExceptionsQueue.isEmpty();
        if(!(bean instanceof EmptyBean || bean instanceof UnknownBean || filteredBean)) {
            csvTreeConstructor.handle(lineNumber, line, bean, thrownExceptionsQueue);
        }
        handleAfterLineProcess(lineNumber, bean, thrownExceptionsQueue, filteredBean);
        updateHasNext();
    }

    private void handleBeforeLineProcess(long lineNumber, String[] line) {
        if(lineProcessListener != null) {
            lineProcessListener.beforeLineProcess(lineNumber, line);
        }
    }

    private void handleAfterLineProcess(long lineNumber, Object csvBean, BlockingQueue<OrderedObject<CsvException>> thrownExceptionsQueue, boolean filteredBean) {
        if(lineProcessListener != null) {
            lineProcessListener.afterLineProcess(lineNumber, csvBean, thrownExceptionsQueue, filteredBean);
        }
    }

    @Override
    public void close() throws IOException {
        csvReader.close();
    }
}
