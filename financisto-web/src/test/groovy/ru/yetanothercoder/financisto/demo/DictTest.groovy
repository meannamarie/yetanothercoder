package ru.yetanothercoder.financisto.demo;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

class DictTest {
	def d;
	
	@Before
	public void setUp() throws Exception {
		d = new DictController();
	}

	@Test
	public final void testGetAll() {
		assert d.getAll([:])['totals'] == [RUB:100, USD:100]
	}

}
