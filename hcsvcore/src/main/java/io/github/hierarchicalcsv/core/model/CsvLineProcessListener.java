package io.github.hierarchicalcsv.core.model;

import com.opencsv.bean.util.OrderedObject;
import com.opencsv.exceptions.CsvException;

import java.util.concurrent.BlockingQueue;

public interface CsvLineProcessListener {

    default void beforeLineProcess(long lineNumber, String[] line) {}

    default void afterLineProcess(long lineNumber, Object csvBean, BlockingQueue<OrderedObject<CsvException>> thrownExceptionsQueue, boolean filteredBean) {}

}
