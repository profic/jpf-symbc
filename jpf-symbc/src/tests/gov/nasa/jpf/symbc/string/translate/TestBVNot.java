package gov.nasa.jpf.symbc.string.translate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestBVNot {
	
	@Test
	public void constructorShouldSetExpression() {
		BVExpr expression = new BVExpr() { @Override public String toSMTLib() {return "";} };
		BVNot bVNot = new BVNot(expression);
				
		assertTrue(bVNot.expr == expression);
	}

	@Test
	public void toSMTLibShouldReturnCorrectExpressionNegation() {
		BVNot bVNot = new BVNot(new BVFalse());
		
		assertEquals("(not false)", bVNot.toSMTLib());
	}
}
