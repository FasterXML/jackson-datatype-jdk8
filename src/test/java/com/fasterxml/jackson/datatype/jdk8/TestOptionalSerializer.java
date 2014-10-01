package com.fasterxml.jackson.datatype.jdk8;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestOptionalSerializer extends ModuleTestBase
{
    private static final TypeReference<Optional<String>> OPTIONAL_STRING_TYPE = new TypeReference<Optional<String>>() {};
    private static final TypeReference<Optional<TestBean>> OPTIONAL_BEAN_TYPE = new TypeReference<Optional<TestBean>>() {};

    private ObjectMapper mapper;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        mapper = mapperWithModule();
    }

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

    private <T> Optional<T> roundtrip(Optional<T> obj, TypeReference<Optional<T>> type) throws IOException
    {
        return mapper.readValue(mapper.writeValueAsBytes(obj), type);
    }

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
}
