package com.fasterxml.jackson.datatype.jdk8;

import java.io.IOException;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.NameTransformer;

public class OptionalSerializer
    extends StdSerializer<Optional<?>>
    implements ContextualSerializer
{
    private static final long serialVersionUID = 1L;

    /**
     * Declared type parameter for Optional.
     */
    protected final JavaType _referredType;

    protected final BeanProperty _property;
    
    protected final JsonSerializer<Object> _valueSerializer;

    /**
     * To support unwrapped values of dynamic types, will need this:
     */
    protected final NameTransformer _unwrapper;

    /**
     * If element type can not be statically determined, mapping from
     * runtime type to serializer is handled using this object
     *
     * @since 2.6
     */
    protected transient PropertySerializerMap _dynamicSerializers;
    
    public OptionalSerializer(JavaType type) {
        this(type, null);
    }

    @SuppressWarnings("unchecked")
    protected OptionalSerializer(JavaType optionalType, JsonSerializer<?> valueSer)
    {
        super(optionalType);
        _referredType = _valueType(optionalType);
        _property = null;
        _valueSerializer = (JsonSerializer<Object>) valueSer;
        _unwrapper = null;
        _dynamicSerializers = PropertySerializerMap.emptyForProperties();
    }

    @SuppressWarnings("unchecked")
    protected OptionalSerializer(OptionalSerializer base,
            BeanProperty property, JsonSerializer<?> valueSer, NameTransformer unwrapper)
    {
        super(base);
        _referredType = base._referredType;
        _dynamicSerializers = base._dynamicSerializers;
        _property = property;
        _valueSerializer = (JsonSerializer<Object>) valueSer;
        _unwrapper = unwrapper;
    }

    protected OptionalSerializer withResolved(BeanProperty prop,
            JsonSerializer<?> ser, NameTransformer unwrapper)
    {
        if ((_property == prop) && (_valueSerializer == ser) && (_unwrapper == unwrapper)) {
            return this;
        }
        return new OptionalSerializer(this, prop, ser, unwrapper);
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider provider,
            BeanProperty property) throws JsonMappingException
    {
        JsonSerializer<?> ser = _valueSerializer;
        if (ser == null) {
            // A few conditions needed to be able to fetch serializer here:
            if (_useStatic(provider, property, _referredType)) {
                ser = _findSerializer(provider, _referredType, property);
            }
        } else {
            ser = provider.handlePrimaryContextualization(ser, property);
        }
        return withResolved(property, ser, _unwrapper);
    }

    protected boolean _useStatic(SerializerProvider provider, BeanProperty property,
            JavaType referredType)
    {
        // First: no serializer for `Object.class`, must be dynamic
        if (_referredType.hasRawClass(Object.class)) {
            return false;
        }
        // but if type is final, might as well fetch
        if (_referredType.isFinal()) { // or should we allow annotation override? (only if requested...)
            return true;
        }
        // if neither, maybe explicit annotation?
        AnnotationIntrospector intr = provider.getAnnotationIntrospector();
        if ((intr != null) && (property != null)) {
            Annotated ann = property.getMember();
            if (ann != null) {
                JsonSerialize.Typing t = intr.findSerializationTyping(property.getMember());
                if (t == JsonSerialize.Typing.STATIC) {
                    return true;
                }
                if (t == JsonSerialize.Typing.DYNAMIC) {
                    return false;
                }
            }
        }
        // and finally, may be forced by global static typing (unlikely...)
        return provider.isEnabled(MapperFeature.USE_STATIC_TYPING);
    }

    @Override
    public JsonSerializer<Optional<?>> unwrappingSerializer(NameTransformer transformer) {
        JsonSerializer<Object> ser = _valueSerializer;
        if (ser != null) {
            ser = ser.unwrappingSerializer(transformer);
        }
        NameTransformer unwrapper = (_unwrapper == null) ? transformer
                : NameTransformer.chainedTransformer(transformer, _unwrapper);
        return withResolved(_property, ser, unwrapper);
    }

    /*
    /**********************************************************
    /* Serialization methods
    /**********************************************************
     */

    @Override
    public void serialize(Optional<?> opt, JsonGenerator gen, SerializerProvider provider)
        throws IOException
    {
        if (opt.isPresent()) {
            Object value = opt.get();
            JsonSerializer<Object> ser = _valueSerializer;
            if (ser == null) {
                ser = _findCachedSerializer(provider, value.getClass());
            }
            ser.serialize(value, gen, provider);
        } else {
            provider.defaultSerializeNull(gen);
        }
    }

    @Override
    public void serializeWithType(Optional<?> opt,
            JsonGenerator gen, SerializerProvider provider,
            TypeSerializer typeSer) throws IOException
    {
        if (opt.isPresent()) {
            Object value = opt.get();
            JsonSerializer<Object> ser = _valueSerializer;
            if (ser == null) {
                ser = _findCachedSerializer(provider, value.getClass());
            }
            ser.serializeWithType(value, gen, provider, typeSer);
        } else {
            provider.defaultSerializeNull(gen);
        }
    }

    /*
    /**********************************************************
    /* API overrides
    /**********************************************************
     */

    @Override
    @Deprecated
    public boolean isEmpty(Optional<?> value) {
        return isEmpty(null, value);
    }

    @Override
    public boolean isEmpty(SerializerProvider provider, Optional<?> value) {
        return (value == null) || !value.isPresent();
    }

    @Override
    public boolean isUnwrappingSerializer() {
        return (_unwrapper != null);
    }

    /*
    /**********************************************************
    /* Introspection support
    /**********************************************************
     */

    @Override
    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException
    {
        JsonSerializer<?> ser = _valueSerializer;
        if (ser == null) {
            ser = _findSerializer(visitor.getProvider(), _referredType, _property);
        }
        ser.acceptJsonFormatVisitor(visitor, _referredType);
    }

    /*
    /**********************************************************
    /* Misc other
    /**********************************************************
     */
    
    protected static JavaType _valueType(JavaType optionalType) {
        JavaType valueType = optionalType.containedType(0);
        if (valueType == null) {
            valueType = TypeFactory.unknownType();
        }
        return valueType;
    }

    /**
     * Helper method that encapsulates logic of retrieving and caching required
     * serializer.
     */
    private final JsonSerializer<Object> _findCachedSerializer(SerializerProvider provider,
            Class<?> type) throws JsonMappingException
    {
        JsonSerializer<Object> ser = _dynamicSerializers.serializerFor(type);
        if (ser == null) {
            ser = _findSerializer(provider, type, _property);
            if (_unwrapper != null) {
                ser = ser.unwrappingSerializer(_unwrapper);
            }
            _dynamicSerializers = _dynamicSerializers.newWith(type, ser);
        }
        return ser;
    }

    private final JsonSerializer<Object> _findSerializer(SerializerProvider provider,
            Class<?> type, BeanProperty prop) throws JsonMappingException
    {
        return provider.findTypedValueSerializer(type, true, prop);
    }

    private final JsonSerializer<Object> _findSerializer(SerializerProvider provider,
        JavaType type, BeanProperty prop) throws JsonMappingException
    {
        return provider.findTypedValueSerializer(type, true, prop);
    }
}
