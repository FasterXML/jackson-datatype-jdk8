package com.fasterxml.jackson.datatype.jdk8;

import java.io.IOException;
import java.util.OptionalInt;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

final class OptionalIntDeserializer extends JsonDeserializer<OptionalInt>
{
    static final OptionalIntDeserializer INSTANCE = new OptionalIntDeserializer();

    @Override
    public OptionalInt getNullValue() {
        return OptionalInt.empty();
    }

    @Override
    public OptionalInt deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException
    {
        return OptionalInt.of(jp.getIntValue());
    }
}
