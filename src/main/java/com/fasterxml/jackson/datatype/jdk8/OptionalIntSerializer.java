package com.fasterxml.jackson.datatype.jdk8;

import java.io.IOException;
import java.util.OptionalInt;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

final class OptionalIntSerializer extends JsonSerializer<OptionalInt>
{
    static final OptionalIntSerializer INSTANCE = new OptionalIntSerializer();

    @Override
    public void serialize(OptionalInt value, JsonGenerator jgen, SerializerProvider provider)
    throws IOException
    {
        if (value.isPresent()) {
            jgen.writeNumber(value.getAsInt());
        } else {
            jgen.writeNull();
        }
    }
}
