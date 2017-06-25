package gov.nasa.jpf.symbc.numeric;

import gov.nasa.jpf.util.test.TestJPF;

import org.junit.Test;

public class TestSymbolicReal extends TestJPF {

	@Test
	public void constructorDefaultShouldCreateObjectAndSetDefaultBounds() {
		SymbolicReal symbolicReal = new SymbolicReal();
		
		assertEquals(symbolicReal.getMin(), MinMax.getMinDouble());
		assertEquals(symbolicReal.getMax(), MinMax.getMaxDouble());
	}
	
	@Test
	public void constructorNameShouldCreateObjectAndSetDefinedNameAndDefaultBounds() {
		String name = "test_name";
		SymbolicReal symbolicReal = new SymbolicReal(name);
		
		assertEquals(symbolicReal.getMin(), MinMax.getMinDouble());
		assertEquals(symbolicReal.getMax(), MinMax.getMaxDouble());
		assertEquals(symbolicReal.getName(), name);
	}
	
	@Test
	public void constructorNameAndBoundsShouldCreateObjectSetDefinedNameAndBounds() {
		String name = "test_name";
		double lowerBound = -21.5d;
		double upperBound = 31.33d;
		SymbolicReal symbolicReal = new SymbolicReal(name, lowerBound, upperBound);
		
		assertEquals(symbolicReal.getMin(), lowerBound);
		assertEquals(symbolicReal.getMax(), upperBound);
		assertEquals(symbolicReal.getName(), name);
	}
}
