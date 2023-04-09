package io.github.hierarchicalcsv.core.model;

import com.opencsv.bean.util.OrderedObject;
import com.opencsv.exceptions.CsvException;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ProcessCsvLineResult<T> {

    private final T bean;

    private final BlockingQueue<OrderedObject<T>> resultantBeanQueue;

    private final BlockingQueue<OrderedObject<CsvException>> thrownExceptionsQueue;

    public ProcessCsvLineResult(ProcessCsvLineDecorator<T> processCsvLine) {
        this(processCsvLine.getBean(), processCsvLine.getResultantBeanQueue(), processCsvLine.getThrownExceptionsQueue());
    }

    public ProcessCsvLineResult(T bean) {
        this.bean = bean;
        this.resultantBeanQueue = new LinkedBlockingQueue<>();
        this.thrownExceptionsQueue = new  LinkedBlockingQueue<>();
    }

    private ProcessCsvLineResult(T bean, BlockingQueue<OrderedObject<T>> resultantBeanQueue, BlockingQueue<OrderedObject<CsvException>> thrownExceptionsQueue) {
        this.bean = bean;
        this.resultantBeanQueue = resultantBeanQueue;
        this.thrownExceptionsQueue = thrownExceptionsQueue;
    }

    public T getBean() {
        return bean;
    }

    public BlockingQueue<OrderedObject<T>> getResultantBeanQueue() {
        return resultantBeanQueue;
    }

    public BlockingQueue<OrderedObject<CsvException>> getThrownExceptionsQueue() {
        return thrownExceptionsQueue;
    }
}
