package org.springframework.data.jdbc.core.convert;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.sql.JDBCType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface JdbcPostgresJson {

    JDBCType jdbcType() default JDBCType.OTHER;
    String converter() default PostgresConvert.POSTGRES_STR_CONVERT;

}
