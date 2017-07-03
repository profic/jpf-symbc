package gov.nasa.jpf.symbc.string.translate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestBVOr {
	
	@Test
	public void constructorShouldSetLeftAndRight() {
		BVTrue left = new BVTrue();
		BVConst right = new BVConst(5);
		
		BVOr bVOr = new BVOr(left, right);
		
		assertTrue(left == bVOr.left);
		assertTrue(right == bVOr.right);
	}
	
	@Test
	public void toSMTLibShouldReturnCorrectTranslation() {
		BVTrue left = new BVTrue();
		BVConst right = new BVConst(5);
		
		BVOr bVOr = new BVOr(left, right);
		
		assertEquals("(or true (_ bv5 8))", bVOr.toSMTLib());
	}
}
