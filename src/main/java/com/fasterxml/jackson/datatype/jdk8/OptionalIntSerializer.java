package com.fasterxml.jackson.datatype.jdk8;

import java.io.IOException;
import java.util.OptionalInt;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

final class OptionalIntSerializer extends StdSerializer<OptionalInt>
{
    private static final long serialVersionUID = 1L;

    static final OptionalIntSerializer INSTANCE = new OptionalIntSerializer();

    public OptionalIntSerializer() {
        super(OptionalInt.class);
    }

    // @since 2.6
    @Override
    public boolean isEmpty(SerializerProvider provider, OptionalInt value) {
        return (value == null) || !value.isPresent();
    }
    
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
