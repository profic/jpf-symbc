package gov.nasa.jpf.symbc.numeric;

import gov.nasa.jpf.util.test.TestJPF;

import org.junit.Test;

public class TestSymbolicInteger extends TestJPF {

	@Test
	public void constructorDefaultShouldCreateObjectAndSetDefaultBounds() {
		SymbolicInteger symbolicInteger = new SymbolicInteger();
		
		assertEquals(symbolicInteger.getMin(), MinMax.getMinInt());
		assertEquals(symbolicInteger.getMax(), MinMax.getMaxInt());
	}
	
	@Test
	public void constructorNameShouldCreateObjectAndSetDefinedNameAndDefaultBounds() {
		String name = "test_name";
		SymbolicInteger symbolicInteger = new SymbolicInteger(name);
		
		assertEquals(symbolicInteger.getMin(), MinMax.getMinInt());
		assertEquals(symbolicInteger.getMax(), MinMax.getMaxInt());
		assertEquals(symbolicInteger.getName(), name);
	}
	
	@Test
	public void constructorNameAndBoundsShouldCreateObjectSetDefinedNameAndBounds() {
		String name = "test_name";
		int lowerBound = -19;
		int upperBound = 29;
		SymbolicInteger symbolicInteger = new SymbolicInteger(name, lowerBound, upperBound);
		
		assertEquals(symbolicInteger.getMin(), lowerBound);
		assertEquals(symbolicInteger.getMax(), upperBound);
		assertEquals(symbolicInteger.getName(), name);
	}
}
