package org.springframework.data.jdbc.core.convert;

import org.postgresql.util.PGobject;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
public class FromPostgresJson implements AnnotationDrivenJdbcConverter<PGobject, String> {


    @Override
    public String convert(PGobject source) {
        return source.getValue();
    }

    @Override
    public boolean matches(Field f) {
        return f.isAnnotationPresent(JdbcPostgresJson.class)
                && f.getAnnotation(JdbcPostgresJson.class)
                    .converter().equals(PostgresConvert.POSTGRES_STR_CONVERT);
    }
}
