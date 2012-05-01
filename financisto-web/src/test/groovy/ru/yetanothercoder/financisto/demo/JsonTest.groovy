package ru.yetanothercoder.financisto.demo;

import static org.junit.Assert.*;
import groovy.json.JsonBuilder;

import org.junit.Test;

class JsonTest {
	
	@Test
	def void test() {
		def data = [name: "Guillaume", age: 33]
		def json = new JsonBuilder()
		json data
	   
		assert json.toString() == '{"name":"Guillaume","age":33}'
	}
}
