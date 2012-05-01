package ru.yetanothercoder.financisto.demo;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

class DemoDataSourceTest {
	def ds;
	
	@Before
	public void setUp() throws Exception {
		ds = new DataSourceController()
	}

	@Test
	public final void testGenerates() {
		println ds.generateStubRubTransactions("RUB")
		println ds.generateStubUsdTransactions("USD")
	}
	
	
	@Test
	public final void testData() {
		def data = ds.getData("RUB", 1, 1, "t")
		println data
		assert data.size() == 1
	}
	
	@Test
	public final void testFetch() {
		def resp = ds.fetch(['_dataSource':"RUB"])
		println resp
	}
	
	

}
