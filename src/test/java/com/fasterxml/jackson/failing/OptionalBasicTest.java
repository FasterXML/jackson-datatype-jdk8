package com.fasterxml.jackson.failing;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.datatype.jdk8.ModuleTestBase;

public class OptionalBasicTest extends ModuleTestBase
{
    @JsonAutoDetect(fieldVisibility=Visibility.ANY)
    public static final class OptionalData {
        public Optional<String> myString = Optional.empty();
    }

    // for [datatype-jdk8#18]
    static class OptionalNonEmptyStringBean {
        @JsonInclude(Include.NON_EMPTY)
        public Optional<String> value;

        public OptionalNonEmptyStringBean() { }
        OptionalNonEmptyStringBean(String str) {
            value = Optional.ofNullable(str);
        }
    }

    private final ObjectMapper MAPPER = mapperWithModule();

    public void testSerOptNonDefault() throws Exception
    {
        OptionalData data = new OptionalData();
        data.myString = null;
        String value = mapperWithModule().setSerializationInclusion(
                JsonInclude.Include.NON_DEFAULT).writeValueAsString(data);
        assertEquals("{}", value);
    }

    public void testSerOptNonEmpty() throws Exception
    {
        OptionalData data = new OptionalData();
        data.myString = null;
        String value = mapperWithModule().setSerializationInclusion(
                JsonInclude.Include.NON_EMPTY).writeValueAsString(data);
        assertEquals("{}", value);
    }

    public void testSerOptNonAbsent() throws Exception
    {
        OptionalData data = new OptionalData();
        data.myString = null;
        String value = mapperWithModule().setSerializationInclusion(
                JsonInclude.Include.NON_ABSENT).writeValueAsString(data);
        assertEquals("{}", value);
    }

    public void testExcludeEmptyStringViaOptional() throws Exception
    {
        String json = MAPPER.writeValueAsString(new OptionalNonEmptyStringBean("x"));
        assertEquals("{\"value\":\"x\"}", json);
        json = MAPPER.writeValueAsString(new OptionalNonEmptyStringBean(null));
        assertEquals("{}", json);
        json = MAPPER.writeValueAsString(new OptionalNonEmptyStringBean(""));
        assertEquals("{}", json);
    }
}
