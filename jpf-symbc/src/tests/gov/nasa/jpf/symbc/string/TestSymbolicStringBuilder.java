package gov.nasa.jpf.symbc.string;

import gov.nasa.jpf.symbc.numeric.Expression;
import gov.nasa.jpf.symbc.string.SymbolicStringBuilder;
import gov.nasa.jpf.util.test.TestJPF;

import static org.junit.Assert.assertNotEquals;
import org.junit.Test;

public class TestSymbolicStringBuilder extends TestJPF {
	
	@Test
	public void constructorDefaultShouldSuccessfullyCreateObjectWithoutStringExpression() {
		SymbolicStringBuilder symbolicStringBuilder = new SymbolicStringBuilder();
		
		assertEquals(symbolicStringBuilder.getStringExpression(), null);
		assertTrue(symbolicStringBuilder instanceof SymbolicStringBuilder);
		assertTrue(symbolicStringBuilder instanceof Expression);
	}
	
	@Test
	public void constructorStringExpressionShouldSuccessfullyCreateObjectWithDefinedStringExpression() {
		StringConstant stringConstant = new StringConstant("str");
		SymbolicStringBuilder symbolicStringBuilder = new SymbolicStringBuilder(stringConstant);
		
		assertEquals(symbolicStringBuilder.getStringExpression(), stringConstant);
	}
	
	@Test
	public void constructorStringShouldSuccessfullyCreateObjectWithDefinedStringConstant() {
		String str = "str";
		StringConstant stringConstant = new StringConstant(str);
		SymbolicStringBuilder symbolicStringBuilder = new SymbolicStringBuilder(str);
		
		assertEquals(symbolicStringBuilder.getStringExpression(), stringConstant);
	}
	
	@Test
	public void cloneShouldReturnNewIdenticalObject() {
		StringConstant stringConstant = new StringConstant("str");
		SymbolicStringBuilder symbolicStringBuilder = new SymbolicStringBuilder(stringConstant);
		SymbolicStringBuilder clonedSymbolicStringBuilder = symbolicStringBuilder.clone();
		
		assertEquals(symbolicStringBuilder.getStringExpression(), clonedSymbolicStringBuilder.getStringExpression());
		clonedSymbolicStringBuilder.setStringExpression(new StringConstant("str2"));
		assertNotEquals(symbolicStringBuilder.getStringExpression(), clonedSymbolicStringBuilder.getStringExpression());
	}
}
