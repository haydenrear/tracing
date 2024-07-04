package org.springframework.data.relational.core.conversion;

import org.springframework.core.ResolvableType;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.jdbc.core.convert.AnnotationDrivenJdbcConverter;
import org.springframework.data.jdbc.core.convert.JdbcAnnotationConverter;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
import org.springframework.data.util.TypeInformation;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.util.List;
import java.util.Objects;

@Component
public class MappingRelationalConverterImpl extends MappingRelationalConverter{

    private final JdbcAnnotationConverter annotationConverter;

    public MappingRelationalConverterImpl(RelationalMappingContext context, JdbcAnnotationConverter jdbcAnnotationConverter) {
        super(context);
        this.annotationConverter = jdbcAnnotationConverter;
    }

    public MappingRelationalConverterImpl(RelationalMappingContext context,
                                          CustomConversions conversions,
                                          JdbcAnnotationConverter annotationConverter) {
        super(context, conversions);
        this.annotationConverter = annotationConverter;
    }

    protected Object getPotentiallyConvertedSimpleRead(Object value, TypeInformation<?> type) {

        Class<?> target = type.getType();

        // TODO: should be added in generic conversion service
        return Objects.requireNonNull(this.annotationConverter.convert(value, type)
                .orElseGet(() -> {
                    if (getConversions().hasCustomReadTarget(value.getClass(), target)) {
                        return getConversionService().convert(value, TypeDescriptor.forObject(value), createTypeDescriptor(type));
                    }

                    if (ClassUtils.isAssignableValue(target, value)) {
                        return value;
                    }

                    if (Enum.class.isAssignableFrom(target) && value instanceof CharSequence) {
                        return Enum.valueOf((Class<Enum>) target, value.toString());
                    }

                    return getConversionService().convert(value, TypeDescriptor.forObject(value), createTypeDescriptor(type));
                }));
    }

    private static TypeDescriptor createTypeDescriptor(TypeInformation<?> type) {

        List<TypeInformation<?>> typeArguments = type.getTypeArguments();
        Class<?>[] generics = new Class[typeArguments.size()];
        for (int i = 0; i < typeArguments.size(); i++) {
            generics[i] = typeArguments.get(i).getType();
        }

        return new TypeDescriptor(ResolvableType.forClassWithGenerics(type.getType(), generics), type.getType(), null);
    }

}
