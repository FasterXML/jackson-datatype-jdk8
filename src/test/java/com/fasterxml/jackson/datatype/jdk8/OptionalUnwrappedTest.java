package com.fasterxml.jackson.datatype.jdk8;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OptionalUnwrappedTest extends ModuleTestBase
{
	static class Child {
		public String name = "Bob";
	}

	static class Parent {
		private Child child = new Child();

		@JsonUnwrapped
		public Child getChild() {
			return child;
		}
	}

	static class OptionalParent {
		@JsonUnwrapped(prefix = "XX.")
		public Optional<Child> child = Optional.of(new Child());
	}

	static class Bean {
	    public String id;
	    @JsonUnwrapped(prefix="child")
	    public Optional<Bean2> bean2;

	    public Bean(String id, Optional<Bean2> bean2) {
	        this.id = id;
	        this.bean2 = bean2;
	    }
	}

	static class Bean2 {
	    public String name;
	}	

	public void testUntypedWithOptionalsNotNulls() throws Exception
	{
		final ObjectMapper mapper = mapperWithModule(false);
		String jsonExp = aposToQuotes("{'XX.name':'Bob'}");
		String jsonAct = mapper.writeValueAsString(new OptionalParent());
		assertEquals(jsonExp, jsonAct);
	}

	// for [datatype-jdk8#20]
	public void testShouldSerializeUnwrappedOptional() throws Exception {
         final ObjectMapper mapper = mapperWithModule(false);
	    
	    assertEquals("{\"id\":\"foo\"}",
	            mapper.writeValueAsString(new Bean("foo", Optional.<Bean2>empty())));
	}
}
