package gov.nasa.jpf.symbc.string;

import gov.nasa.jpf.symbc.numeric.Expression;
import gov.nasa.jpf.symbc.numeric.IntegerConstant;
import gov.nasa.jpf.symbc.numeric.IntegerExpression;
import gov.nasa.jpf.symbc.numeric.RealConstant;
import gov.nasa.jpf.symbc.numeric.RealExpression;
import gov.nasa.jpf.symbc.string.SymbolicStringBuilder;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestSymbolicStringBuilder {

	@Test
	public void constructorDefaultShouldSuccessfullyCreateObjectWithoutStringExpression() {
		SymbolicStringBuilder symStringBuilder = new SymbolicStringBuilder();

		assertEquals(symStringBuilder.getStringExpression(), null);
		assertTrue(symStringBuilder instanceof SymbolicStringBuilder);
		assertTrue(symStringBuilder instanceof Expression);
	}

	@Test
	public void constructorStringExpressionShouldSuccessfullyCreateObjectWithDefinedStringExpression() {
		StringConstant stringConstant = new StringConstant("str");
		SymbolicStringBuilder symStringBuilder = new SymbolicStringBuilder(stringConstant);

		assertEquals(symStringBuilder.getStringExpression(), stringConstant);
	}

	@Test
	public void constructorStringShouldSuccessfullyCreateObjectWithDefinedStringConstant() {
		String str = "str";
		StringConstant stringConstant = new StringConstant(str);
		SymbolicStringBuilder symStringBuilder = new SymbolicStringBuilder(str);

		assertEquals(symStringBuilder.getStringExpression(), stringConstant);
	}

	@Test
	public void cloneShouldReturnNewIdenticalObject() {
		StringConstant stringConstant = new StringConstant("str");
		SymbolicStringBuilder symStringBuilder = new SymbolicStringBuilder(stringConstant);
		SymbolicStringBuilder clonedSymbolicStringBuilder = symStringBuilder.clone();

		assertEquals(symStringBuilder.getStringExpression(), clonedSymbolicStringBuilder.getStringExpression());
		clonedSymbolicStringBuilder.setStringExpression(new StringConstant("str2"));
		assertNotEquals(symStringBuilder.getStringExpression(), clonedSymbolicStringBuilder.getStringExpression());
	}
	
	@Test
	public void appendIntSolutionShouldReturnCorrectString() {
		int[] intValues = new int[] { Integer.MIN_VALUE, -15, 0, 23, Integer.MAX_VALUE };

		for (int intValue : intValues) {
			SymbolicStringBuilder symStringBuilder = new SymbolicStringBuilder(new StringConstant());
			symStringBuilder._append(intValue);

			assertEquals(Integer.toString(intValue), symStringBuilder.getStringExpression().solution());
		}
	}
	
	@Test
	public void appendLongSolutionShouldReturnCorrectString() {
		long[] longValues = new long[] { Long.MIN_VALUE, -15, 0, 23, Long.MAX_VALUE };

		for (long longValue : longValues) {
			SymbolicStringBuilder symStringBuilder = new SymbolicStringBuilder(new StringConstant());
			symStringBuilder._append(longValue);

			assertEquals(Long.toString(longValue), symStringBuilder.getStringExpression().solution());
		}
	}

	@Test
	public void appendDoubleSolutionShouldReturnCorrectString() {
		double[] doubleValues = new double[] { Double.MIN_VALUE, -5.0d, 0, 11.0d, Double.MAX_VALUE };

		for (double doubleValue : doubleValues) {
			SymbolicStringBuilder symStringBuilder = new SymbolicStringBuilder(new StringConstant());
			symStringBuilder._append(doubleValue);

			assertEquals(Double.toString(doubleValue), symStringBuilder.getStringExpression().solution());
		}
	}
	
	@Test
	public void appendFloatSolutionShouldReturnCorrectString() {
		float[] floatValues = new float[] { -5.0f, 0, 11.0f };

		for (float floatValue : floatValues) {
			SymbolicStringBuilder symStringBuilder = new SymbolicStringBuilder(new StringConstant());
			symStringBuilder._append(floatValue);

			assertEquals(Float.toString(floatValue), symStringBuilder.getStringExpression().solution());
		}
	}
	
	@Test
	public void appendIntegerExpressionSolutionShouldReturnCorrectString() {
		int intValue = 5;
		IntegerExpression integerExpression = new IntegerConstant(intValue);
		SymbolicStringBuilder symStringBuilder = new SymbolicStringBuilder(new StringConstant());
		symStringBuilder._append(integerExpression);
		
		assertEquals(Integer.toString(intValue), symStringBuilder.getStringExpression().solution());
	}
	
	@Test
	public void appendRealExpressionSolutionShouldReturnCorrectString() {
		double doubleValue = 6.0d;
		RealExpression realExpression = new RealConstant(doubleValue);
		SymbolicStringBuilder symStringBuilder = new SymbolicStringBuilder(new StringConstant());
		symStringBuilder._append(realExpression);
		
		assertEquals(Double.toString(doubleValue), symStringBuilder.getStringExpression().solution());
	}
	
	@Test
	public void appendStringExpressionSolutionShouldReturnCorrectString() {
		String string = "str";
		StringExpression stringExpression = new StringConstant(string);
		SymbolicStringBuilder symStringBuilder = new SymbolicStringBuilder(new StringConstant());
		symStringBuilder._append(stringExpression);
		
		assertEquals(string, symStringBuilder.getStringExpression().solution());
	}
	
	@Test
	public void appendSymbolicStringBuilderSolutionShouldReturnCorrectString() {
		int intValue = 5;
		float floatValue = 11.0f;
		
		SymbolicStringBuilder symStringBuilder1 = new SymbolicStringBuilder(new StringConstant());
		SymbolicStringBuilder symStringBuilder2 = new SymbolicStringBuilder(new StringConstant());
		symStringBuilder1._append(intValue);
		symStringBuilder2._append(floatValue);
		symStringBuilder1._append(symStringBuilder2);

		assertEquals(Integer.toString(intValue).concat(Float.toString(floatValue)),
				symStringBuilder1.getStringExpression().solution());
	}
}
