package gov.nasa.jpf.symbc.string.translate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestBVEq {
	
	@Test
	public void constructorShouldSetLeftAndRight() {
		BVConst left = new BVConst(-7);
		BVConst right = new BVConst(12);
		
		BVEq bVEq = new BVEq(left, right);
		
		assertTrue(left == bVEq.left);
		assertTrue(right == bVEq.right);
	}
	
	@Test
	public void toSMTLibShouldReturnCorrectTranslation() {
		BVConst left = new BVConst(-7);
		BVConst right = new BVConst(12);
		
		BVEq bVEq = new BVEq(left, right);
		
		assertEquals("(= (_ bv-7 8) (_ bv12 8))", bVEq.toSMTLib());
	}
}
