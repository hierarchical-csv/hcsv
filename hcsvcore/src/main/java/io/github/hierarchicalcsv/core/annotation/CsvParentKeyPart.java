package io.github.hierarchicalcsv.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation indicates that the destination field is represents a
 * <b>parent key</b> (or in database terms, a foreign key).
 * Multiple fields in the class can have this annotation. Knowing that
 * the key for the parent would be composed of these fields ordered by
 * the value of {@code order()}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CsvParentKeyPart {

    /**
     * The parent's class type. The parent <b>must</b> be annotated with
     * {@link HCSVBean} annotation and <b>must</b> have a field with the
     * <b><u>same return type</u></b> as this destination annotated field,
     * and it <b>must</b> be annotated with {@link CsvKey}.
     * @return The parent's class type
     */
    Class<?> value();

    /**
     * An integer representing the order in which the destination field
     * would be placed to compose the full foreign key. Defaults to 0.
     * @return The order of the field
     */
    int order() default 0;

}
