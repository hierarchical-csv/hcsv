package io.github.hierarchicalcsv.core.util;

public final class HCSVErrorMessageUtils {

    public static final String UNABLE_TO_READ_FILE = "hcsv-error.unable-to-read-file";
    public static final String UNABLE_TO_READ_FILE_WITH_EXCEPTION = "hcsv-error.unable-to-read-file-with-exception";
    public static final String UNABLE_TO_INVOKE_METHOD = "hcsv-error.unable-to-invoke-method";
    public static final String UNABLE_TO_INVOKE_METHOD_WITH_EXCEPTION = "hcsv-error.unable-to-invoke-method-with-exception";
    public static final String PARENT_CLASS_SET_MULTIPLE_TIMES_IN_CHILD = "hcsv-error.parent-class-set-multiple-times-in-child";
    public static final String PARENT_CLASS_NOT_SET_IN_CHILD = "hcsv-error.parent-class-not-set-in-child";
    public static final String PARENT_CLASS_NOT_CSV_BEAN = "hcsv-error.parent-class-not-csv-bean";
    public static final String CHILD_CLASS_NOT_CSV_BEAN = "hcsv-error.child-class-not-csv-bean";
    public static final String PARENT_BEAN_HAS_NO_KEY = "hcsv-error.parent-bean-has-no-key";
    public static final String PARENT_BEAN_CODE_NOT_DEFINED = "hcsv-error.parent-bean-code-not-defined";
    public static final String CHILD_BEAN_CODE_NOT_DEFINED = "hcsv-error.child-bean-code-not-defined";
    public static final String NO_HCSV_BEAN_TYPE_DEFINED = "hcsv-error.no-hcsv-bean-type-defined";
    public static final String MULTIPLE_PARENT_KEY_WITH_SAME_ORDER = "hcsv-error.multiple-parent-key-with-same-order";
    public static final String CHILD_WRAPPER_NOT_COLLECTION = "hcsv-error.child-wrapper-not-collection";
    public static final String BEAN_CODE_NOT_DEFINED = "hcsv-error.bean-code-not-defined";
    public static final String BEAN_CODE_DEFINED_MULTIPLE_TIMES = "hcsv-error.bean-code-defined-multiple-times";

    // Open CSV Inherited
    public static final String UNKNOWN_BEAN_TYPE = "hcsv-error.open-csv.unknown-bean-type";
    public static final String PARENT_BEAN_NOT_FOUND = "hcsv-error.open-csv.parent-bean-not-found";
    public static final String CHILD_WRAPPER_NOT_INITIALIZED = "hcsv-error.open-csv.child-wrapper-not-initialized";
    public static final String CAUSED_CSV = "hcsv-error.open-csv.caused-csv";

    private HCSVErrorMessageUtils() {
    }

}
