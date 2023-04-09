package io.github.hierarchicalcsv.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation indicates that the destination field is CSV bean.
 * The field class type <b>must</b> be annotated with {@link HCSVBean}.
 * And it should either be <b>public</b> or have a <b>setter method</b>.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CsvChild {
}
