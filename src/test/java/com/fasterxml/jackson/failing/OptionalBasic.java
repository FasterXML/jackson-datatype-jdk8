package com.fasterxml.jackson.failing;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.datatype.jdk8.ModuleTestBase;
import com.fasterxml.jackson.datatype.jdk8.TestConfigureAbsentsAsNulls.OptionalData;

public class OptionalBasic extends ModuleTestBase {
	public void testSerOptNonDefault() throws Exception {
		OptionalData data = new OptionalData();
		data.myString = null;
		String value = mapperWithModule().setSerializationInclusion(
				JsonInclude.Include.NON_DEFAULT).writeValueAsString(data);
		assertEquals("{}", value);
	}
}
