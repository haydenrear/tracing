package org.springframework.data.jdbc.core.convert;

import com.hayden.utilitymodule.MapFunctions;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.util.TypeInformation;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class JdbcAnnotationConverter {

    @Autowired
    private Collection<AnnotationDrivenJdbcConverter<?,?>> converters;


    public <S, U> Optional<U> convert(S source, TypeInformation<? extends S> name) {
        if (name.toTypeDescriptor().getResolvableType().getSource() instanceof Field f)
            return Optional.ofNullable(this.<S, U>retrieveConverter(f))
                    .map(a -> a.convert(source));

        return Optional.empty();
    }

    public <S> boolean matches(TypeInformation<? extends S> typeHint) {
        return typeHint.toTypeDescriptor().getResolvableType().getSource() instanceof Field f
                && retrieveConverter(f) != null;
    }

    public <S, U> @Nullable AnnotationDrivenJdbcConverter<S, U> retrieveConverter(Field name) {
        try {
            return (AnnotationDrivenJdbcConverter<S, U>) converters.stream().filter(a -> a.matches(name))
                    .findFirst().orElse(null);
        } catch (ClassCastException e) {
            return null;
        }
    }

}
