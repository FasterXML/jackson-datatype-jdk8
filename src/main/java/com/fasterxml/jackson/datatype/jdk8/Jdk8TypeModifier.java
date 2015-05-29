package com.fasterxml.jackson.datatype.jdk8;

import java.lang.reflect.Type;
import java.util.*;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeBindings;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.type.TypeModifier;

/**
 * We need to ensure `Optional` is a `ReferenceType`
 */
public class Jdk8TypeModifier extends TypeModifier
{
    /**
     * Set of single-parameter-type types that we can handle using "standard" handling,
     * no additional tricks needed.
     */
    private final static Class<?>[] OPTIONAL_TYPES = new Class<?>[] {
        OptionalInt.class, OptionalLong.class, OptionalDouble.class
    };

    private final static Class<?>[] OPTIONAL_TYPE_PARAMS = new Class<?>[] {
        Integer.TYPE, Long.TYPE, Double.TYPE
    };
    
    @Override
    public JavaType modifyType(JavaType type, Type jdkType, TypeBindings context, TypeFactory typeFactory)
    {
        final Class<?> raw = type.getRawClass();

        for (int i = 0, len = OPTIONAL_TYPES.length; i < len; ++i) {
            if (raw == OPTIONAL_TYPES[i]) {
                return typeFactory.constructReferenceType(raw,
                        typeFactory.constructType(OPTIONAL_TYPE_PARAMS[i]));
            }
        }
        if (Optional.class.isAssignableFrom(raw)) {
            JavaType[] types = typeFactory.findTypeParameters(type, Optional.class);
            JavaType t = (types == null || types.length == 0) ? null : types[0];
            if (t == null) {
                t = TypeFactory.unknownType();
            }
            return typeFactory.constructReferenceType(raw, t);
        }
        return type;
    }
}
