package com.fasterxml.jackson.datatype.jdk8;

import java.io.IOException;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestOptionalSerializer extends ModuleTestBase
{
    private static final TypeReference<Optional<String>> OPTIONAL_STRING_TYPE = new TypeReference<Optional<String>>() {};
    private static final TypeReference<Optional<TestBean>> OPTIONAL_BEAN_TYPE = new TypeReference<Optional<TestBean>>() {};

    public static class TestBean
    {
        public int foo;
        public String bar;

        @JsonCreator
        public TestBean(@JsonProperty("foo") int foo, @JsonProperty("bar") String bar)
        {
            this.foo = foo;
            this.bar = bar;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (obj.getClass() != getClass()) {
                return false;
            }
            TestBean castObj = (TestBean) obj;
            return castObj.foo == foo && Objects.equals(castObj.bar, bar);
        }

        @Override
        public int hashCode() {
            return foo ^ bar.hashCode();
        }
    }

    static class OptionalStringBean {
        public Optional<String> value;
    }

    // [issue#4]
    static class Issue4Entity {
        private final Optional<String> data;
 
        @JsonCreator
        public Issue4Entity(Optional<String> data) {
            this.data = Objects.requireNonNull(data, "data");
        }
 
        @JsonProperty ("data")
        public Optional<String> data() {
            return data;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Issue4Entity entity = (Issue4Entity) o;
            return data.equals(entity.data);
        }
    }    

    private ObjectMapper mapper;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        mapper = mapperWithModule();
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    public void testStringAbsent() throws Exception
    {
        assertFalse(roundtrip(Optional.empty(), OPTIONAL_STRING_TYPE).isPresent());
    }

    public void testStringPresent() throws Exception
    {
        assertEquals("test", roundtrip(Optional.of("test"), OPTIONAL_STRING_TYPE).get());
    }

    public void testIntAbsent() throws Exception
    {
        assertFalse(mapper.readValue(mapper.writeValueAsBytes(OptionalInt.empty()), OptionalInt.class).isPresent());
    }

    public void testIntPresent() throws Exception
    {
        assertEquals(5, mapper.readValue(mapper.writeValueAsBytes(OptionalInt.of(5)), OptionalInt.class).getAsInt());
    }

    public void testLongAbsent() throws Exception
    {
        assertFalse(mapper.readValue(mapper.writeValueAsBytes(OptionalLong.empty()), OptionalLong.class).isPresent());
    }

    public void testLongPresent() throws Exception
    {
        assertEquals(Long.MAX_VALUE, mapper.readValue(mapper.writeValueAsBytes(OptionalLong.of(Long.MAX_VALUE)), OptionalLong.class).getAsLong());
    }

    public void testDoubleAbsent() throws Exception
    {
        assertFalse(mapper.readValue(mapper.writeValueAsBytes(OptionalInt.empty()), OptionalInt.class).isPresent());
    }

    public void testDoublePresent() throws Exception
    {
        assertEquals(Double.MIN_VALUE, mapper.readValue(mapper.writeValueAsBytes(OptionalDouble.of(Double.MIN_VALUE)), OptionalDouble.class).getAsDouble());
    }

    public void testBeanAbsent() throws Exception
    {
        assertFalse(roundtrip(Optional.empty(), OPTIONAL_BEAN_TYPE).isPresent());
    }

    public void testBeanPresent() throws Exception
    {
        final TestBean bean = new TestBean(Integer.MAX_VALUE, "woopwoopwoopwoopwoop");
        assertEquals(bean, roundtrip(Optional.of(bean), OPTIONAL_BEAN_TYPE).get());
    }

    // [issue#4]
    public void testBeanWithCreator() throws Exception
    {
        final Issue4Entity emptyEntity = new Issue4Entity(Optional.empty());
        final String entityString = mapper.writeValueAsString(emptyEntity);
        final Issue4Entity deserialisedEntity = mapper.readValue(entityString, Issue4Entity.class);
        if (!deserialisedEntity.equals(emptyEntity)) {
            throw new IOException("Entities not equal");
        }
        throw new Error();
    }
    
    // [issue#4]
    public void testOptionalStringInBean() throws Exception
    {
        OptionalStringBean bean = mapper.readValue("{\"value\":\"xyz\"}", OptionalStringBean.class);
        assertNotNull(bean.value);
        assertEquals("xyz", bean.value.get());
    }

    /*
    /**********************************************************
    /* Helper methods
    /**********************************************************
     */
    
    private <T> Optional<T> roundtrip(Optional<T> obj, TypeReference<Optional<T>> type) throws IOException
    {
        return mapper.readValue(mapper.writeValueAsBytes(obj), type);
    }
}
