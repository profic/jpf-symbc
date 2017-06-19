package gov.nasa.jpf.symbc.string;

import gov.nasa.jpf.util.test.TestJPF;
import gov.nasa.jpf.symbc.numeric.IntegerExpression;
import gov.nasa.jpf.symbc.string.StringConstant;
import gov.nasa.jpf.symbc.string.StringExpression;
import gov.nasa.jpf.symbc.string.StringSymbolic;
import gov.nasa.jpf.symbc.string.SymbolicLastIndexOfInteger;

import org.junit.Test;

public class TestStringExpression extends TestJPF {
	
	@Test
	public void lastIndexOf() {
		StringConstant string = new StringConstant("str");
		StringConstant substring = new StringConstant("tr");
		IntegerExpression integerExpression = string._lastIndexOf(substring);
		
		assertTrue(integerExpression instanceof SymbolicLastIndexOfInteger);
		SymbolicLastIndexOfInteger symLastIndexOfInt = (SymbolicLastIndexOfInteger)integerExpression;
		
		assertTrue(symLastIndexOfInt.getSource().equals(string));
		assertTrue(symLastIndexOfInt.getExpression().equals(substring));
	}
}
