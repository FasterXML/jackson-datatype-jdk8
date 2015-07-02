package com.fasterxml.jackson.datatype.jdk8;

import java.util.Optional;

import org.junit.Ignore;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class OptionalUnwrappedTest extends ModuleTestBase {
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

	@Ignore
	public void failedTestUntypedWithOptionalsNotNulls() throws Exception {
		final ObjectMapper mapper = mapperWithModule(false);
		String jsonExp = aposToQuotes("{'XX.name':'Bob'}");
		String jsonAct = mapper.writeValueAsString(new OptionalParent());
		assertEquals(jsonExp, jsonAct);
	}
}
