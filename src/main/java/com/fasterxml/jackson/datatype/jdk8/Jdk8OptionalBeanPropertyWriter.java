package com.fasterxml.jackson.datatype.jdk8;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;

import java.util.Optional;

public class Jdk8OptionalBeanPropertyWriter extends BeanPropertyWriter {

    protected Jdk8OptionalBeanPropertyWriter(BeanPropertyWriter base) {
        super(base);
    }

    @Override
    public void serializeAsField(Object bean, JsonGenerator jgen, SerializerProvider prov) throws Exception
    {
        if (_nullSerializer == null) {
            Object value = get(bean);
            if (value == null || Optional.empty().equals(value)) {
                return;
            }
        }
        super.serializeAsField(bean, jgen, prov);
    }

}
