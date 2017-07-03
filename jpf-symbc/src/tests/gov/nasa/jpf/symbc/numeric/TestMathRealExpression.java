package gov.nasa.jpf.symbc.numeric;

import gov.nasa.jpf.util.test.TestJPF;

import org.junit.Test;

public class TestMathRealExpression extends TestJPF {

	private static MathFunction[] oneArgumentFunctions = { MathFunction.ABS, MathFunction.SIN, MathFunction.COS,
			MathFunction.EXP, MathFunction.ASIN, MathFunction.ACOS, MathFunction.ATAN, MathFunction.LOG,
			MathFunction.TAN, MathFunction.SQRT };
	
	private static MathFunction[] twoArgumentsFunctions = { MathFunction.POW, MathFunction.ATAN2 };

	@Test
	public void constructorOneRealExpressionArgumentShouldSetFunctionAndFirstArgument() {
		RealConstant realConstant = new RealConstant(5);

		for (MathFunction function : oneArgumentFunctions) {
			MathRealExpression expression = new MathRealExpression(function, realConstant);

			assertEquals(function, expression.getFunction());
			assertEquals(realConstant, expression.getArgument1());
			assertEquals(null, expression.getArgument2());
		}
	}
	
	@Test
	public void constructorTwoRealExpressionArgumentsShouldSetFunctionAndTwoArguments() {
		RealConstant realConstant1 = new RealConstant(5);
		RealConstant realConstant2 = new RealConstant(6);

		for (MathFunction function : twoArgumentsFunctions) {
			MathRealExpression expression = new MathRealExpression(function, realConstant1, realConstant2);

			assertEquals(function, expression.getFunction());
			assertEquals(realConstant1, expression.getArgument1());
			assertEquals(realConstant2, expression.getArgument2());
		}
	}
	
	@Test
	public void constructorRealExpressionAndDoubleArgumentsShouldSetFunctionAndTwoArguments() {
		double doubleValue = 6;
		RealConstant realConstant1 = new RealConstant(5);
		RealConstant realConstant2 = new RealConstant(doubleValue);

		for (MathFunction function : twoArgumentsFunctions) {
			MathRealExpression expression = new MathRealExpression(function, realConstant1, doubleValue);

			assertEquals(function, expression.getFunction());
			assertEquals(realConstant1, expression.getArgument1());
			assertEquals(realConstant2, expression.getArgument2());
		}
	}
	
	@Test
	public void constructorDoubleAndRealExpressionArgumentsShouldSetFunctionAndTwoArguments() {
		double doubleValue = 5;
		RealConstant realConstant1 = new RealConstant(doubleValue);
		RealConstant realConstant2 = new RealConstant(6);

		for (MathFunction function : twoArgumentsFunctions) {
			MathRealExpression expression = new MathRealExpression(function, doubleValue, realConstant2);

			assertEquals(function, expression.getFunction());
			assertEquals(realConstant1, expression.getArgument1());
			assertEquals(realConstant2, expression.getArgument2());
		}
	}
	
	@Test
	public void compareToEqualOneArgumentExpressionShouldReturnZero() {
		MathFunction function = oneArgumentFunctions[0];
		RealConstant realConstant = new RealConstant(1);

		MathRealExpression expression1 = new MathRealExpression(function, realConstant);
		MathRealExpression expression2 = new MathRealExpression(function, realConstant);

		assertEquals(0, expression1.compareTo(expression2));
	}
	
	@Test
	public void compareToEqualTwoArgumentsExpressionShouldReturnZero() {
		MathFunction function = twoArgumentsFunctions[0];
		RealConstant realConstant1 = new RealConstant(1);
		RealConstant realConstant2 = new RealConstant(2);

		MathRealExpression expression1 = new MathRealExpression(function, realConstant1, realConstant2);
		MathRealExpression expression2 = new MathRealExpression(function, realConstant1, realConstant2);

		assertEquals(0, expression1.compareTo(expression2));
	}
	
	@Test
	public void getStringPathConditionOneArgumentFunctionShouldReturnCorrectString() {
		for (MathFunction function : oneArgumentFunctions) {
			RealConstant realConstant = new RealConstant(1.0d);
			MathRealExpression expression = new MathRealExpression(function, realConstant);
			assertEquals(String.format("(%s(%s))", function.toString(), realConstant.getStringPathCondition()),
					expression.getStringPathCondition());
		}
	}
	
	@Test
	public void getStringPathConditionTwoArgumentsFunctionShouldReturnCorrectString() {
		for (MathFunction function : twoArgumentsFunctions) {
			RealConstant realConstant1 = new RealConstant(1.0d);
			RealConstant realConstant2 = new RealConstant(-2.0d);
			MathRealExpression expression = new MathRealExpression(function, realConstant1, realConstant2);
			assertEquals(String.format("(%s(%s,%s))", function.toString(),
					realConstant1.getStringPathCondition(), realConstant2.getStringPathCondition()),
					expression.getStringPathCondition());
		}
	}
}
