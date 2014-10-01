package com.fasterxml.jackson.datatype.jdk8;

import java.io.IOException;
import java.util.OptionalLong;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

final class OptionalLongDeserializer extends JsonDeserializer<OptionalLong>
{
    static final OptionalLongDeserializer INSTANCE = new OptionalLongDeserializer();

    @Override
    public OptionalLong getNullValue() {
        return OptionalLong.empty();
    }

    @Override
    public OptionalLong deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException
    {
        return OptionalLong.of(jp.getLongValue());
    }
}
