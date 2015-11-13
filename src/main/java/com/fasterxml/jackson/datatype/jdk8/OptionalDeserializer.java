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
    public JavaType getValueType() { return _fullType; }

    @Override
    public Optional<?> getNullValue() { return Optional.empty(); }

    /**
     * Overridable fluent factory method used for creating contextual
     * instances.
     */
    protected OptionalDeserializer withResolved(
            TypeDeserializer typeDeser, JsonDeserializer<?> valueDeser)
    {
        return new OptionalDeserializer(_fullType, _referenceType,
                typeDeser, valueDeser);
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

        if (deser == null) {
            deser = ctxt.findContextualValueDeserializer(_referenceType, property);
        } else { // otherwise directly assigned, probably not contextual yet:
            deser = ctxt.handleSecondaryContextualization(deser, property, _referenceType);
        }
        if (typeDeser != null) {
            typeDeser = typeDeser.forProperty(property);
        }
        if (deser == _valueDeserializer && typeDeser == _valueTypeDeserializer) {
            return this;
        }
        return withResolved(typeDeser, deser);
    }

    @Override
    public Optional<?> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException
    {
        Object refd;

        if (_valueTypeDeserializer == null) {
            refd = _valueDeserializer.deserialize(jp, ctxt);
        } else {
            refd = _valueDeserializer.deserializeWithType(jp, ctxt, _valueTypeDeserializer);
        }
        return Optional.ofNullable(refd);
    }

    /* NOTE: usually should not need this method... but for some reason, it is needed here.
     */
    @Override
    public Optional<?> deserializeWithType(JsonParser jp, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
        throws IOException
    {
        final JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.VALUE_NULL) {
            return getNullValue();
        }
        // 03-Nov-2013, tatu: This gets rather tricky with "natural" types
        //   (String, Integer, Boolean), which do NOT include type information.
        //   These might actually be handled ok except that nominal type here
        //   is `Optional`, so special handling is not invoked; instead, need
        //   to do a work-around here.
        if (t != null && t.isScalarValue()) {
            return deserialize(jp, ctxt);
        }
        // with type deserializer to use here? Looks like we get passed same one?
        return Optional.ofNullable(typeDeserializer.deserializeTypedFromAny(jp, ctxt));
    }
}
