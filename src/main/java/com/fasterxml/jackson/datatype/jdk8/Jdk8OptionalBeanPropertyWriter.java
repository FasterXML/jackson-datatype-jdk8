package com.fasterxml.jackson.datatype.jdk8;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.util.NameTransformer;

import java.util.Optional;

public class Jdk8OptionalBeanPropertyWriter extends BeanPropertyWriter {

    protected Jdk8OptionalBeanPropertyWriter(BeanPropertyWriter base) {
        super(base);
    }

    protected Jdk8OptionalBeanPropertyWriter(BeanPropertyWriter base, PropertyName newName) {
        super(base, newName);
    }

    // !!! TODO: in 2.7, no need to override
    @Override
    public BeanPropertyWriter rename(NameTransformer transformer) {
        String newName = transformer.transform(_name.getValue());
        if (newName.equals(_name.toString())) {
            return this;
        }
        return _new(PropertyName.construct(newName));
    }

    // NOTE: 
//    @Override
    protected BeanPropertyWriter _new(PropertyName newName) {
        return new Jdk8OptionalBeanPropertyWriter(this, newName);
    }

    @Override
    public BeanPropertyWriter unwrappingWriter(NameTransformer unwrapper) {
        return new Jdk8UnwrappingOptionalBeanPropertyWriter(this, unwrapper);
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
