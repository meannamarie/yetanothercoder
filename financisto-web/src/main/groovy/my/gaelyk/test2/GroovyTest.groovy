package my.gaelyk.test2

class GroovyTest {
	String name
	int age
	
	def List filter(List col) {
		def filtered = col.findAll{ w -> w.size() > 2 }
		return filtered
	}
	
	@Override
	def String toString() {
		return name + ", " + age 
	}
	
	static main(args) {
		println "Hello from Groovy7"
		
		def gt = new GroovyTest(name: "asdf", age: 8)
		
		
		gt.getName();
		println gt
	}

}
