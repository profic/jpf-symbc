package gov.nasa.jpf.symbc.string.translate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestBVAnd {
	
	@Test
	public void constructorShouldSetLeftAndRight() {
		BVConst left = new BVConst(-7);
		BVFalse right = new BVFalse();
		
		BVAnd bVAnd = new BVAnd(left, right);
		
		assertTrue(left == bVAnd.left);
		assertTrue(right == bVAnd.right);
	}
	
	@Test
	public void toSMTLibShouldReturnCorrectTranslation() {
		BVConst left = new BVConst(-7);
		BVFalse right = new BVFalse();
		
		BVAnd bVAnd = new BVAnd(left, right);
		
		assertEquals("(and (_ bv-7 8) false)", bVAnd.toSMTLib());
	}
}
