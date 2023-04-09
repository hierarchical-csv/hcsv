package io.github.hierarchicalcsv.core.model;

import com.opencsv.bean.BeanVerifier;
import com.opencsv.bean.CsvToBeanFilter;
import com.opencsv.bean.exceptionhandler.CsvExceptionHandler;

import java.util.List;

public class CsvBeanType<T> {

    protected Class<? extends T> type;

    protected CsvToBeanFilter filter;

    protected List<BeanVerifier<T>> verifiers;

    protected CsvExceptionHandler exceptionHandler;

    public CsvBeanType(Class<? extends T> type) {
        this.type = type;
    }

    public CsvBeanType(Class<? extends T> type, CsvToBeanFilter filter) {
        this.type = type;
        this.filter = filter;
    }

    public CsvBeanType(Class<? extends T> type, CsvToBeanFilter filter, List<BeanVerifier<T>> verifiers) {
        this.type = type;
        this.filter = filter;
        this.verifiers = verifiers;
    }

    public CsvBeanType(Class<? extends T> type, CsvToBeanFilter filter, List<BeanVerifier<T>> verifiers, CsvExceptionHandler exceptionHandler) {
        this.type = type;
        this.filter = filter;
        this.verifiers = verifiers;
        this.exceptionHandler = exceptionHandler;
    }

    public Class<? extends T> getType() {
        return type;
    }

    public CsvToBeanFilter getFilter() {
        return filter;
    }

    public List<BeanVerifier<T>> getVerifiers() {
        return verifiers;
    }

    public CsvExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }
}
