package com.fasterxml.jackson.datatype.jdk8;

import java.util.Optional;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.UnwrappingBeanPropertyWriter;
import com.fasterxml.jackson.databind.util.NameTransformer;

public class Jdk8UnwrappingOptionalBeanPropertyWriter extends UnwrappingBeanPropertyWriter
{
    public Jdk8UnwrappingOptionalBeanPropertyWriter(BeanPropertyWriter base,
            NameTransformer transformer) {
        super(base, transformer);
    }

    protected Jdk8UnwrappingOptionalBeanPropertyWriter(UnwrappingBeanPropertyWriter base,
            NameTransformer transformer, SerializedString name) {
        // TODO: in 2.7 call this ctor instead:
//        super(base, transformer, name);
        super(base, transformer);
    }

    // TODO: In 2.7, should not need to override this method, just _new(...)
    @Override
    public UnwrappingBeanPropertyWriter rename(NameTransformer transformer)
    {
        String oldName = _name.getValue();
        String newName = transformer.transform(oldName);

        // important: combine transformers:
        transformer = NameTransformer.chainedTransformer(transformer, _nameTransformer);
    
        return _new(transformer, new SerializedString(newName));
    }

    // NOTE: was added in one of later 2.6.0 RCs; uncomment once available
//    @Override
    protected UnwrappingBeanPropertyWriter _new(NameTransformer transformer, SerializedString newName)
    {
        return new Jdk8UnwrappingOptionalBeanPropertyWriter(this, transformer, newName);
    }

    @Override
    public void serializeAsField(Object bean, JsonGenerator gen, SerializerProvider prov) throws Exception
    {
        if (_nullSerializer == null) {
            Object value = get(bean);
            if (value == null || Optional.empty().equals(value)) {
                return;
            }
        }
        super.serializeAsField(bean, gen, prov);
    }
}
