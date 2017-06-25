package gov.nasa.jpf.symbc.numeric;

import gov.nasa.jpf.util.test.TestJPF;

import static gov.nasa.jpf.symbc.numeric.Operator.*;

import org.junit.Test;

public class TestBinaryRealExpression extends TestJPF {
	
	@Test
	public void constructorShouldCorrectlySetLeftAndRightAndOperator() {
		RealConstant left = new RealConstant(2.0d);
		RealConstant right = new RealConstant(-1.0d);
		Operator operator = PLUS;
		
		BinaryRealExpression expression = new BinaryRealExpression(left, operator, right);
		
		assertTrue(expression.getLeft().equals(left));
		assertTrue(expression.getRight().equals(right));
		assertTrue(expression.getOperator().equals(operator));
	}
}
