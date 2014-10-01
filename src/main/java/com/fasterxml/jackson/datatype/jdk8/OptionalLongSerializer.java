package com.fasterxml.jackson.datatype.jdk8;

import java.io.IOException;
import java.util.OptionalLong;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

final class OptionalLongSerializer extends JsonSerializer<OptionalLong>
{
    static final OptionalLongSerializer INSTANCE = new OptionalLongSerializer();

    @Override
    public void serialize(OptionalLong value, JsonGenerator jgen, SerializerProvider provider)
    throws IOException
    {
        if (value.isPresent()) {
            jgen.writeNumber(value.getAsLong());
        } else {
            jgen.writeNull();
        }
    }
}
