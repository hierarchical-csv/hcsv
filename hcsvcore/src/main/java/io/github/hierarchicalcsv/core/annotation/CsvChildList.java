package io.github.hierarchicalcsv.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation indicates that the destination field is <b>collection</b>
 * of CSV beans.
 * The field class type <b>must</b> be a {@link java.util.Collection} and the
 * generic class <b>must</b> annotated with {@link HCSVBean}.
 * The field should be either <b>public</b> or have a <b>setter</b> and
 * <b>getter</b> methods.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CsvChildList {
}
