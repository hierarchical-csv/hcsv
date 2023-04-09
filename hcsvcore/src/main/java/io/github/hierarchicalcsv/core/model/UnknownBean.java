package io.github.hierarchicalcsv.core.model;

public final class UnknownBean {

    private UnknownBean() {
    }

    public static UnknownBean getNewInstance() {
        return new UnknownBean();
    }
}
