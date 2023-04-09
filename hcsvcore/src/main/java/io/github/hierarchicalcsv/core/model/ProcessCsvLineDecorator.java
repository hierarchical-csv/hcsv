package io.github.hierarchicalcsv.core.model;

import com.opencsv.bean.BeanVerifier;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBeanFilter;
import com.opencsv.bean.concurrent.ProcessCsvLine;
import com.opencsv.bean.exceptionhandler.CsvExceptionHandler;
import com.opencsv.bean.util.OrderedObject;
import com.opencsv.exceptions.CsvException;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.LinkedBlockingQueue;

public class ProcessCsvLineDecorator<T> {

    private ProcessCsvLine<T> delegate;
    private long lineNumber;
    private final ColumnPositionMappingStrategy<T> mapper;
    private CsvToBeanFilter filter;
    private List<BeanVerifier<T>> verifiers;
    private final BlockingQueue<OrderedObject<T>> resultantBeanQueue;
    private final BlockingQueue<OrderedObject<CsvException>> thrownExceptionsQueue;
    private final SortedSet<Long> expectedRecords;
    private CsvExceptionHandler exceptionHandler;

    private ProcessCsvLineDecorator(Class<? extends T> type) {
        this.lineNumber = 0;
        this.mapper = new ColumnPositionMappingStrategy<>();
        this.mapper.setType(type);
        this.verifiers = new ArrayList<>();
        this.resultantBeanQueue = new LinkedBlockingQueue<>();
        this.thrownExceptionsQueue = new  LinkedBlockingQueue<>();
        this.expectedRecords = new ConcurrentSkipListSet<>();
        this.expectedRecords.add(1L);
    }

    private ProcessCsvLineDecorator<T> run() {
        delegate.run();
        return this;
    }

    public CsvToBeanFilter getFilter() {
        return filter;
    }

    public List<BeanVerifier<T>> getVerifiers() {
        return verifiers;
    }

    public BlockingQueue<OrderedObject<CsvException>> getThrownExceptionsQueue() {
        return thrownExceptionsQueue;
    }

    public CsvExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    public BlockingQueue<OrderedObject<T>> getResultantBeanQueue() {
        return resultantBeanQueue;
    }

    public T getBean() {
        var resultQueueOptional = resultantBeanQueue.peek();
        if(resultQueueOptional != null) {
            return resultQueueOptional.getElement();
        }
        return null;
    }

    public static class Builder<B> {

        private final ProcessCsvLineDecorator<B> instance;

        public Builder(Class<? extends B> type) {
            instance = new ProcessCsvLineDecorator<>(type);
        }

        public Builder<B> setLineNumber(long lineNumber) {
            instance.lineNumber = lineNumber;
            return this;
        }
        public Builder<B> setFilter(CsvToBeanFilter filter) {
            instance.filter = filter;
            return this;
        }
        public Builder<B> setVerifiers(List<BeanVerifier<B>> verifiers) {
            if(verifiers != null) {
                instance.verifiers = verifiers;
            }
            return this;
        }
        public Builder<B> setExceptionHandler(CsvExceptionHandler exceptionHandler) {
            if(exceptionHandler != null) {
                instance.exceptionHandler = exceptionHandler;
            }
            return this;
        }

        public ProcessCsvLineDecorator<B> build(String[] line) {
            instance.delegate = new ProcessCsvLine<>(instance.lineNumber, instance.mapper, instance.filter,
                    instance.verifiers, line, instance.resultantBeanQueue, instance.thrownExceptionsQueue,
                    instance.expectedRecords, instance.exceptionHandler);
            return instance;
        }

        public ProcessCsvLineResult<B> buildAndRun(String[] line) {
            return new ProcessCsvLineResult<>(build(line).run());
        }

    }

}
