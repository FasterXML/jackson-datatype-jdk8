package com.fasterxml.jackson.datatype.jdk8;

import java.io.IOException;
import java.util.OptionalInt;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonIntegerFormatVisitor;
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
    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor,
            JavaType typeHint) throws JsonMappingException {
        JsonIntegerFormatVisitor v2 = visitor
                .expectIntegerFormat(typeHint);
        if (v2 != null) {
            v2.numberType(JsonParser.NumberType.INT);
        }
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
