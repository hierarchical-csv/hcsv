package io.github.hierarchicalcsv.core.annotation;

import io.github.hierarchicalcsv.core.util.HCSVUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation indicates that the annotated class represents
 * a Hierarchical CSV Bean. <b>Mandatory</b> to all beans to be
 * parsed by the {@link io.github.hierarchicalcsv.core.HCSVReader}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface HCSVBean {

    /**
     * The position of the code inside a CSV line that permits to
     * transform this line into the annotated bean. Defaults to
     * {@literal 0}.
     * @return The position of the code
     */
    int codePosition() default HCSVUtils.DEFAULT_CODE_POSITION;

    /**
     * The value of the code inside a CSV line (at the index
     * {@code codePosition()}) that permits to transform this line
     * into the annotated bean.
     * @return The code value
     */
    String codeValue();

}
