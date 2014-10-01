package com.fasterxml.jackson.datatype.jdk8;

import java.io.IOException;
import java.util.OptionalDouble;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

final class OptionalDoubleDeserializer extends JsonDeserializer<OptionalDouble>
{
    static final OptionalDoubleDeserializer INSTANCE = new OptionalDoubleDeserializer();

    @Override
    public OptionalDouble getNullValue() {
        return OptionalDouble.empty();
    }

    @Override
    public OptionalDouble deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException
    {
        return OptionalDouble.of(jp.getDoubleValue());
    }
}
