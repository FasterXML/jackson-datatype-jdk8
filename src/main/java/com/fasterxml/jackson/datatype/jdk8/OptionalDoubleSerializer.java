package com.fasterxml.jackson.datatype.jdk8;

import java.io.IOException;
import java.util.OptionalDouble;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class OptionalDoubleSerializer extends StdSerializer<OptionalDouble>
{
    private static final long serialVersionUID = 1L;

    static final OptionalDoubleSerializer INSTANCE = new OptionalDoubleSerializer();

    public OptionalDoubleSerializer() {
        super(OptionalDouble.class);
    }

    @Override
    public boolean isEmpty(SerializerProvider provider, OptionalDouble value) {
        return (value == null) || !value.isPresent();
    }
    
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
