package io.github.hierarchicalcsv.core.model;

public final class EmptyBean {

    private EmptyBean() {
    }

    public static EmptyBean getNewInstance() {
        return new EmptyBean();
    }
}
