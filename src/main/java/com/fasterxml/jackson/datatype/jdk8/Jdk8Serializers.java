package com.fasterxml.jackson.datatype.jdk8;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.Serializers;

class Jdk8Serializers extends Serializers.Base
{
    @Override
    public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc)
    {
        final Class<?> raw = type.getRawClass();
        if (Optional.class.isAssignableFrom(raw)) {
            return new OptionalSerializer(type);
        }
        if (OptionalInt.class.isAssignableFrom(raw)) {
            return OptionalIntSerializer.INSTANCE;
        }
        if (OptionalLong.class.isAssignableFrom(raw)) {
            return OptionalLongSerializer.INSTANCE;
        }
        if (OptionalDouble.class.isAssignableFrom(raw)) {
            return OptionalDoubleSerializer.INSTANCE;
        }
        return super.findSerializer(config, type, beanDesc);
    }
}
