package com.fasterxml.jackson.datatype.jdk8;

import java.io.IOException;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

final class OptionalDeserializer
    extends StdDeserializer<Optional<?>>
    implements ContextualDeserializer
{
    private static final long serialVersionUID = 1L;

    protected final JavaType _fullType;

    protected final JavaType _referenceType;

    protected final JsonDeserializer<?> _valueDeserializer;

    protected final TypeDeserializer _valueTypeDeserializer;

    public OptionalDeserializer(JavaType fullType, JavaType refType,
            TypeDeserializer typeDeser, JsonDeserializer<?> valueDeser)
    {
        super(fullType);
        _fullType = fullType;
        _referenceType = refType;
        _valueTypeDeserializer = typeDeser;
        _valueDeserializer = valueDeser;
    }

    @Override
    public Optional<?> getNullValue(DeserializationContext ctxt) {
        return Optional.empty();
    }

    /**
     * Overridable fluent factory method used for creating contextual
     * instances.
     */
    protected OptionalDeserializer withResolved(JavaType refType,
            TypeDeserializer typeDeser, JsonDeserializer<?> valueDeser)
    {
        if ((refType == _referenceType)
                && (valueDeser == _valueDeserializer) && (typeDeser == _valueTypeDeserializer)) {
            return this;
        }
        return new OptionalDeserializer(_fullType, refType, typeDeser, valueDeser);
    }

    /*
    /**********************************************************
    /* Validation, post-processing
    /**********************************************************
     */

    /**
     * Method called to finalize setup of this deserializer,
     * after deserializer itself has been registered. This
     * is needed to handle recursive and transitive dependencies.
     */
    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt,
            BeanProperty property) throws JsonMappingException
    {
        JsonDeserializer<?> deser = _valueDeserializer;
        TypeDeserializer typeDeser = _valueTypeDeserializer;
        JavaType refType = _referenceType;

        if (deser == null) {
            deser = ctxt.findContextualValueDeserializer(refType, property);
        } else { // otherwise directly assigned, probably not contextual yet:
            deser = ctxt.handleSecondaryContextualization(deser, property, refType);
        }
        if (typeDeser != null) {
            typeDeser = typeDeser.forProperty(property);
        }
        return withResolved(refType, typeDeser, deser);
    }

    @Override
    public Optional<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException
    {
        Object refd = (_valueTypeDeserializer == null)
                ? _valueDeserializer.deserialize(p, ctxt)
                : _valueDeserializer.deserializeWithType(p, ctxt, _valueTypeDeserializer);
        return Optional.ofNullable(refd);
    }

    @Override
    public Optional<?> deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
        throws IOException
    {
        final JsonToken t = p.getCurrentToken();
        if (t == JsonToken.VALUE_NULL) {
            return getNullValue(ctxt);
        }
        // 03-Nov-2013, tatu: This gets rather tricky with "natural" types
        //   (String, Integer, Boolean), which do NOT include type information.
        //   These might actually be handled ok except that nominal type here
        //   is `Optional`, so special handling is not invoked; instead, need
        //   to do a work-around here.
        // 22-Oct-2015, tatu: Most likely this is actually wrong, result of incorrewct
        //   serialization (up to 2.6, was omitting necessary type info after all);
        //   but safest to leave in place for now
        if (t != null && t.isScalarValue()) {
            return deserialize(p, ctxt);
        }
        return (Optional<?>) typeDeserializer.deserializeTypedFromAny(p, ctxt);
    }
}
