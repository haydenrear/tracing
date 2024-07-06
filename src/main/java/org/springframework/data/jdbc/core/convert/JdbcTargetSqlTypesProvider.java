package org.springframework.data.jdbc.core.convert;

import org.springframework.data.jdbc.support.JdbcUtil;
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty;
import org.springframework.stereotype.Component;

import java.sql.SQLType;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JdbcTargetSqlTypesProvider {

    private final Map<RelationalPersistentProperty, SQLType> persistentPropertyCache = new ConcurrentHashMap<>();


    public SQLType retrieve(RelationalPersistentProperty property) {
        return persistentPropertyCache.compute(property, (key, prev) -> prev != null ? prev : getPropertyType(property));
    }

    public SQLType getPropertyType(RelationalPersistentProperty property) {
        return Optional.ofNullable(property.getField())
                .filter(f -> f.isAnnotationPresent(JdbcPostgresJson.class))
                .<SQLType>map(f -> f.getAnnotation(JdbcPostgresJson.class).jdbcType())
                .orElse(JdbcUtil.targetSqlTypeFor(property.getType()));
    }

}
