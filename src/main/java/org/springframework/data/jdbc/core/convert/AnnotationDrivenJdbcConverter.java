package org.springframework.data.jdbc.core.convert;

import org.springframework.core.convert.converter.Converter;

import java.lang.reflect.Field;

public interface AnnotationDrivenJdbcConverter<T, U> extends Converter<T, U> {

    boolean matches(Field f);

}
