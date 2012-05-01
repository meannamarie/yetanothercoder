package my.gaelyk.test2;

class CategoryTest extends GroovyTestCase {

	public final void testCategory() {
		def c = new Category(1, "my name", new Date());
		//c.id = 2;
		assert c.id == 1
		assert c.name == "my name"
	}

}
