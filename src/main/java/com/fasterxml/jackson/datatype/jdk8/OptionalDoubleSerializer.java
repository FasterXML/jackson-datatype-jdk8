package com.fasterxml.jackson.datatype.jdk8;

import java.io.IOException;
import java.util.OptionalDouble;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

final class OptionalDoubleSerializer extends JsonSerializer<OptionalDouble>
{
    static final OptionalDoubleSerializer INSTANCE = new OptionalDoubleSerializer();

    @Override
    public void serialize(OptionalDouble value, JsonGenerator jgen, SerializerProvider provider)
    throws IOException
    {
        if (value.isPresent()) {
            jgen.writeNumber(value.getAsDouble());
        } else {
            jgen.writeNull();
        }
    }
}
