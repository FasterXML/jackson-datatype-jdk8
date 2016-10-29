package com.fasterxml.jackson.datatype.jdk8;

import java.io.IOException;
import java.util.OptionalLong;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;

public class OptionalLongDeserializer extends StdScalarDeserializer<OptionalLong>
{
    private static final long serialVersionUID = 1L;

    static final OptionalLongDeserializer INSTANCE = new OptionalLongDeserializer();

    public OptionalLongDeserializer() {
        super(OptionalLong.class);
    }

    @Override
    public OptionalLong getNullValue(DeserializationContext ctxt) {
        return OptionalLong.empty();
    }

    @Override
    public OptionalLong deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        return OptionalLong.of(p.getLongValue());
    }
}
